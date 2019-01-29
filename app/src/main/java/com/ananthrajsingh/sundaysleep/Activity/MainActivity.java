package com.ananthrajsingh.sundaysleep.Activity;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ananthrajsingh.sundaysleep.AppDatabase;
import com.ananthrajsingh.sundaysleep.Sleep;
import com.ananthrajsingh.sundaysleep.SleepDao;
import com.ananthrajsingh.sundaysleep.R;
import com.ananthrajsingh.sundaysleep.RestartServiceReceiver;
import com.ananthrajsingh.sundaysleep.SharedPreferenceUtil;

import com.ananthrajsingh.sundaysleep.SleepService;


import java.util.Calendar;
import java.util.List;


/**
 * So I want an application which can predict how much time a person slept at night. Currently
 * I am  concerned with only night to limit complexity. If night works fine, folding out for
 * whole day won't be an issue.
 *
 * When a person is sleeping, 2  things are for sure
 *  1. Screen is off (provided that no one else is using phone while our user is asleep)
 *  2. Phone movements are minimal
 *
 *  I will be developing first prototype using these two variables. Adding more variables could be
 *  done in later phases.
 *
 *  In layman terms, I want this application to know that okay it is night, get to work. Work for
 *  application means constantly checking if sleep variables are positive. In case isAsleep() is
 *  true, start a timer which runs until isAwake(). Now check if this time is long enough (eg. 15
 *  minutes of isAsleep to be true in most cases won't mean user was asleep, and not add it to
 *  sleep time. One other way of achieving this could be to store all isAsleep() times, and regard
 *  only the longest one.
 */
public class MainActivity extends AppCompatActivity {


    private SharedPreferenceUtil mSharedPreferenceUtil;
    Intent mServiceIntent;
    private SleepService mSleepService;
    private Button mSleepHistoryButton;
    public static final long HOURS_24_IN_MILLISECONDS = 1000 * 60 * 60 * 24;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSleepHistoryButton = findViewById(R.id.sleepHistoryButton);
        mSleepHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SleepHistoryActivity.class);
                startActivity(intent);
            }
        });
        mSharedPreferenceUtil = new SharedPreferenceUtil(getApplicationContext());


        mSleepService = new SleepService();
        //TODO Check on this below
        Calendar calendar = Calendar.getInstance();

        Intent broadcastIntent = new Intent(this, RestartServiceReceiver.class);

        mServiceIntent = new Intent(getApplicationContext(), SleepService.class);

        if (!isServiceRunning(mSleepService.getClass())){
            startService(mServiceIntent);

            PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, mServiceIntent, 0);
            AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 5*60*1000, pendingIntent);
            Log.e("MainActivity", "Alarm Set for 30 seconds");
        }


    }

    private boolean isServiceRunning(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isSleepServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isSleepServiceRunning?", false+"");
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
         * We are giving a delay of one second because without it
         * UI was updated before shared preferences was updated with screen on time
         */
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do task after 1000ms
                Log.e("onResume", "Invoked");


                TextView textView = findViewById(R.id.sleepDurationTv);
                long currentTimeMilli = Calendar.getInstance().getTimeInMillis();
                long thresholdTime = currentTimeMilli - HOURS_24_IN_MILLISECONDS;
                AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
                SleepDao sleepDao = db.sleepDao();
                List<Sleep> sleepList = sleepDao.getUptoTime(thresholdTime);
                int minutes = 0;
                int hours = 0;
                for (int i = 0 ; i < sleepList.size() ; i++ ){
                    Sleep currentSleep = sleepList.get(i);
                    long sleepLength = currentSleep.endTime - currentSleep.startTime;
                    int sleepLengthInMinutes = (int) (sleepLength / (1000 * 60));
                    minutes = minutes +  (int) (sleepLengthInMinutes % 60);
                }
                hours = (int) ( minutes / 60 );
                minutes = minutes % 60;

                String hoursStr = "Hours";
                String minutesStr = "Minutes";
                if (minutes == 1){
                    minutesStr = "Minute";
                }
                if (hours == 1){
                    hoursStr = "Hour";
                }
                String finalStr = hours + " " + hoursStr + " " + minutes + " " + minutesStr;
                textView.setText(finalStr);

            }
        }, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();



        //TODO: other code
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // If we don't stop service, it will die with app

        Intent broadcastIntent = new Intent(this, RestartServiceReceiver.class);
        this.sendBroadcast(broadcastIntent);
        Log.e("MainActivity", "We are in onDestroy()");

    }
}
