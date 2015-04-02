package demo.actions;

import com.hyd.appserver.*;
import com.hyd.appserver.annotations.*;
import demo.pojos.Content;

@Function(description = "查询内容", result = @Result(properties = {
        @Property(name = "content", type = Type.Pojo, description = "内容", pojoType = Content.class)
}))
public class QueryContent implements Action {

    @Override
    public Response execute(Request request) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
