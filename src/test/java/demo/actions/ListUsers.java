package demo.actions;

import com.hyd.appserver.Action;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;
import com.hyd.appserver.annotations.Function;
import com.hyd.appserver.annotations.ListProperty;
import com.hyd.appserver.annotations.Parameter;
import com.hyd.appserver.annotations.Result;
import com.hyd.appserver.annotations.Type;
import demo.pojos.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Function(description = "查询用户列表", parameters = {
        @Parameter(name = "name", type = Type.String, description = "参数名", required = true)
}, result = @Result(listProperties =
@ListProperty(name = "users", description = "用户列表", type = User.class)))
@Component
public class ListUsers implements Action {

    @Override
    public Response execute(Request request) throws Exception {
        String name = request.getString("name");
        
        List users = new ArrayList();
        
        // ....
        
        return Response.success().put("users", users);
    }
}
