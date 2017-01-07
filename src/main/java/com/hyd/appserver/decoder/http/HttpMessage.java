package com.hyd.appserver.decoder.http;

/**
 * (description)
 * created at 17/01/07
 *
 * @author yiding_he
 */
public abstract class HttpMessage {

    private HttpHeader header;

    private HttpBody body;

    public HttpHeader getHeader() {
        return header;
    }

    public void setHeader(HttpHeader header) {
        this.header = header;
    }

    public HttpBody getBody() {
        return body;
    }

    public void setBody(HttpBody body) {
        this.body = body;
    }
}
