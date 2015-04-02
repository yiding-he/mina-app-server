package com.hyd.appserver.annotations;

/**
 * 用来描述多行返回值
 *
 * @author yiding.he
 */
public @interface ListProperty {

    // 返回值名称
    String name();

    // 描述
    String description() default "";

    // 列表元素的类别（可选。其值为 POJO 类的完整名称）
    Class type() default Object.class;
    
    // 列表元素的属性
    Property[] properties() default {};
}
