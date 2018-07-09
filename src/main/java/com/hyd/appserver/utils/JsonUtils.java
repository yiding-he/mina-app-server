package com.hyd.appserver.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.hyd.appserver.Response;
import com.hyd.appserver.core.ClientInfo;
import com.hyd.appserver.json.JsonRequestMessage;
import com.hyd.appserver.utils.dynamicobj.DynamicObject;

import java.util.*;

/**
 * (description)
 *
 * @author yiding.he
 */
public class JsonUtils {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static SerializeConfig mapping = new SerializeConfig();

    static {
        mapping.put(Date.class, new SimpleDateFormatSerializer(DATE_FORMAT));
    }

    public static <T> T parse(Class<T> type, String json) {
        return JSON.parseObject(json, type);
    }

    public static JsonRequestMessage parseRequest(String json) {

        JSONObject jsonObject = JSON.parseObject(json);
        JsonRequestMessage request = new JsonRequestMessage();

        String functionPath = StringUtils.defaultIfBlank(
                jsonObject.getString("functionPath"),
                jsonObject.getString("functionName")
        );

        request.setFunctionPath(functionPath);
        request.setCheckCode(jsonObject.getString("checkCode"));
        request.setTimestamp(jsonObject.getString("timestamp"));

        if (jsonObject.containsKey("parameters")) {

            Set<Map.Entry<String, Object>> set = jsonObject.getJSONObject("parameters").entrySet();

            for (Map.Entry<String, Object> entry : set) {
                String key = entry.getKey();

                Object value = entry.getValue();
                if (value instanceof JSONObject) {
                    request.setParameter(key, value.toString());
                } else if (value instanceof JSONArray) {
                    request.setParameter(key, ((JSONArray) value).toArray(new String[]{}));
                }
            }
        }

        if (jsonObject.containsKey("clientInfo")) {
            ClientInfo clientInfo = jsonObject.getObject("clientInfo", ClientInfo.class);
            request.setClientInfo(clientInfo);
        }

        return request;
    }

    public static Response parseResponse(String json) {

        JSONObject jsonObject = JSON.parseObject(json);
        Response response = new Response();
        response.setSuccess(jsonObject.getBoolean("success"));

        if (jsonObject.containsKey("message")) {
            response.setMessage(jsonObject.getString("message"));
        }

        if (jsonObject.containsKey("data")) {
            response.setData(fromJsonObject(jsonObject.getJSONObject("data")));
        }

        if (jsonObject.containsKey("code")) {
            response.setCode(jsonObject.getInteger("code"));
        }

        return response;

    }

    public static String toJson(Object object) {
        return toJson(object, false);
    }

    public static String toJson(Object object, boolean prettyFormat) {
        if (prettyFormat) {
            return JSON.toJSONString(object, mapping, SerializerFeature.PrettyFormat);
        } else {
            return JSON.toJSONString(object, mapping);
        }
    }

    /////////////////////////////////////////////////////////

    private static DynamicObject fromJsonObject(JSONObject jsonObject) {

        DynamicObject result = new DynamicObject();

        for (String key : jsonObject.keySet()) {
            Object data = jsonObject.get(key);

            if (data instanceof JSONArray) {
                result.put(key, fromJsonArray((JSONArray) data));
            } else if (data instanceof JSONObject) {
                result.put(key, fromJsonObject((JSONObject) data));
            } else {
                result.put(key, data);
            }
        }

        return result;
    }

    private static List<Object> fromJsonArray(JSONArray array) {

        List<Object> result = new ArrayList<Object>();

        for (Object item : array) {

            if (item instanceof JSONObject) {
                result.add(fromJsonObject((JSONObject) item));
            } else if (item instanceof JSONArray) {
                result.add(fromJsonArray((JSONArray) item));
            } else {
                result.add(item);
            }
        }

        return result;
    }
}
