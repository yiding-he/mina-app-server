package com.hyd.appserver.filters;

import com.hyd.appserver.json.JsonRequestMessage;
import com.hyd.appserver.snapshot.Snapshot;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

/**
 * 用来记录当前正在执行的接口的过滤器
 *
 * @author 贺一丁
 */
public class IoPerformanceFilter extends IoFilterAdapter {

    private final Snapshot snapshot;

    public IoPerformanceFilter(Snapshot snapshot) {
        this.snapshot = snapshot;
    }

    @Override
    public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
        if (session.containsAttribute("functionName")) {
            snapshot.processorFinished(session.getId());
        }

        super.messageSent(nextFilter, session, writeRequest);
    }

    @Override
    public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
        if (message instanceof JsonRequestMessage) {
            String functionName = ((JsonRequestMessage) message).getFunctionName();
            session.setAttribute("functionName", functionName);
            snapshot.processStarted(session.getId(), functionName);
        }
        super.messageReceived(nextFilter, session, message);
    }

    @Override
    public void sessionClosed(NextFilter nextFilter, IoSession session) throws Exception {
        snapshot.processorFinished(session.getId());
        super.sessionClosed(nextFilter, session);
    }
}
