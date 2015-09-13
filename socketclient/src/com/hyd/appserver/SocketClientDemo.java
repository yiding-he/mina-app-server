package com.hyd.appserver;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * 连接服务器示例
 *
 * @author yiding.he
 */
public class SocketClientDemo {

    public static final String SERVER = "10.123.96.74";

    public static final int PORT = 8090;

    private static final String USERNAME = "anxun";

    private static final String PASSWORD = "AYqkGpeF12CJmR9T";

    // 这里假设调用一个名为 GetUserList 的接口，该接口需要一个名为 role 的参数，其返回值中
    // 包含一个名为 total 的属性和一个名为 users 的列表。
    public static void main(String[] args) throws Exception {

        // 构建 Request 对象
        Request request = new Request("GetUserList");       // 设置接口名
        request.setParameter("role", 1);   // 设置参数

        // 设置鉴权信息
        CheckCodeGenerator.injectCheckCode(request, USERNAME, PASSWORD);

        // 创建连接
        Socket socket = new Socket(SERVER, PORT);

        // 发送请求
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write((JsonUtils.toJson(request) + "\n").getBytes());
        outputStream.flush();

        // 读取回应
        InputStream inputStream = socket.getInputStream();
        byte[] responseBytes = new byte[0];
        byte[] buffer = new byte[4096];                 // buffer size

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byte[] temp = new byte[responseBytes.length + len];
            System.arraycopy(responseBytes, 0, temp, 0, responseBytes.length);
            System.arraycopy(buffer, 0, temp, responseBytes.length, len);
            responseBytes = temp;

            if (buffer[len - 1] == (byte) 0x0A) {       // END flag
                break;
            }
        }

        // 关闭连接（实际使用时必须放入 finally）
        socket.close();

        // 解析回应并获取值
        String responseJson = new String(responseBytes, Charset.forName("UTF-8"));
        Response response = JsonUtils.parse(Response.class, responseJson);

        System.out.println("success: " + response.isSuccess());
        System.out.println("message: " + response.getMessage());
        System.out.println("total: " + response.getInteger("total"));
        
        // 以 Map 方式获取列表
        List<Map<String,String>> users = response.getList("users");
        for (Map<String, String> user : users) {
            System.out.println("user - id:" + user.get("id"));
            System.out.println("user - name:" + user.get("name"));
        }

        // 以 JavaBean 方式获取列表
        List<DemoUser> users1 = response.getList("users", DemoUser.class);
        for (DemoUser demoUser : users1) {
            System.out.println("demoUser - id:" + demoUser.getId());
            System.out.println("demoUser - name:" + demoUser.getName());
        }
    }
}
