package com.hyd.appserver;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 接口返回值
 *
 * @author yiding.he
 */
public class Response {

    /**
     * 创建一个表示“调用成功”的 Response 对象
     *
     * @return 表示“调用成功”的 Response 对象
     */
    public static Response success() {
        Response response = new Response();
        response.setSuccess(true);
        return response;
    }

    /**
     * 创建一个表示“调用成功”的 Response 对象
     *
     * @param message 调用结果描述
     *
     * @return 表示“调用成功”的 Response 对象
     */
    public static Response success(String message) {
        Response response = new Response();
        response.setSuccess(true);
        response.setMessage(message);
        return response;
    }

    /**
     * 创建一个表示“调用失败”的 Response 对象
     *
     * @param message 调用结果描述
     *
     * @return 表示“调用失败”的 Response 对象
     */
    public static Response fail(String message) {
        Response response = new Response();
        response.setSuccess(false);
        response.setCode(-1);
        response.setMessage(message);
        return response;
    }

    /**
     * 创建一个表示“调用失败”的 Response 对象
     *
     * @param message 结果描述
     * @param code    结果代码
     *
     * @return 表示“调用失败”的 Response 对象
     */
    public static Response fail(String message, int code) {
        Response response = new Response();
        response.setSuccess(false);
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    /**
     * 创建一个表示“查询结果为空”的 Response 对象
     *
     * @return 表示“查询结果为空”的 Response 对象
     */
    public static Response empty() {
        return new Response();
    }

    /////////////////////////////////////////

    private String message;         // 对接口调用结果的描述（通常在调用失败时需要描述失败的原因）

    private boolean success = true; // 接口调用是否成功

    private int code;               // 返回结果状态码（0 表示成功，其他值由应用自定义）

    protected Map<String, Object> data = new HashMap<String, Object>();

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    /////////////////////////////////////////

    /**
     * @return 原始数据
     *
     * @deprecated Use {@link #getOriginalData(Response)} instead.
     */
    @Deprecated
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * 设置原始数据
     *
     * @param data 原始数据
     *
     * @deprecated use {@link #setOriginalData(Response, java.util.Map)} instead.
     */
    @Deprecated
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public static void setOriginalData(Response response, Map<String, Object> data) {
        response.data = data;
    }

    public static Map<String, Object> getOriginalData(Response response) {
        return response.data;
    }

    /////////////////////////////////////////

    /**
     * 设置返回值属性
     *
     * @param key   属性名
     * @param value 属性值
     *
     * @return this
     */
    public Response put(String key, Object value) {
        data.put(key, value);
        return this;
    }

    /**
     * 设置返回值属性
     *
     * @param map 包含属性名和属性值的 Map 对象，如果属性值为 null，则该属性将被忽略。
     *
     * @return this
     */
    public Response putAll(Map<String, ?> map) {
        for (String key : map.keySet()) {
            Object value = map.get(key);
            if (value != null) {
                put(key, value);
            }
        }

        return this;
    }

    /////////////////////////////////////////

    /**
     * 查询返回值包含哪些属性
     *
     * @return 返回值包含的属性名
     */
    public String[] getPropertyNames() {
        String[] result = new String[data.size()];

        Iterator<String> iterator = data.keySet().iterator();
        int counter = 0;
        while (iterator.hasNext()) {
            result[counter] = iterator.next();
            counter++;
        }

        return result;
    }

    /**
     * 以特定类型获取返回值属性
     *
     * @param key  属性名
     * @param type 属性值
     * @param <T>  属性值类型
     *
     * @return 如果属性所代表的 JsonElement 对象能够被转化成 T 对象的话，则返回一个包含了属性值的 T 对象。
     */
    @SuppressWarnings({"unchecked"})
    public <T> T getObject(String key, Class<T> type) {
        Object o = data.get(key);
        if (o == null) {
            return null;
        }

        if (type.isAssignableFrom(o.getClass())) {
            return (T) o;
        } else if (o instanceof JsonElement) {
            JsonElement obj = (JsonElement) o;
            return JsonUtils.parse(type, obj);
        }

        throw new AppServerException("Unmatched Type: value of type "
                + o.getClass() + " cannot be cast to " + type);
    }

    public Map<String, String> getMap(String key) {
        Object o = data.get(key);
        if (o == null) {
            return null;
        }

        if (o instanceof JsonElement) {
            if (((JsonElement) o).isJsonObject()) {
                return toMap(((JsonElement) o).getAsJsonObject());
            } else {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("value", ((JsonElement) o).getAsString());
                return map;
            }
        } else {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("value", o.toString());
            return map;
        }
    }

    public List<Map<String, String>> getList(String key) {
        Object o = data.get(key);
        if (o == null) {
            return null;
        }

        if (o instanceof JsonElement) {
            JsonElement obj = (JsonElement) o;

            if (obj.isJsonArray()) {
                JsonArray arr = (JsonArray) obj;
                List<Map<String, String>> list = new ArrayList<Map<String, String>>();

                for (int i = 0; i < arr.size(); i++) {
                    if (arr.get(i) == null) {
                        list.add(null);
                    } else {
                        JsonObject object = arr.get(i).getAsJsonObject();
                        list.add(toMap(object));
                    }
                }

                return list;
            }
        }

        throw new AppServerException("Unmatched Type: value of type "
                + o.getClass() + " cannot be cast to Map");
    }

    private Map<String, String> toMap(JsonObject object) {
        Map<String, String> map = new HashMap<String, String>();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            map.put(entry.getKey(), entry.getValue().getAsString());
        }
        return map;
    }

    public <T> List<T> getList(String key, Class<T> type) {
        Object o = data.get(key);
        if (o == null) {
            return null;
        }

        if (o instanceof JsonElement) {
            JsonElement obj = (JsonElement) o;

            if (obj.isJsonArray()) {
                JsonArray arr = (JsonArray) obj;
                List<T> list = new ArrayList<T>();

                for (int i = 0; i < arr.size(); i++) {
                    list.add(JsonUtils.parse(type, arr.get(i)));
                }

                return list;
            }
        }

        throw new AppServerException("Unmatched Type: value of type "
                + o.getClass() + " cannot be cast to " + type);
    }

    public String getString(String key) {
        return getObject(key, String.class);
    }

    public Integer getInteger(String key) {
        return getObject(key, Integer.class);
    }

    public Long getLong(String key) {
        return getObject(key, Long.class);
    }

    public Double getDouble(String key) {
        return getObject(key, Double.class);
    }

    public Date getDate(String key) {
        return getObject(key, Date.class);
    }

    public Boolean getBoolean(String key) {
        return getObject(key, Boolean.class);
    }

    public boolean isEmpty() {
        return data == null || data.isEmpty();
    }
}
