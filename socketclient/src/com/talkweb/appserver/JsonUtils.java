package com.hyd.appserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * (description)
 *
 * @author yiding.he
 */
public class JsonUtils {

    private static Gson gson;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Response.class, new ResponseDeserializer());
        builder.registerTypeAdapter(Request.class, new RequestDeserializer());
        gson = builder.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    public static <T> T parse(Class<T> type, String json) {
        return gson.fromJson(json, type);
    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T parse(Class<T> type, JsonElement obj) {
        return gson.fromJson(obj, type);
    }

    /////////////////////////////////////////

    static class RequestDeserializer implements JsonDeserializer<Request> {

        public Request deserialize(JsonElement jsonElement, Type type,
                                   JsonDeserializationContext context) throws JsonParseException {
            Request request = new Request();
            JsonObject object = jsonElement.getAsJsonObject();

            request.setFunctionName(object.get("functionName").getAsString());

            if (object.has("parameters")) {
                Set<Map.Entry<String, JsonElement>> set = object.get("parameters").getAsJsonObject().entrySet();
                for (Map.Entry<String, JsonElement> entry : set) {
                    String key = entry.getKey();

                    JsonArray jsonArray = entry.getValue().getAsJsonArray();
                    String[] value = new String[jsonArray.size()];
                    for (int i = 0; i < jsonArray.size(); i++) {
                        value[i] = jsonArray.get(i).getAsString();
                    }

                    request.setParameter(key, value);
                }
            }

            if (object.has("headers")) {
                Set<Map.Entry<String, JsonElement>> set = object.get("headers").getAsJsonObject().entrySet();
                for (Map.Entry<String, JsonElement> entry : set) {
                    String key = entry.getKey();
                    String value = entry.getValue().getAsJsonObject().getAsString();
                    request.setHeader(key, value);
                }
            }

            return request;
        }
    }

    static class ResponseDeserializer implements JsonDeserializer<Response> {

        public Response deserialize(JsonElement jsonElement, Type type,
                                    JsonDeserializationContext context) throws JsonParseException {
            Response response = new Response();
            JsonObject object = jsonElement.getAsJsonObject();

            response.setSuccess(object.get("success").getAsBoolean());
            if (object.has("message")) {
                response.setMessage(object.get("message").getAsString());
            }

            if (object.has("data")) {
                Map<String, Object> map = new HashMap<String, Object>();
                Set<Map.Entry<String, JsonElement>> set = object.get("data").getAsJsonObject().entrySet();
                for (Map.Entry<String, JsonElement> entry : set) {
                    map.put(entry.getKey(), entry.getValue());
                }

                Response.setOriginalData(response, map);
            }

            return response;
        }
    }
}
