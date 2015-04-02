package com.hyd.appserver;

/**
 * (描述)
 *
 * @author HeYiding
 */
public class MultiThreadRequestForLongDelayActions {

    public static void main(String[] args) throws Exception {
        final MinaAppClient client = new MinaAppClient("localhost", 8090);
        final Request request = new Request("LongDelay");

        Runnable runnable = new Runnable() {
            public void run() {
                client.send(request);
            }
        };

        for (int i = 0; i < 20; i++) {
            new Thread(runnable).start();
        }
    }
}
