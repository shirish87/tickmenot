package com.buggycoder.tickmenot.service;

import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.buggycoder.tickmenot.event.NotifAccessChangedEvent;
import com.buggycoder.tickmenot.lib.BusProvider;
import com.buggycoder.tickmenot.lib.Tuple;
import com.buggycoder.tickmenot.model.WhatsappNotif;
import com.buggycoder.tickmenot.notif.UnsupportedNotifException;
import com.buggycoder.tickmenot.notif.WhatsappNotifParser;
import com.squareup.otto.Bus;

import java.util.List;

import timber.log.Timber;

public class NotifService extends NotificationListenerService {

    private Bus mBus;
    private WhatsappNotifParser mNotifParser;

    @Override
    public void onCreate() {
        super.onCreate();

        mBus = BusProvider.getBus();
        mBus.register(this);

        mNotifParser = new WhatsappNotifParser(getResources());
        Timber.d("onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mBus.unregister(this);
        Timber.d("onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Timber.d("onBind");
        mBus.post(new NotifAccessChangedEvent(true));
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Timber.d("onUnbind");
        mBus.post(new NotifAccessChangedEvent(false));
        return super.onUnbind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Timber.d("onNotificationPosted: %s", sbn.toString());

        try {
            Tuple<String, List<WhatsappNotif>> parse = mNotifParser.parse(sbn);
            Timber.d("Parsed %s", parse._1);
        } catch (UnsupportedNotifException e) {
            Timber.e(e, "Parsing failed");
        }

        WhatsappNotif test = new WhatsappNotif("event", "sender", "message", 1);
        test.save();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Timber.d("onNotificationRemoved: %s", sbn.toString());
    }
}
