package de.heidegger.phillip.chat.events.userEvents;


import de.heidegger.phillip.events.EventMarker;
import de.heidegger.phillip.utils.EMail;
import de.heidegger.phillip.utils.Sha1Hash;

@EventMarker
public class ChangedUserPassword implements UserEvent {
    private final EMail email;
    private final Sha1Hash newSha1PWHash;


    public ChangedUserPassword(EMail email, Sha1Hash newSha1PWHash) {
        this.email = email;
        this.newSha1PWHash = newSha1PWHash;
    }

    public EMail getEmail() {
        return email;
    }

    public Sha1Hash getNewSha1PWHash() {
        return newSha1PWHash;
    }
}
