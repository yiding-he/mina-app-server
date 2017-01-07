package com.hyd.appserver.decoder.http;

import com.hyd.appserver.decoder.http.HttpHeaderValue.StringValue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * (description)
 * created at 17/01/07
 *
 * @author yiding_he
 */
public class HttpHeader {

    private Map<String, HttpHeaderValue> fields = new HashMap<>();

    public Set<String> getFieldNames() {
        return new HashSet<>(fields.keySet());
    }

    @SuppressWarnings("unchecked")
    public <T extends HttpHeaderValue> T getFieldValue(String field) {
        if (!fields.containsKey(field)) {
            return null;
        } else {
            return (T) fields.get(field);
        }
    }

    public String getFieldStringValue(String field) {
        StringValue stringValue = (StringValue) fields.get(field);
        return stringValue == null ? null : stringValue.getValue();
    }

    public void setFieldValue(String field, HttpHeaderValue value) {
        this.fields.put(field, value);
    }

    public void setFieldStringValue(String field, String value) {
        this.setFieldValue(field, new StringValue(value));
    }

    public void removeField(String field) {
        this.fields.remove(field);
    }
}
