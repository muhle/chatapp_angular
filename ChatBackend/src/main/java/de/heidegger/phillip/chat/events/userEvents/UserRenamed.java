package de.heidegger.phillip.chat.events.userEvents;

import de.heidegger.phillip.events.EventMarker;
import de.heidegger.phillip.utils.EMail;

@EventMarker
public class UserRenamed implements UserEvent {
    private final EMail email;
    private final String name;

    public UserRenamed(String name, EMail email) {
        this.email = email;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public EMail getEmail() {
        return email;
    }
}
