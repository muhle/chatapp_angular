package de.heidegger.phillip.chat;

import de.heidegger.phillip.chat.commands.Command;
import de.heidegger.phillip.chat.commands.NoAuthCheck;
import de.heidegger.phillip.chat.commands.roomCommands.*;
import de.heidegger.phillip.chat.commands.userCommands.*;
import de.heidegger.phillip.chat.events.Event;
import de.heidegger.phillip.chat.events.roomEvents.RoomJoined;
import de.heidegger.phillip.chat.events.userEvents.UserEvent;
import de.heidegger.phillip.chat.views.ChatServerRoomView;
import de.heidegger.phillip.chat.views.ChatServerUserView;
import de.heidegger.phillip.chat.views.EventView;
import de.heidegger.phillip.utils.EMail;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

public class ChatServer {
    private static final Class<?>[] userCommands = {RegisterUser.class, RenameUser.class,
            ChangeUserPassword.class};
    private static final Class<?>[] loginCommands = {Login.class, Logout.class};
    private static final Class<?>[] roomCommands = {JoinRoom.class, LeaveRoom.class,
            SendMessageToRoom.class, SetInviteRoom.class, SetVoiceRoom.class,
            GrantOp.class, GrantVoice.class};
    private static final Class<?>[][] allCommands = { userCommands, loginCommands, roomCommands };

    private static final Logger logger = Logger.getLogger(ChatServer.class.getSimpleName());
    static {
        logger.addHandler(new StreamHandler(System.out, new SimpleFormatter()));
    }
    private static final Path userFile = Paths.get("users.json");
    private static AtomicLong nextFreeClientId = new AtomicLong();

    private final ChatServerManager manager = new MyChatServerManager();

    // contains all users known to the system with their mail, name and sha1PasswordHash
    private final ChatServerUserView chatServerUserView = new ChatServerUserView(userFile);

    private final ChatServerRoomView chatServerRoomView = new ChatServerRoomView(manager,
            chatServerUserView);

    // contains the currently connected users. These instances also contain the WebSocket
    // objects, so that we can deliver events to them.
    private final Map<Long, ChatClient> clients = new ConcurrentHashMap<>();

    // This contains a list of views created based on the events that were emitted.
    // Most events have a command as an origin, e.g. a client is sending a command
    // to the server. Task of a view is to take events and build data structures out
    // of them to represent the current state of affairs with respect to the focus of the
    // view.
    private final List<EventView> views = new ArrayList<>();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ChatServer() {
        this.views.add(chatServerUserView);
        this.views.add(chatServerRoomView);
    }

    public ChatClient createClientInstance() {
        long nextFreeId = nextFreeClientId.getAndIncrement();
        ChatClient chatClient = new ChatClient(this, nextFreeId);
        this.views.add(chatClient);
        this.clients.put(nextFreeId, chatClient);
        logger.log(Level.INFO, "Create client instance with id: " + chatClient.getId());
        return chatClient;
    }

    public void destroyClientInstance(ChatClient chatClient) {
        logger.log(Level.INFO, "Mark client as dead, WebSocket is closed." + chatClient.getId());
        // Remove first, because the session is already removed from the object
        // Hence, we cannot send stuff to the client anymore.
        executorService.submit(() -> {
            logger.log(Level.INFO, "Remove client from Server, WebSocket is closed." + chatClient.getId());
            ChatClientUser chatClientUser = chatServerUserView.getUser(chatClient.getEmail());
            this.views.remove(chatClient);
            this.clients.remove(chatClient.getId());

            // Client is disconnected from the other objects. Now, let them know that the client left.
            chatServerRoomView.leaveAllRooms(chatClientUser).forEach(roomName -> {
                new LeaveRoom(roomName).execute(chatClient, manager);
            });
            if (chatClient.isAuthenticated()) {
                new Logout().execute(chatClient, manager);
            }
        });
    }


    /**
     * Processes the command by checking if the user has the permissions
     * to perform it. If the user has, an event is generated to protocol
     * the command was performed. Also, the side effects are executed.
     *
     * The implementation of this method delegates the commands to methods that
     * take care of the different kind of commands.
     *
     * @param clientId The id of the client that wants to execute the command.
     * @param clientCommand The command itself.
     * @return If the command was rejected, false is returned.
     *         If the command is executed, true is returned.
     */
    public boolean handleClientCommands(long clientId, Object clientCommand) {
        return clientCommand instanceof Command
            && handleClientCommands(clientId, (Command) clientCommand);
    }

    private boolean handleClientCommands(long clientId, Command command) {
        if (clients.containsKey(clientId)) {
            ChatClient client = clients.get(clientId);
            for (Class<?>[] commands: allCommands) {
                if (handleCommands(commands, client, command))
                    return true;
            }
        }
        return false;
    }

    public boolean handleCommands(Class<?>[] commands, ChatClient client, Command command) {
        for (Class<?> c  : commands) {
            if (c.isInstance(command)) {
                if (c.isAnnotationPresent(NoAuthCheck.class) || client.isAuthenticated()) {
                    executorService.submit(() -> {
                        command.execute(client, manager);
                    });
                    return true;
                }
            }
        }
        return false;
    }


    private static String toJsonMessageObject(String msg) {
        return "{\"msg\": \"" + msg.replace("\"", "\\\"") + "\"}";
    }

    public void log(Level level, String msg, Throwable t) {
        logger.log(level, msg, t);
    }

    public void log(Level level, String s) {
        logger.log(level, s);
    }

    private class MyChatServerManager implements ChatServerManager {

        @Override
        public void spread(Event event) {
            views.forEach(v -> executorService.submit(() -> v.consume(event)));
        }

        @Override
        public void log(Level info, String s) {
            logger.log(info, s);
        }

        @Override
        public boolean login(EMail email, String password) {
            return chatServerUserView.login(email, password);
        }

        @Override
        public void promoteClient(ChatClient client, EMail email, String name) {
            client.promote(email, name);
        }

        @Override
        public ChatClientUser getUser(EMail email) {
            return chatServerUserView.getUser(email);
        }

        @Override
        public boolean applied(UserEvent usre) {
            return chatServerUserView.applied(usre);
        }

        @Override
        public void saveUserViewSnapshot() {
            System.out.println("Save users to file");
            chatServerUserView.save(userFile);
        }

        @Override
        public boolean leaveRoomCheck(ChatClient chatClient, String roomName) {
            ChatClientUser chatClientUser = chatServerUserView.getUser(chatClient.getEmail());
            return chatServerRoomView.leaveRoomCheck(chatClientUser, roomName);
        }

        @Override
        public boolean canSpeak(ChatClient chatClient, String roomName) {
            ChatClientUser chatClientUser = chatServerUserView.getUser(chatClient.getEmail());
            return chatServerRoomView.canSpeak(chatClientUser, roomName);
        }

        @Override
        public void informUserAboutrooms(ChatClient client) {
            chatServerRoomView.findRooms(client.getEmail()).forEach(client::sendEvent);
        }

        @Override
        public boolean hasOp(ChatClient chatClient, String roomName) {
            ChatClientUser chatClientUser = chatServerUserView.getUser(chatClient.getEmail());
            return chatServerRoomView.hasOp(chatClientUser, roomName);
        }

        @Override
        public boolean joinRoomCheck(ChatClient chatClient, String roomName) {
            ChatClientUser chatClientUser = chatServerUserView.getUser(chatClient.getEmail());
            return chatServerRoomView.joinRoomCheck(chatClientUser, roomName);
        }

        @Override
        public void informUserAboutRoom(ChatClient chatClient, String roomName) {
            chatServerRoomView.createRoomInformation(roomName).stream()
                .filter(e -> {
                    if (e instanceof RoomJoined) {
                        RoomJoined roomJoined = (RoomJoined) e;
                        return !roomJoined.getEmail().equals(chatClient.getEmail());
                    }
                    return true;
                }).forEach(e -> executorService.submit(() -> {
                    chatClient.sendEvent(e);
            }));
        }
    }
}
