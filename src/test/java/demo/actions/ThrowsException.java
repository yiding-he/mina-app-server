package demo.actions;

import com.hyd.appserver.Action;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;

/**
 * @author yiding.he
 */
public class ThrowsException implements Action {

    @Override
    public Response execute(Request request) throws Exception {
        throw new Exception("Hahahaha!");
    }
}
