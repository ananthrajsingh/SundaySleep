package com.ananthrajsingh.sundaysleep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ananthrajsingh on 26/01/19
 */
public class SleepReceiver extends BroadcastReceiver {

    private static String TAG = SleepReceiver.class.getSimpleName();
    private static SharedPreferenceUtil mSharedPreferenceUtil;


    @Override
    public void onReceive(Context context, Intent intent) {
        mSharedPreferenceUtil = new SharedPreferenceUtil(context);
        Log.e(TAG, ":onReceive " + intent.getAction());

        Date currentTime = Calendar.getInstance().getTime();
        long currentTimeMilli = Calendar.getInstance().getTimeInMillis();

        //TODO: Implement this block
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_SCREEN_OFF))
        {
            Log.e(TAG, "SCREEN OFF, TIME: " + currentTime);
            mSharedPreferenceUtil.setLastScreenOffTime(currentTimeMilli);
            mSharedPreferenceUtil.setIsScreenOn(false);
        }
        else if (intent.getAction().equalsIgnoreCase(Intent.ACTION_SCREEN_ON)) {
            Log.e(TAG, "SCREEN ON, TIME: " + currentTime);
            mSharedPreferenceUtil.setLastScreenOnTime(currentTimeMilli);
            mSharedPreferenceUtil.setIsScreenOn(true);
            Log.e(TAG, "ON TIME SET");


            // Let us do some checking on sleep time
            long sleepTime = mSharedPreferenceUtil.getLastScreenOnTime()
                    - mSharedPreferenceUtil.getLastScreenOffTime();
            mSharedPreferenceUtil.setSleepTime(sleepTime);
            Log.e(TAG, "SLEEP TIME IN SECONDS: " + sleepTime/1000);

            Sleep currentSleep = new Sleep();
            currentSleep.startTime = mSharedPreferenceUtil.getLastScreenOffTime();
            currentSleep.endTime = mSharedPreferenceUtil.getLastScreenOnTime();
            AppDatabase db = AppDatabase.getDatabase(context);
            db.sleepDao().insertAll(currentSleep);


        }

    }


}
