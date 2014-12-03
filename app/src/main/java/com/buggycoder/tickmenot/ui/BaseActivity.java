package com.buggycoder.tickmenot.ui;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.buggycoder.tickmenot.lib.BusProvider;

import butterknife.ButterKnife;

/**
 * Inherit this Activity to have Otto and Butterknife set up
 */
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

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ButterKnife.inject(this);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        ButterKnife.inject(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.inject(this);
    }
}
