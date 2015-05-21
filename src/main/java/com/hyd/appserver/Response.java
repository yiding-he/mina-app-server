package com.hyd.appserver;

import com.hyd.appserver.utils.JsonUtils;
import com.hyd.appserver.utils.ThrowableUtils;
import com.hyd.appserver.utils.dynamicobj.DynamicObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 接口返回值
 *
 * @author yiding.he
 */
@SuppressWarnings("unchecked")
public class Response implements Serializable {

    public static final SimpleDateFormat FORMAT = new SimpleDateFormat(JsonUtils.DATE_FORMAT);

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
     * @param e 异常信息
     *
     * @return 表示“调用失败”的 Response 对象
     */
    public static Response fail(Throwable e) {
        Response response = fail(e.getMessage());
        response.setStackTrace(ThrowableUtils.toString(e));
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
     * 创建一个表示“调用失败”的 Response 对象
     *
     * @param message 调用结果描述
     * @param type    业务处理 Action 类
     *
     * @return 表示“调用失败”的 Response 对象
     */
    public static Response fail(String message, Class<Action> type) {
        Response response = new Response();
        response.setSuccess(false);
        response.setCode(-1);
        response.setMessage(message);
        response.actionType = type;
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

    private String stackTrace;      // 服务器异常堆栈，调试用

    private String originalJson;    // 原始 JSON 字符串

    protected DynamicObject data = new DynamicObject();

    public String getOriginalJson() {
        return originalJson;
    }

    public void setOriginalJson(String originalJson) {
        this.originalJson = originalJson;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

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

    public void setData(DynamicObject data) {
        this.data = data;
    }

    public DynamicObject getData() {
        return data;
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
        Object value = data.get(key);
        if (value == null) {
            return null;
        }

        if (value instanceof DynamicObject) {
            return ((DynamicObject) value).convert(type);
        } else if (type == Date.class) {

            if (value instanceof Number || value.toString().matches("\\d+")) {
                return (T)new Date(Long.valueOf(value.toString()));
            }

            try {
                return (T) FORMAT.parse(value.toString());
            } catch (ParseException e) {
                throw new AppServerException("Property \"" + key + "\" cannot be converted to " + type, e);
            }
        } else if (type.isAssignableFrom(value.getClass())) {
            return (T) value;
        } else if (Number.class.isAssignableFrom(type)) {
            try {
                String strValue = value.toString();

                // 如果目标类型为整数，则去掉小数部分
                strValue = removeDecimalsIfNecessary(type, strValue);

                return type.getDeclaredConstructor(new Class<?>[]{String.class}).newInstance(strValue);
            } catch (Exception e) {
                throw new AppServerException("Property \"" + key + "\" cannot be converted to " + type, e);
            }
        }

        throw new AppServerException("Property \"" + key + "\" cannot be converted to " + type);
    }

    // 如果 type 为整数，则去掉 strValue 的小数部分
    private <T> String removeDecimalsIfNecessary(Class<T> type, String strValue) {
        int dotIndex = strValue.indexOf(".");
        if ((type == Integer.class || type == Long.class) && dotIndex >= 0) {
            strValue = strValue.substring(0, dotIndex);
        }
        return strValue;
    }

    /**
     * 获取 DynamicObject 类型的属性值。如果属性值不是 DynamicObject，
     * 则包装为一个 DynamicObject，真实的值是其 value 属性。
     *
     * @param key 属性名
     *
     * @return 属性值
     */
    public DynamicObject getDynamicObject(String key) {
        return data.getObject(key);
    }

    public List<DynamicObject> getList(String key) {
        Object value = data.get(key);
        if (value == null) {
            return null;
        }

        if (value instanceof List) {
            return (List<DynamicObject>) value;
        }

        throw new AppServerException("Property \"" + key + "\" is not a list");
    }

    public <T> List<T> getList(String key, Class<T> type) {
        Object value = data.get(key);

        if (value == null) {
            return null;
        }

        if (value instanceof List) {
            return data.getList(key, type);
        }

        throw new AppServerException("Property \"" + key + "\" is not a list");
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public String getString(String key, String defaultValue) {
        Object value = this.data.get(key);
        return value == null ? defaultValue : value.toString();
    }

    public Integer getInteger(String key) {
        return getObject(key, Integer.class);
    }

    public int getInteger(String key, int defaultValue) {
        Integer value = getInteger(key);
        return value == null ? defaultValue : value;
    }

    public Long getLong(String key) {
        return getObject(key, Long.class);
    }

    public long getLong(String key, long defaultValue) {
        Long value = getLong(key);
        return value == null ? defaultValue : value;
    }

    public Double getDouble(String key) {
        return getObject(key, Double.class);
    }

    public double getDouble(String key, double defaultValue) {
        Double value = getDouble(key);
        return value == null ? defaultValue : value;
    }

    public Date getDate(String key) {
        return getObject(key, Date.class);
    }

    public Date getDate(String key, Date defaultValue) {
        Date value = getDate(key);
        return value == null ? defaultValue : value;
    }

    public Boolean getBoolean(String key) {
        return getObject(key, Boolean.class);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        Boolean value = getBoolean(key);
        return value == null ? defaultValue : value;
    }

    /////////////////////////////////////////

    public transient Class<Action> actionType;
}
