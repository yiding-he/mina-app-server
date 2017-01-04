package com.hyd.appserver.authentication;

import com.hyd.appserver.Authenticator;
import com.hyd.appserver.Request;
import com.hyd.appserver.utils.Base64;
import com.hyd.appserver.utils.TripleDESUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于“用户名-密码”的服务器端消息验证。可以用于验证多套用户名和密码，一个用户名只能对应一个密码。
 *
 * @author yiding.he
 */
public class BasicAuthenticator implements Authenticator {

    static final Logger log = LogManager.getLogger(BasicAuthenticator.class);

    static final Logger securelog = LogManager.getLogger("com.hyd.appserver.sesure");

    private Map<String, String> keyMappings = new HashMap<String, String>();

    /**
     * 设置用户名和密码
     *
     * @param name 用户名
     * @param key  密码。如果 key 为 null 则表示删除该鉴权。
     */
    public void putKeyMapping(String name, String key) {
        if (key == null) {
            keyMappings.remove(name);
        }
        keyMappings.put(name, key);
    }

    @Override
    public boolean authenticate(Request request) throws Exception {
        String name_and_encoded = request.getCheckCode();

        if (name_and_encoded == null || !name_and_encoded.contains("\n")) {
            return false;       // 格式不正确。正确的格式应该是“名称\n加密字符串”
        }

        String[] parts = name_and_encoded.split("\n");
        securelog.debug("checkcode=\"" + parts[0] + "\\n" + parts[1] + "\"");
        
        String name = parts[0];

        if (!keyMappings.containsKey(name)) {
            return false;       // 名称不存在，无法解密
        }

        try {
            byte[] encoded = Base64.decode(parts[1]);
            String _text = TripleDESUtils.decrypt(this.keyMappings.get(name), encoded);
            String text = request.getTimestamp() + "|" + request.getFunctionName() + "|" + name;

            return text.equals(_text);  // 比较解密后的字符串
        } catch (Exception e) {
            return false;
        }
    }
}
