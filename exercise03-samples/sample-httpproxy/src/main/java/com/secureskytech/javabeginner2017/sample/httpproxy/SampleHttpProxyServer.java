package com.secureskytech.javabeginner2017.sample.httpproxy;

import java.io.File;
import java.net.InetSocketAddress;

import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import net.lightbody.bmp.mitm.manager.ImpersonatingMitmManager;

public class SampleHttpProxyServer {

    private final InetSocketAddress listeningAddress;
    private final File dataDir;
    private final ImpersonatingMitmManager mitmManager =
        ImpersonatingMitmManager.builder().trustAllServers(true).build();

    private HttpProxyServer server;

    public SampleHttpProxyServer(final InetSocketAddress listeningAddress, final File dataDir) {
        this.listeningAddress = listeningAddress;
        this.dataDir = dataDir;
    }

    public void start() {
        server =
            DefaultHttpProxyServer
                .bootstrap()
                .withAddress(listeningAddress)
                .withManInTheMiddle(mitmManager)
                .withFiltersSource(new SampleHttpProxyFiltersSourceImpl(dataDir))
                .start();
    }

    public void stop() {
        server.stop();
    }
}
