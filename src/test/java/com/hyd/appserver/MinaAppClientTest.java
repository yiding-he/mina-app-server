package com.hyd.appserver;

public class MinaAppClientTest {

    public static void main(String[] args) {
        try(MinaAppClient client = new MinaAppClient("localhost", 18090)) {
            Response response = client.send(new Request("QueryEduCartoonContentById"));
            System.out.println(response.getMessage());
        }
    }
}
