package minimal;

import com.hyd.appserver.MinaAppClient;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;

public class MinimalClientTest {

    public static void main(String[] args) {
        // 注意：MinaAppClient 不是一次性的对象，实际开发中不要这么做。
        try (MinaAppClient client = new MinaAppClient("localhost", 8090)) {
            Response response = client.send(new Request("/base/get-time"));
            System.out.println(response.getDate("now"));
        }
    }
}
