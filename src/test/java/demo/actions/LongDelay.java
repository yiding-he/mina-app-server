package demo.actions;

import com.hyd.appserver.Action;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;

/**
 * (描述)
 *
 * @author HeYiding
 */
public class LongDelay implements Action {

    @Override
    public Response execute(Request request) throws Exception {
        System.out.println("waiting for 40s...");
        Thread.sleep(40000);
        System.out.println("ok.");
        return Response.success();
    }
}
