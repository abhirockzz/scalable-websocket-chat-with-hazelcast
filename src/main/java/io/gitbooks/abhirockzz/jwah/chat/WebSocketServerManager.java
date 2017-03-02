package io.gitbooks.abhirockzz.jwah.chat;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import io.gitbooks.abhirockzz.jwah.chat.eventbus.ChatEventBus;
import java.util.concurrent.CountDownLatch;
import org.glassfish.tyrus.server.Server;

public final class WebSocketServerManager {

    private WebSocketServerManager() {
    }
    private static WebSocketServerManager INSTANCE = null;

    public static WebSocketServerManager getInstance() {
        return INSTANCE;
    }

    static void start(boolean autoShutDown, String port) throws Exception {
        if (started) {
            throw new IllegalStateException("Server instance is already running");
        }
        INSTANCE = new WebSocketServerManager();
        INSTANCE.runServer(autoShutDown, port);
    }

    private Server server;
    private static boolean started = false;
    private HazelcastInstance HZ = null;

    void runServer(boolean autoShutDown, String port) throws Exception {

        server = new Server("localhost",
                Integer.parseInt(port),
                "", null, ChatServer.class);

        server.start();
        HZ = Hazelcast.newHazelcastInstance();

        if (autoShutDown) {
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    stop();
                    System.out.println("Stopped WebSocket server & HZ");

                }
            }));

            System.out.print("Shutdown hook added");

        }
        started = true;
    }

    void stop() {
        if (!started) {
            throw new IllegalStateException("Server instance is not running");
        }

        server.stop();
        ChatEventBus.getInstance().deregister();
        HZ.shutdown();

        started = false;
    }

    public HazelcastInstance getHazelcastInstance() {
        return HZ;
    }

    public static void main(String[] args) throws Exception {
        String port = args.length == 0 ? "8080" : args[0];
        start(true, port);

        new CountDownLatch(1).await();
    }

}
