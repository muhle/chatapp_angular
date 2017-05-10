package de.heidegger.phillip.chat.commands.roomCommands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.heidegger.phillip.chat.ChatClient;
import de.heidegger.phillip.chat.ChatServerManager;
import de.heidegger.phillip.chat.events.roomEvents.VoiceGranted;
import de.heidegger.phillip.events.CommandMarker;
import de.heidegger.phillip.utils.EMail;

@CommandMarker
@RequireOp
public class GrantVoice extends AbstractRoomUserCommand {

        private final boolean voice;

        @JsonCreator
        public GrantVoice(@JsonProperty("roomName") String roomName, @JsonProperty("email") EMail eMail,
                          @JsonProperty("voice") boolean voice) {
            super(roomName, eMail);
            this.voice = voice;
        }

        @Override
        public void executeWhenAllowed(ChatClient chatClient, ChatServerManager manager) {
            manager.spread(new VoiceGranted(roomName, email, voice));
        }
    }
