package de.heidegger.phillip.chat.events.roomEvents;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.heidegger.phillip.events.EventMarker;
import de.heidegger.phillip.utils.EMail;

@EventMarker
public class RoomJoined extends AbstractRoomEvent {
    private final EMail email;
    private final String name;

    @JsonCreator
    public RoomJoined(@JsonProperty String roomName, @JsonProperty EMail email,
                      @JsonProperty String name) {
        super(roomName);
        this.email = email;
        this.name = name;
    }

    public EMail getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

}
