package com.hyd.appserver.utils;

/**
 * todo: description
 *
 * @author yiding.he
 */
public class ArgumentsTest {

    public static void main(String[] args) {
        Arguments arguments = new Arguments("-p", "7765", "haha", "-disabled", "-path", "/path/to/dest", "extra");
        System.out.println(arguments.getString("p"));
        System.out.println(arguments.getString("disabled"));
        System.out.println(arguments.getString("path"));
        System.out.println(arguments.hasOption("ddddd"));
    }
}
