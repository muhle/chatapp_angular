package de.heidegger.phillip.chat.commands.userCommands;

import de.heidegger.phillip.chat.ChatClient;
import de.heidegger.phillip.chat.ChatServerManager;
import de.heidegger.phillip.chat.commands.Command;
import de.heidegger.phillip.chat.commands.NoAuthCheck;
import de.heidegger.phillip.chat.events.userEvents.UserRegistered;
import de.heidegger.phillip.events.CommandMarker;
import de.heidegger.phillip.utils.EMail;
import de.heidegger.phillip.utils.Sha1Hash;

@CommandMarker
@NoAuthCheck
public class RegisterUser implements Command {
    private final String email;
    private final String name;
    private final String password;

    public RegisterUser(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public void execute(ChatClient client, ChatServerManager manager) {
        // register user commands does not require privileges, so just
        // convert them to an event and apply them
        UserRegistered usre = new UserRegistered(new EMail(email), name, Sha1Hash.createFromValue
                (password));
        manager.spread(usre);

        // next, lets see if it was applied to the chatServerUserView. If it was
        // the mail address was free, and a new user was created. In this case
        // it makes sense to save a new snapshot of the user list to hard drive.
        // In a more sophisticated event based system, this should happen in a
        // background thread on a regular bases.
        if (manager.applied(usre)) {
            manager.saveUserViewSnapshot();
        }
    }
}
