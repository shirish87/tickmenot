package com.buggycoder.tickmenot.notif;

import android.annotation.TargetApi;
import android.app.Notification;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;

import timber.log.Timber;

public class NotifParser {

    protected interface NotificationProps {
        String TITLE = "android.title";
        String TEXT = "android.text";
        String SUMMARY_TEXT = "android.summaryText";
        String TEXT_LINES = "android.textLines";
    }

    private final String mPackageName;

    public NotifParser(String packageName) {
        mPackageName = packageName;
    }

    public void parse(StatusBarNotification sbn) {
        dumpStatusBarNotification(sbn);
    }

    public boolean isValid(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        Notification notification = sbn.getNotification();

        /* (in)sanity checks */
        return !((mPackageName != null &&
                (packageName == null || !packageName.startsWith(mPackageName))) ||
                notification == null ||
                notification.tickerText == null ||
                notification.extras == null);
    }

    public String getPackageName() {
        return mPackageName;
    }

    /* debugging */
    protected void dumpStatusBarNotification(StatusBarNotification sbn) {
        Timber.d("Package: %s", sbn.getPackageName());
        Timber.d("Id: %s", sbn.getId());
        Timber.d("Tag: %s", sbn.getTag());
        Timber.d("Post time: %s", sbn.getPostTime());

        dumpUserHandle(sbn);

        Notification notification = sbn.getNotification();
        if (notification != null) {
            Timber.d("Priority: %s", notification.priority);
            Timber.d("Ticker Text: %s", notification.tickerText);
            Timber.d("No. of events: %s", notification.number);
            dumpExtras(notification);
            dumpNotificationActions(notification);
        }
    }

    @TargetApi(Build.VERSION_CODES.L)
    private void dumpUserHandle(StatusBarNotification sbn) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {
            UserHandle userHandle = sbn.getUser();
            if (userHandle != null) {
                Timber.d("User handle: %s", userHandle.toString());
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void dumpNotificationActions(Notification notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Notification.Action[] actions = notification.actions;
            if (actions != null) {
                for (Notification.Action action : actions) {
                    Timber.d("Action title: %s", action.title);
                    dumpExtras(action);
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void dumpExtras(Notification notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dumpExtras(notification.extras);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    private void dumpExtras(Notification.Action action) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            dumpExtras(action.getExtras());
        }
    }

    private void dumpExtras(Bundle extras) {
        if (extras != null) {
            for (String k : extras.keySet()) {
                Object o = extras.get(k);
                if (o instanceof CharSequence[]) {
                    // case for "textLines" and such

                    CharSequence[] data = (CharSequence[]) o;
                    for (CharSequence d : data) {
                        Timber.d("%s => %s", k, d);
                    }
                } else {
                    Timber.d("%s => %s", k, o);
                }
            }
        }
    }
}
