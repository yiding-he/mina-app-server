package com.hyd.appserver.json;

import com.hyd.appserver.MinaAppServer;
import com.hyd.appserver.Response;
import com.hyd.appserver.core.Protocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.MessageHandler;

/**
 * (description)
 *
 * @author yiding.he
 */
public class JsonRequestHandler implements MessageHandler<JsonRequestMessage> {

    private static final Logger LOG = LogManager.getLogger(JsonRequestHandler.class);

    private MinaAppServer server;

    public JsonRequestHandler(MinaAppServer server) {
        this.server = server;
    }

    public void handleMessage(IoSession ioSession, JsonRequestMessage request) throws Exception {

        if (!request.isValid()) {
            LOG.error("请求格式不正确，来自 " + ioSession.getRemoteAddress() + ": " + request.getOriginalString());
            ioSession.closeNow();

        } else {
            Response response = server.getCore().process(request, Protocol.Json);
            ioSession.write(new JsonResponseMessage(response));

            // 正常处理完后不要关闭 IoSession，因为客户端使用了连接池。如果
            // 客户端完成调用后主动关闭连接，那就 OK；否则的话，空闲超时过后
            // 服务器端也会主动关闭连接。
        }

    }
}
