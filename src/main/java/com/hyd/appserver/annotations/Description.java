package com.hyd.appserver.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 纯粹的描述
 *
 * @author yiding.he
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {

    public String value();
}
