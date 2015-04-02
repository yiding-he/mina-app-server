package demo;

import com.hyd.appserver.*;
import com.hyd.appserver.Response;
import com.hyd.appserver.core.ActionInvocation;
import com.hyd.appserver.core.Protocol;

/**
 * (description)
 *
 * @author yiding.he
 */
public class CustomInterceptor {

    public static void main(String[] args) {
        MinaAppServer server = new MinaAppServer(8090);
        server.setActionPackages("demo.actions");
        
        server.getCore().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(ActionInvocation invocation) throws Exception {
                if (invocation.getActionContext().getProtocol() == Protocol.Http) {
                    return Response.fail("request blocked!!!");
                }
                return invocation.invoke();
            }
        });
        
        server.start();
    }
}
