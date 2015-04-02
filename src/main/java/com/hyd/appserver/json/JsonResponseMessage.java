package com.hyd.appserver.json;

import com.hyd.appserver.Response;

/**
 * 包装 Response 对象
 *
 * @author yiding.he
 */
public class JsonResponseMessage extends Response {

    public JsonResponseMessage(Response response) {
        this.setSuccess(response.isSuccess());
        this.setCode(response.getCode());
        this.setMessage(response.getMessage());
        this.data = response.getData();
    }
}
