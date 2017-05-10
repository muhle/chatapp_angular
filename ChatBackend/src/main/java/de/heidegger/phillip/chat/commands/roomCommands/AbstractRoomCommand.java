package de.heidegger.phillip.chat.commands.roomCommands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.heidegger.phillip.chat.ChatClient;
import de.heidegger.phillip.chat.ChatServerManager;
import de.heidegger.phillip.chat.commands.Command;

public abstract class AbstractRoomCommand implements Command {
    protected final String roomName;

    @JsonCreator
    public AbstractRoomCommand(@JsonProperty("roomName") String roomName) {
        this.roomName = roomName;
    }

    @Override
    public final void execute(ChatClient chatClient, ChatServerManager manager) {
        if (this.getClass().isAnnotationPresent(RequireOp.class)) {
            if (manager.hasOp(chatClient, roomName)) {
                this.executeWhenAllowed(chatClient, manager);
            }
            return ;
        }
        if (this.getClass().isAnnotationPresent(RequireVoice.class)) {
            if (manager.canSpeak(chatClient, roomName)) {
                this.executeWhenAllowed(chatClient, manager);
            }
            return ;
        }
        this.executeWhenAllowed(chatClient, manager);
    }

    public abstract void executeWhenAllowed(ChatClient chatClient, ChatServerManager manager);
}
