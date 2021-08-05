package com.hyd.appserver;

public class ClientAutoBalancingTest {

    public static void main(String[] args) throws Exception {
        ClientConfiguration cc = new ClientConfiguration();
        cc.addServer("localhost", 8090);
        cc.addServer("localhost", 8091);
        cc.addServer("localhost", 8092);

        MinaAppClient client = new MinaAppClient(cc);
        while (true) {
            client.send(new Request("123"));
            Thread.sleep(10000);
        }
    }
}
