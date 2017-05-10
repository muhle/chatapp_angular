package de.heidegger.phillip.chat.commands.roomCommands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.heidegger.phillip.chat.ChatClient;
import de.heidegger.phillip.chat.ChatServerManager;
import de.heidegger.phillip.chat.events.roomEvents.OpGranted;
import de.heidegger.phillip.events.CommandMarker;
import de.heidegger.phillip.utils.EMail;

@CommandMarker
@RequireOp
public class GrantOp extends AbstractRoomUserCommand {

    private final boolean op;

    @JsonCreator
    public GrantOp(@JsonProperty("roomName") String roomName, @JsonProperty("email") EMail eMail,
                   @JsonProperty("op") boolean op) {
        super(roomName, eMail);
        this.op = op;
    }

    @Override
    public void executeWhenAllowed(ChatClient chatClient, ChatServerManager manager) {
        manager.spread(new OpGranted(roomName, email, op));
    }
}
