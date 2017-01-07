package com.hyd.appserver.decoder.http;

/**
 * (description)
 * created at 17/01/07
 *
 * @author yiding_he
 */
public class HttpResponse extends HttpMessage {

    private HttpResponseStatus status;

    public HttpResponseStatus getStatus() {
        return status;
    }

    public void setStatus(HttpResponseStatus status) {
        this.status = status;
    }
}
