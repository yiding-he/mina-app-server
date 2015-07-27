package multipleserver;

import com.hyd.appserver.ClientConfiguration;
import com.hyd.appserver.MinaAppClient;
import com.hyd.appserver.MinaAppServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;

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

        startClient();
    }

    private static void startClient() {
        ArrayList<InetSocketAddress> list = new ArrayList<InetSocketAddress>();
        list.add(new InetSocketAddress("localhost", 8090));
        list.add(new InetSocketAddress("localhost", 8091));
        list.add(new InetSocketAddress("localhost", 8092));

        ClientConfiguration conf = new ClientConfiguration();
        conf.setServerAddresses(list);
        MinaAppClient minaAppClient = new MinaAppClient(conf);
    }

    private static void startServer(int port) {
        MinaAppServer server = new MinaAppServer(port);
        server.setActionPackages("demo.actions");

        server.start();
    }
}
