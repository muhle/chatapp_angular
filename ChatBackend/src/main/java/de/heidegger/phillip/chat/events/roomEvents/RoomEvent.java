package de.heidegger.phillip.chat.events.roomEvents;

import de.heidegger.phillip.chat.events.Event;

public interface RoomEvent extends Event {
    String getRoomName();
}
