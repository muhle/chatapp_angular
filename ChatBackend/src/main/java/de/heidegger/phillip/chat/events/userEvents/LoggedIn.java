package de.heidegger.phillip.chat.events.userEvents;

import de.heidegger.phillip.events.EventMarker;
import de.heidegger.phillip.utils.EMail;

@EventMarker
public class LoggedIn implements UserEvent {
    private final long id;
    private final String name;
    private final EMail email;

    public LoggedIn(long id, String name, EMail email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public EMail getEmail() {
        return email;
    }

    public long getId() {
        return id;
    }
}
