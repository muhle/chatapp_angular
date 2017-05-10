package de.heidegger.phillip.chat.events;

import de.heidegger.phillip.events.EventMarker;

@EventMarker
public class SocketIdEvent {
    private final long id;

    public SocketIdEvent(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
