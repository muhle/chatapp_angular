package de.heidegger.phillip.chat;

import de.heidegger.phillip.utils.EMail;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ChatClientSet implements Iterable<ChatClientUser> {
    private final Set<ChatClientUser> users = new HashSet<>();

    public boolean contains(ChatClientUser chatClientUser) {
        return users.contains(chatClientUser);
    }

    public boolean add(ChatClientUser chatClientUser) {
        return users.add(chatClientUser);
    }

    public boolean remove(ChatClientUser chatClientUser) {
        return users.remove(chatClientUser);
    }

    @Override
    public Iterator<ChatClientUser> iterator() {
        return users.iterator();
    }

    public int size() {
        return users.size();
    }

    public boolean contains(EMail email) {
        return users.stream().anyMatch(chatClientUser -> email.equals(chatClientUser.getEmail()));
    }

    public boolean isEmpty() {
        return users.isEmpty();
    }
}
