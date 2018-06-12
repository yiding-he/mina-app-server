package minimal;

import com.hyd.appserver.Action;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;
import com.hyd.appserver.annotations.*;

@Function(path = "/greetings", description = "问候", parameters = {
        @Parameter(name = "name", type = Type.String, description = "名字")
}, result = @Result(properties = {
        @Property(name = "greetings", type = Type.String, description = "问候语")
}))
public class Greetings implements Action {

    @Override
    public Response execute(Request request) throws Exception {
        String name = request.getString("name");
        return Response.success().put("greetings", "Hello, " + name);
    }
}
