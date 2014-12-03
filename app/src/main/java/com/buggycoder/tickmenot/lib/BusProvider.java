package com.buggycoder.tickmenot.lib;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public enum BusProvider {
    INSTANCE;

    private Bus mBus;

    BusProvider() {
        mBus = new Bus(ThreadEnforcer.ANY);
    }

    public static Bus getBus() {
        return INSTANCE.mBus;
    }
}
