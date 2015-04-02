package externalBugs;

import com.hyd.appserver.MinaAppClient;
import com.hyd.appserver.Request;
import com.hyd.appserver.Response;
import com.hyd.appserver.authentication.BasicAuthentication;

/**
 * (description)
 *
 * @author yiding.he
 */
public class ShareServer {

    public static void main(String[] args) {
        MinaAppClient client = new MinaAppClient("124.232.143.155", 12262);
        client.setAuthentication(new BasicAuthentication("shareTools", "shareTools"));

        Request request = new Request("GetUserInfo");
        request.setParameter("userShareId", 1);

        Response response = client.send(request);
        System.out.println(response.isSuccess());
        System.out.println(response.getDynamicObject("userInfo"));
        
        client.close();
    }
}
