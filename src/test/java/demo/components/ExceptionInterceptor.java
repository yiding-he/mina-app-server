package demo.components;

import com.hyd.appserver.core.ActionInvocation;
import com.hyd.appserver.Interceptor;
import com.hyd.appserver.Response;

/**
 * todo: description
 *
 * @author yiding.he
 */
public class ExceptionInterceptor implements Interceptor {

    @Override
    public Response intercept(ActionInvocation invocation) throws Exception {
        Response response;

        try {
            response = invocation.invoke();
        } catch (Exception e) {
            System.out.println("================================================");
            System.out.println("================= Exception ====================");
            System.out.println("================================================");
            e.printStackTrace();
            System.out.println("================================================");
            System.out.println("================================================");
            response = Response.fail(e.getMessage());
        }

        return response;
    }
}
