package com.animal.harness.hodoo.hodooharness.service;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.animal.harness.hodoo.hodooharness.base.BaseService;

public class StopWatchService extends BaseService {
    private IBinder binder = new StopWatchBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    class StopWatchBinder extends Binder {
        StopWatchService getService() {
            return StopWatchService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
    }
}
