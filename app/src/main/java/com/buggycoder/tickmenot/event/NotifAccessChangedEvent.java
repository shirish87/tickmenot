package com.buggycoder.tickmenot.event;

public class NotifAccessChangedEvent {
    public final boolean isAllowed;

    public NotifAccessChangedEvent(boolean isAllowed) {
        this.isAllowed = isAllowed;
    }
}
