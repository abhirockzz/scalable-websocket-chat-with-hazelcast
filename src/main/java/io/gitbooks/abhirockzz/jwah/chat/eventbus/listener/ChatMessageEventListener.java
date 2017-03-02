package io.gitbooks.abhirockzz.jwah.chat.eventbus.listener;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import io.gitbooks.abhirockzz.jwah.chat.ChatServer;
import io.gitbooks.abhirockzz.jwah.chat.model.ChatMessage;
import io.gitbooks.abhirockzz.jwah.chat.model.Reply;
import java.util.function.Predicate;
import javax.websocket.Session;

/**
 *
 * @author agupgupt
 */
public class ChatMessageEventListener implements MessageListener<ChatMessage> {

    @Override
    public void onMessage(Message<ChatMessage> event) {
        System.out.println("HZ ChatMessage Topic Listener invoked");
        ChatMessage msg = event.getMessageObject();

        Predicate<Session> filterCriteria = null;
        if (!msg.isPrivate()) {
            //for ALL (except self)
            filterCriteria = (session) -> !session.getUserProperties().get("user").equals(msg.from());

        } else {
            String privateRecepient = msg.getRecepient();
            //private IM
            filterCriteria = (session) -> privateRecepient.equals(session.getUserProperties().get("user"));
        }

        ChatServer.getSessions().stream()
                .filter(filterCriteria)
                .forEach((session) -> session.getAsyncRemote().sendObject(new Reply(msg.getMsg(), msg.from(), msg.isPrivate())));
    }

}
