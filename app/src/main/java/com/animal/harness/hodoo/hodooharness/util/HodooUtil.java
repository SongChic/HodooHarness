package com.animal.harness.hodoo.hodooharness.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.animal.harness.hodoo.hodooharness.R;
import com.animal.harness.hodoo.hodooharness.constant.HodooConstant;

public class HodooUtil {
    public interface AlertCallback {
        void setNegativeButton(DialogInterface dialog, int which);
        void setPositiveButton(DialogInterface dialog, int which);
    }
    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
    public static DisplayMetrics getDisplayMetrics ( Context context ) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm;
    }
    public static float pxToSp ( Context context, int px ) {
        return px / context.getResources().getDisplayMetrics().scaledDensity;
    }
    public static int spToPx ( Context context, int sp ) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
    public static void changeDrawableColor ( Drawable d, int colorResource ) {
        d.mutate();
        d.setColorFilter(colorResource, PorterDuff.Mode.SRC_ATOP);
    }
    public static void GPSCheck (Context context, final AlertCallback callback) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE );
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if ( !statusOfGPS ) {
            /* GSP OFF */
            AlertDialog builder = new AlertDialog.Builder(context)
                    .setTitle("GPS가 꺼져 있습니다.")
                    .setMessage("GPS가 꺼져 있습니다.\nGPS를 켜야 어플을 사용 할 수 있습니다.")
                    .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if ( callback != null )
                                callback.setNegativeButton(dialog, which);
                        }
                    })
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if ( callback != null )
                                callback.setPositiveButton(dialog, which);
                        }
                    })
                    .setCancelable(false)
                    .create();
            builder.show();
        }
    }
    public static boolean getGPSState(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE );
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
