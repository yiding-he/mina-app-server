package minimal;

import com.hyd.appserver.Action;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;
import com.hyd.appserver.annotations.Function;

import java.util.Date;

@Function(value = "/base/get-time", description = "查询当前时间")
public class GetTime implements Action {

    @Override
    public Response execute(Request request) throws Exception {
        return Response.success().put("now", new Date());
    }
}
