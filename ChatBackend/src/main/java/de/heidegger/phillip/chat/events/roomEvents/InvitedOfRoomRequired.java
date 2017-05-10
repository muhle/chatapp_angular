package de.heidegger.phillip.chat.events.roomEvents;

import de.heidegger.phillip.events.EventMarker;

@EventMarker
public class InvitedOfRoomRequired extends AbstractRoomEvent {

    private final boolean inviteRequired;

    public InvitedOfRoomRequired(String roomName, boolean inviteRequired) {
        super(roomName);
        this.inviteRequired = inviteRequired;
    }

    public boolean isInviteRequired() {
        return inviteRequired;
    }
}
