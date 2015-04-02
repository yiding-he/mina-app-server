package demo.actions;

import com.hyd.appserver.ActionContext;
import com.hyd.appserver.Request;
import com.hyd.appserver.annotations.Function;
import com.hyd.appserver.annotations.Parameter;
import com.hyd.appserver.annotations.Property;
import com.hyd.appserver.annotations.Result;
import com.hyd.appserver.annotations.Type;
import demo.Response;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * (description)
 *
 * @author yiding.he
 */
@Function(
        description = "将字符串转化为时间",
        parameters = {
                @Parameter(name = "time", type = Type.String, description = "时间字符串"),
                @Parameter(name = "pattern", type = Type.String, description = "格式", 
                        required = false, defaultValue = GetTime.DEFAULT_PATTERN)
        },
        result = @Result(properties = {
                @Property(name = "now", type = Type.Date, description = "转换后的时间")
        })
)
@Component
public class GetTime implements CommonAction {

    public static final String DEFAULT_PATTERN = "yyyyMMddHHmm";

    public Response execute(Request request) throws Exception {
        ActionContext.getContext().put("name", "hahahahahaha");

        String timeString = request.getString("time");
        String pattern = request.getString("pattern");

        TimeUnit.SECONDS.sleep(1);

        Date date = new SimpleDateFormat(pattern).parse(timeString);

        Response response = new Response();
        response.put("now", date);
        return response;
    }
}
