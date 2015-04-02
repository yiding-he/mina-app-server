package errors;

import com.hyd.appserver.json.JsonRequestMessage;
import com.hyd.appserver.utils.JsonUtils;
import org.apache.mina.proxy.utils.ByteUtilities;

/**
 * todo: description
 *
 * @author yiding.he
 */
public class DumpViewer {

    public static void main(String[] args) {
        String dump = "35 32 31 37 37 39 39 39 35 32 22 2C 22 63 6C 69 65 6E 74 49 6E 66 6F 22 3A 7B 22 6E 61 6D 65 22 3A 22 22 7D 7D 0A";

        byte[] bytes = ByteUtilities.asByteArray(dump.replace(" ", ""));
        String json = new String(bytes).trim();
        System.out.println(json);

        JsonRequestMessage request = JsonUtils.parse(JsonRequestMessage.class, json);
        System.out.println(request.getParameters());
    }
}
