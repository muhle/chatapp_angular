package de.heidegger.phillip.chat.events.roomEvents;

import de.heidegger.phillip.events.EventMarker;
import de.heidegger.phillip.utils.EMail;

@EventMarker
public class OpGranted extends AbstractRoomEvent {
    private final boolean op;
    private final EMail eMail;

    public OpGranted(String roomName, EMail eMail, boolean op) {
        super(roomName);
        this.op = op;
        this.eMail = eMail;
    }

    public boolean getOp() {
        return op;
    }

    public EMail getEMail() {
        return eMail;
    }
}
