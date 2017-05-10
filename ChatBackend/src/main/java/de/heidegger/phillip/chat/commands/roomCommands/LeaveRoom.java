package de.heidegger.phillip.chat.commands.roomCommands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.heidegger.phillip.chat.ChatClient;
import de.heidegger.phillip.chat.ChatServerManager;
import de.heidegger.phillip.chat.events.roomEvents.RoomLeft;
import de.heidegger.phillip.events.CommandMarker;

@CommandMarker
public class LeaveRoom extends AbstractRoomCommand {

    @JsonCreator
    public LeaveRoom(@JsonProperty("roomName") String roomName) {
        super(roomName);
    }

    @Override
    public void executeWhenAllowed(ChatClient chatClient, ChatServerManager manager) {
        if (manager.leaveRoomCheck(chatClient, roomName)) {
            manager.spread(new RoomLeft(roomName, chatClient.getEmail()));
        };

    }
}
