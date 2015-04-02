package demo;

import com.hyd.appserver.MinaAppClient;
import com.hyd.appserver.Request;

/**
 * (description)
 *
 * @author yiding.he
 */
public class CloseClientOnServerShutdown {

    public static void main(String[] args) {
        MinaAppClient client = new MinaAppClient("128.128.4.2", 19999);
        Request request = new Request("QueryWebsites");
        System.out.println(client.send(request).getList("websites"));
    }
}
