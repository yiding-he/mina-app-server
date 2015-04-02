package com.hyd.appserver.authentication;

/**
 * (description)
 *
 * @author yiding.he
 */
public class BasicAuthenticationTest {

    public static void main(String[] args) {
        BasicAuthentication authentication = new BasicAuthentication("anxun", "AYqkGpeF12CJmR9T");
        String checkcode = authentication.generateCheckCode("UpdateContentStatus", "20120727104338145");
        System.out.println(checkcode);
    }
}
