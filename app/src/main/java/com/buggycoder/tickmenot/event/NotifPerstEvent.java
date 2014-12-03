package com.buggycoder.tickmenot.event;


import com.buggycoder.tickmenot.model.WhatsappNotif;

import java.util.List;

public class NotifPerstEvent {
    public final List<WhatsappNotif> notifs;
    public final boolean isSuccess;

    public NotifPerstEvent(List<WhatsappNotif> notifs, boolean isSuccess) {
        this.notifs = notifs;
        this.isSuccess = isSuccess;
    }
}
