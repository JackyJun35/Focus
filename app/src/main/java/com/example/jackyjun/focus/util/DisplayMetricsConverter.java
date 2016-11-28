package com.example.jackyjun.focus.util;

import android.content.Context;
import android.util.DisplayMetrics;

import static java.security.AccessController.getContext;

/**
 * Created by jackyjun on 16/11/22.
 */

public class DisplayMetricsConverter {

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }
}
