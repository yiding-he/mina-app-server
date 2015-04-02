package externalBugs;

import com.hyd.appserver.MinaAppClient;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;
import com.hyd.appserver.authentication.BasicAuthentication;
import com.hyd.appserver.utils.dynamicobj.DynamicObject;

import java.util.List;

/**
 * [description]
 *
 * @author yiding.he
 */
public class SearchServerTest {

    public static void main(String[] args) {
        MinaAppClient client = new MinaAppClient("128.128.4.22", 11000);
        client.setAuthentication(new BasicAuthentication("gdyx", "gdyx"));

        Request request = new Request("NewSearchAction");
        request.setParameter("typeid", 1);

        Response response = client.send(request);
        List<DynamicObject> list = response.getDynamicObject("searchResult").getList("list");
        for (DynamicObject item : list) {
            System.out.println(item.get("contentName"));
        }

        client.close();
    }
}
