package com.hyd.appserver;

/**
 * 服务器端用于鉴权的接口。客户端生成的鉴权信息会
 * 放在 Request 的 checkCode 属性内，服务器端
 * 需要依据该属性值以及 Request 中的其他信息判断
 * 客户端的请求是否合法。
 *
 * @author yiding.he
 */
public interface Authenticator {

    /**
     * 判断请求是否合法
     *
     * @param request 请求
     *
     * @return 如果请求合法则返回 true，否则返回 false
     *
     * @throws Exception 如果鉴权出错
     */
    boolean authenticate(Request request) throws Exception;
}
