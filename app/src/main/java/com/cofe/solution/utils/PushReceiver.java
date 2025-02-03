package com.cofe.solution.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.cofe.solution.ui.device.push.view.DevPushService;

public class PushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Restarting Background Service", Toast.LENGTH_SHORT).show();
        context.startService(new Intent(context, DevPushService.class));
    }
}
