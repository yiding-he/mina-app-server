package minimal;

import com.hyd.appserver.MinaAppServer;
import com.hyd.appserver.core.ServerConfiguration;

public class MinimalServerTest {

    public static void main(String[] args) {
        ServerConfiguration serverConfiguration = new ServerConfiguration();
        MinaAppServer minaAppServer = new MinaAppServer(serverConfiguration);
        minaAppServer.start();
    }
}
