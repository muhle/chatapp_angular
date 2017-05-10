package de.heidegger.phillip.events;

public enum Permission {
    DENY, ALLOW, NO_PERMISSION;

    public static Permission combine(Permission permission, Permission permission1) {
        if (permission == DENY || permission1 == DENY) {
            return DENY;
        }
        if (permission == ALLOW || permission1 == ALLOW) {
            return ALLOW;
        }
        return NO_PERMISSION;
    }
}
