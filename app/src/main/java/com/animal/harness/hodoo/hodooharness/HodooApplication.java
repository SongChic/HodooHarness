package com.animal.harness.hodoo.hodooharness;

import android.app.Application;
import android.util.DisplayMetrics;

public class HodooApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }
    public DisplayMetrics getDeviceMetrics() {
        return getApplicationContext().getResources().getDisplayMetrics();
    }
}
