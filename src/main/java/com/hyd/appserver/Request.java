package com.hyd.appserver;

import com.hyd.appserver.core.ClientInfo;
import com.hyd.appserver.utils.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 接口调用请求，包含接口名和参数
 *
 * @author yiding.he
 */
@SuppressWarnings("UnusedDeclaration")
public class Request implements Serializable {

    private String functionPath;        // 接口名

    private String functionName;        // 接口名（旧）

    private Map<String, String[]> parameters = new HashMap<String, String[]>();     // 接口特有参数

    private String timestamp;           // 时间戳，仅当发送的时候赋值

    private String checkCode;           // 校验码

    private ClientInfo clientInfo;
    
    private transient String originalString;    // 原始请求字符串

    /**
     * 复制 Request 对象。paramter 的值会复制，但里面的元素不会
     *
     * @param request 要复制的 Request 对象
     *
     * @return 复制的 Request 对象
     */
    public static Request clone(Request request) {
        Request clone = new Request();
        clone.functionPath = StringUtils.defaultIfEmpty(request.functionPath, request.functionName);
        clone.functionName = StringUtils.defaultIfEmpty(request.functionPath, request.functionName);
        clone.parameters = new HashMap<>(request.parameters);
        clone.clientInfo = request.clientInfo;
        clone.timestamp = request.timestamp;
        clone.checkCode = request.checkCode;
        clone.originalString = request.originalString;
        return clone;
    }

    /////////////////////////////////////////

    public Request() {
    }

    public Request(String functionPath) {
        this.functionPath = functionPath;
        this.functionName = functionPath;
    }

    public String getOriginalString() {
        return originalString;
    }

    public void setOriginalString(String originalString) {
        this.originalString = originalString;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }

    public String getFunctionPath() {
        return functionPath;
    }

    public Request setFunctionPath(String functionPath) {
        this.functionPath = functionPath;
        this.functionName = functionPath;
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

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
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

    public Request setParameter(String key, Date... value) {
        String[] strValue = new String[value.length];

        for (int i = 0; i < value.length; i++) {
            Date date = value[i];
            strValue[i] = date == null ? null : String.valueOf(date.getTime());
        }

        parameters.put(key, strValue);
        return this;
    }

    public Map<String, String[]> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "Request{" +
                "functionPath='" + functionPath + '\'' +
                ", parameters=" + parameterToString() +
                ", timestamp='" + timestamp + '\'' +
                ", checkCode='" + checkCode + '\'' +
                ", clientInfo=" + clientInfo +
                ", originalString='" + originalString + '\'' +
                '}';
    }

    private String parameterToString() {
        StringBuilder sb = new StringBuilder('{');

        for (String key : parameters.keySet()) {
            String[] values = parameters.get(key);
            sb.append(key).append('=').append(Arrays.toString(values)).append(',');
        }

        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.append('}').toString();
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

        String[] values = parameters.get(key);
        if (values.length == 1 && StringUtils.isEmpty(values[0])) {
            return new String[0];
        }
        return values;
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
                return StringUtils.isEmpty(value) ? null : Double.valueOf(value);
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
                return StringUtils.isEmpty(value)? null: Integer.valueOf(value);
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
                return StringUtils.isEmpty(value) ? null : Long.valueOf(value);
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

    public Date[] getDateValues(String key) {
        String[] values = getStringValues(key);

        return new ArrayConverter<Date>(Date.class) {

            @Override
            Date convert(String value) {
                if (value == null) {
                    return null;
                }

                try {
                    if (value.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                        return new SimpleDateFormat("yyyy-MM-dd").parse(value);
                    } else if (value.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$")) {
                        return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(value);
                    } else if (value.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")) {
                        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value);
                    }
                } catch (ParseException e) {
                    throw new IllegalArgumentException("无法解析日期字符串'" + value + "'");
                }

                return new Date(Long.parseLong(value));
            }
        }.convert(values);
    }

    public Date getDate(String key) {
        Date[] values = getDateValues(key);
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
