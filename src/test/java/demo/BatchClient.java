package demo;

import com.hyd.appserver.ClientConfiguration;
import com.hyd.appserver.MinaAppClient;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * (description)
 *
 * @author yiding.he
 */
public class BatchClient {

    private static final MinaAppClient CLIENT;

    static {
        ClientConfiguration configuration = new ClientConfiguration();
        configuration.addServer("localhost", 8090);
        configuration.setMaxConnectionsPerServer(50);
        configuration.setPoolTimeoutSec(1);
        configuration.setSocketConnTimeoutSec(1);
        configuration.setSocketDataTimeoutSec(1);

        CLIENT = new MinaAppClient(configuration);
    }

    public static void main(String[] args) throws Exception {

        int threadCount = 1000;         // 线程数
        int batchCount = 1;             // 每个线程发送多少组请求
        int batchSize = 10;             // 每组请求大小

        int total = threadCount * batchSize * batchCount;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Integer>> futures = new ArrayList<Future<Integer>>();

        long start = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            Future<Integer> future = executor.submit(new TestCallable(batchSize, batchCount));
            futures.add(future);
        }

        int success = 0;
        for (Future<Integer> future : futures) {
            success += future.get();
        }

        long end = System.currentTimeMillis();

        System.out.println("线程数: " + threadCount);
        System.out.println("处理速度: " + (success / ((end - start) / 1000.0)) + " 个请求每秒");
        System.out.println("总请求数: " + total + "  成功数: " + success);
        System.out.println("请求成功率: " + ((double) success / total * 100.0) + "%");

        CLIENT.close();
        executor.shutdown();
    }

    private static class TestCallable implements Callable<Integer> {

        private int batchSize = 100;

        private int batchCount = 5;

        private TestCallable(int batchSize, int batchCount) {
            this.batchSize = batchSize;
            this.batchCount = batchCount;
        }

        public Integer call() throws Exception {
            int total = batchCount * batchSize;

            for (int i = 0; i < batchCount; i++) {
                for (int j = 0; j < batchSize; j++) {
                    total += call0();
                }
            }

            return total;
        }

        private int call0() {
            Response response = CLIENT.send(new Request("Simple"));
            return response.isSuccess() ? 0 : -1;
        }
    }
}
