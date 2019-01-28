package com.ananthrajsingh.sundaysleep.Activity;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ananthrajsingh.sundaysleep.Database.AppDatabase;
import com.ananthrajsingh.sundaysleep.Database.Sleep;
import com.ananthrajsingh.sundaysleep.Database.SleepDao;
import com.ananthrajsingh.sundaysleep.R;
import com.ananthrajsingh.sundaysleep.RestartServiceReceiver;
import com.ananthrajsingh.sundaysleep.SharedPreferenceUtil;
import com.ananthrajsingh.sundaysleep.SleepJob;
import com.ananthrajsingh.sundaysleep.SleepService;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.ananthrajsingh.sundaysleep.SleepJob.TAG_I;
import static com.ananthrajsingh.sundaysleep.SleepJob.TAG_S;

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

//    private TextView mLastScreenOnTv;
//    private TextView mLastScreenOffTv;
//    private TextView mSleepTimeTv;
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
//        mLastScreenOnTv = findViewById(R.id.lastScreenOnTimeTv);
//        mLastScreenOffTv = findViewById(R.id.lastScreenOffTimeTv);
//        mSleepTimeTv = findViewById(R.id.sleepTimeTv);
//        cancelImmediateJobScheduler();

//        runJobScheduler();
//        Intent intent = new Intent();
//        String packageName = this.getPackageName();
//        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
//            if (pm.isIgnoringBatteryOptimizations(packageName))
//                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
//            else {
//                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
//                intent.setData(Uri.parse("package:" + packageName));
//            }
//        }
//        this.startActivity(intent);

        mSleepService = new SleepService();
        //TODO Check on this below
        Calendar calendar = Calendar.getInstance();

        mServiceIntent = new Intent(getApplicationContext(), SleepService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, mServiceIntent, 0);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 30*1000, pendingIntent);
        Log.e("MainActivity", "Alarm Set for 30 seconds");
//        if (!isServiceRunning(mSleepService.getClass())){
//            Log.e("MainActivity","Starting service from MainActivity onCreate");
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                getApplicationContext().startForegroundService(new Intent(getApplicationContext(), SleepService.class));
//            }
//            else {
//                getApplicationContext().startService(new Intent(getApplicationContext(), SleepService.class));
//            }
//        }
//            startService(mServiceIntent);

//        Intent broadcastIntent = new Intent(this, RestartServiceReceiver.class);
//        this.sendBroadcast(broadcastIntent);
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
//                SimpleDateFormat formatter = new SimpleDateFormat("dd:HH:mm:ss", Locale.UK);
////        Log.e(TAG, "We are in updateTimeUi");
//                Date time1 = new Date(mSharedPreferenceUtil.getLastScreenOnTime());
//                Date time2 = new Date(mSharedPreferenceUtil.getLastScreenOffTime());
//                Date time3 = new Date(mSharedPreferenceUtil.getSleepTime());
//
//                String onTime = formatter.format(time1);
//                String offTime = formatter.format(time2);
//                String sleepTime = formatter.format(time3);
////        Log.e(TAG, "offTime " + offTime + " onTime " + onTime );
//
//                TextView mLastScreenOnTv = findViewById(R.id.lastScreenOnTimeTv);
//                TextView mLastScreenOffTv = findViewById(R.id.lastScreenOffTimeTv);
//                TextView mSleepTimeTv = findViewById(R.id.sleepTimeTv);
//
//                mLastScreenOnTv.setText(onTime);
//                mLastScreenOffTv.setText(offTime);
//                mSleepTimeTv.setText(Long.toString(mSharedPreferenceUtil.getSleepTime()/1000));


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
                    hours = hours + (int) (sleepLengthInMinutes / 60);
                }
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

    private void runJobScheduler(){
        Set<JobRequest> jobRequestsImmediate;
        Set<JobRequest> jobRequestsScheduled;

        jobRequestsImmediate = JobManager.instance().getAllJobRequestsForTag(TAG_I);
        jobRequestsScheduled = JobManager.instance().getAllJobRequestsForTag(TAG_S);

//        if (jobRequestsImmediate == null || jobRequestsImmediate.isEmpty()) {
//            SleepJob.runJobImmediately();
//        }
        if (jobRequestsScheduled == null || jobRequestsScheduled.isEmpty()) {
            SleepJob.schedulePeriodicJob();
        }

        //Cancel pending job scheduler if mutiple instance are running.
        if (jobRequestsScheduled != null && jobRequestsScheduled.size() > 2) {
            JobManager.instance().cancelAllForTag(SleepJob.TAG_S);
        }

        if (jobRequestsImmediate != null) {
            jobRequestsImmediate.clear();
        }
        if (jobRequestsScheduled != null) {
            jobRequestsScheduled.clear();
        }
        jobRequestsImmediate = jobRequestsScheduled = null;
    }

    private void cancelImmediateJobScheduler() {
        JobManager.instance().cancelAllForTag(TAG_I);
//        JobManager.instance().cancelAllForTag(SleepJob.TAG_S);
    }

    @Override
    protected void onStop() {
        super.onStop();

        cancelImmediateJobScheduler();

//        Set<JobRequest> jobRequestsScheduled = JobManager.instance().getAllJobRequestsForTag(TAG_S);
//        Log.e("MainActivity", "We are in onStop()");
//        if (jobRequestsScheduled == null || jobRequestsScheduled.isEmpty()) {
//            SleepJob.schedulePeriodicJob();
//        }



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
