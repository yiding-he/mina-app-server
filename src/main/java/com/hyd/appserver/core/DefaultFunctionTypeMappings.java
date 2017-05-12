package com.hyd.appserver.core;

import com.hyd.appserver.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通过接口名称找到相应的类
 *
 * @author yiding.he
 */
@SuppressWarnings({"unchecked"})
public class DefaultFunctionTypeMappings<T> implements FunctionTypeMappings<T> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultFunctionTypeMappings.class);

    private List<String> packages = new ArrayList<String>();

    private Map<String, Class> mappings = new HashMap<String, Class>();

    @Override
    public void setPackages(String[] packages) {
        for (String aPackage : packages) {
            addPackage(aPackage);
        }
    }

    @Override
    public String[] getPackages() {
        return packages.toArray(new String[packages.size()]);
    }

    @Override
    public boolean removePackage(String aPackage) {
        if (!packages.contains(aPackage)) {
            return false;
        }

        packages.remove(aPackage);
        return true;
    }

    @Override
    public boolean addPackage(String aPackage) {
        if (packages.contains(aPackage)) {
            return false;
        }

        packages.add(aPackage);
        return true;
    }

    @Override
    public Class<T> find(String className) {
        if (StringUtils.isEmpty(className)) {
            return null;
        }

        if (mappings.containsKey(className)) {
            return mappings.get(className);
        }

        if (packages == null || packages.size() == 0) {
            return null;
        }

        for (String p : packages) {
            String full = p + "." + className;

            try {
                Class result = Class.forName(full);
                mappings.put(className, result);
                return result;
            } catch (Exception e) {
                LOG.info("class not found: " + full);
            }
        }

        return null;
    }
}
