package io.gitbooks.abhirockzz.jwah.chat.eventbus.listener;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import io.gitbooks.abhirockzz.jwah.chat.ChatServer;
import io.gitbooks.abhirockzz.jwah.chat.model.NewJoineeNotification;

public class NewJoineeNotificationEventListener implements MessageListener<NewJoineeNotification> {

    @Override
    public void onMessage(Message<NewJoineeNotification> msg) {
        System.out.println("HZ NewJoineeNotification Topic Listener invoked");
        ChatServer.getSessions().stream()
                .filter((sn) -> !sn.getUserProperties().get("user").equals(msg.getMessageObject().getNewJoinee()))
                .forEach((sn) -> sn.getAsyncRemote().sendObject(msg.getMessageObject()));
    }

}
