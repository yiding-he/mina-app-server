package demo.actions;

import com.hyd.appserver.Action;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;
import com.hyd.appserver.annotations.Function;
import com.hyd.appserver.annotations.Parameter;
import com.hyd.appserver.annotations.Property;
import com.hyd.appserver.annotations.Result;
import com.hyd.appserver.annotations.Type;
import demo.pojos.User;

/**
 * 用户登录
 *
 * @author yiding.he
 */
@Function(description = "用户登录", parameters = {
        @Parameter(name = "username", description = "用户名", type = Type.String),
        @Parameter(name = "password", description = "密码", type = Type.String)
}, result = @Result(properties = {
        @Property(name = "user", description = "已登录用户信息", type = Type.Pojo, pojoType = User.class)
}))
public class UserLogin implements Action {

    public Response execute(Request request) throws Exception {
        User user = new User();
        user.setUsername(request.getString("username"));
        user.setPassword(request.getString("password"));

        return new Response().put("user", user);
    }
}
