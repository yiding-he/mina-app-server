package com.hyd.appserver;

import com.hyd.appserver.core.ServerConfiguration;

public class MultipleServerTest {

    public static void main(String[] args) throws Exception {
        new MinaAppServer(config(8090)).start();
        Thread.sleep(15000);
        new MinaAppServer(config(8091)).start();
        Thread.sleep(15000);
        new MinaAppServer(config(8092)).start();
    }

    private static ServerConfiguration config(int port) {
        ServerConfiguration sc = new ServerConfiguration();
        sc.setListenPort(port);
        sc.setAdminPort(port + 1000);
        return sc;
    }
}
