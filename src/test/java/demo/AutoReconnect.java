package demo;

import com.hyd.appserver.*;
import com.hyd.appserver.Response;
import com.hyd.appserver.authentication.BasicAuthentication;

/**
 * (description)
 *
 * @author yiding.he
 */
public class AutoReconnect {

    public static void main(String[] args) throws Exception {
        MinaAppClient client = new MinaAppClient("localhost", 8090);
        client.setAuthentication(new BasicAuthentication("username", "password"));
        Request request = new Request();
        request.setFunctionName("QueryUsers");

        while (true) {
            Response response = client.send(request);
            System.out.println("response: " + response.isSuccess());
            Thread.sleep(5000);
        }
    }
}
