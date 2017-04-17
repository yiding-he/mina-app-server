package com.hyd.appserver.utils;

import java.util.List;

/**
 * (description)
 * created at 2017/4/17
 *
 * @author yidin
 */
public interface ClassHelper {

    <T> List<Class<T>> findClasses(ClassLoader classLoader, Class<T> iface, String... packageNames);
}
