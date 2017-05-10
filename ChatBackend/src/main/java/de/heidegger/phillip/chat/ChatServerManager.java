package de.heidegger.phillip.chat;

import de.heidegger.phillip.chat.events.Event;
import de.heidegger.phillip.chat.events.userEvents.UserEvent;
import de.heidegger.phillip.utils.EMail;

import java.util.logging.Level;

public interface ChatServerManager {

    void spread(Event event);

    void log(Level info, String s);

    boolean login(EMail email, String password);

    void promoteClient(ChatClient client, EMail email, String name);

    ChatClientUser getUser(EMail email);

    boolean applied(UserEvent usre);

    /**
     * Tests if it is possible for the user to join the room.
     *
     * @param chatClient The chat client that wants to join the room.
     * @param roomName The name of the room.
     * @return if the join is possible.
     */
    boolean joinRoomCheck(ChatClient chatClient, String roomName);

    /**
     * Fires Events to the chatClient to tell the client which users
     * are is the room, etc.
     *
     * @param chatClient The user jointed the room and requires the information.
     * @param roomName The room that was joined.
     */
    void informUserAboutRoom(ChatClient chatClient, String roomName);


    @Deprecated
    void saveUserViewSnapshot();

    boolean leaveRoomCheck(ChatClient chatClient, String roomName);

    boolean canSpeak(ChatClient chatClient, String roomName);

    void informUserAboutrooms(ChatClient client);

    boolean hasOp(ChatClient chatClient, String roomName);
}
