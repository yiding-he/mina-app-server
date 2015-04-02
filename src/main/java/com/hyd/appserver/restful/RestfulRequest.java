package com.hyd.appserver.restful;

import com.hyd.appserver.Request;

/**
 * (description)
 * created at 2014/8/9
 *
 * @author Yiding
 * @deprecated 尚未实现
 */
public class RestfulRequest extends Request {

    public RestfulRequest(String functionName, String method, String entity) {
        this.setFunctionName(functionName);
        this.setParameter("method", method);
        this.setParameter("entity", entity);
    }


}
