package com.animal.harness.hodoo.hodooharness;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
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
