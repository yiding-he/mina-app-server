package com.hyd.appserver;

import com.hyd.appserver.annotations.AnnotationUtils;
import com.hyd.appserver.annotations.Function;

/**
 * 每个业务接口都要实现 Action。
 *
 * @author yiding.he
 */
public interface Action<Q extends Request, R extends Response> {

    R execute(Q request) throws Exception;

    default String getFullFunctionPath() {
        Function function = AnnotationUtils.getFunction(this.getClass());
        return function == null ? null : function.path();
    }
}
