package externalBugs;

import com.hyd.appserver.Response;
import com.hyd.appserver.utils.JsonUtils;

/**
 * (描述)
 *
 * @author 贺一丁
 */
public class GsonTest {

    public static void main(String[] args) {
        Response response = new Response();
        response.put("value", new int[]{1, 2, 3, 4, 5});

        System.out.println(JsonUtils.toJson(response));
    }
}
