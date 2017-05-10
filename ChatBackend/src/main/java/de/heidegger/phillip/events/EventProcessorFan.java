package de.heidegger.phillip.events;

import de.heidegger.phillip.events.permission.EventPermissionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

/**
 * The job of an EventProcessorFan is to process Events. It
 * It has special treatment for Login and Logout
 * by managing the currently loggedin user.
 *
 * It filters out all Events that requires a logged in user
 * when no user is logged in.
 *
 * It also will filter our all events the user does not have
 * right to perform.
 */
public class EventProcessorFan implements EventHandler {
    private static final Logger logger = Logger.getLogger(EventProcessorFan.class.getSimpleName());
    static {
        logger.addHandler(new StreamHandler(System.out, new SimpleFormatter()));
    }
    private final List<EventHandler> handlerList = new ArrayList<>();
    private final List<EventPermissionManager> permissionManagers = new ArrayList<>();

    public void addEventHandler(EventHandler eventHandler) {
        handlerList.add(eventHandler);
    }

    public void addPermissionManager(EventPermissionManager eventPermissionManager) {
        this.permissionManagers.add(eventPermissionManager);
    }

    public boolean handle(Object event) {
        Permission permission = this.permissionManagers.stream()
                .map(epm -> epm.checkPermission(event)).reduce(Permission.NO_PERMISSION, Permission::combine);
        if (permission == Permission.DENY || permission == Permission.NO_PERMISSION) {
            logger.log(Level.INFO, "No permission to process event" + event);
            return true;
        }

        return this.handlerList.stream().anyMatch(h -> h.handle(event));
    }

}
