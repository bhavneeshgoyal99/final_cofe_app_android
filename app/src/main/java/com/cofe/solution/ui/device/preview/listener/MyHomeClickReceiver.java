package com.cofe.solution.ui.device.preview.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyHomeClickReceiver extends BroadcastReceiver {

    private static final String TAG = "MyHomeClickReceiver";
    private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    private static final String SYSTEM_DIALOG_REASON_RECENTAPPS = "recentapps";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            Log.d(TAG, "System dialog action received: " + reason);

            if (SYSTEM_DIALOG_REASON_RECENTAPPS.equals(reason)) {
                // Recent apps button clicked
                return;
            }

            if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason) || "fs_gesture".equals(reason)) {
                Log.d(TAG, "Home button pressed.");
                // Handle home button press
            }
        }
    }
}
