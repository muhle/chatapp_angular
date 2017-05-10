package de.heidegger.phillip.chat.events.roomEvents;

import de.heidegger.phillip.events.EventMarker;
import de.heidegger.phillip.utils.EMail;

@EventMarker
public class VoiceGranted extends AbstractRoomEvent {
    private final boolean voice;
    private final EMail eMail;

    public VoiceGranted(String roomName, EMail eMail, boolean voice) {
        super(roomName);
        this.voice = voice;
        this.eMail = eMail;
    }

    public boolean getVoice() {
        return voice;
    }

    public EMail getEMail() {
        return eMail;
    }
}
