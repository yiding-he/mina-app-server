package clientinterceptor;

import com.hyd.appserver.*;

/**
 * (description)
 * created at 2015/5/28
 *
 * @author Yiding
 */
public class ClientInterceptorDemo {

    // 假设我们需要在所有的请求中添加参数 version=1.0，但是又不想到处修改代码，那么可以
    // 这样：为 MinaAppClient 设置一个 ClientInterceptor，用它来为每个请求添加参数。
    public static void main(String[] args) {

        MinaAppClient client = new MinaAppClient("localhost", 8090);
        client.setInterceptor(new ClientInterceptor() {

            @Override
            public Response intercept(ClientInvocation invocation) {
                invocation.getRequest().setParameter("version", "1.0");
                return invocation.invoke();
            }
        });

        Request request = new Request("UserLogin")
                .setParameter("user", "admin")
                .setParameter("pass", "admin");

        // 尽管 request 中只有两个参数，但实际发送给服务器的请求当中会自动加上 version 参
        // 数，这就是 ClientInterceptor 的作用
        client.send(request);
    }
}
