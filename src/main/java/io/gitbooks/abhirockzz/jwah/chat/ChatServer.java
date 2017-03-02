package io.gitbooks.abhirockzz.jwah.chat;

import io.gitbooks.abhirockzz.jwah.chat.eventbus.ChatEventBus;
import io.gitbooks.abhirockzz.jwah.chat.internal.NewJoineeMessageEncoder;
import io.gitbooks.abhirockzz.jwah.chat.internal.LogOutMessageEncoder;
import io.gitbooks.abhirockzz.jwah.chat.internal.ReplyEncoder;
import io.gitbooks.abhirockzz.jwah.chat.internal.ChatMessageDecoder;
import io.gitbooks.abhirockzz.jwah.chat.internal.DuplicateUserMessageEncoder;
import io.gitbooks.abhirockzz.jwah.chat.internal.WelcomeMessageEncoder;
import io.gitbooks.abhirockzz.jwah.chat.model.WelcomeMessage;
import io.gitbooks.abhirockzz.jwah.chat.model.ChatMessage;
import io.gitbooks.abhirockzz.jwah.chat.model.DuplicateUserNotification;
import io.gitbooks.abhirockzz.jwah.chat.model.LogOutNotification;
import io.gitbooks.abhirockzz.jwah.chat.model.NewJoineeNotification;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(
        value = "/chat/{user}/",
        encoders = {ReplyEncoder.class,
            WelcomeMessageEncoder.class,
            NewJoineeMessageEncoder.class,
            LogOutMessageEncoder.class,
            DuplicateUserMessageEncoder.class},
        decoders = {ChatMessageDecoder.class}
)

public class ChatServer {

    private static Set<String> USERS = null;
    private static final List<Session> SESSIONS = new CopyOnWriteArrayList();
    private String user;
    private Session s;
    private boolean dupUserDetected;
    public static final String LOGOUT_MSG = "[logout]";

    final static String NEW_JOINEE_NOTIFICATIONS_TOPIC_NAME = "new-joinee-notifications-topic";
    final static String CHAT_TOPIC_NAME = "chat-msg-topic";
    final static String LOGOUT_NOTIFICATIONS_TOPIC_NAME = "logout-notifications-topic";
    final static String ALL_USERS_DISTRIBUTED_SET = "all-users";

    public ChatServer() {
        USERS = WebSocketServerManager.getInstance().getHazelcastInstance().getSet(ALL_USERS_DISTRIBUTED_SET);
    }


    public static List<Session> getSessions() {
        return SESSIONS;
    }

    @OnOpen
    public void userConnectedCallback(@PathParam("user") String user, Session s) {

        if (USERS.contains(user)) {
            try {
                dupUserDetected = true;
                s.getBasicRemote().sendObject(new DuplicateUserNotification(user));
                s.close();
                return;
            } catch (Exception ex) {
                Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        this.s = s;
        SESSIONS.add(s);
        s.getUserProperties().put("user", user);
        this.user = user;
        USERS.add(user);

        welcomeNewJoinee();
        announceNewJoinee();
    }

    private void welcomeNewJoinee() {

        try {
            s.getBasicRemote().sendObject(new WelcomeMessage(this.user));
        } catch (Exception ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void announceNewJoinee() {
        ChatEventBus.getInstance().publishNewJoineeNotification(new NewJoineeNotification(user));
        System.out.println("New Joineed notification placed on HZ Topic " + NEW_JOINEE_NOTIFICATIONS_TOPIC_NAME);

    }

    @OnMessage
    public void msgReceived(ChatMessage msg, Session s) {
        msg.from(user);
        if (msg.getMsg().equals(LOGOUT_MSG)) {
            try {
                s.close();
                return;
            } catch (IOException ex) {
                Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        ChatEventBus.getInstance().publishChat(msg);
        System.out.println("Chat Message placed on HZ Topic " + CHAT_TOPIC_NAME);

    }

    @OnClose
    public void onCloseCallback() {
        if (!dupUserDetected) {
            USERS.remove(this.user);
            SESSIONS.remove(s);
            processLogout();
        }

    }

    private void processLogout() {
        try {
            ChatEventBus.getInstance().publishLogoutNotification(new LogOutNotification(user));
        } catch (Exception ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
