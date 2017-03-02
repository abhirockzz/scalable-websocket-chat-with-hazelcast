package io.gitbooks.abhirockzz.jwah.chat.eventbus.listener;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import io.gitbooks.abhirockzz.jwah.chat.ChatServer;
import io.gitbooks.abhirockzz.jwah.chat.model.LogOutNotification;

public class LogoutNotificationEventListener implements MessageListener<LogOutNotification> {

    @Override
    public void onMessage(Message<LogOutNotification> event) {
        System.out.println("HZ Logout notification Topic Listener invoked");
        LogOutNotification msg = event.getMessageObject();
        ChatServer.getSessions().stream()
                    .filter((sn) -> sn.isOpen())
                    .forEach((session) -> session.getAsyncRemote().sendObject(msg));
     
    }

}
