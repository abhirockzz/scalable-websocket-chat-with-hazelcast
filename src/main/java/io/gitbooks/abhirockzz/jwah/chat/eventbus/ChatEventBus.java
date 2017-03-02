package io.gitbooks.abhirockzz.jwah.chat.eventbus;

import com.hazelcast.core.ITopic;
import io.gitbooks.abhirockzz.jwah.chat.WebSocketServerManager;
import io.gitbooks.abhirockzz.jwah.chat.eventbus.listener.ChatMessageEventListener;
import io.gitbooks.abhirockzz.jwah.chat.eventbus.listener.LogoutNotificationEventListener;
import io.gitbooks.abhirockzz.jwah.chat.eventbus.listener.NewJoineeNotificationEventListener;
import io.gitbooks.abhirockzz.jwah.chat.model.ChatMessage;
import io.gitbooks.abhirockzz.jwah.chat.model.LogOutNotification;
import io.gitbooks.abhirockzz.jwah.chat.model.NewJoineeNotification;

/**
 * Central place for handling Topic based inter-node communication
 */
public final class ChatEventBus {
    final String NEW_JOINEE_NOTIFICATIONS_TOPIC_NAME = "new-joinee-notifications-topic";
    private ITopic<NewJoineeNotification> NEW_JOINEE_NOTIFICATIONS_TOPIC = null;

    final String CHAT_TOPIC_NAME = "chat-msg-topic";
    private ITopic<ChatMessage> CHAT_TOPIC = null;

    final String LOGOUT_NOTIFICATIONS_TOPIC_NAME = "logout-notifications-topic";
    private ITopic<LogOutNotification> LOGOUT_NOTIFICATIONS_TOPIC = null;
    
    private final String newJoineeNotificationTopicRegID;
    private final String chatTopicRegID;
    private final String logoutNotificationTopicRegID;

    private ChatEventBus() {
        NEW_JOINEE_NOTIFICATIONS_TOPIC = WebSocketServerManager.getInstance().getHazelcastInstance().getTopic(NEW_JOINEE_NOTIFICATIONS_TOPIC_NAME);
        this.newJoineeNotificationTopicRegID = NEW_JOINEE_NOTIFICATIONS_TOPIC.addMessageListener(new NewJoineeNotificationEventListener());

        CHAT_TOPIC = WebSocketServerManager.getInstance().getHazelcastInstance().getTopic(CHAT_TOPIC_NAME);
        this.chatTopicRegID = CHAT_TOPIC.addMessageListener(new ChatMessageEventListener());

        LOGOUT_NOTIFICATIONS_TOPIC = WebSocketServerManager.getInstance().getHazelcastInstance().getTopic(LOGOUT_NOTIFICATIONS_TOPIC_NAME);
        this.logoutNotificationTopicRegID = LOGOUT_NOTIFICATIONS_TOPIC.addMessageListener(new LogoutNotificationEventListener());
        
        System.out.println("HZ Topics setup");
    }
    
    private static final ChatEventBus INSTANCE = new ChatEventBus();
    
    public static ChatEventBus getInstance(){
        return INSTANCE;
    }
    
    public void publishChat(ChatMessage msg){
        CHAT_TOPIC.publish(msg);
    }
    
    public void publishNewJoineeNotification(NewJoineeNotification newJoineeNotification){
        NEW_JOINEE_NOTIFICATIONS_TOPIC.publish(newJoineeNotification);
    }
    
    public void publishLogoutNotification(LogOutNotification logoutNotification){
        LOGOUT_NOTIFICATIONS_TOPIC.publish(logoutNotification);
    }
    
    public void deregister(){
        LOGOUT_NOTIFICATIONS_TOPIC.removeMessageListener(logoutNotificationTopicRegID);
        NEW_JOINEE_NOTIFICATIONS_TOPIC.removeMessageListener(newJoineeNotificationTopicRegID);
        CHAT_TOPIC.removeMessageListener(chatTopicRegID);
        
        System.out.println("Deregistered all listeners");
    }
    
}
