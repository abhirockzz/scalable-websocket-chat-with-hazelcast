package io.gitbooks.abhirockzz.jwah.chat.internal;

import io.gitbooks.abhirockzz.jwah.chat.model.DuplicateUserNotification;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;


public class DuplicateUserMessageEncoder implements Encoder.Text<DuplicateUserNotification> {

    @Override
    public String encode(DuplicateUserNotification duplicateUserNotification) throws EncodeException {
        return duplicateUserNotification.getDuplicateUserNotificationMsg();
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
