package de.heidegger.phillip.chat.views;

import de.heidegger.phillip.chat.ChatClientSet;
import de.heidegger.phillip.chat.ChatClientUser;
import de.heidegger.phillip.chat.events.Event;
import de.heidegger.phillip.chat.events.roomEvents.RoomJoined;
import de.heidegger.phillip.utils.EMail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatRoom {

    private final String name;
    private final ChatClientSet users = new ChatClientSet();
    private final ChatClientSet ops = new ChatClientSet();
    private final ChatClientSet withVoice = new ChatClientSet();

    private boolean requiresVoice = false;
    private boolean requiresInvite = false;

    private final Set<EMail> invitedUsers = new HashSet<>();

    ChatRoom(String name) {
        this.name = name;
    }

    boolean isInRoom(ChatClientUser chatClient) {
        return users.contains(chatClient);
    }

    boolean joinRoomCheck(ChatClientUser chatClientUser) {
        if (!requiresInvite || invitedUsers.contains(chatClientUser.getEmail())) {
            return !users.contains(chatClientUser);
        }
        return false;
    }

    boolean joinRoom(ChatClientUser chatClient) {
        if (joinRoomCheck(chatClient)) {
            return users.add(chatClient);
        }
        return false;
    }

    boolean leaveRoom(ChatClientUser chatClientUser) {
        return users.remove(chatClientUser);
    }

    boolean isOp(ChatClientUser chatClientUser) {
        return ops.contains(chatClientUser);
    }

    boolean hasVoice(ChatClientUser chatClientUser) {
        return withVoice.contains(chatClientUser) || isOp(chatClientUser);
    }

    boolean giveVoice(ChatClientUser chatClientUser, boolean voice) {
        if (voice) {
            return withVoice.add(chatClientUser);
        } else {
            return withVoice.remove(chatClientUser);
        }
    }

    boolean giveOp(ChatClientUser chatClientUser, boolean op) {
        if (op) {
            return ops.add(chatClientUser);
        } else {
            return ops.remove(chatClientUser);
        }
    }

    public void inviteUser(ChatClientUser op, EMail user) {
        if (isOp(op)) {
            invitedUsers.add(user);
        } else {
            // TODO: tell the user
        }
    }

    List<Event> createRoomInformation() {
        List<Event> eventList = new ArrayList<>(users.size());
        users.forEach(u -> eventList.add(new RoomJoined(name, u.getEmail(),u.getName())));
        return eventList;
    }

    public String getName() {
        return name;
    }

    boolean canSpeak(ChatClientUser chatClientUser) {
        return isInRoom(chatClientUser) && (!requiresVoice || hasVoice(chatClientUser));
    }

    boolean isInRoom(EMail email) {
        return users.contains(email);
    }

    void setRequiresInvite(boolean invite) {
        this.requiresInvite = invite;
    }

    void setRequiresVoice(boolean requiresVoice) {
        this.requiresVoice = requiresVoice;
    }

    boolean isEmpty() {
        return users.isEmpty();
    }
}
