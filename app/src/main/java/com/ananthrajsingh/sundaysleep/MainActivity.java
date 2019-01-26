package com.ananthrajsingh.sundaysleep;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferenceUtil = new SharedPreferenceUtil(getApplicationContext());
//        mLastScreenOnTv = findViewById(R.id.lastScreenOnTimeTv);
//        mLastScreenOffTv = findViewById(R.id.lastScreenOffTimeTv);
//        mSleepTimeTv = findViewById(R.id.sleepTimeTv);
//        cancelImmediateJobScheduler();

        runJobScheduler();
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
                SimpleDateFormat formatter = new SimpleDateFormat("dd:HH:mm:ss", Locale.UK);
//        Log.e(TAG, "We are in updateTimeUi");
                Date time1 = new Date(mSharedPreferenceUtil.getLastScreenOnTime());
                Date time2 = new Date(mSharedPreferenceUtil.getLastScreenOffTime());
                Date time3 = new Date(mSharedPreferenceUtil.getSleepTime());

                String onTime = formatter.format(time1);
                String offTime = formatter.format(time2);
                String sleepTime = formatter.format(time3);
//        Log.e(TAG, "offTime " + offTime + " onTime " + onTime );

                TextView mLastScreenOnTv = findViewById(R.id.lastScreenOnTimeTv);
                TextView mLastScreenOffTv = findViewById(R.id.lastScreenOffTimeTv);
                TextView mSleepTimeTv = findViewById(R.id.sleepTimeTv);

                mLastScreenOnTv.setText(onTime);
                mLastScreenOffTv.setText(offTime);
                mSleepTimeTv.setText(Long.toString(mSharedPreferenceUtil.getSleepTime()/1000));

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
        Log.e("MainActivity", "We are in onDestroy()");

    }
}
