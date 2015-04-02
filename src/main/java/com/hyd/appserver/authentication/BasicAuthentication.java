package com.hyd.appserver.authentication;

import com.hyd.appserver.Authentication;
import com.hyd.appserver.Request;
import com.hyd.appserver.utils.Base64;
import com.hyd.appserver.utils.TripleDESUtils;

/**
 * Authentication 接口的缺省实现：基于“用户名-密码”的验证方式。服务器如果要求验证的话，必须也是基于该验证方式的。
 *
 * @author yiding.he
 */
public class BasicAuthentication implements Authentication {

    private String name;

    private String key;

    public BasicAuthentication(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    @Override
    public String generateCheckCode(Request request) {
        return generateCheckCode(request.getFunctionName(), request.getTimestamp());
    }

    public String generateCheckCode(String functionName, String timeStamp) {
        String text = timeStamp + "|" + functionName + "|" + this.name;
        String key = this.key;

        byte[] encrypted = TripleDESUtils.encrypt(key, text);
        return this.name + "\n" + Base64.encodeBytes(encrypted);
    }
}
