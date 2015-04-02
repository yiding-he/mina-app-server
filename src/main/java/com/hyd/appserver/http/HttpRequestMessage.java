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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A HTTP request message.
 *
 * @author The Apache MINA Project (dev@mina.apache.org)
 * @version $Rev: 576402 $, $Date: 2007-09-17 21:37:27 +0900 (월, 17  9월 2007) $
 */
public class HttpRequestMessage {

    private Map<String, String[]> parameters = new HashMap<String, String[]>();
    
    private Map<String, String> headers = new HashMap<String, String>();

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setData(Map<String, String[]> data) {

        for (String key : data.keySet()) {
            if (key.startsWith("@")) {
                parameters.put(key.substring(1), data.get(key));
            } else {
                String[] values = data.get(key);

                if (values.length > 0) {
                    headers.put(key, values[0]);
                } else {
                    headers.put(key, "");
                }
            }
        }
    }

    public String getContext() {
        String context = headers.get("Context");
        return context == null ? "" : context;
    }

    public String getParameter(String name) {
        String[] param = parameters.get(name);
        return (param == null || param.length == 0) ? "" : param[0];
    }

    public String[] getParameters(String name) {
        String[] param = parameters.get(name);
        return param == null ? new String[]{} : param;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public List<String> getParameterNames() {
        List<String> names = new ArrayList<String>();
        
        for (String s : parameters.keySet()) {
            names.add(s);
        }
        
        return names;
    }
}