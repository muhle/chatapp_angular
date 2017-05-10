package de.heidegger.phillip.chat.commands.userCommands;

import de.heidegger.phillip.chat.ChatClient;
import de.heidegger.phillip.chat.ChatServerManager;
import de.heidegger.phillip.chat.commands.Command;
import de.heidegger.phillip.chat.events.userEvents.LoggedOut;
import de.heidegger.phillip.events.CommandMarker;

import java.util.logging.Level;

@CommandMarker
public class Logout implements Command {

    @Override
    public void execute(ChatClient client, ChatServerManager manager) {
        manager.log(Level.INFO, "User " + client.getEmail() + " logged out.");
        LoggedOut loggedOut = new LoggedOut(client.getId(), client.getEmail());
        // client.sendEvent(loggedOut);
        manager.spread(loggedOut);
        client.demote();
    }

}
