package com.hyd.appserver;

/**
 * 发生在客户端的异常
 * 可能的原因：无法连接到服务器、无法生成请求、无法解析服务器端回应
 *
 * @author yiding.he
 */
public class AppClientException extends RuntimeException {

    public AppClientException() {
    }

    public AppClientException(Throwable cause) {
        super(cause);
    }

    public AppClientException(String message) {
        super(message);
    }

    public AppClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
