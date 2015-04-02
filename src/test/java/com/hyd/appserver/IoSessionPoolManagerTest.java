package com.hyd.appserver;

import com.hyd.appserver.core.IoSessionPoolManager;

/**
 * todo: description
 *
 * @author yiding.he
 */
public class IoSessionPoolManagerTest {

    public static void main(String[] args) {
        ClientConfiguration configuration = new ClientConfiguration();
        configuration.addServer("192.168.39.243", 11111);
        configuration.addServer("192.168.39.243", 8090);

        IoSessionPoolManager manager = new IoSessionPoolManager(configuration);
        try {
            borrow(manager);
            borrow(manager);
            borrow(manager);
            borrow(manager);
        } catch (Exception e) {
            System.out.println();
            e.printStackTrace();
        }

        manager.shutdown();
    }

    private static void borrow(IoSessionPoolManager manager) {
        manager.borrowIoSession().close(false);
        System.out.println("---- borrowed.");
    }
}
