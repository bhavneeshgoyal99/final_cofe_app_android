package com.cofe.solution.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cofe.solution.ui.device.push.view.DevPushService;

public class ServiceRestartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, DevPushService.class));
    }
}