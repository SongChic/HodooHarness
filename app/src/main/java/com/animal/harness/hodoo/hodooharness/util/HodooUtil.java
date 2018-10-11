package com.animal.harness.hodoo.hodooharness.util;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.animal.harness.hodoo.hodooharness.R;

public class HodooUtil {
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
}
