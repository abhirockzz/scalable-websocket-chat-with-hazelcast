package io.gitbooks.abhirockzz.jwah.chat.internal;

import io.gitbooks.abhirockzz.jwah.chat.model.NewJoineeNotification;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;


public class NewJoineeMessageEncoder implements Encoder.Text<NewJoineeNotification> {

    @Override
    public String encode(NewJoineeNotification newJoineeMessage) throws EncodeException {
        return newJoineeMessage.getNewJoineeMessage();
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
