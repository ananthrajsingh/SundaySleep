package com.ananthrajsingh.sundaysleep;

import android.content.Context;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

/**
 * Created by ananthrajsingh on 26/01/19
 */
public class SleepJob extends Job {

    public static final String TAG = SleepJob.class.getSimpleName();
    // This is used to recognize our Job
    public static final String TAG_I = "immediate_sleep_job_tag";
    public static final String TAG_S = "scheduled_sleep_job_tag";
    public static SleepReceiver mSleepReceiver;
    public static final String SCREEN_OFF_ACTION = "android.intent.action.SCREEN_OFF";
    public static final String SCREEN_ON_ACTION = "android.intent.action.SCREEN_ON";


    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        String jobTag = params.getTag();
        Log.e(TAG, "Job started " + jobTag);

        PowerManager powerManager = (PowerManager) getContext()
                .getSystemService(Context.POWER_SERVICE);
        assert powerManager != null;
        boolean isAwake = powerManager.isInteractive();

        if (mSleepReceiver != null)
            getContext().getApplicationContext().unregisterReceiver(mSleepReceiver);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SCREEN_OFF_ACTION);
        intentFilter.addAction(SCREEN_ON_ACTION);
        mSleepReceiver = new SleepReceiver();
        getContext().getApplicationContext().registerReceiver(mSleepReceiver, intentFilter);

        if (isAwake){
            //TODO: Do any relevant task, phone is awake
            Log.e(TAG, "Phone is currently awake");
        }

        return Result.SUCCESS;
    }

    public static int schedulePeriodicJob() {
        return new JobRequest.Builder(TAG_S)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15), TimeUnit.MINUTES.toMillis(14))
                .build()
                .schedule();
    }

    public static int runJobImmediately() {
        return new JobRequest.Builder(TAG_I)
                .startNow()
                .build()
                .schedule();
    }


    public static void cancelJob(int jobId) {
        JobManager.instance().cancel(jobId);
    }
}
