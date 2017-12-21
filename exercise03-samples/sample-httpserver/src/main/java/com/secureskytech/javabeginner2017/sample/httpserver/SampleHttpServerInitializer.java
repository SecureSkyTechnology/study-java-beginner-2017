package com.secureskytech.javabeginner2017.sample.httpserver;

import java.io.File;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

public class SampleHttpServerInitializer extends ChannelInitializer<SocketChannel> {
    final SslContext sslCtx;
    final File staticRootDir;

    public SampleHttpServerInitializer(final File staticRootDir, SslContext sslCtx) {
        this.sslCtx = sslCtx;
        this.staticRootDir = staticRootDir;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpObjectAggregator(10 * 1024 * 1024));
        p.addLast(new SampleHttpServerHandler(this.staticRootDir));
    }
}
