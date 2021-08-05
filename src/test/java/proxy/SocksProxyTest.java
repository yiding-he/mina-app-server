package proxy;

import com.hyd.appserver.MinaAppClient;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;
import com.hyd.appserver.utils.JsonUtils;

import java.net.Socket;
import java.util.Scanner;

/**
 * (description)
 * created at 2017/2/11
 *
 * @author yidin
 */
public class SocksProxyTest {

    static {
        System.setProperty("socksProxyHost", "localhost");
        System.setProperty("socksProxyPort", "2346");
    }

    public static void main(String[] args) throws Exception {
        testSocketConnection();
        testMinaClient();
    }

    private static void testSocketConnection() throws Exception {
        Socket socket = new Socket("10.10.20.204", 8090);
        socket.getOutputStream().write("{\"functionName\":\"Fxxk\"}\n".getBytes());
        String response = new Scanner(socket.getInputStream()).nextLine();
        System.out.println(response);
        socket.close();
    }

    private static void testMinaClient() {
        MinaAppClient client = new MinaAppClient("10.10.20.204", 8090);
        Response response = client.send(new Request("ffffffff"));
        System.out.println(JsonUtils.toJson(response, true));
    }
}
