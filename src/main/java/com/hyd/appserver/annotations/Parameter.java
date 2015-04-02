package com.hyd.appserver.annotations;

/**
 * 用来描述一个接口参数
 *
 * @author yiding.he
 */
public @interface Parameter {

    // 参数名
    String name();
    
    // 参数类型
    Type type();

    // 参数描述
    String description();
    
    // 是否必须。如果 required 为 true，则 defaultValue 不起作用。
    boolean required() default true;

    // 正则表达式模板。如果用户传的值不为空，则必须满足该正则表达式。缺省值不受此限制。
    String pattern() default "";

    // 缺省值
    String defaultValue() default "";
}
