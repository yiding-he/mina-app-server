package classloader;

/**
 * (description)
 * created at 2014/10/17
 *
 * @author Yiding
 */
public class ThreadTest {

    public static void main(String[] args) throws Exception {

        final String[] results = {null};

        Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    results[0] = "返回值";
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();         // 线程执行需要 5 秒
        thread.join(6000);
        System.out.println(results[0]);

        thread.interrupt();     // 强制停止线程，线程内会抛出异常
        System.out.println("Thread interrupted.");
    }
}
