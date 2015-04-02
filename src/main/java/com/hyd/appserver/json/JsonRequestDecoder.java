package com.hyd.appserver.json;

import com.hyd.appserver.core.ClientInfo;
import com.hyd.appserver.utils.JsonUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderAdapter;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * (description)
 *
 * @author yiding.he
 */
public class JsonRequestDecoder extends MessageDecoderAdapter {

    private static final Logger log = LoggerFactory.getLogger(JsonRequestDecoder.class);

    private String status = "ready";

    private final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();

    public MessageDecoderResult decodable(IoSession ioSession, IoBuffer in) {

        int last = in.remaining() - 1;
        MessageDecoderResult result;

        if (status.equals("ready") && in.get(0) != '{') {
            result = NOT_OK;
        } else {
            if (in.get(last) == (byte) Constants.END_SIGN) {
                status = "ready";
                result = OK;
            } else {
                status = "pending";
                result = NEED_DATA;
            }
        }

        return result;
    }

    public MessageDecoderResult decode(IoSession ioSession,
                                       IoBuffer in, ProtocolDecoderOutput out) throws Exception {

        String str = in.getString(decoder).trim();
        String[] jsons = str.split("\n+");

        for (String json : jsons) {
            log.debug("Received original json: " + json);

            JsonRequestMessage request;
            try {
                request = JsonUtils.parseRequest(json);
            } catch (Exception e) {
                log.error("Error parsing request message", e);

                request = new JsonRequestMessage();
                request.setValid(false);
            }

            request.setOriginalString(json);

            ClientInfo info = request.getClientInfo();
            if (info == null) {
                info = new ClientInfo();
                request.setClientInfo(info);
            }

            info.setIpAddress(((InetSocketAddress) ioSession.getRemoteAddress()).getAddress().getHostAddress());

            out.write(request);
        }

        return OK;
    }

    @Override
    public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
        this.status = "ready";
    }
}
