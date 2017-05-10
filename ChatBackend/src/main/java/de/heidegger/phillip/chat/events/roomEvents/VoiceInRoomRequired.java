package de.heidegger.phillip.chat.events.roomEvents;

public class VoiceInRoomRequired extends AbstractRoomEvent {

    private final boolean voice;

    public VoiceInRoomRequired(String roomName, boolean voice) {
        super(roomName);
        this.voice = voice;
    }

    public boolean getVoice() {
        return voice;
    }
}
