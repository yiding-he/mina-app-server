package com.hyd.appserver.utils.dynamicobj;

/**
 * (描述)
 *
 * @author 贺一丁
 */
public class DynamicObjectException extends RuntimeException {

    public DynamicObjectException() {
    }

    public DynamicObjectException(String message) {
        super(message);
    }

    public DynamicObjectException(String message, Throwable cause) {
        super(message, cause);
    }

    public DynamicObjectException(Throwable cause) {
        super(cause);
    }
}
