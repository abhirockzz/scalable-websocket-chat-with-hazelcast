This application demonstrates a service built using the [Java WebSocket API (JSR 356)](jcp.org/en/jsr/detail?id=356). In addition to showcasing practical usage of the API, it also focuses on the scalbility aspect

## Components used

- Uses the [Tyrus](https://tyrus.java.net) implementation for the core logic
- Leverages the [Grizzly](https://grizzly.java.net/) container support available in Tyrus
- [Hazelcast](https://hazelcast.com/)
- Packaged as a standalone JAR 

## Build & run

The application is a Maven project

- To build, just execute `mvn clean install` which will produce an independent (fat/uber) JAR
- Run it using `java -jar target/websocket-chat-scale.jar <port_number>` (8080 is the default if you do not provide a port number)
- You can run multiple instances of this application (on different ports)

## Features

Here is what you can do with the chat application

- Join the chat room
- Send public messages
- Send private messages
- Get notified about new users joining
- Leave the chat room
- Get notified another user leaves the chat room

> All these work no matter which node the user is connected to

## Scaling the application

This section gives you quick background, talks about the actual problem and the solution. 

> Although some of the concepts have been discussed in the context of this application, they are generally applicable for WebSocket applications in general (specially the ones which use the Java WebSocket API)

Underneath the covers, WebSocket communication b/w a pair of peers takes place over a **single TCP connection** - this makes it stateful in nature. It's different from HTTP which is a request-response cycle which gets repeated over and over again (for each request initiated by the client). This can manifest itself in different ways for different implementations, but please note the below implications as far the Java WebSocket API is concerned

- Once the WebSocket connection is established, the `javax.websocket.Session` representing that connection is specific to the node (JVM) on which the request was initiated - going forward, all the communication will happen over this connection i.e. the client is now tied to this node 
- The `javax.websocket.Session` is not `Serializable` which means that the connection state cannot be exchanged between JVMs
 
In the context of this chat application, being able to 'scale up' implies having the ability to accommodate more and more users and at the same time ensure that 

- they are able to use all the features properly (e.g. private & public messages, joinee notifications, logout notifications etc.) 
- and they get consistent experience (in terms of performance etc.)

You can only accommodate a certain number of users on a single instance of your application due to resource constraints. A possible solution is to have multiple instances of the chat application. This would work for a stateless HTTP based service, but is not enough for a stateful protocol like WebSocket. The chat application depends on inter-client communication/broadcast - that's one the main reasons for choosing WebSocket. If we scale our application to multiple instances, different clients can connect to different ones which means that the `javax.session.Session` object will remain on that node (JVM) and the `getOpenSessions` method call (for broadcast) will also return the `Session` handle to the clients on the node on which the `Session` object resides. Given that that `Session` object is the component on which we solely depend on for the broadcast capability (which in turn is the foundation of the chat application), think about the following

- How will clients connected to different nodes be able to send public messages to other clients (who are potentially) connected to other nodes ?
- How will clients connected to different nodes be able to send private messages to other clients (who are potentially) connected to other nodes ?
- How will clients receive notifications (new joinee & logout) for events happening on other nodes ?
 
### Enough of problems! 

A (potential) solution is to have the notification and messaging (broadcasting in general) to be handled at a different layer. A **Hazelcast distributed Topic** has been introduced and here is how it helps

- it acts as a **central event bus** to receive and route broadcasts (chat messages, notifications)
- There are different topics for various features - chat, new joinee notifications, logout notifications (this can be solved in a different way but a dedicated topic/feature was chosen for ease of demonstration)
- the internal `ChatServer` logic delegates to this topic rather than directly interacting with `Session` to broadcast to associated clients - it does so by **publishing** events to it
- there are associated **topic listeners** for each topic (on every node) which react to published events and execute the actual broadcasting feature

> A note on High availability & fault tolerance
> It's very important to understand that this does not help with HA of the WebSocket sessions/connections. As mentioned earlier, a `Session` object is not `Serializable` - hence a client once disconnected, has to connect again. It is possible to partially implement HA/fault tolerant session management - but that's not in scope for this project  

## Code

Before you explore the source code yourself, here is quick overview

|Class(es)|Category|Description|
|---------|--------|-----------|		
|`ChatServer`|Core|It contains the core business logic of the application|
|`WebSocketServerManager`|Bootstrap|Manages bootstrap and shutdown process of the WebSocket container|
|`ChatMessage`,<br>`DuplicateUserNotification`,<br>`LogOutNotification`,<br>`NewJoineeNotification`,<br>`Reply`,<br>`WelcomeMessage`|Domain objects|Simple POJOs to model the application level entities|
|`ChatMessageDecoder`|Decoder|Converts chats sent by users into Java (domain) object which can be used within the application|
|`DuplicateUserMessageEncoder`<br>,`LogOutMessageEncoder`,<br>`NewJoineeMessageEncoder`,<br>`ReplyEncoder`,<br>`WelcomeMessageEncoder`|Encoder(s)|Converts Java (domain) objects into native (text) payloads which can be sent over the wire using the WebSocket protocol|
|`ChatEventBus`|Events|Central place for handling Topic based inter-node communication|
|`ChatMessageEventListener`<br>,`LogoutNotificationEventListener`,<br>`NewJoineeNotificationEventListener`|Events|Act as topic listeners (to events) and execute broadcasting logic in the implementation|


## Try it out

You would need a WebSocket client for this example - try the [Simple WebSocket Client](https://chrome.google.com/webstore/detail/simple-websocket-client/pfdhoblngboilpfeibdedpjgfnlcodoo?hl=en) which is a Chrome browser plugin. Here is a transcript

- Start 3 instances of the application (on ports 8080, 8081, 8082)
- Users **foo** and **bar** join the chatroom. To do so, you need to connect to the WebSocket endpoint URL e.g. `ws://localhost:8080/chat/foo/` and `ws://localhost:8081/chat/bar/`. **foo** gets notified about **bar**
- User **john** joins (`ws://localhost:8082/chat/john/`). **foo** and **bar** are notified
- **foo** sends a message to everyone (public). Both **bar** and **john** get the message
- **bar** sends a private message to **foo**. Only **foo** gets it
- In the meanwhile, **john** gets bored and decides to leave the chat room. Both **foo** and **bar** get notified