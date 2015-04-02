package com.hyd.appserver.restful;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyd.appserver.Action;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;
import com.hyd.appserver.annotations.Function;
import com.hyd.appserver.annotations.Parameter;
import com.hyd.appserver.annotations.Type;

/**
 * @param <Q>
 * @param <R>
 *
 * @deprecated 尚未实现
 */
@Function(description = "", parameters = {
        @Parameter(name = "method", type = Type.String, description =
                "要执行的操作：GET=查询，DELETE=删除，PUT=保存（主键为空则表示新增，否则表示修改），也可以定义其他操作"),
        @Parameter(name = "entity", type = Type.String, description = "相关参数")
})
public abstract class RestfulAction
        <Q extends Request, R extends Response> implements Action<Q, R> {

    @Override
    public R execute(Q request) throws Exception {
        String method = request.getString("method");
        String entity = request.getString("entity");

        JSONObject entityObject = JSON.parseObject(entity);
        return execute(method, entityObject);
    }

    protected abstract R execute(String method, JSONObject entityObject);
}
