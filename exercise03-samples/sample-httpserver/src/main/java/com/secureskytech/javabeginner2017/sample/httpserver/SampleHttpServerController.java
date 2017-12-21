package com.secureskytech.javabeginner2017.sample.httpserver;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.io.Files;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;

public class SampleHttpServerController {
    private static final Logger LOG = LoggerFactory.getLogger(SampleHttpServerController.class);

    final File rootDir;
    final FullHttpRequest request;
    final URI uri;
    final String path;
    final HttpHeaders headers;
    final String requestContentType;
    final byte[] requestBody;
    final HexDumper dumper;

    public SampleHttpServerController(final File rootDir, FullHttpRequest request) throws URISyntaxException {
        this.rootDir = rootDir;
        this.request = request;
        this.uri = new URI(request.getUri());
        this.path = uri.getPath();
        this.headers = request.headers();
        this.requestContentType = headers.get(CONTENT_TYPE);
        ByteBuf requestBodyByteBuf = request.content();
        this.requestBody = new byte[requestBodyByteBuf.readableBytes()];
        requestBodyByteBuf.readBytes(this.requestBody);
        this.dumper = new HexDumper();
        dumper.setPrefix("0x");
        dumper.setSeparator(",");
        dumper.setToUpperCase(true);
    }

    public FullHttpResponse dispatch() throws UnsupportedOperationException, IllegalArgumentException, IOException {
        switch (path) {
        case "/echoasis":
            return echoAsIs();
        default:
            try {
                return staticResource();
            } catch (Exception e) {
                LOG.debug("static resource falldown error", e);
                throw new UnsupportedOperationException("path[" + path + "] is not supported.");
            }
        }
    }

    FullHttpResponse staticResource() throws IOException {
        String basename = "/".equals(path) ? "/index.html" : path;
        basename = (basename.indexOf('?') > 0) ? basename.substring(0, basename.indexOf('?')) : basename;
        File destPath = new File(rootDir, "." + basename);
        LOG.debug("destPath = {}", destPath.getAbsolutePath());
        byte[] contents = Files.toByteArray(destPath);
        final String ext = ApacheHttpdMimeTypes.defaultMimeTypes.getExtension(basename);
        String mimetype = ApacheHttpdMimeTypes.defaultMimeTypes.getMimeType(ext);
        if (mimetype.startsWith("text/") || "application/javascript".equals(mimetype)) {
            mimetype = mimetype + "; charset=utf-8";
        }
        LOG.debug("staticResource for {} found, ext={}, mimetype detected as {}", basename, ext, mimetype);
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(contents));
        HttpHeaders.setContentLength(response, contents.length);
        HttpHeaders.setHeader(response, CONTENT_TYPE, mimetype);
        return response;
    }

    FullHttpResponse echoAsIs() {
        LOG.info("echoasis : Content-Type={}, requestBody={}", requestContentType, dumper.dump(requestBody));

        String responseContentType = "application/octet-stream";
        if (!Strings.isNullOrEmpty(requestContentType)) {
            responseContentType = headers.get(CONTENT_TYPE);
        }
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(this.requestBody));
        HttpHeaders.setContentLength(response, this.requestBody.length);
        HttpHeaders.setHeader(response, CONTENT_TYPE, responseContentType);

        return response;
    }

}
