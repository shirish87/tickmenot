package com.buggycoder.tickmenot.notif;

import android.app.Notification;
import android.content.res.Resources;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import com.buggycoder.tickmenot.R;
import com.buggycoder.tickmenot.lib.Tuple;
import com.buggycoder.tickmenot.model.WhatsappNotif;

import java.util.ArrayList;
import java.util.List;

public class WhatsappNotifParser extends NotifParser<WhatsappNotif> {

    private final String mGroupNotifTitle;
    private final String mSenderSeparator;

    public WhatsappNotifParser(Resources res) {
        super(res.getString(R.string.notif_package));

        mGroupNotifTitle = res.getString(R.string.group_notif_title);
        mSenderSeparator = res.getString(R.string.sender_separator);
    }

    @Override
    public Tuple<String, List<WhatsappNotif>> parse(StatusBarNotification sbn) throws UnsupportedNotifException {
        dumpStatusBarNotification(sbn);

        if (!isValid(sbn)) {
            throw new UnsupportedNotifException("Not a WhatsApp notification");
        }

        List<WhatsappNotif> notifs = new ArrayList<>();
        Notification notification = sbn.getNotification();

        Bundle extras = notification.extras;
        String summaryText = extras.getString(NotificationProps.SUMMARY_TEXT, "");

        return new Tuple<>(summaryText, notifs);
    }
}
