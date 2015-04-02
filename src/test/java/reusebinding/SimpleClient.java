package reusebinding;

import com.hyd.appserver.MinaAppClient;
import com.hyd.appserver.Request;

/**
 * @author yiding.he
 */
public class SimpleClient {

    public static void main(String[] args) {
        MinaAppClient client = new MinaAppClient("localhost", 8765);
        client.setName("TestClient");
        client.send(new Request("ThrowsException"));
        client.close();
    }
}
