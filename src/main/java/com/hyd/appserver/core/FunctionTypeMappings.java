package com.hyd.appserver.core;

/**
 * (description)
 *
 * @author yiding.he
 */
public interface FunctionTypeMappings<T> {

    void setPackages(String[] packages);

    String[] getPackages();

    boolean addPackage(String aPackage);

    boolean removePackage(String aPackage);

    Class<T> find(String className);
}
