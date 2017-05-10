package de.heidegger.phillip.chat.commands.userCommands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.heidegger.phillip.chat.ChatClient;
import de.heidegger.phillip.chat.ChatClientUser;
import de.heidegger.phillip.chat.ChatServerManager;
import de.heidegger.phillip.chat.commands.Command;
import de.heidegger.phillip.chat.commands.NoAuthCheck;
import de.heidegger.phillip.chat.events.userEvents.LoggedIn;
import de.heidegger.phillip.events.CommandMarker;
import de.heidegger.phillip.utils.EMail;

import java.util.logging.Level;

@CommandMarker
@NoAuthCheck
public class Login implements Command {
    private final EMail email;
    private final String password;

    @JsonCreator
    public Login(@JsonProperty("email") EMail email, @JsonProperty("password") String password) {
        this.email = email;
        this.password = password;
    }

    public String toString() {
        return "Login of '" + this.email + "' with '" + this.password + "'";
    }


    public String getPassword() {
        return password;
    }

    public EMail getEmail() {
        return email;
    }

    @Override
    public void execute(ChatClient client, ChatServerManager manager) {
        if (!manager.login(getEmail(), getPassword())) {
            //client.sendData(toJsonMessageObject("LOGIN FAILED."));
            return;
        }

        // If the client provides correct login credentials, but is already
        // authenticated, we have to switch the client to the new identity (maybe)
        if (client.isAuthenticated()) {
            // Same identity, login is a NOOP. But create log entry, a correctly implemented
            // client should never login if the socket is already promoted
            if (getEmail().equals(client.getEmail())) {
                manager.log(Level.WARNING, "User " + getEmail() + " already " +
                        "logged in. No need to login again.");
                return;
            } else {
                new Logout().execute(client, manager);
            }
        }
        manager.log(Level.INFO, "User " + getEmail() + " logged in.");
        ChatClientUser chatClientUser = manager.getUser(getEmail());

        manager.promoteClient(client, getEmail(), chatClientUser.getName());
        LoggedIn loggedIn = new LoggedIn(client.getId(), client.getUserName(), client.getEmail());
        manager.spread(loggedIn);
        manager.informUserAboutrooms(client);
    }

    private static String toJsonMessageObject(String msg) {
        return "{\"msg\": \"" + msg.replace("\"", "\\\"") + "\"}";
    }}

