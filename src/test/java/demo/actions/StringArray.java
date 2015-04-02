package demo.actions;

import com.hyd.appserver.Action;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;
import com.hyd.appserver.annotations.*;

@Function(description = "test string array", parameters = {
        @Parameter(name = "name", type = Type.StringArray, description = "")
})
public class StringArray implements Action {

    @Override
    public Response execute(Request request) throws Exception {
        System.out.println(request.getStringValues("name"));
        return Response.success();
    }
}
