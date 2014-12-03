package com.buggycoder.tickmenot.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.buggycoder.tickmenot.R;


public class MainActivity extends BaseActivity {

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String SETTINGS_NOTIF_LISTENER = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    private String mPackageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPackageName = getPackageName();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!hasNotifAccessPermission()) {
            requestNotifAccess();
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
}
