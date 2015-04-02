package com.hyd.appserver;

/**
 * 生成校验码的类。校验码用于服务器端识别请求是否是合法的。
 *
 * @author yiding.he
 */
public interface Authentication {

    /**
     * 生成校验码
     *
     * @param request 请求
     *
     * @return 校验码
     */
    String generateCheckCode(Request request);
}
