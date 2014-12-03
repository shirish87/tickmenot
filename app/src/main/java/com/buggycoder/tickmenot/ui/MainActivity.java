package com.buggycoder.tickmenot.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.buggycoder.tickmenot.R;
import com.buggycoder.tickmenot.event.NotifAccessChangedEvent;
import com.buggycoder.tickmenot.event.NotifPerstEvent;
import com.buggycoder.tickmenot.model.WhatsappNotif;
import com.squareup.otto.Subscribe;

import java.util.Date;

import butterknife.InjectView;
import timber.log.Timber;


public class MainActivity extends BaseActivity {

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String SETTINGS_NOTIF_LISTENER = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    private String mPackageName;

    @InjectView(R.id.requestNotifAccess)
    Button mRequestNotifAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRequestNotifAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestNotifAccess();
            }
        });

        mPackageName = getPackageName();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!hasNotifAccessPermission()) {
            requestNotifAccess();
        } else {
            updateNotifAccessView(false);
        }
    }

    private boolean hasNotifAccessPermission() {
        ContentResolver contentResolver = getContentResolver();
        String listeners = Settings.Secure.getString(contentResolver, ENABLED_NOTIFICATION_LISTENERS);
        return (listeners != null && listeners.contains(mPackageName));
    }

    private void requestNotifAccess() {
        Toast.makeText(MainActivity.this, getString(R.string.request_notif_access),
                        Toast.LENGTH_SHORT).show();
        startActivity(new Intent(SETTINGS_NOTIF_LISTENER));
    }

    private void updateNotifAccessView(final boolean isAccessEnabled) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRequestNotifAccess.setVisibility(isAccessEnabled ? View.GONE : View.VISIBLE);
            }
        });
    }

    @Subscribe
    public void onNotifAccessChangedEvent(final NotifAccessChangedEvent event) {
        updateNotifAccessView(event.isAllowed);
    }

    @Subscribe
    public void onNotifPerstEvent(final NotifPerstEvent notifPerst) {
        if (notifPerst.notifs.size() > 0) {
            WhatsappNotif notif = notifPerst.notifs.get(notifPerst.notifs.size() - 1);
            String status = notifPerst.isSuccess ? "Success" : "Error";

            String msg = String.format("%s: %s: %s | %s",
                    status, notif.sender, notif.message, new Date(notif.postTime));
            Timber.d(msg);
        }
    }
}
