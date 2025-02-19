package com.cofe.solution.utils;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

public class BatteryOptimizationHelper {

    public static void checkAndRequestBatteryOptimization(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        if (powerManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check if app is already exempted
            boolean isIgnoringOptimizations = powerManager.isIgnoringBatteryOptimizations(context.getPackageName());

            Log.d("BatteryOptimization", "Is Ignoring Optimizations: " + isIgnoringOptimizations);

            if (!isIgnoringOptimizations) {
                try {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception e) {
                    Log.e("BatteryOptimization", "Failed to open battery settings", e);
                }
            }
        }
    }
}
