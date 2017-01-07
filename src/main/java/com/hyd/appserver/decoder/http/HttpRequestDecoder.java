package com.hyd.appserver.decoder.http;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * (description)
 * created at 17/01/07
 *
 * @author yiding_he
 */
public class HttpRequestDecoder implements ProtocolDecoder {

    private static final String CONTEXT_KEY = "MINA_APP_SERVER_HTTP_DECODER_CONTEXT";

    @Override
    public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        session.setAttributeIfAbsent(CONTEXT_KEY, new Context());
        Context context = (Context) session.getAttribute(CONTEXT_KEY);

        if (context.status == DecodeStatus.Ready) {
            context.expect(in, DecodeStatus.RequestLineMethod);
        }
    }

    @Override
    public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {

    }

    @Override
    public void dispose(IoSession session) throws Exception {

    }

    //////////////////////////////////////////////////////////////

    private static class Context {

        private DecodeStatus status = DecodeStatus.Ready;
    }

    //////////////////////////////////////////////////////////////

    private enum DecodeStatus {
        Ready, RequestLineMethod,
    }
}
