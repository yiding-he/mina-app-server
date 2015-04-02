package com.hyd.appserver.utils.dynamicobj;

import com.hyd.appserver.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 动态对象，最大程度的对属性和类型达到兼容
 *
 * @author 贺一丁
 */
@SuppressWarnings("unchecked")
public class DynamicObject extends HashMap<String, Object> {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicObject.class);

    public static final SimpleDateFormat[] FORMATS = {
            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"),
            new SimpleDateFormat("yyyy/MM/dd HH:mm"),
            new SimpleDateFormat("yyyy/MM/dd"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
            new SimpleDateFormat("yyyy-MM-dd HH:mm"),
            new SimpleDateFormat("yyyy-MM-dd"),
            new SimpleDateFormat("yyyyMMddHHmmssSSS"),
            new SimpleDateFormat("yyyyMMddHHmmss"),
            new SimpleDateFormat("yyyyMMddHHmm"),
            new SimpleDateFormat("yyyyMMdd")
    };

    /**
     * 获取 int 类型的值
     *
     * @param key          属性名
     * @param defaultValue 缺省值
     *
     * @return 属性值。如果无法获取属性值，则返回缺省值
     */
    public int getInteger(String key, int defaultValue) {
        Object value = get(key);

        if (value == null) {
            return defaultValue;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof Date) {
            return Long.valueOf(((Date) value).getTime()).intValue();
        } else {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
                LOG.warn("", e);
                return defaultValue;
            }
        }
    }

    /**
     * 获取 long 类型的属性值
     *
     * @param key          属性名
     * @param defaultValue 缺省值
     *
     * @return 属性值。如果无法获取属性值，则返回缺省值
     */
    public long getLong(String key, long defaultValue) {
        Object value = get(key);

        if (value == null) {
            return defaultValue;
        } else if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value instanceof Date) {
            return ((Date) value).getTime();
        } else {
            try {
                return Long.parseLong(value.toString());
            } catch (NumberFormatException e) {
                LOG.warn("", e);
                return defaultValue;
            }
        }
    }

    /**
     * 获取 double 类型的属性值
     *
     * @param key          属性名
     * @param defaultValue 缺省值
     *
     * @return 属性值。如果无法获取属性值，则返回缺省值
     */
    public double getDouble(String key, double defaultValue) {
        Object value = get(key);

        if (value == null) {
            return defaultValue;
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof Date) {
            return (double) ((Date) value).getTime();
        } else {
            try {
                return Double.parseDouble(value.toString());
            } catch (NumberFormatException e) {
                LOG.warn("", e);
                return defaultValue;
            }
        }
    }

    /**
     * 获取 String 类型的属性值
     *
     * @param key          属性名
     * @param defaultValue 缺省值
     *
     * @return 属性值。如果无法获取属性值或属性值为 null，则返回缺省值
     */
    public String getString(String key, String defaultValue) {
        Object value = get(key);

        if (value == null) {
            return defaultValue;
        } else {
            return value.toString();
        }
    }

    /**
     * 获取属性值并转化为指定类型
     *
     * @param key  属性名
     * @param type 类型
     *
     * @return 属性值
     */
    public <T> T get(String key, Class<T> type) {
        Object value = get(key);
        return convertObject(key, type, value);
    }

    public DynamicObject getObject(String key) {
        Object value = get(key);

        if (value == null) {
            return null;
        } else if (value instanceof DynamicObject) {
            return (DynamicObject) value;
        } else {
            return new DynamicObject().append("value", value);
        }
    }

    public List<DynamicObject> getList(String key) {
        return (List<DynamicObject>) get(key);
    }

    public DynamicObject append(String key, Object value) {
        put(key, value);
        return this;
    }

    /**
     * 尝试将属性值转换为指定类型
     *
     * @param key   属性名
     * @param type  指定类型
     * @param value 属性值
     *
     * @return 转换结果
     */
    private <T> T convertObject(String key, Class<T> type, Object value) {
        if (value == null) {
            return null;
        }

        if (type.isAssignableFrom(value.getClass())) {
            return (T) value;
        } else if (isPrimitive(type)) {
            return convertPrimitive(type, value);
        } else if (Number.class.isAssignableFrom(type)) {
            return tryToCreateInstanceWithString(key, type, value);
        } else if (Date.class == type) {
            if (value instanceof Date) {
                return (T) value;
            } else {
                return (T) convertDate(value.toString());
            }
        } else if (value instanceof DynamicObject) {
            return ((DynamicObject) value).convert(type);
        } else {
            return tryToCreateInstanceWithString(key, type, value);
        }
    }

    // 字符串转化为日期
    private Date convertDate(String s) {

        if (StringUtils.isEmpty(s)) {
            return null;
        }

        if (s.matches("\\d+")) {
            return new Date(Long.parseLong(s));
        }

        for (SimpleDateFormat format : FORMATS) {
            try {
                return format.parse(s);
            } catch (Exception e) {
                // ignore date parsing error
            }
        }

        return null;
    }

    private <T> boolean isPrimitive(Class<T> type) {
        return type.isPrimitive() || type == Boolean.class || type == Character.class
                || type == Byte.class || type == Short.class || type == Integer.class || type == Long.class
                || type == Float.class || type == Double.class || type == Void.class;
    }

    private <T> T convertPrimitive(Class<T> type, Object value) {
        boolean isEmpty = false;
        String stringVal = value.toString();

        if (StringUtils.isEmpty(stringVal) || stringVal.trim().equals("null")) {
            isEmpty = true;
        }

        try {
            if (type == Boolean.class || type == Boolean.TYPE) {
                return isEmpty? (T)Boolean.FALSE: (T) Boolean.valueOf(stringVal);
            } else if (type == Character.class || type == Character.TYPE) {
                return isEmpty? (T)Character.valueOf('\0'): (T) new Character(stringVal.charAt(0));
            } else if (type == Byte.class || type == Byte.TYPE) {
                return isEmpty? (T)Byte.valueOf((byte)0): (T) new Byte(new BigDecimal(stringVal).byteValue());
            } else if (type == Short.class || type == Short.TYPE) {
                return isEmpty? (T)Short.valueOf((short)0): (T) new Short(new BigDecimal(stringVal).shortValue());
            } else if (type == Integer.class || type == Integer.TYPE) {
                return isEmpty? (T)Integer.valueOf(0): (T) new Integer(new BigDecimal(stringVal).intValue());
            } else if (type == Long.class || type == Long.TYPE) {
                return isEmpty? (T)Long.valueOf(0): (T) new Long(new BigDecimal(stringVal).longValue());
            } else if (type == Float.class || type == Float.TYPE) {
                return isEmpty? (T)Float.valueOf(0): (T) new Float(new BigDecimal(stringVal).floatValue());
            } else if (type == Double.class || type == Double.TYPE) {
                return isEmpty? (T)Double.valueOf(0): (T) new Double(new BigDecimal(stringVal).doubleValue());
            } else if (type == Void.class || type == Void.TYPE) {
                return null;
            }
        } catch (Exception e) {
            LOG.error("Failed converting " + value + " to " + type + ": " + e);
        }

        return null;
    }

    // 尝试通过带字符串参数的构造方法创建对象
    private <T> T tryToCreateInstanceWithString(String key, Class<T> type, Object value) {
        try {
            return type.getDeclaredConstructor(new Class<?>[]{String.class}).newInstance(value.toString());
        } catch (Exception e) {
            LOG.info("Property \"" + key + "\" cannot be converted to "
                    + type + ": String constructor cannot be executed");
            return null;
        }
    }

    // 尝试将 DynamicObject 对象转换为指定类型
    public <T> T convert(Class<T> type) {
        if (isPrimitive(type)) {
            throw new DynamicObjectException("Cannot cast " + toString() + " to " + type);
        } else if (type == String.class) {
            return (T) toString();
        } else {

            if (type.isInterface()) {
                if (type == Map.class || type == HashMap.class) {
                    return (T) this;
                }

                LOG.info("DynamicObject \"" + this + "\" cannot be converted to " + type);
                return null;
            }

            try {
                T value = type.newInstance();

                BeanInfo beanInfo = Introspector.getBeanInfo(type);
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

                for (PropertyDescriptor descriptor : propertyDescriptors) {
                    String key = descriptor.getName();

                    if (this.get(key) == null) {
                        continue;
                    }

                    Class<?> propertyType = descriptor.getPropertyType();

                    try {

                        if (!this.containsKey(key)) {
                            continue;
                        }

                        // 属性值是 DynamicObject，
                        if (this.get(key) instanceof DynamicObject) {
                            DynamicObject propertyValue = (DynamicObject) this.get(key);
                            descriptor.getWriteMethod().invoke(value, propertyValue.convert(propertyType));
                            continue;
                        }

                        // 属性值为数组
                        if (propertyType.isArray()) {
                            convertPropertyToArray(value, descriptor, key, propertyType);
                            continue;
                        }

                        // 属性值为 List
                        if (List.class.isAssignableFrom(propertyType)) {
                            convertPropertyToList(type, value, descriptor, key);
                            continue;
                        }

                        // 属性可以直接赋值
                        if (propertyType.isAssignableFrom(this.get(key).getClass())) {
                            descriptor.getWriteMethod().invoke(value, this.get(key));
                            continue;
                        }

                        // 属性类型为基本型别
                        if (isPrimitive(propertyType)) {
                            Object primValue = convertPrimitive(propertyType, this.get(key));
                            if (primValue != null && descriptor.getWriteMethod() != null) {
                                descriptor.getWriteMethod().invoke(value, primValue);
                            }
                            continue;
                        }

                        // 其他情况
                        Object propertyValue = convertObject(key, propertyType, this.get(key));
                        if (propertyValue != null && descriptor.getWriteMethod() != null) {
                            descriptor.getWriteMethod().invoke(value, propertyValue);
                        }

                    } catch (Exception e) {
                        LOG.info("Property \"" + key + "\" cannot be converted to " +
                                propertyType + " (" + this.get(key) + ")", e);
                    }
                }

                return value;
            } catch (Exception e) {
                LOG.info("Cannot cast " + toString() + " to " + type, e);
                return null;
            }
        }
    }

    private <T> void convertPropertyToList(Class<T> type,
                                           T value,
                                           PropertyDescriptor descriptor,
                                           String key
    ) throws IllegalAccessException, InvocationTargetException {

        Class<?> genericClass = null;

        try {
            Field field = getField(type, key);

            if (field != null) {
                Type genericType = field.getGenericType();

                if (genericType != null && genericType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericType;
                    genericClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                }
            }
        } catch (Exception e) {
            LOG.info("Property \"" + key + "\" cannot be cast to generic list.", e);
        }

        descriptor.getWriteMethod().invoke(value, getList(key, genericClass));
    }

    private <T> void convertPropertyToArray(T value,
                                            PropertyDescriptor descriptor,
                                            String key,
                                            Class<?> propertyType
    ) throws IllegalAccessException, InvocationTargetException {

        if (!(this.get(key) instanceof List)) {
            LOG.info("Property \"" + key + "\" cannot be cast to array");
            return;
        }

        List list = (List) this.get(key);
        Class<?> componentType = propertyType.getComponentType();
        Object arr = Array.newInstance(componentType, list.size());

        for (int i = 0, listSize = list.size(); i < listSize; i++) {
            Object o = list.get(i);
            Array.set(arr, i, convertObject(key, componentType, o));
        }

        descriptor.getWriteMethod().invoke(value, arr);
    }

    private Field getField(Class<?> type, String fieldName) {
        Class<?> _type = type;

        while (_type != Object.class) {
            for (Field field : _type.getDeclaredFields()) {
                if (field.getName().equals(fieldName)) {
                    return field;
                }
            }

            _type = _type.getSuperclass();
        }

        return null;
    }

    public <T> List<T> getList(String key, Class<T> type) {
        Object value = get(key);

        if (value == null) {
            return null;
        }

        if (value instanceof List) {
            List list = (List) value;

            // 没有指定要转换的类型
            if (type == null) {
                return (List<T>) list;
            }

            // 列表为空
            if (list.isEmpty() || list.get(0) == null) {
                return list;
            }

            Class elementType = list.get(0).getClass();

            // 元素类型已经匹配，无需转换
            if (type.isAssignableFrom(elementType)) {
                List<T> listOfType = new ArrayList<T>();
                for (Object o : list) {
                    listOfType.add((T) o);
                }
                return listOfType;
            }

            // 列表元素需要转换
            if (elementType == DynamicObject.class) {
                List<T> listOfType = new ArrayList<T>();
                for (Object o : list) {
                    if (o != null) {
                        listOfType.add(((DynamicObject) o).convert(type));
                    }
                }
                return listOfType;
            }
        }

        LOG.warn("Unable to convert property \"" + key + "\" to list");
        return Collections.emptyList();
    }
}
