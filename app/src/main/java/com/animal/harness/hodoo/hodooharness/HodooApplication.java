package com.animal.harness.hodoo.hodooharness;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

import com.animal.harness.hodoo.hodooharness.util.DBHelper;

public class HodooApplication extends Application {
    private final String TAG = HodooApplication.class.getSimpleName();
    public DBHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DBHelper(getApplicationContext());
        Log.e(TAG, "application onCreate");
    }
    public DisplayMetrics getDeviceMetrics() {
        return getApplicationContext().getResources().getDisplayMetrics();
    }
}
