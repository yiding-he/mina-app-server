package externalBugs;

import com.hyd.appserver.MinaAppClient;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;

/**
 * (描述)
 *
 * @author HeYiding
 */
public class BusiContentTest {

    public static void main(String[] args) {
        MinaAppClient client = new MinaAppClient("192.168.39.243", 8990);
        Request request = new Request("AutoRegisterUser");
        request.setParameter("registerType", 8);

        Response response = client.send(request);
        System.out.println(response.isSuccess());

        client.close();
    }
}
