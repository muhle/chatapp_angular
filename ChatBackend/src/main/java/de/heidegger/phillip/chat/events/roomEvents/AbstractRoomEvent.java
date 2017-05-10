package de.heidegger.phillip.chat.events.roomEvents;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by phe on 5/6/2017.
 */
public class AbstractRoomEvent implements RoomEvent {
    protected final String roomName;

    public AbstractRoomEvent(@JsonProperty String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }
}
