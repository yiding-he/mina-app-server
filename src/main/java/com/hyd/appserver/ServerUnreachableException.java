package com.hyd.appserver;

/**
 * (description)
 *
 * @author yiding.he
 */
public class ServerUnreachableException extends AppServerException {

    public ServerUnreachableException() {
    }

    public ServerUnreachableException(String message) {
        super(message);
    }

    public ServerUnreachableException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerUnreachableException(Throwable cause) {
        super(cause);
    }
}
