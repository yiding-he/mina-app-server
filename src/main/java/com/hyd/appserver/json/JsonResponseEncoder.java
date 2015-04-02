package com.hyd.appserver.json;

import com.hyd.appserver.utils.JsonUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * (description)
 *
 * @author yiding.he
 */
public class JsonResponseEncoder implements MessageEncoder<JsonResponseMessage> {
    
    private final CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();

    public void encode(IoSession ioSession, JsonResponseMessage response,
                       ProtocolEncoderOutput out) throws Exception {
        IoBuffer buffer = IoBuffer.allocate(2048, true).setAutoExpand(true);
        String json = JsonUtils.toJson(response) + Constants.END_SIGN;
        buffer.putString(json, encoder).flip();
        out.write(buffer);
        out.flush();
    }
}
