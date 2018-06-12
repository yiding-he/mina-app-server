package com.hyd.appserver.annotations;

import com.hyd.appserver.Action;
import com.hyd.appserver.utils.ArrayMerger;
import com.hyd.appserver.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于获取 Action 类的 Function 注解。注意：真正的 Action 注解是 Action
 * 类自身的注解以及其父类的注解的合并结果，所以要获取 Action 的注解，一定要用这个类的方法，而不要直接获取。
 *
 * @author yiding.he
 */
@SuppressWarnings("unchecked")
public class AnnotationUtils {

    // 缓存 Action 类的 Function 注解
    public static final Map<Class, Function> FUNCTION_MAP = new HashMap<Class, Function>();

    /**
     * 获取 Action 类型的 Function 注解
     *
     * @param type Action 类型
     *
     * @return 该类型的 Function 注解对象
     */
    public static Function getFunction(Class<? extends Action> type) {
        if (type == null) {
            return null;
        }

        if (FUNCTION_MAP.containsKey(type)) {
            return FUNCTION_MAP.get(type);
        }

        synchronized (FUNCTION_MAP) {
            if (FUNCTION_MAP.containsKey(type)) {
                return FUNCTION_MAP.get(type);
            }

            List<Class<?>> types = listTypes(type);
            Function function = null;

            for (Class<?> aType : types) {
                if (aType.isAnnotationPresent(Function.class)) {
                    function = function == null ? aType.getAnnotation(Function.class) :
                            mergeFunction(aType.getAnnotation(Function.class), function);
                }
            }

            FUNCTION_MAP.put(type, function);
            return function;
        }

    }

    private static List<Class<?>> listTypes(Class<?> type) {
        ArrayList<Class<?>> result = new ArrayList<Class<?>>();
        List<Class<?>> classes = listClassHierachy(type);

        for (Class<?> clazz : classes) {

            if (!result.contains(clazz)) {
                result.add(clazz);
            }

            Class<?>[] interfaces = clazz.getInterfaces();

            for (Class<?> anInterface : interfaces) {
                if (!result.contains(anInterface)) {
                    result.add(anInterface);
                }

                for (Class<?> _anInterface : listTypes(anInterface)) {
                    if (!result.contains(_anInterface)) {
                        result.add(_anInterface);
                    }
                }
            }
        }

        return result;
    }

    private static List<Class<?>> listClassHierachy(Class<?> type) {
        List<Class<?>> result = new ArrayList<Class<?>>();
        Class<?> _type = type;

        while (_type != null) {
            result.add(_type);
            _type = _type.getSuperclass();
        }

        return result;
    }

    /**
     * 将父类的 Function 和 子类的 Function 合并
     *
     * @param superFunction 父类 Function
     * @param subFunction   子类 Function
     *
     * @return 合并后的 Function
     */
    private static Function mergeFunction(final Function superFunction, final Function subFunction) {

        if (superFunction == null) {
            return subFunction;
        } else if (subFunction == null) {
            return superFunction;
        }

        // 合并 description
        final String description = StringUtils.defaultIfEmpty(subFunction.description(), superFunction.description());

        // 合并 parameters
        List<Parameter> parameterList = new ArrayMerger<Parameter>() {

            @Override
            protected boolean isEqual(Parameter element1, Parameter element2) {
                return element1.name().equals(element2.name());
            }
        }.merge(superFunction.parameters(), subFunction.parameters());

        final Parameter[] parameters = parameterList.toArray(new Parameter[parameterList.size()]);

        // 返回结果
        return new Function() {

            @Override
            public String path() {
                String superPath = getFunctionPath(superFunction);
                String subPath = getFunctionPath(subFunction);
                return (StringUtils.isBlank(superPath)? "": (superPath + "/")) + subPath;
            }

            @Override
            public String description() {
                return description;
            }

            @Override
            public Parameter[] parameters() {
                return parameters;
            }

            @Override
            public Result result() {
                return mergeResult(superFunction.result(), subFunction.result());
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return subFunction.annotationType();
            }
        };
    }

    private static String getFunctionPath(Function f) {
        if (f == null) {
            return "";
        }

        return StringUtils.removeEnd(StringUtils.removeStart(f.path(), "/"), "/");
    }

    private static Result mergeResult(Result superResult, final Result subResult) {

        // 合并 properties
        final List<Property> propertyList = new ArrayMerger<Property>() {

            @Override
            protected boolean isEqual(Property element1, Property element2) {
                return element1.name().equals(element2.name());
            }
        }.merge(superResult.properties(), subResult.properties());

        // 合并 listProperties
        final List<ListProperty> listPropertyList = new ArrayMerger<ListProperty>() {

            @Override
            protected boolean isEqual(ListProperty element1, ListProperty element2) {
                return element1.name().equals(element2.name());
            }
        }.merge(superResult.listProperties(), subResult.listProperties());

        //返回结果
        return new Result() {

            @Override
            public String success() {
                return subResult.success();
            }

            @Override
            public Property[] properties() {
                return propertyList.toArray(new Property[propertyList.size()]);
            }

            @Override
            public ListProperty[] listProperties() {
                return listPropertyList.toArray(new ListProperty[listPropertyList.size()]);
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return subResult.annotationType();
            }
        };
    }

    public static Description getDescription(Field field) {
        if (field.isAnnotationPresent(Description.class)) {
            return field.getAnnotation(Description.class);
        } else {
            return null;
        }
    }
}
