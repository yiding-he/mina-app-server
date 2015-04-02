package demo.actions;

import com.hyd.appserver.Action;
import com.hyd.appserver.annotations.*;

@Function(description = "", parameters = {
        @Parameter(description = "公共参数", name = "common", type = Type.String, required = false, defaultValue = "1")
}, result = @Result(properties = {
        @Property(name = "common", description = "公共返回值", type = Type.String)
}, listProperties = @ListProperty(name = "commonList", description = "公共多行返回值")))
public interface CommonAction extends Action {

}
