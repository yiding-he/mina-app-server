package com.hyd.appserver.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 读取命令行参数
 */
public class Arguments {

    private Map<String, String> arguments = new HashMap<String, String>();

    /**
     * 构造方法
     *
     * @param args 命令行参数
     */
    public Arguments(String... args) {
        String argName = "", argValue = "";

        for (String arg : args) {
            if (arg.startsWith("-")) {
                if (argName.length() > 0) {
                    arguments.put(argName, argValue.trim());
                    argValue = "";
                }
                argName = arg.substring(1);
            } else {
                argValue += " " + arg;
            }
        }

        if (argValue.length() > 0) {
            arguments.put(argName, argValue.trim());
        }
    }

    public void put(String key, String value) {
        this.arguments.put(key, value);
    }

    /**
     * 获取字符串值
     *
     * @param key 参数名
     *
     * @return 参数值
     */
    public String getString(String key) {
        String value = arguments.get(key);
        return value == null ? "" : value;
    }

    /**
     * 获取数字值
     *
     * @param key          参数名
     * @param defaultValue 缺省值
     *
     * @return 参数值
     */
    public int getInteger(String key, int defaultValue) {
        try {
            return Integer.parseInt(getString(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 判断命令行参数是否存在指定的选项。用于开关式选项
     *
     * @param key 选项
     *
     * @return 如果存在则返回 true
     */
    public boolean hasOption(String key) {
        return arguments.containsKey(key);
    }
}
