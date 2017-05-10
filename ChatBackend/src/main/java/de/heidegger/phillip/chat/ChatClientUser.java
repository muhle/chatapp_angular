package de.heidegger.phillip.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.heidegger.phillip.chat.events.userEvents.UserRegistered;
import de.heidegger.phillip.utils.EMail;
import de.heidegger.phillip.utils.Sha1Hash;

public class ChatClientUser {

    // Primary key
    private final EMail email;

    private final String name;
    private final Sha1Hash sha1PWHash;

    public ChatClientUser(@JsonProperty("email") EMail email, @JsonProperty("name") String name,
                          @JsonProperty("sha1PWHash") Sha1Hash sha1PWHash) {
        if (name == null || email == null || sha1PWHash == null) {
            throw new NullPointerException();
        }
        this.name = name;
        this.email = email;
        this.sha1PWHash = sha1PWHash;
    }

    public String getName() {
        return name;
    }

    public EMail getEmail() {
        return email;
    }

    public Sha1Hash getsha1PWHash() {
        return sha1PWHash;
    }

    public boolean matches(UserRegistered ure) {
        return (email.equals(ure.getEMail()) && name.equals(ure.getName()) && sha1PWHash.equals
                (ure.getSha1PWHash()));
    }

}
