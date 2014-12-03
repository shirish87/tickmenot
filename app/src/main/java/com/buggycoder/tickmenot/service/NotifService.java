package com.buggycoder.tickmenot.service;

import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.buggycoder.tickmenot.event.NotifAccessChangedEvent;
import com.buggycoder.tickmenot.lib.BusProvider;
import com.squareup.otto.Bus;

import timber.log.Timber;

public class NotifService extends NotificationListenerService {

    private Bus mBus;

    @Override
    public void onCreate() {
        super.onCreate();

        mBus = BusProvider.getBus();
        mBus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mBus.unregister(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        mBus.post(new NotifAccessChangedEvent(true));
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mBus.post(new NotifAccessChangedEvent(false));
        return super.onUnbind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Timber.d("onNotificationPosted: %s", sbn.toString());
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Timber.d("onNotificationRemoved: %s", sbn.toString());
    }
}
