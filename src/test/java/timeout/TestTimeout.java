package timeout;

import com.hyd.appserver.MinaAppClient;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * (description)
 * created at 2014/12/15
 *
 * @author Yiding
 */
public class TestTimeout {

    public static void main(String[] args) throws Exception {
        Random random = new Random();
        final MinaAppClient minaAppClient = new MinaAppClient("localhost", 8090);

        Runnable runnable = new Runnable() {

            public void run() {
                long id = Thread.currentThread().getId();
                System.out.println("Thread " + id + " started.");
                Response response = minaAppClient.send(new Request("LongDelay"));
                System.out.println("Thread " + id + ": " + response.isSuccess() + ", " + response.getMessage());
                System.out.println("Pool: " + minaAppClient.getPoolStatus());
            }
        };

        // 启动 20 个线程调用 LongDelay 接口，因为缺省线程池大小为 10，所以
        // 后面的都会在等待可用连接
        Thread[] threads = new Thread[20];
        for (int i = 0; i < threads.length; i++) {

            System.out.println("Pool: " + minaAppClient.getPoolStatus());
            System.out.println("Starting " + i);
            threads[i] = new Thread(runnable);
            threads[i].start();

            TimeUnit.MILLISECONDS.sleep(random.nextInt(500) + 100);
        }

        for (Thread thread : threads) {
            thread.join();
        }

        minaAppClient.close();
    }
}
