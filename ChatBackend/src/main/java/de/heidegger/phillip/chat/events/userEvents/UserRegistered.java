package de.heidegger.phillip.chat.events.userEvents;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.heidegger.phillip.chat.events.userEvents.UserEvent;
import de.heidegger.phillip.events.EventMarker;
import de.heidegger.phillip.utils.EMail;
import de.heidegger.phillip.utils.Sha1Hash;

@EventMarker
public class UserRegistered implements UserEvent {
    private final String name;
    private final EMail email;
    private final Sha1Hash sha1PWHash;

    @JsonCreator
    public UserRegistered(@JsonProperty("email") EMail email,
                          @JsonProperty("name") String name,
                          @JsonProperty("sha1PWHash") String sha1PWHash) {
        this.name = name;
        this.email = email;
        this.sha1PWHash = Sha1Hash.createFromHash(sha1PWHash);
    }

    public UserRegistered(EMail email, String name, Sha1Hash sha1PWHash) {
        this.name = name;
        this.email = email;
        this.sha1PWHash = sha1PWHash;
    }


    public String getName() {
        return name;
    }

    public EMail getEMail() {
        return email;
    }

    public Sha1Hash getSha1PWHash() {
        return sha1PWHash;
    }
}
