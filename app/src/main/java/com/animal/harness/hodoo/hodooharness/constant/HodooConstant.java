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

    /* GPS */
    public static final long MIN_DISTANCE = 1;
    public static final long MIN_UPDATE_TIME = 100;

    /* DataBases */
    public static final String LOCATION_DB_NAME = "LOCATION_DB";
    public static final int DATABASE_VERSION = 1;
}
