package minimal;

import com.hyd.appserver.Action;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;
import com.hyd.appserver.annotations.Function;

/**
 * @author yidin
 */
@Function(value = "/ThisIsAnAction/WithVery/LongNameAndPath"
, description = "你可以使用'AcVeryLongPat'来搜索")
public class VeryLongNameAction implements Action {

    @Override
    public Response execute(Request request) throws Exception {
        return null;
    }
}
