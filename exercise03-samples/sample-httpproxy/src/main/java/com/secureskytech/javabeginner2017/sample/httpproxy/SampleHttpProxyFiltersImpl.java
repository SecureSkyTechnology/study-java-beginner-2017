package com.secureskytech.javabeginner2017.sample.httpproxy;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import org.littleshoot.proxy.HttpFiltersAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.google.common.io.Files;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class SampleHttpProxyFiltersImpl extends HttpFiltersAdapter {
    private static Logger LOG = LoggerFactory.getLogger(SampleHttpProxyFiltersImpl.class);
    private final Marker m;
    private final InetSocketAddress clientAddress;
    private final File dataDir;
    private final long httpCount;
    private boolean isHttps = false;
    private InetSocketAddress connectedRemoteAddress;

    /* HTTPSのMITMを通すときにインスタンスが新しく作成されるために引き継げないフィールドがある。
     * 以下は、それら引き継げないフィールドを channel の attribute に設定するkey。
     * 
     * 例: resolvedRemoteAddress ->
     * HTTPSでMITMを通った時は、resolvedされるのは最初のCONNECT時のインスタンスで、
     * MITMの中を通すときのインスタンスは別となり、resolveされたInetSocketAddressが引き継がれない。
     * また、MITMの中を通すときのインスタンス中から ctx.channel().remoteAddress() を呼んでもそれは
     * MITM用のサーバとなってしまうため、本当の接続先情報とはならない。
     * このため、proxyToServerResolutionSucceeded() の時点で InetSocketAddress を channel のattribute
     * にset()し、これを使うことでhttp/https共用、かつ、外部からインジェクション可能な状態にして
     * テストコードを書ける状態にしておく。
     * 
     * テストコード作成を考慮するとchannelのmockが非常に手間取ることがわかったため、可能な限り
     * 外部からインジェクション可能な設計にしている。
     * (= adapterなどの補助クラスが見当たらない)
     */
    private final AttributeKey<Boolean> isHttpsAttrKey = AttributeKey.valueOf("isHttps");
    private final AttributeKey<InetSocketAddress> resolvedRemoteAddressKey =
        AttributeKey.valueOf("resolvedRemoteAddress");

    public SampleHttpProxyFiltersImpl(HttpRequest originalRequest, ChannelHandlerContext ctx, final File dataDir,
            final InetSocketAddress clientAddress, final long httpCount) {
        super(originalRequest, ctx);
        this.clientAddress = clientAddress;
        this.m = MarkerFactory.getMarker("http#" + httpCount);
        this.httpCount = httpCount;
        this.dataDir = dataDir;
        LOG.debug(m, "client IP : {}", clientAddress.toString());
    }

    private void saveRequest(FullHttpRequest fhr) {
        LocalDateTime ldt0 = LocalDateTime.now();
        DateTimeFormatter dtf0 = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
        final File dataf = new File(dataDir, ldt0.format(dtf0) + "_" + this.httpCount + ".req");
        byte[] asByte = SampleNettyUtil.FullHttpRequestToBytes(fhr.copy());
        try {
            Files.write(asByte, dataf);
        } catch (IOException e) {
            LOG.debug(m, "saveRequest() error", e);
        }
    }

    private void saveResponse(FullHttpResponse fhr) {
        LocalDateTime ldt0 = LocalDateTime.now();
        DateTimeFormatter dtf0 = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
        final File dataf = new File(dataDir, ldt0.format(dtf0) + "_" + this.httpCount + ".res");
        byte[] asByte = SampleNettyUtil.FullHttpResponseToBytes(fhr.copy());
        try {
            Files.write(asByte, dataf);
        } catch (IOException e) {
            LOG.debug(m, "saveResponse() error", e);
        }
    }

    @Override
    public HttpResponse clientToProxyRequest(HttpObject httpObject) {
        if (httpObject instanceof HttpRequest) {
            HttpRequest hr = (HttpRequest) httpObject;
            Attribute<Boolean> isHttpsAttr = this.ctx.attr(this.isHttpsAttrKey);
            Boolean isHttpsVal = isHttpsAttr.get();
            if (Objects.isNull(isHttpsVal)) {
                isHttpsVal = false;
            }
            if (hr.getMethod().equals(HttpMethod.CONNECT)) {
                isHttpsVal = true;
            }
            this.isHttps = isHttpsVal.booleanValue();
            isHttpsAttr.set(isHttpsVal);
            LOG.debug(
                m,
                "http request : https? : {}, {} {} {}",
                this.isHttps,
                hr.getMethod(),
                hr.getUri(),
                hr.getProtocolVersion());
        }
        return null;
    }

    @Override
    public void proxyToServerResolutionSucceeded(String serverHostAndPort, InetSocketAddress resolvedRemoteAddress) {
        LOG.debug(m, "proxyToServerResolutionSucceeded({}, {}", serverHostAndPort, resolvedRemoteAddress);
        Attribute<InetSocketAddress> resolvedRemoteAddressAttr = this.ctx.attr(this.resolvedRemoteAddressKey);
        resolvedRemoteAddressAttr.set(resolvedRemoteAddress);
    }

    @Override
    public HttpResponse proxyToServerRequest(HttpObject httpObject) {
        LOG.trace(m, "proxyToServerRequest()");
        if (httpObject instanceof FullHttpRequest) {
            FullHttpRequest fhr = (FullHttpRequest) httpObject;
            Attribute<InetSocketAddress> resolvedRemoteAddressAttr = this.ctx.attr(this.resolvedRemoteAddressKey);
            InetSocketAddress currentResolved = resolvedRemoteAddressAttr.get();
            this.saveRequest(fhr);
            LOG.trace(
                m,
                "proxyToServerRequest() : https?{} method={} client={}, remote={} url={}",
                this.isHttps,
                fhr.getMethod().name(),
                this.clientAddress,
                currentResolved,
                fhr.getUri());
        }
        return null;
    }

    @Override
    public void proxyToServerConnectionStarted() {
        LOG.trace(m, "proxyToServerConnectionStarted()");
    }

    @Override
    public void proxyToServerConnectionSucceeded(ChannelHandlerContext serverCtx) {
        this.connectedRemoteAddress = (InetSocketAddress) serverCtx.channel().remoteAddress();
        LOG.debug(m, "proxyToServerConnectionSucceeded() to {}", this.connectedRemoteAddress);
    }

    @Override
    public void proxyToServerRequestSent() {
        LOG.trace(m, "proxyToServerRequestSent()");
    }

    @Override
    public HttpObject serverToProxyResponse(HttpObject httpObject) {
        LOG.trace(m, "serverToProxyResponse()");
        if (httpObject instanceof FullHttpResponse) {
            FullHttpResponse fhr = (FullHttpResponse) httpObject;
            final HttpResponseStatus status = fhr.getStatus();
            final HttpVersion version = fhr.getProtocolVersion();
            final long contentLength = HttpHeaders.getContentLength(fhr, 0L);
            this.saveResponse(fhr);
            LOG.debug(
                m,
                "serverToProxyResponse(): {} {} {}, length={}",
                version.text(),
                status.code(),
                status.reasonPhrase(),
                contentLength);
        }
        return httpObject;
    }

    @Override
    public HttpObject proxyToClientResponse(HttpObject httpObject) {
        LOG.trace(m, "proxyToClientResponse()");
        if (httpObject instanceof FullHttpResponse) {
            FullHttpResponse fhr = (FullHttpResponse) httpObject;
            final HttpResponseStatus status = fhr.getStatus();
            final HttpVersion version = fhr.getProtocolVersion();
            final long contentLength = HttpHeaders.getContentLength(fhr, 0L);
            LOG.debug(
                m,
                "proxyToClientResponse(): {} {} {}, length={}",
                version.text(),
                status.code(),
                status.reasonPhrase(),
                contentLength);
        }
        return httpObject;
    }

    @Override
    public void serverToProxyResponseReceived() {
        LOG.trace(m, "serverToProxyResponseReceived()");
    }
}
