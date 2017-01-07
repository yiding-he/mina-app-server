package com.hyd.appserver.decoder.http;

/**
 * (description)
 * created at 17/01/07
 *
 * @author yiding_he
 */
public class HttpRequest extends HttpMessage {

    private HttpRequestStatus status;

    public HttpRequestStatus getStatus() {
        return status;
    }

    public void setStatus(HttpRequestStatus status) {
        this.status = status;
    }
}
