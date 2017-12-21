package com.secureskytech.javabeginner2017.sample.httpserver;

import java.io.File;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public class SampleHttpServer {
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    final File staticRootDir;
    final int port;
    final boolean isHttps;

    private SampleHttpServer(final File staticRootDir, final int port, final boolean isHttps) {
        this.staticRootDir = staticRootDir;
        this.port = port;
        this.isHttps = isHttps;
    }

    public static SampleHttpServer createHTTP(final File staticRootDir, final int port) {
        return new SampleHttpServer(staticRootDir, port, false);
    }

    public static SampleHttpServer createHTTPS(final File staticRootDir, final int port) {
        return new SampleHttpServer(staticRootDir, port, true);
    }

    public Channel start() throws InterruptedException, SSLException, CertificateException {
        SslContext sslCtx;
        if (isHttps) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        ServerBootstrap b = new ServerBootstrap();
        b.option(ChannelOption.SO_BACKLOG, 1024);
        b
            .group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .handler(new LoggingHandler(LogLevel.INFO))
            .childHandler(new SampleHttpServerInitializer(staticRootDir, sslCtx));
        return b.bind(port).sync().channel();
    }

    public void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
