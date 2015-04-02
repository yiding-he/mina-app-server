package com.hyd.appserver;

/**
 * 发生在服务器端的异常
 *
 * @author yiding.he
 */
public class AppServerException extends RuntimeException {

    public static final int CONNECTION_FAILED = 1;

    private int code = -1;

    public int getCode() {
        return code;
    }

    public AppServerException() {
    }

    public AppServerException(int code) {
        this("code=" + code);
        this.code = code;
    }

    public AppServerException(String message) {
        super(message);
    }

    public AppServerException(int code, Throwable cause) {
        this("code=" + code, cause);
        this.code = code;
    }

    public AppServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppServerException(Throwable cause) {
        super(cause);
    }
}
