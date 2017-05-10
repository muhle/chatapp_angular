package de.heidegger.phillip.chat.events.roomEvents;

import de.heidegger.phillip.events.EventMarker;
import de.heidegger.phillip.utils.EMail;

@EventMarker
public class MessageSendToRoom extends AbstractRoomEvent {

    private final EMail email;
    private final String message;

    public MessageSendToRoom(String roomName, EMail email, String message) {
        super(roomName);
        this.email = email;
        this.message = message;
    }

    public EMail getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }
}
