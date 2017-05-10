package de.heidegger.phillip.chat.commands.userCommands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.heidegger.phillip.chat.ChatClient;
import de.heidegger.phillip.chat.ChatServerManager;
import de.heidegger.phillip.chat.commands.Command;
import de.heidegger.phillip.chat.events.userEvents.UserRenamed;
import de.heidegger.phillip.events.CommandMarker;
import de.heidegger.phillip.utils.EMail;

@CommandMarker
public class RenameUser implements Command {
    private final EMail email;
    private final String userName;

    @JsonCreator
    public RenameUser(@JsonProperty("email") EMail email, @JsonProperty("userName") String userName) {
        this.email = email;
        this.userName = userName;
    }

    public String toString() {
        return "Change username of '" + this.email + "' to '" + this.userName + "'";
    }

    public String getUserName() {
        return this.userName;
    }

    public EMail getEmail() {
        return email;
    }

    @Override
    public void execute(ChatClient client, ChatServerManager manager) {
        UserRenamed userRenamed = new UserRenamed(getUserName(), getEmail());
        manager.spread(userRenamed);
        // client.sendEvent(userRenamed);
        manager.saveUserViewSnapshot();
    }
}
