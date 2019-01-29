package com.ananthrajsingh.sundaysleep;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

/**
 * Created by ananthrajsingh on 26/01/19
 */
public class RestartServiceReceiver extends BroadcastReceiver {
    private static final String TAG = RestartServiceReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "Starting service from RestartServiceReceiver's onReceive");
//        context.startService(new Intent(context, SleepService.class));

        boolean isServiceRunning = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SleepService.class.getName().equals(service.service.getClassName())) {
                isServiceRunning = true;
            }
        }
        if (isServiceRunning){
            Log.e("RestartServiceReceiver", "Service ALREADY RUNNING,NOT starting");
        }
        if (!isServiceRunning){
            Log.e("RestartServiceReceiver", "Service not already running starting again...");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, SleepService.class));
            }
            else {
                context.startService(new Intent(context, SleepService.class));
            }
        }

    }
}
