package de.heidegger.phillip.chat.events.roomEvents;


import com.fasterxml.jackson.annotation.JsonProperty;
import de.heidegger.phillip.events.EventMarker;
import de.heidegger.phillip.utils.EMail;

@EventMarker
public class RoomLeft extends AbstractRoomEvent {
    private final EMail email;

    public RoomLeft(@JsonProperty String roomName, @JsonProperty EMail email) {
        super(roomName);
        this.email = email;
    }

    public EMail getEmail() {
        return email;
    }
}
