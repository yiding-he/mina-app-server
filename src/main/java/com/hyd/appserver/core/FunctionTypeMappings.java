package com.hyd.appserver.core;

/**
 * 根据功能路径查询合适的处理 Action 类
 *
 * @author yiding.he
 */
public interface FunctionTypeMappings<T> {

    Class<? extends T> find(String functionPath);
}
