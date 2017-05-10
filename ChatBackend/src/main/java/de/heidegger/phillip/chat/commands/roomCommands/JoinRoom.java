package de.heidegger.phillip.chat.commands.roomCommands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.heidegger.phillip.chat.ChatClient;
import de.heidegger.phillip.chat.ChatServerManager;
import de.heidegger.phillip.chat.events.roomEvents.RoomJoined;
import de.heidegger.phillip.events.CommandMarker;

@CommandMarker
public class JoinRoom extends AbstractRoomCommand {

    @JsonCreator
    public JoinRoom(@JsonProperty("roomName") String roomName) {
        super(roomName);
    }

    @Override
    public void executeWhenAllowed(ChatClient chatClient, ChatServerManager manager) {
        if (manager.joinRoomCheck(chatClient, roomName)) {
            manager.spread(new RoomJoined(roomName, chatClient.getEmail(),
                    chatClient.getUserName()));
            manager.informUserAboutRoom(chatClient, roomName);
        };
    }
}
