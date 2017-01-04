package com.hyd.appserver.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * (description)
 *
 * @author yiding.he
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    public static boolean isEmpty(String string) {
        return string == null || string.trim().equals("");
    }

    public static String defaultIfEmpty(String string, String defaultValue) {
        return isEmpty(string) ? defaultValue : string;
    }

    public static String decodeUrl(String string) {
        if (isEmpty(string)) {
            return string;
        }

        try {
            return URLDecoder.decode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return string;
        }
    }

    public static String dateToString(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static String dateToString(long millis, String pattern) {
        return dateToString(new Date(millis), pattern);
    }

    /**
     * 将字符串转换为可以在页面上显示的形式
     *
     * @param str 要转换的字符串
     *
     * @return 可以在页面上显示的形式
     */
    public static String toHtml(String str) {
        return str.replace("<", "&lt;").replace(">", "&gt;")
                .replace("\t", "    ").replace(" ", "&nbsp;").replace("\n", "<br/>");
    }
}
