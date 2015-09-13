package com.hyd.appserver;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 接口调用请求，包含接口名和参数
 *
 * @author yiding.he
 */
public class Request implements Serializable {

    private String functionName;                                   // 接口名

    private Map<String, String> headers = new HashMap<String, String>();        // 公共参数

    private Map<String, String[]> parameters = new HashMap<String, String[]>();   // 接口特有参数

    private String timestamp;           // 时间戳，每个请求都请尽量取不同的值

    private String checkCode;           // 校验码

    public Request() {
    }

    public Request(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionName() {
        return functionName;
    }

    public Request setFunctionName(String functionName) {
        this.functionName = functionName;
        return this;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Request setHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public Request setParameter(String key, String... value) {
        parameters.put(key, value);
        return this;
    }

    public Request setParameter(String key, Number... value) {
        String[] strValue = new String[value.length];

        for (int i = 0; i < value.length; i++) {
            Number number = value[i];
            strValue[i] = number == null ? null :
                    new BigDecimal(number.toString()).toPlainString();
        }

        parameters.put(key, strValue);
        return this;
    }

    public Request setParameter(String key, Boolean... value) {
        String[] strValue = new String[value.length];

        for (int i = 0; i < value.length; i++) {
            Boolean bool = value[i];
            strValue[i] = bool == null ? null : bool.toString();
        }

        parameters.put(key, strValue);
        return this;
    }

    public Map<String, String[]> getParameters() {
        return parameters;
    }

    /////////////////////////////////////////

    /**
     * 获得参数值数组
     *
     * @param key 参数名
     *
     * @return 参数值。如果没有参数值则返回空数组
     */
    public String[] getStringValues(String key) {
        if (!parameters.containsKey(key)) {
            return new String[0];
        }

        return parameters.get(key);
    }

    public String getString(String key) {
        String[] values = getStringValues(key);
        return values.length == 0 ? null : values[0];
    }

    /**
     * 获得参数值数组
     *
     * @param key 参数名
     *
     * @return 参数值。如果没有参数值则返回空数组
     */
    public Double[] getDoubleValues(String key) {
        String[] values = getStringValues(key);

        return new ArrayConverter<Double>(Double.class) {

            @Override
            Double convert(String value) {
                return Double.valueOf(value);
            }
        }.convert(values);
    }

    public Double getDouble(String key) {
        Double[] values = getDoubleValues(key);
        return values.length == 0 ? null : values[0];
    }

    /**
     * 获得参数值数组
     *
     * @param key 参数名
     *
     * @return 参数值。如果没有参数值则返回空数组
     */
    public Integer[] getIntegerValues(String key) {
        String[] values = getStringValues(key);

        return new ArrayConverter<Integer>(Integer.class) {

            @Override
            Integer convert(String value) {
                return Integer.valueOf(value);
            }
        }.convert(values);
    }

    public Integer getInteger(String key) {
        Integer[] values = getIntegerValues(key);
        return values.length == 0 ? null : values[0];
    }

    /**
     * 获得参数值数组
     *
     * @param key 参数名
     *
     * @return 参数值。如果没有参数值则返回空数组
     */
    public Long[] getLongValues(String key) {
        String[] values = getStringValues(key);

        return new ArrayConverter<Long>(Long.class) {

            @Override
            Long convert(String value) {
                return Long.valueOf(value);
            }
        }.convert(values);
    }

    public Long getLong(String key) {
        Long[] values = getLongValues(key);
        return values.length == 0 ? null : values[0];
    }

    public Boolean[] getBooleanValues(String key) {
        String[] values = getStringValues(key);

        return new ArrayConverter<Boolean>(Boolean.class) {

            @Override
            Boolean convert(String value) {
                return value != null && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes")) ?
                        Boolean.TRUE : Boolean.FALSE;
            }
        }.convert(values);
    }

    public Boolean getBoolean(String key) {
        Boolean[] values = getBooleanValues(key);
        return values.length == 0 ? null : values[0];
    }

    /////////////////////////////////////////

    /**
     * 转化数组对象的工具，不会返回 null
     *
     * @param <T>
     */
    @SuppressWarnings({"unchecked"})
    private abstract class ArrayConverter<T> {

        private Class<T> type;

        protected ArrayConverter(Class<T> type) {
            this.type = type;
        }

        T[] convert(String[] values) {

            T[] result;

            if (values != null) {
                result = (T[]) Array.newInstance(type, values.length);
                for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
                    String value = values[i];
                    try {
                        result[i] = value == null ? null : convert(value);
                    } catch (Exception e) {
                        throw new AppServerException("提取 " + type + " 类型参数值失败", e);
                    }
                }
            } else {
                result = (T[]) Array.newInstance(type, 0);
            }

            return result;
        }

        abstract T convert(String value);
    }
}
