package de.heidegger.phillip.chat.commands;

import de.heidegger.phillip.chat.ChatClient;
import de.heidegger.phillip.chat.ChatServerManager;

public interface Command {
    void execute(ChatClient chatClient, ChatServerManager manager);
}
