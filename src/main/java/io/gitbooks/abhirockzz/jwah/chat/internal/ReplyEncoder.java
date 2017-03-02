package io.gitbooks.abhirockzz.jwah.chat.internal;

import io.gitbooks.abhirockzz.jwah.chat.model.Reply;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;


public class ReplyEncoder implements Encoder.Text<Reply> {

    
    @Override
    public String encode(Reply reply) throws EncodeException {        //reply.from(from);
        return reply.getMsg();
    }

    @Override
    public void init(EndpointConfig ec) {
        //no-op
    }

    @Override
    public void destroy() {
        //no-op
    }
    
}
