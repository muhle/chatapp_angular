package de.heidegger.phillip.chat.commands.roomCommands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.heidegger.phillip.chat.ChatClient;
import de.heidegger.phillip.chat.ChatServerManager;
import de.heidegger.phillip.chat.events.roomEvents.VoiceInRoomRequired;
import de.heidegger.phillip.events.CommandMarker;

@CommandMarker
@RequireOp
public class SetVoiceRoom extends AbstractRoomCommand {
    private final boolean voice;

    @JsonCreator
    public SetVoiceRoom(@JsonProperty("roomName") String roomName,
                        @JsonProperty("voice") boolean voice) {
        super(roomName);
        this.voice = voice;
    }

    @Override
    public void executeWhenAllowed(ChatClient chatClient, ChatServerManager manager) {
        manager.spread(new VoiceInRoomRequired(roomName, voice));
    }
}
