package com.hyd.appserver.http;

import com.hyd.appserver.annotations.AnnotationUtils;
import com.hyd.appserver.annotations.Description;
import com.hyd.appserver.annotations.ExposeablePojo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * (description)
 *
 * @author yiding.he
 */
public class PojoInfoPage {

    static final Logger log = LogManager.getLogger(PojoInfoPage.class);

    private static final String page_pattern = "<html><head>" +
            "<title>POJO 结构</title><link rel=\"stylesheet\" href=\"../default.css\"/>" +
            "</head><body>" +
            "  <h1>%s 类结构（需要的话请复制下面的代码）：</h1>" +
            "<pre>%s</pre>" +
            "</body></html>";

    public static final String class_pattern = "public class %s {\n\n%s}";

    public static final String comment_pattern = "" +
            "    /**\n" +
            "     * %s\n" +
            "     */\n";

    public static final String property_pattern = "    private %s %s;\n\n";

    private Class type;

    public PojoInfoPage(Class type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format(page_pattern, type.getSimpleName(), getPojoCode());
    }

    private String getPojoCode() {
        String properties = getPropertiesCode();
        return String.format(class_pattern, type.getSimpleName(), properties);
    }

    private String getPropertiesCode() {
        StringBuilder sb = new StringBuilder();

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(type);
            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor descriptor : descriptors) {
                if (ignore(descriptor)) {
                    continue;
                }

                String description = getDescription(descriptor);
                if (!description.equals("")) {
                    sb.append(String.format(comment_pattern, description));
                }

                sb.append(String.format(property_pattern,
                        getPropertyTypeName(descriptor), descriptor.getName()));
            }
        } catch (IntrospectionException e) {
            log.error("", e);
            sb.append("    // Error parsing properties: ").append(e).append("\n\n");
        }

        sb.append("    // getters and setters...\n\n");

        return sb.toString();
    }

    private String getPropertyTypeName(PropertyDescriptor descriptor) {
        Class<?> propertyType = descriptor.getPropertyType();
        String simpleName = propertyType.getSimpleName();

        // 获取成员
        Field field;
        try {
            field = type.getDeclaredField(descriptor.getName());
        } catch (NoSuchFieldException e) {
            return simpleName;
        }

        // 获取泛型类型
        Type genericType = field.getGenericType();

        if (genericType == null || !(genericType instanceof ParameterizedType)) {
            return simpleName;
        }

        return extractTypeName(genericType);
    }

    // 提取类型的名字。如果类型包含了泛型，则返回泛型的类名，如 Map<String, List<Map<Integer, Object>>>
    private String extractTypeName(Type type) {

        String result = ((Class) ((ParameterizedType) type).getRawType()).getSimpleName();
        Type[] argTypes = ((ParameterizedType) type).getActualTypeArguments();

        if (argTypes.length > 0) {
            result += "&lt;";

            for (Type argType : argTypes) {

                if (argType instanceof Class) {
                    Class<?> genericClass = (Class<?>) argType;

                    if (genericClass.isAnnotationPresent(ExposeablePojo.class)) {
                        result += "<a href=\"" + genericClass.getName() + "\">" + genericClass.getSimpleName() + "</a>";
                    } else {
                        result += genericClass.getSimpleName();
                    }

                } else if (argType instanceof ParameterizedType) {
                    result += extractTypeName(argType);
                } else {
                    log.error("Unknown Type: " + argType);
                }

                result += ",";
            }

            if (result.endsWith(",")) {
                result = result.substring(0, result.length() - 1);
            }

            result += "&gt;";
        }

        return result;
    }

    private String getDescription(PropertyDescriptor descriptor) {
        Field field;
        try {
            field = findField(type, descriptor.getName());
        } catch (NoSuchFieldException e) {
            return "(No such field)";
        }

        Description description = AnnotationUtils.getDescription(field);

        if (description != null) {
            return description.value();
        } else {
            return "";
        }
    }

    // 判断指定的属性是否应该忽略
    private boolean ignore(PropertyDescriptor descriptor) {

        // "class" 属性应该忽略
        if (descriptor.getName().equals("class")) {
            return true;
        }

        // 标记为 transient 的属性应该忽略，因为它的值不会进行序列化
        try {
            Field field = findField(type, descriptor.getName());
            if (Modifier.isTransient(field.getModifiers())) {
                return true;
            }
        } catch (NoSuchFieldException e) {
            return true;
        }

        return false;
    }

    private Field findField(Class type, String fieldName) throws NoSuchFieldException {
        try {
            return type.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (type.getSuperclass() == Object.class) {
                throw e;
            } else {
                return findField(type.getSuperclass(), fieldName);
            }
        }
    }
}
