package com.hyd.appserver;

public class AppServerException extends RuntimeException {

    public AppServerException() {
    }

    public AppServerException(String message) {
        super(message);
    }

    public AppServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppServerException(Throwable cause) {
        super(cause);
    }
}
