package com.hyd.appserver;

import com.hyd.appserver.authentication.BasicAuthentication;
import com.hyd.appserver.utils.JsonUtils;

/**
 * (描述)
 *
 * @author 贺一丁
 */
public class AuthenticatedRequestTest {

    public static void main(String[] args) {
        Request request = new Request("GetUserInfo");
        request.setParameter("userId", 100);
        request.setTimestamp("2012072412345678");

        BasicAuthentication authentication = new BasicAuthentication("username", "password");
        request.setCheckCode(authentication.generateCheckCode(request));

        System.out.println(JsonUtils.toJson(request));
    }

}
