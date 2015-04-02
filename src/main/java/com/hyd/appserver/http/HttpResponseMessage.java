package com.hyd.appserver.http;

/*
*  Licensed to the Apache Software Foundation (ASF) under one
*  or more contributor license agreements.  See the NOTICE file
*  distributed with this work for additional information
*  regarding copyright ownership.  The ASF licenses this file
*  to you under the Apache License, Version 2.0 (the
*  "License"); you may not use this file except in compliance
*  with the License.  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*
*/

import com.hyd.appserver.MinaAppServer;
import org.apache.mina.core.buffer.IoBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A HTTP response message.
 *
 * @author The Apache MINA Project (dev@mina.apache.org)
 * @version $Rev: 581234 $, $Date: 2007-10-02 22:39:48 +0900 (화, 02 10월 2007) $
 */
public class HttpResponseMessage {

    /**
     * HTTP response codes
     */
    public static final int HTTP_STATUS_SUCCESS = 200;

    public static final int HTTP_STATUS_NOT_FOUND = 404;

    public static final int HTTP_STATUS_NOT_MODIFIED = 304;

    private static final String CHARSET = "UTF-8";

    /**
     * Map<String, String>
     */
    private final Map<String, String> headers = new HashMap<String, String>();

    /**
     * Storage for body of HTTP response.
     */
    private final ByteArrayOutputStream body = new ByteArrayOutputStream(1024);

    private int responseCode = HTTP_STATUS_SUCCESS;
    
    public static final SimpleDateFormat FORMATTER = 
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);

    public HttpResponseMessage() {
        headers.put("Server", "HttpServer (" + MinaAppServer.VERSION_STRING + ')');
        headers.put("Cache-Control", "private");
        headers.put("Content-Type", "text/html; charset=" + CHARSET);
        headers.put("Connection", "keep-alive");
        headers.put("Keep-Alive", "200");
        headers.put("Date", FORMATTER.format(new Date()));
    }

    public HttpResponseMessage(HttpRequestMessage requestMessage) {
        this();
        
        this.headers.putAll(requestMessage.getHeaders());
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setContentType(String contentType) {
        headers.put("Content-Type", contentType);
    }

    public void setMimeType(String mimeType) {
        headers.put("Content-Type", mimeType + "; charset=" + CHARSET);
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public void appendBody(byte[] b) {
        try {
            body.write(b);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void appendBody(String s) {
        try {
            body.write(s.getBytes(CHARSET));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public IoBuffer getBody() {
        return IoBuffer.wrap(body.toByteArray());
    }

    public int getBodyLength() {
        return body.size();
    }
}