package multipleserver;

import com.hyd.appserver.MinaAppServer;

/**
 * todo: description
 *
 * @author yiding.he
 */
public class TestMultipleServer {

    public static void main(String[] args) {
        startServer(8090);
        startServer(8091);
        startServer(8092);
    }

    private static void startServer(int port) {
        MinaAppServer server = new MinaAppServer(port);
        server.setActionPackages("demo.actions");

        server.start();
    }
}
