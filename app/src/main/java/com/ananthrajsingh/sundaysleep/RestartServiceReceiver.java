package com.ananthrajsingh.sundaysleep;

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, SleepService.class));
        }
        else {
            context.startService(new Intent(context, SleepService.class));
        }
    }
}
