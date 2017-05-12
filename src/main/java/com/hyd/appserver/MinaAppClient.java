package com.hyd.appserver;

import com.hyd.appserver.core.ClientInfo;
import com.hyd.appserver.core.IoSessionPoolManager;
import com.hyd.appserver.utils.JsonUtils;
import com.hyd.appserver.utils.StringUtils;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

/**
 * <p>客户端。每个 MinaAppClient 对象包含了一个连接池，所以对每个服务器只需要创建一个 MinaAppClient 对象即可。</p>
 * <p>如果服务器端要求对客户端进行鉴权，则需要调用客户端的
 * {@link #setAuthentication(Authentication)} 方法（一次即可）。</p>
 * <p>对于一个服务器，只需要创建一个客户端对象即可，MinaAppClient 是线程安全的。</p>
 *
 * @author yiding.he
 * @since 1.7.0:
 *        新增对多服务器的（轮询式）负载均衡。
 */
public class MinaAppClient implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(MinaAppClient.class);

    private static final Logger IO_LOG = LoggerFactory.getLogger(MinaAppClient.class.getName() + ".io");

    private static final Logger POOL_LOG = LoggerFactory.getLogger(MinaAppClient.class.getName() + ".pool");

    private ClientConfiguration clientConfiguration;

    private IoSessionPoolManager poolManager;

    private Authentication authentication = null;

    private ClientInterceptor interceptor;

    private String name = "";

    private boolean closed = false;

    /**
     * 构造方法
     *
     * @param configuration 配置
     */
    public MinaAppClient(ClientConfiguration configuration) {
        this.poolManager = new IoSessionPoolManager(configuration);
        this.clientConfiguration = configuration;
    }

    /**
     * 构造方法
     *
     * @param server 服务器地址
     * @param port   服务器端口
     */
    public MinaAppClient(String server, int port) {
        ClientConfiguration configuration = new ClientConfiguration();
        configuration.addServer(server, port);

        this.poolManager = new IoSessionPoolManager(configuration);
        this.clientConfiguration = configuration;
    }

    public void addServer(String server, int port) {
        this.clientConfiguration.addServer(server, port);
        this.poolManager.addServer(server, port);
    }

    public void deleteServer(String server, int port) {
        this.clientConfiguration.deleteServer(server, port);
        this.poolManager.deleteServer(server, port);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isClosed() {
        return closed;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public ClientInterceptor getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(ClientInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    public ClientConfiguration getClientConfiguration() {
        return clientConfiguration;
    }

    public Map<String, String> getPoolStatus() {
        return this.poolManager.getPoolStatus();
    }

    public List<String> getAvailableServerAddresses() {
        return this.poolManager.getAvailablePoolNames();
    }

    /**
     * 设置客户端验证方式。如果服务器端要求进行验证，则初始化 MinaAppClient 对象时需要调用此方法。
     *
     * @param authentication 验证方式，根据服务器的要求而定
     */
    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public Response send(final Request request) {

        // 接口名不能为空
        if (StringUtils.isEmpty(request.getFunctionName())) {
            throw new AppClientException("function name not specified.");
        }

        // 如果定义了拦截器，则将发送请求交给拦截器执行
        // 否则直接发送
        if (this.interceptor != null) {
            ClientInvocation invocation = new ClientInvocation() {

                @Override
                public MinaAppClient getClient() {
                    return MinaAppClient.this;
                }

                @Override
                public Request getRequest() {
                    return request;
                }

                @Override
                public Response invoke() {
                    return send0(request);
                }
            };

            return this.interceptor.intercept(invocation);
        } else {
            return send0(request);
        }

    }

    private Response send0(Request request) {
        // 请求不能为空
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        // 已经被关闭的客户端不能再使用
        if (this.closed) {
            throw new IllegalStateException("Client already been closed.");
        }

        try {
            request.setTimestamp(String.valueOf(System.nanoTime()));

            if (request.getClientInfo() == null && this.name != null) {
                ClientInfo clientInfo = new ClientInfo();
                clientInfo.setName(this.name);
                request.setClientInfo(clientInfo);
            }

            if (this.authentication != null) {
                request.setCheckCode(this.authentication.generateCheckCode(request));
            }

            String requestJson = JsonUtils.toJson(request);
            String responseJson = sendString(requestJson, request.getFunctionName());
            return parseResponse(responseJson);

        } catch (AppServerException e) {
            throw e;
        } catch (Exception e) {
            throw new AppServerException(e);
        }
    }

    private Response parseResponse(String json) {
        if (json == null || json.trim().equals("")) {
            return Response.fail("(无法获取服务器回应)");
        }

        Response response = JsonUtils.parseResponse(json);
        response.setOriginalJson(json);
        return response;
    }

    private String sendString(String string, String functionName) {
        // create session
        final IoSession session;

        try {
            session = getSessionFromPool();
            POOL_LOG.debug("Sending request to " + session.getRemoteAddress() + "...");
        } catch (Exception e) {
            LOG.error("", e);
            return JsonUtils.toJson(Response.fail("(连接服务器" + getClientConfiguration().getServerAddresses() + "失败)" + e.getMessage()));
        }

        try {
            return sendString0(string, functionName, session);
        } finally {
            returnSession(session);
        }
    }

    private String sendString0(final String string, final String functionName, final IoSession session) {
        IO_LOG.debug("request: " + string);

        // message object for reading
        // (decoded by com.hyd.appserver.utils.IoSessionPool#createTextLineCodecFactory)
        final Object[] messages = new Object[1];
        final int timeout = this.clientConfiguration.getSocketDataTimeoutSec() * 1000;

        session.write(string);
        read(functionName, session, messages, timeout);

        // parse message
        Object message = messages[0];
        String result = null;
        if (message != null) {
            result = message.toString();
            IO_LOG.debug("response: " + result);
        }
        return result;
    }

    private void read(final String functionName,
                      final IoSession session,
                      final Object[] messages, final int timeout) {
        final ReadFuture readFuture = session.read();

        Runnable runnable = new Runnable() {

            public void run() {
                try {
                    readFuture.await(timeout);
                    messages[0] = readFuture.getMessage();
                } catch (InterruptedException e) {
                    LOG.error("请求服务器" + session.getRemoteAddress() + "超时[" + functionName + "]:" + e.toString());
                    session.closeNow();
                }
            }
        };

        try {
            Thread t = new Thread(runnable);
            t.start();
            t.join(timeout);
        } catch (InterruptedException e) {
            LOG.error("", e);
        }
    }

    private void returnSession(IoSession session) {
        this.poolManager.returnIoSession(session);
    }

    private IoSession getSessionFromPool() {
        return this.poolManager.borrowIoSession();
    }

    @Override
    public void close() {
        if (this.closed) {
            return;
        }

        this.poolManager.shutdown();
        this.closed = true;
    }
}
