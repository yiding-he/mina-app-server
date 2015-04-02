package com.hyd.appserver.filters;

import com.hyd.appserver.Response;
import com.hyd.appserver.http.HttpResponseMessage;
import com.hyd.appserver.json.JsonResponseMessage;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

/**
 * 根据客户端 IP 地址过滤。当地址列表为空时，允许所有地址；否则仅允许列表中的地址。
 *
 * @author yiding.he
 */
public class IpWhiteListFilter extends IoFilterAdapter {

    private String protocol;

    private List<String> ipWhiteList;

    public IpWhiteListFilter(List<String> ipWhiteList, String protocol) {
        this.ipWhiteList = ipWhiteList;
        this.protocol = protocol;
    }

    public void sessionCreated(NextFilter nextFilter, IoSession ioSession) throws Exception {

        if (ipWhiteList == null || ipWhiteList.isEmpty()) {
            nextFilter.sessionCreated(ioSession);
            return;
        }

        SocketAddress address = ioSession.getRemoteAddress();

        if (address instanceof InetSocketAddress) {
            InetSocketAddress sAddr = (InetSocketAddress) address;
            String ip = sAddr.getAddress().getHostAddress();

            if (!ipWhiteList.contains(ip)) {
                ioSession.write(createForbiddenMessage()).addListener(IoFutureListener.CLOSE);
                return;
            }
        }

        nextFilter.sessionCreated(ioSession);
    }

    private Object createForbiddenMessage() {
        Object message;

        if (protocol.equals("json")) {
            message = new JsonResponseMessage(Response.fail("Access Denied."));
        } else if (protocol.equals("http")) {
            HttpResponseMessage resp = new HttpResponseMessage();
            resp.setResponseCode(500);
            message = resp;
        } else {
            message = null;
        }

        return message;
    }
}
