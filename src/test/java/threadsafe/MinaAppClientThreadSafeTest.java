package threadsafe;

import com.hyd.appserver.MinaAppClient;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;
import org.junit.Test;

import java.util.Arrays;

/**
 * todo: description
 *
 * @author yiding.he
 */
public class MinaAppClientThreadSafeTest {

    @Test
    public void testThreadSafe() throws Exception {
        final MinaAppClient client = new MinaAppClient("localhost", 8765);

        char[] arr = new char[1024000];
        Arrays.fill(arr, 'a');
        final String arg = new String(arr);

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                Request getTime = new Request("GetTime").setParameter("arg", arg);
                Response response = client.send(getTime);
                System.out.println(response);
            }
        };

        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();

        Thread.sleep(5000);
        client.close();
    }
}
