package client;

import com.hyd.appserver.MinaAppClient;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;

import java.util.Date;

/**
 * todo: description
 *
 * @author yiding.he
 */
public class TestConnectToOldServer {

    public static void main(String[] args) {
        MinaAppClient client = new MinaAppClient("localhost", 8765);

        Response response = client.send(
                new Request("TestAction")
                        .setParameter("do", "haha")
                        .setParameter("now", new Date())
                        .setParameter("count", 100)
                        .setParameter("done", true)
        );

        System.out.println(response.isSuccess() + "," + response.getMessage() + ",\n" + response.getData());
        client.close();
    }
}
