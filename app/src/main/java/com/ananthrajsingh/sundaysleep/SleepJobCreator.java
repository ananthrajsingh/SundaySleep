package com.ananthrajsingh.sundaysleep;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by ananthrajsingh on 26/01/19
 * This class takes care which job to create for us. We also need to pass it's object to
 * the JobManager. Here we are dealing with only one Job.
 */
public class SleepJobCreator implements JobCreator {

    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch(tag){
            case SleepJob.TAG_I: return new SleepJob();
            case SleepJob.TAG_S: return new SleepJob();
            default: return null;
        }
    }
}
