package com.cofe.solution.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.cofe.solution.ui.device.push.view.DevPushService;

public class PushWorker extends Worker {
    public PushWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Intent serviceIntent = new Intent(getApplicationContext(), DevPushService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getApplicationContext().startForegroundService(serviceIntent);
        } else {
            getApplicationContext().startService(serviceIntent);
        }
        return Result.success();
    }
}
