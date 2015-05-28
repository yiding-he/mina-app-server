package clientinterceptor;

import com.hyd.appserver.ClientInterceptor;
import com.hyd.appserver.ClientInvocation;
import com.hyd.appserver.MinaAppClient;
import com.hyd.appserver.Response;

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
    }
}
