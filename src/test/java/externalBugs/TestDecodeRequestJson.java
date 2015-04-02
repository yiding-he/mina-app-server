package externalBugs;

import com.hyd.appserver.json.JsonRequestMessage;
import com.hyd.appserver.utils.JsonUtils;

/**
 * todo: description
 *
 * @author yiding.he
 */
public class TestDecodeRequestJson {

    public static void main(String[] args) {

        String json = "{\"checkCode\":\"gdyx\\nvR9dtzoXI7S/9kxhEqIShPiJfcfTI9jUDxNjd0QCB9a+1s2hB3aPCQ==\",\"clientInfo\":" +
                "{\"name\":\"\"},\"functionName\":\"FindChannel\",\"parameters\":{\"id\":[\"-1\"],\"name\":[\"554\"]},\"timestamp\":\"479773085280095\"}";

        JsonRequestMessage message = JsonUtils.parseRequest(json);
        System.out.println(message);
    }
}
