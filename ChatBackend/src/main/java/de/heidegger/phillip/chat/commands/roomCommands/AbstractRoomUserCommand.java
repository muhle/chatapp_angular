package de.heidegger.phillip.chat.commands.roomCommands;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.heidegger.phillip.utils.EMail;

public abstract class AbstractRoomUserCommand extends AbstractRoomCommand {
    protected final EMail email;

    public AbstractRoomUserCommand(String roomName, @JsonProperty("email") EMail eMail) {
        super(roomName);
        this.email = eMail;
    }
}
