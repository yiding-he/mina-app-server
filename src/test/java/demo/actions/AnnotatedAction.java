package demo.actions;

import com.hyd.appserver.Action;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;
import org.springframework.stereotype.Service;

/**
 * todo: description
 *
 * @author yiding.he
 */
@Service
public class AnnotatedAction implements Action {

    @Override
    public Response execute(Request request) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
