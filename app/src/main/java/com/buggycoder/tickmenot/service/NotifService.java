package com.buggycoder.tickmenot.service;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import timber.log.Timber;

public class NotifService extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Timber.d("onNotificationPosted: %s", sbn.toString());
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Timber.d("onNotificationRemoved: %s", sbn.toString());
    }
}
