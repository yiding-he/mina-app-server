package com.hyd.appserver.core;

import com.hyd.appserver.MinaAppServer;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

public class AppServerFactoryTest {

    @Test
    public void testCreateServer() throws Exception {
        Properties properties = new Properties();
        properties.put(AppServerFactory.PROPERTY_PORT, 8090);   // 传 "8090" 字符串也可以
        properties.put(AppServerFactory.PROPERTY_ACTION_PACKAGES, "pack1,pack2,pack3");  // 逗号隔开

        MinaAppServer server = AppServerFactory.createServer(properties);
        server.start();
    }
}

