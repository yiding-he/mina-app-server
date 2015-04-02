package com.hyd.appserver.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用来描述一个接口文档
 *
 * @author yiding.he
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Function {

    // 接口描述
    String description();

    // 接口参数
    Parameter[] parameters() default {};

    // 接口返回值
    Result result() default @Result();
}
