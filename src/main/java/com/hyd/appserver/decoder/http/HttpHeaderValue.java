package com.hyd.appserver.decoder.http;

/**
 * (description)
 * created at 17/01/07
 *
 * @author yiding_he
 */
public abstract class HttpHeaderValue {

    public static class StringValue extends HttpHeaderValue {

        private String value;

        public StringValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
