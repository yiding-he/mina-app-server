package loadbalance;

import com.hyd.appserver.ClientConfiguration;
import com.hyd.appserver.MinaAppClient;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * todo: description
 *
 * @author yiding.he
 */
public class LoadBalanceClientTest {

    public static void main(String[] args) {
        ClientConfiguration conf = new ClientConfiguration();

        for (int port : RunMultipleServers.PORTS) {
            conf.addServer("localhost", port);
        }

        MinaAppClient client = new MinaAppClient(conf);

        Request request = new Request("Test");

        for (int i = 0; i < 10; i++) {
            Response response = client.send(request);
            System.out.println(ReflectionToStringBuilder.toString(response));
            System.out.println();
        }

        client.close();
    }
}
