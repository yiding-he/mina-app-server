package loadbalance;

import com.hyd.appserver.MinaAppServer;

/**
 * todo: description
 *
 * @author yiding.he
 */
public class RunMultipleServers {

    public static final int[] PORTS = new int[]{9991, 9992, 9993, 9994, 9995};

    public static void main(String[] args) {

        for (int port : PORTS) {
            MinaAppServer server = new MinaAppServer(port);
            server.start();
        }
    }
}
