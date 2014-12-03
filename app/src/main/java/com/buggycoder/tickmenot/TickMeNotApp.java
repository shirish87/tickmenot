package com.buggycoder.tickmenot;

import android.app.Application;

import timber.log.Timber;

public class TickMeNotApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());
    }
}
