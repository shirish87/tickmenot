package com.buggycoder.tickmenot;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

import timber.log.Timber;

public class TickMeNotApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ActiveAndroid.initialize(this);
        Timber.plant(new Timber.DebugTree());
    }
}
