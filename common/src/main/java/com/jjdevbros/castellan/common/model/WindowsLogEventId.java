package com.jjdevbros.castellan.common.model;

import org.apache.commons.lang3.tuple.Pair;

public enum WindowsLogEventId {
    //User successfully logs into the machine
    LOG_IN(Pair.of("Login", 4648)),

    //User logs off out of the machine
    LOG_OUT(Pair.of("Logout", 4647)),

    //User locks the machine explicitly
    SCREEN_LOCK(Pair.of("ScreenLock", 4800)),

    //User unlocks the machine explicitly
    SCREEN_UNLOCK(Pair.of("ScreenUnlock", 4801)),

    //Inactivity due to keyboard / mouse not moved
    SCREENSAVER_ACTIVE(Pair.of("ScreenSaverActive", 4802)),

    //Transition from inactivity to activity
    SCREENSAVER_INACTIVE(Pair.of("ScreenSaverInactive", 4803)),

    //User gets disconnected from the network
    NETWORK_DISCONNECTED(Pair.of("NetworkDisconnected", 10001)),

    //User gets connected from the network
    NETWORK_CONNECTED(Pair.of("NetworkConnected", 100000)),

    //User shuts down the machine
    SHUTDOWN(Pair.of("Shutdown", 1074));

    private final Pair<String, Integer> tuple;

    WindowsLogEventId(Pair<String, Integer> tuple) {
        this.tuple = tuple;
    }

    public int getCode() {
        return tuple.getRight();
    }

    public static WindowsLogEventId fromString(String eventName) {
        for (WindowsLogEventId event : WindowsLogEventId.values()) {
            if (event.tuple.getLeft().equalsIgnoreCase(eventName)) {
                return event;
            }
        }

        return  null;
    }

    public static WindowsLogEventId fromEventId(int eventId) {
        for (WindowsLogEventId event : WindowsLogEventId.values()) {
            if (event.tuple.getRight() == eventId) {
                return event;
            }
        }

        return  null;
    }

}
