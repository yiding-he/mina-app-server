package com.hyd.appserver.annotations;

/**
 * 用来描述接口返回值的属性
 *
 * @author yiding.he
 */
public @interface Property {

    // 属性名
    String name();
    
    // 属性类型
    Type type();

    // 描述
    String description() default "";

    // （可选）用于指定 ExposeablePojo 类型。值必须是完整的 POJO 类名
    Class pojoType() default Object.class;
}
