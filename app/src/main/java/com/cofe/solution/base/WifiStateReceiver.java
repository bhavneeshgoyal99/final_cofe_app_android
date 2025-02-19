package com.cofe.solution.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class WifiStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

            switch (wifiState) {
                case WifiManager.WIFI_STATE_ENABLED:
                    Toast.makeText(context, "WiFi is Enabled", Toast.LENGTH_SHORT).show();
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    Toast.makeText(context, "WiFi is Disabled", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Toast.makeText(context, "Connected to WiFi", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Disconnected from WiFi", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
