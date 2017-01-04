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

import com.hyd.appserver.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderAdapter;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link MessageDecoder} that decodes {@link HttpRequestMessage}.
 *
 * @author The Apache MINA Project (dev@mina.apache.org)
 */
public class HttpRequestDecoder extends MessageDecoderAdapter {

    static final Logger log = LogManager.getLogger(HttpRequestDecoder.class);

    private static final byte[] CONTENT_LENGTH = "Content-Length:".getBytes();

    private final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();

    private HttpRequestMessage request = null;

    private String status = "READY";

    public HttpRequestDecoder() {
    }

    public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
        // Return NEED_DATA if the whole header is not read yet.
        MessageDecoderResult result;

        try {
            Boolean completed = messageComplete(in);
            if (completed == null) {
                result = NOT_OK;
            } else {
                result = completed ? MessageDecoderResult.OK : MessageDecoderResult.NEED_DATA;
            }
        } catch (Exception ex) {
            log.error("", ex);
            result = MessageDecoderResult.NOT_OK;
        }

        return result;
    }

    public MessageDecoderResult decode(IoSession session, IoBuffer in,
                                       ProtocolDecoderOutput out) throws Exception {
        // Try to decode body
        HttpRequestMessage m = decodeBody(in);

        // Return NEED_DATA if the body is not fully read.
        if (m == null) {
            return MessageDecoderResult.NEED_DATA;
        }

        out.write(m);

        return MessageDecoderResult.OK;
    }

    private Boolean messageComplete(IoBuffer in) throws Exception {
        int last = in.remaining() - 1;
        if (in.remaining() < 4) {
            return false;
        }

        // to speed up things we check if the Http request is a GET or POST
        if (in.get(0) == (byte) 'G' && in.get(1) == (byte) 'E'
                && in.get(2) == (byte) 'T') {

            // Http GET request therefore the last 4 bytes should be 0x0D 0x0A 0x0D 0x0A
            boolean completed = in.get(last) == (byte) 0x0A
                    && in.get(last - 1) == (byte) 0x0D
                    && in.get(last - 2) == (byte) 0x0A
                    && in.get(last - 3) == (byte) 0x0D;

            if (!completed) {
                status = "pending";
            } else {
                status = "ready";
            }
            return completed;
        } else if (in.get(0) == (byte) 'P' && in.get(1) == (byte) 'O'
                && in.get(2) == (byte) 'S' && in.get(3) == (byte) 'T') {
            // Http POST request
            // first the position of the 0x0D 0x0A 0x0D 0x0A bytes
            int eoh = -1;
            for (int i = last; i > 2; i--) {
                if (in.get(i) == (byte) 0x0A && in.get(i - 1) == (byte) 0x0D
                        && in.get(i - 2) == (byte) 0x0A
                        && in.get(i - 3) == (byte) 0x0D) {
                    eoh = i + 1;
                    break;
                }
            }
            if (eoh == -1) {
                status = "pending";
                return false;
            }
            for (int i = 0; i < last; i++) {
                boolean found = false;
                for (int j = 0; j < CONTENT_LENGTH.length; j++) {
                    if (in.get(i + j) != CONTENT_LENGTH[j]) {
                        found = false;
                        break;
                    }
                    found = true;
                }
                if (found) {
                    // retrieve value from this position till next 0x0D 0x0A
                    StringBuilder contentLength = new StringBuilder();
                    for (int j = i + CONTENT_LENGTH.length; j < last; j++) {
                        if (in.get(j) == 0x0D) {
                            break;
                        }
                        contentLength.append(new String(
                                new byte[]{in.get(j)}));
                    }
                    // if content-length worth of data has been received then the message is complete
                    boolean completed =
                            Integer.parseInt(contentLength.toString().trim()) + eoh == in.remaining();
                    if (!completed) {
                        status = "pending";
                    } else {
                        status = "ready";
                    }
                    return completed;
                }
            }
        }

        // the message is not complete and we need more data
        return status.equals("ready") ? null : false;
    }

    private HttpRequestMessage decodeBody(IoBuffer in) {
        request = new HttpRequestMessage();
        try {
            request.setData(parseRequest(new StringReader(in.getString(decoder))));
            return request;
        } catch (CharacterCodingException ex) {
            log.error("", ex);
        }

        return null;
    }

    private Map<String, String[]> parseRequest(Reader is) {
        Map<String, String[]> map = new HashMap<String, String[]>();
        BufferedReader rdr = new BufferedReader(is);

        try {
            // Get request URL.
            String line = rdr.readLine();
            String[] url = line.split(" ");
            if (url.length < 3) {
                return map;
            }

            map.put("URI", new String[]{line});
            map.put("Method", new String[]{url[0].toUpperCase()});
            map.put("Context", new String[]{url[1].substring(1)});
            map.put("Protocol", new String[]{url[2]});

            // Read header
            while ((line = rdr.readLine()) != null && line.length() > 0) {
                String[] tokens = line.split(": ");
                map.put(tokens[0], new String[]{tokens[1]});
            }

            // If method 'POST' then read Content-Length worth of data
            if (url[0].equalsIgnoreCase("POST")) {
                int len = Integer.parseInt(map.get("Content-Length")[0]);
                char[] buf = new char[len];
                if (rdr.read(buf) == len) {
                    line = String.copyValueOf(buf);
                }
            } else if (url[0].equalsIgnoreCase("GET")) {
                int idx = url[1].indexOf('?');
                if (idx != -1) {
                    map.put("Context",
                            new String[]{url[1].substring(1, idx)});
                    line = url[1].substring(idx + 1);
                } else {
                    line = null;
                }
            }

            // Parse URI parameters
            if (line != null) {
                String[] match = line.split("&");
                for (String element : match) {
                    String[] params = new String[1];
                    String[] tokens = element.split("=");
                    switch (tokens.length) {
                        case 0:
                            map.put("@".concat(element), new String[]{});
                            break;
                        case 1:
                            map.put("@".concat(tokens[0]), new String[]{});
                            break;
                        default:
                            String name = "@".concat(tokens[0]);
                            if (map.containsKey(name)) {
                                params = map.get(name);
                                String[] tmp = new String[params.length + 1];
                                System.arraycopy(params, 0, tmp, 0, params.length);
                                params = tmp;
                            }
                            params[params.length - 1] = StringUtils.decodeUrl(tokens[1].trim());
                            map.put(name, params);
                    }
                }
            }
        } catch (IOException ex) {
            log.error("", ex);
        }

        return map;
    }
}