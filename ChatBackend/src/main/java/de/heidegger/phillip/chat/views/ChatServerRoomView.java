package de.heidegger.phillip.chat.views;

import de.heidegger.phillip.chat.ChatClientUser;
import de.heidegger.phillip.chat.ChatServerManager;
import de.heidegger.phillip.chat.events.Event;
import de.heidegger.phillip.chat.events.roomEvents.*;
import de.heidegger.phillip.utils.EMail;

import java.util.*;

/**
 * A view that manages the rooms. This includes:
 * - Which useres are currently in the room
 * - Which users do have operator or voice
 * - Does the room require voice to speak
 * - Which users are invited to a room
 * - Does the room require an invitation to join the room
 *
 * Currently the view is not persistent. Hence, each restart of the server results in
 * an new empty set of rooms.
 */
public class ChatServerRoomView implements EventView {

    private final ChatServerUserView chatServerUserView;
    private final Map<String, ChatRoom> rooms = new HashMap<>();
    private final ChatServerManager chatServerManager;

    public ChatServerRoomView(ChatServerManager chatServerManager, ChatServerUserView chatServerUserView) {
        this.chatServerManager = chatServerManager;
        this.chatServerUserView = chatServerUserView;
    }

    @Override
    public boolean applied(Event event) {
        return false;
    }

    @Override
    public void consume(Event event) {
        if (event instanceof RoomEvent) {
            this.consume((RoomEvent) event);
        }
    }

    public void consume(RoomEvent roomEvent) {
        final String roomName = roomEvent.getRoomName();
        if (roomEvent instanceof RoomJoined) {
            RoomJoined roomJoined = (RoomJoined) roomEvent;

            final boolean makeOp;
            final boolean giveVoice;
            ChatRoom room;
            if (!rooms.containsKey(roomName)) {
                room = new ChatRoom(roomName);
                rooms.put(roomName, room);
                makeOp = giveVoice = true;
            } else {
                room = rooms.get(roomName);
                makeOp = room.isEmpty();
                giveVoice = room.isEmpty();
            }
            ChatClientUser client = chatServerUserView.getUser(roomJoined.getEmail());
            if (room.joinRoom(client)) {
                if (makeOp) {
                    chatServerManager.spread(new OpGranted(roomName, client.getEmail(), true));
                }
                if (giveVoice) {
                    chatServerManager.spread(new VoiceGranted(roomName, client.getEmail(), true));
                }
            }
        }
        ChatRoom room = rooms.get(roomName);
        if (room != null) {
            if (roomEvent instanceof RoomLeft) {
                RoomLeft roomLeft = (RoomLeft) roomEvent;
                room.leaveRoom(chatServerUserView.getUser(roomLeft.getEmail()));
            }
            if (roomEvent instanceof InvitedOfRoomRequired) {
                InvitedOfRoomRequired invitedOfRoomRequired = (InvitedOfRoomRequired) roomEvent;
                room.setRequiresInvite(invitedOfRoomRequired.isInviteRequired());
            }
            if (roomEvent instanceof VoiceInRoomRequired) {
                VoiceInRoomRequired voiceInRoomRequired = (VoiceInRoomRequired) roomEvent;
                room.setRequiresVoice(voiceInRoomRequired.getVoice());
            }
            if (roomEvent instanceof OpGranted) {
                OpGranted opGranted = (OpGranted) roomEvent;
                room.giveOp(chatServerUserView.getUser(opGranted.getEMail()), opGranted.getOp());
            }
            if (roomEvent instanceof VoiceGranted) {
                VoiceGranted voiceGranted = (VoiceGranted) roomEvent;
                room.giveVoice(chatServerUserView.getUser(voiceGranted.getEMail()), voiceGranted.getVoice());
            }
        }
    }

    public boolean joinRoomCheck(ChatClientUser chatClientUser, String roomName) {
        ChatRoom chatRoom = rooms.get(roomName);
        return chatRoom == null || chatRoom.joinRoomCheck(chatClientUser);
    }

    public boolean leaveRoomCheck(ChatClientUser chatClientUser, String roomName) {
        ChatRoom chatRoom = rooms.get(roomName);
        return chatRoom != null && chatRoom.isInRoom(chatClientUser);
    }
    public List<Event> createRoomInformation(String roomName) {
        ChatRoom chatRoom = rooms.get(roomName);
        if (chatRoom == null) {
            return Collections.EMPTY_LIST;
        }
        return chatRoom.createRoomInformation();
    }


    public List<String> leaveAllRooms(ChatClientUser chatClientUser) {
        List<String> leaveEvents = new ArrayList<>();
        rooms.values().forEach(r -> {
            if (r.isInRoom(chatClientUser)) {
                leaveEvents.add(r.getName());
            };
        });
        return leaveEvents;
    }

    public boolean canSpeak(ChatClientUser chatClientUser, String roomName) {
        ChatRoom chatRoom = rooms.get(roomName);
        return chatRoom != null && chatRoom.canSpeak(chatClientUser);
    }

    public List<Event> findRooms(EMail email) {
        if (email == null) {
            return Collections.EMPTY_LIST;
        }
        List<Event> events = new ArrayList<>();
        rooms.values().forEach(r -> {
            if (r.isInRoom(email)) {
                events.addAll(createRoomInformation(r.getName()));
            }
        });
        return events;
    }

    public boolean hasOp(ChatClientUser chatClientUser, String roomName) {
        ChatRoom chatRoom = rooms.get(roomName);
        return chatRoom != null && chatRoom.isOp(chatClientUser);
    }
}
