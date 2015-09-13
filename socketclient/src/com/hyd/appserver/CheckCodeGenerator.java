package com.hyd.appserver;

import com.hyd.appserver.utils.Base64;
import com.hyd.appserver.utils.TripleDESUtils;

/**
 * 生成校验码
 *
 * @author yiding.he
 */
public class CheckCodeGenerator {

    // 根据 username 和 password 生成 checkCode 放入 request 中
    public static void injectCheckCode(Request request, String username, String password) {
        request.setTimestamp(String.valueOf(System.currentTimeMillis()));
        
        String text = request.getTimestamp() + "|" + request.getFunctionName() + "|" + username;
        byte[] encrypted = TripleDESUtils.encrypt(password, text);
        String checkCode = username + "\n" + Base64.encodeBytes(encrypted);

        request.setCheckCode(checkCode);
    }
}
