package com.cofe.solution.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.cofe.solution.R;
import com.cofe.solution.ui.device.push.view.DevPushService;

public class PushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, context.getString(R.string.restarting_background_service), Toast.LENGTH_SHORT).show();
        context.startService(new Intent(context, DevPushService.class));
    }
}
