package com.buggycoder.tickmenot.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

import com.buggycoder.tickmenot.R;
import com.buggycoder.tickmenot.event.NotifAccessChangedEvent;
import com.buggycoder.tickmenot.event.NotifLoadRequestEvent;
import com.buggycoder.tickmenot.event.NotifPerstEvent;
import com.buggycoder.tickmenot.lib.BusProvider;
import com.buggycoder.tickmenot.lib.Tuple;
import com.buggycoder.tickmenot.model.WhatsappNotif;
import com.buggycoder.tickmenot.notif.UnsupportedNotifException;
import com.buggycoder.tickmenot.notif.WhatsappNotifParser;
import com.buggycoder.tickmenot.ui.MainActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import timber.log.Timber;

public class NotifService extends NotificationListenerService {
    private static final int SELF_NOTIF_ID = 100;

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
        if (TextUtils.isEmpty(sbn.getPackageName()) ||
                !sbn.getPackageName().startsWith(mNotifParser.getPackageName())) {
            Timber.d("Ignored notification");
            return;
        }

        //new Delete().from(WhatsappNotif.class).execute();

        updateNotifs();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Timber.d("onNotificationRemoved: %s", sbn.toString());
    }


    private void updateNotifs() {
        StatusBarNotification[] activeNotifications = getActiveNotifications();
        if (activeNotifications == null) {
            return;
        }

        for (StatusBarNotification sbn : activeNotifications) {
            if (TextUtils.isEmpty(sbn.getPackageName()) ||
                    !sbn.getPackageName().startsWith(mNotifParser.getPackageName())) {
                Timber.d("Ignored notification");
                continue;
            }

            String summaryText;
            List<WhatsappNotif> notifs;

            try {
                Tuple<String, List<WhatsappNotif>> parsedSbn = mNotifParser.parse(sbn);
                summaryText = parsedSbn._1;
                notifs = parsedSbn._2;
            } catch (UnsupportedNotifException e) {
                // for anyone who's interested
                BusProvider.getBus().post(e);
                return;
            }

            if (notifs != null && notifs.size() > 0) {
                for (WhatsappNotif notif : notifs) {
                    saveNotif(notif);
                }

                BusProvider.getBus().post(new NotifPerstEvent(notifs, true));

                if (!summaryText.isEmpty()) {
                    notifyUser(summaryText);
                }
            } else {
                Timber.w("No new notifs");
            }
        }
    }
    
    private Long saveNotif(final WhatsappNotif notif) {
        if (!WhatsappNotif.isDup(notif)) {
            Timber.d("Saving notif");
            return notif.save();
        }

        return (long) -1;
    }

    private void notifyUser(String summaryText) {
        Intent viewIntent = new Intent(this, MainActivity.class);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);

        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle(getString(R.string.self_notif_title))
                .setContentText(summaryText)
                .setContentIntent(viewPendingIntent);

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        notificationManager.notify(SELF_NOTIF_ID, notificationBuilder.build());
    }

    @Subscribe
    public void onNotifLoadRequestEvent(NotifLoadRequestEvent event) {
        updateNotifs();
    }
}
