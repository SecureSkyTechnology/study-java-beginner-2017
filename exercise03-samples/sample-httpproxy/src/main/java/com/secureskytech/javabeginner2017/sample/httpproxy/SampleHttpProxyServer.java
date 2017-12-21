package com.secureskytech.javabeginner2017.sample.httpproxy;

import java.net.InetSocketAddress;

import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import net.lightbody.bmp.mitm.manager.ImpersonatingMitmManager;

public class SampleHttpProxyServer {

    private final InetSocketAddress listeningAddress;
    private final ImpersonatingMitmManager mitmManager =
        ImpersonatingMitmManager.builder().trustAllServers(true).build();

    private HttpProxyServer server;

    public SampleHttpProxyServer(final InetSocketAddress listeningAddress) {
        this.listeningAddress = listeningAddress;
    }

    public void start() {
        server =
            DefaultHttpProxyServer
                .bootstrap()
                .withAddress(listeningAddress)
                .withManInTheMiddle(mitmManager)
                .withFiltersSource(new SampleHttpProxyFiltersSourceImpl())
                .start();
    }

    public void stop() {
        server.stop();
    }
}
