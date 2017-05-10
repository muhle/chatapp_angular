package de.heidegger.phillip.chat.commands.userCommands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.heidegger.phillip.chat.ChatClient;
import de.heidegger.phillip.chat.ChatClientUser;
import de.heidegger.phillip.chat.ChatServerManager;
import de.heidegger.phillip.chat.commands.Command;
import de.heidegger.phillip.chat.events.userEvents.ChangedUserPassword;
import de.heidegger.phillip.events.CommandMarker;
import de.heidegger.phillip.utils.EMail;
import de.heidegger.phillip.utils.Sha1Hash;

@CommandMarker
public class ChangeUserPassword implements Command {
    private final EMail email;
    private final Sha1Hash oldSha1PWHash;
    private final Sha1Hash newSha1PWHash;

    @JsonCreator
    public ChangeUserPassword(@JsonProperty("email")EMail email,
                              @JsonProperty("oldPassword") String oldPassword,
                              @JsonProperty("newPassword") String newPassword) {
        this.email = email;
        this.oldSha1PWHash = Sha1Hash.createFromValue(oldPassword);
        this.newSha1PWHash = Sha1Hash.createFromValue(newPassword);
    }

    public EMail getEmail() {
        return email;
    }

    public Sha1Hash getOldSha1PWHash() {
        return oldSha1PWHash;
    }

    public Sha1Hash getNewSha1PWHash() {
        return newSha1PWHash;
    }

    @Override
    public void execute(ChatClient chatClient, ChatServerManager manager) {
        ChatClientUser chatClientUser = manager.getUser(email);
        if (chatClientUser.getsha1PWHash().equals(oldSha1PWHash)) {
            manager.spread(new ChangedUserPassword(email, newSha1PWHash));
            manager.saveUserViewSnapshot();
        }
    }
}
