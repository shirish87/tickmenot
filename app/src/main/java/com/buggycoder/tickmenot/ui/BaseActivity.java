package com.buggycoder.tickmenot.ui;

import android.app.Activity;

import com.buggycoder.tickmenot.lib.BusProvider;

public class BaseActivity extends Activity {

    @Override
    protected void onStart() {
        super.onStart();

        BusProvider.getBus().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        BusProvider.getBus().unregister(this);
    }

}
