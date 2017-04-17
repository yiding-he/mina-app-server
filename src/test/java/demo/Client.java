package demo;

import com.hyd.appserver.MinaAppClient;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;
import com.hyd.appserver.authentication.BasicAuthentication;
import org.junit.Test;

/**
 * (description)
 *
 * @author yiding.he
 */
public class Client {

    public static void main(String[] args) throws Exception {
        MinaAppClient client = new MinaAppClient("localhost", 8090);
        Response response = client.send(new Request("UserLogin")
                .setParameter("username", "admin").setParameter("password", "admin"));

        System.out.println(response.isSuccess());
        System.out.println(response.getMessage());

        client.close();
    }

    @Test
    public void testQueryUsers() throws Exception {
        MinaAppClient client = new MinaAppClient("localhost", 8090);
        Response response = client.send(new Request("QueryUsers"));
        System.out.println(response.getData());
    }

    private static void testCall() throws InterruptedException {
        // 创建 client 对象，该对象是线程安全的
        final MinaAppClient client = new MinaAppClient("localhost", 8765);
        client.setAuthentication(new BasicAuthentication("username", "password"));

        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    // 构造请求
                    Request request = new Request();
                    request.setFunctionName("GetTime").setParameter("time", "201205251234");

                    // 发送请求并获得回应
                    Response resp = client.send(request);
                    System.out.println(resp.getDynamicObject("now"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Thread  t1 = new Thread(runnable);
        Thread  t2 = new Thread(runnable);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        client.close();
    }
}
