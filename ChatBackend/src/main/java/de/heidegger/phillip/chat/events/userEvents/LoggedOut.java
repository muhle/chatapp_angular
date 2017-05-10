package de.heidegger.phillip.chat.events.userEvents;

import de.heidegger.phillip.chat.events.Event;
import de.heidegger.phillip.events.EventMarker;
import de.heidegger.phillip.utils.EMail;

@EventMarker
public class LoggedOut implements Event {
    private final long id;
    private final EMail email;

    public LoggedOut(long id, EMail email) {
        this.id = id;
        this.email = email;
    }

    public EMail getEmail() {
        return email;
    }

    public long getId() {
        return id;
    }
}
