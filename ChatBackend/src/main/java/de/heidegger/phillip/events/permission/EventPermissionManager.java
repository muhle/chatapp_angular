package de.heidegger.phillip.events.permission;

import de.heidegger.phillip.events.Permission;

public interface EventPermissionManager {
    Permission checkPermission(Object event);
}
