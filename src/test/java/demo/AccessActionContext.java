package demo;

import com.hyd.appserver.ActionContext;
import com.hyd.appserver.ContextListener;
import com.hyd.appserver.LogHandler;
import com.hyd.appserver.MinaAppServer;
import com.hyd.appserver.core.ServerConfiguration;
import com.hyd.appserver.core.Protocol;

/**
 * (description)
 *
 * @author yiding.he
 */
public class AccessActionContext {

    public static void main(String[] args) {
        MinaAppServer server = new MinaAppServer(8090);
        server.setActionPackages("demo.actions");
        server.setLogHandler(new DemoLogHandler());
        server.setHttpTestEnabled(false);
        server.setContextListener(new ContextListener() {
            @Override
            public void initialize(ServerConfiguration configuration) {
                System.out.println("Server starting...");
            }

            @Override
            public void destroy(ServerConfiguration configuration) {
                System.out.println("Shutting down...");
            }
        });

        server.start();
    }

    /////////////////////////////////////////

    private static class DemoLogHandler implements LogHandler {

        @Override
        public void addLog(ActionContext context) {
            
            context.getResponse();
            Protocol protocol = context.getProtocol();

            if (protocol == Protocol.Json) {
                System.out.println("调用：" + context.getRequest().getFunctionName());
            }
        }
    }
}
