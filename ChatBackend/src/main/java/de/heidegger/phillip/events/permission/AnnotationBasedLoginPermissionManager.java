package de.heidegger.phillip.events.permission;

import de.heidegger.phillip.events.EventHandler;
import de.heidegger.phillip.events.Permission;

import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

public class AnnotationBasedLoginPermissionManager implements EventPermissionManager, EventHandler {
    private static final Logger logger = Logger.getLogger(AnnotationBasedLoginPermissionManager.class.getSimpleName());
    static {
        logger.addHandler(new StreamHandler(System.out, new SimpleFormatter()));
    }
    private boolean authenticated = false;
    private String userName = null;

    @Override
    public Permission checkPermission(Object event) {


        return Permission.ALLOW;
    }

    @Override
    public boolean handle(Object event) {
        return true;
    }
}
