package demo.actions;

import com.hyd.appserver.Action;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;
import com.hyd.appserver.annotations.Function;
import com.hyd.appserver.annotations.ListProperty;
import com.hyd.appserver.annotations.Property;
import com.hyd.appserver.annotations.Result;
import com.hyd.appserver.annotations.Type;
import demo.pojos.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Function(description = "查询所有用户", result = @Result(
        listProperties = @ListProperty(name = "users", description = "用户列表", type=User.class, properties = {
                @Property(name = "id", type = Type.Integer, description = "ID"),
                @Property(name = "birthday", type = Type.Date, description = "出生日期"),
                @Property(name = "username", type = Type.String, description = "用户名"),
                @Property(name = "password", type = Type.String, description = "密码")
        })))
public class QueryUsers implements Action {

    @Override
    public Response execute(Request request) throws Exception {
        List<User> users = new ArrayList<User>();
        
        User user = new User();
        user.setUsername("user1");
        user.setPassword("user1");
        user.setBirthday(new Date());
        users.add(user);
        
        user = new User();
        user.setId(1000);
        user.setUsername("user2");
        user.setPassword("user2");
        users.add(user);
        
        return new Response().put("users", users);
    }
}
