package demo.actions;

import com.hyd.appserver.Action;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;
import com.hyd.appserver.annotations.*;

@Function(description = "一个简单的接口，什么都不做。一个简单的接口，什么都不做。一个简单的接口，" +
        "什么都不做。一个简单的接口，什么都不做。一个简单的接口，什么都不做。" +
        "什么都不做。一个简单的接口，什么都不做。一个简单的接口，什么都不做。" +
        "什么都不做。一个简单的接口，什么都不做。一个简单的接口，什么都不做。" +
        "什么都不做。一个简单的接口，什么都不做。一个简单的接口，什么都不做。" +
        "一个简单的接口，什么都不做。")
public class Simple implements Action {

    public Response execute(Request request) throws Exception {
        return new Response();
    }
}
