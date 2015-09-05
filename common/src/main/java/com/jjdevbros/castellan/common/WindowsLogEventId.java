package com.jjdevbros.castellan.common;

public enum WindowsLogEventId {
    //User successfully logs into the machine
    LOG_IN(4648),

    //User logs off out of the machine
    LOG_OUT(4647),

    //User locks the machine explicitly
    SCREEN_LOCK(4800),

    //User unlocks the machine explicitly
    SCREEN_UNLOCK(4801),

    //Inactivity due to keyboard / mouse not moved
    USER_INACTIVE(1000000),

    //Transition from inactivity to activity
    USER_ACTIVE(1000001),

    //User gets disconnected from the network
    NETWORK_DISCONNECTED(10001),

    //User gets connected from the network
    NETWORK_CONNECTED(10000);

    private final int id;

    WindowsLogEventId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
