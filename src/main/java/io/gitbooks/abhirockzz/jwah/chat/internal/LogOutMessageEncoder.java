package io.gitbooks.abhirockzz.jwah.chat.internal;

import io.gitbooks.abhirockzz.jwah.chat.model.LogOutNotification;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;


public class LogOutMessageEncoder implements Encoder.Text<LogOutNotification> {

    @Override
    public String encode(LogOutNotification logOutMessage) throws EncodeException {
        return logOutMessage.getLogoutMsg();
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
