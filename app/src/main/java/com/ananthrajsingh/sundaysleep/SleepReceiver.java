package com.ananthrajsingh.sundaysleep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ananthrajsingh.sundaysleep.Database.AppDatabase;
import com.ananthrajsingh.sundaysleep.Database.Sleep;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
//            SimpleDateFormat formatter = new SimpleDateFormat("dd:HH:mm:ss", Locale.UK);
//
//            Date time1 = new Date(mSharedPreferenceUtil.getLastScreenOnTime());
//            Date time2 = new Date(mSharedPreferenceUtil.getLastScreenOffTime());
//
//            String offTime = formatter.format(time1);
//            String onTime = formatter.format(time2);
//            Log.e(TAG, "offTime " + offTime + " onTime " + onTime );

//            updateTimeInUi(context);

        }

    }

//    public static void updateTimeInUi(Context context){
//
//        View view = View.inflate(context, R.layout.activity_main, null);
//
//        SimpleDateFormat formatter = new SimpleDateFormat("dd:HH:mm:ss", Locale.UK);
//        Log.e(TAG, "We are in updateTimeUi");
//        Date time1 = new Date(mSharedPreferenceUtil.getLastScreenOnTime());
//        Date time2 = new Date(mSharedPreferenceUtil.getLastScreenOffTime());
//        Date time3 = new Date(mSharedPreferenceUtil.getSleepTime());
//
//        String offTime = formatter.format(time1);
//        String onTime = formatter.format(time2);
//        String sleepTime = formatter.format(time3);
//        Log.e(TAG, "offTime " + offTime + " onTime " + onTime );
//
//        TextView mLastScreenOnTv = view.findViewById(R.id.lastScreenOnTimeTv);
//        TextView mLastScreenOffTv = view.findViewById(R.id.lastScreenOffTimeTv);
//        TextView mSleepTimeTv = view.findViewById(R.id.sleepTimeTv);
//
//        mLastScreenOnTv.setText(onTime);
//        mLastScreenOffTv.setText(offTime);
//        mSleepTimeTv.setText(sleepTime);
//
//    }
}
