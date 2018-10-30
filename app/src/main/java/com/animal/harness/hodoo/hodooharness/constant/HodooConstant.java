package com.animal.harness.hodoo.hodooharness.constant;

import android.Manifest;

/**
 * Created by Song on 2018-09-10.
 */
public class HodooConstant {
    public static final boolean DEBUG = true;

    /* permission */
    public static  final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    public static final int PERMISSION_REQUEST_CODE = 1000;
    public static final int GPS_SETTING_REQUEST_CODE = 1200;
    public static final int BLUETOOTH_REQUEST_CODE = 2000;
    public static final int GPS_INIT_VAL = 200;

    /* GPS */
    public static final long MIN_DISTANCE = 0;
    public static final long MIN_UPDATE_TIME = 0;
    //0, 0일 경우 가능한 빨리 값을 가져온다.

    /* DataBases */
    public static final String LOCATION_DB_NAME = "LOCATION_DB";
    public static final int DATABASE_VERSION = 1;
}
