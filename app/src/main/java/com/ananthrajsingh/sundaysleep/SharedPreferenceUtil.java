package com.ananthrajsingh.sundaysleep;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ananthrajsingh on 26/01/19
 * This is a helper class to deal with shared preferences.
 */
public class SharedPreferenceUtil {

    private Context context;
    private SharedPreferences.Editor editor;

    private SharedPreferences mStatus;
    private SharedPreferences mTime;

    /* SHARED PREFERENCES NAME */
    private final String status = "status";
    private final String time = "time";

    /* PREFERENCES NAME */
    private final String isScreenOn = "SCREEN-ON";
    private final String lastScreenOnTime = "LAST-SCREEN-ON-TIME";
    private final String lastScreenOffTime = "LAST-SCREEN-OFF-TIME";
    private final String sleepTime = "SLEEP-TIME";

    public SharedPreferenceUtil( Context context ){
        this.context = context;
        mStatus = context.getSharedPreferences(status, Context.MODE_PRIVATE);
        mTime = context.getSharedPreferences(time, Context.MODE_PRIVATE);
    }

    public void setIsScreenOn(boolean isScreenOn){
        editor = mStatus.edit();
        editor.putBoolean(this.isScreenOn, isScreenOn);
        editor.apply();
    }

    public void setLastScreenOnTime(long milliseconds){
        editor = mTime.edit();
        editor.putLong(lastScreenOnTime, milliseconds);
        editor.apply();
    }

    public void setLastScreenOffTime(long milliseconds){
        editor = mTime.edit();
        editor.putLong(lastScreenOffTime, milliseconds);
        editor.apply();
    }

    public void setSleepTime(long milliseconds){
        editor = mTime.edit();
        editor.putLong(sleepTime, milliseconds);
        editor.apply();
    }

    public boolean getIsScreenOn(){
        return mStatus.getBoolean(isScreenOn, false);
    }

    public long getLastScreenOnTime(){
        return mTime.getLong(lastScreenOnTime, 0);
    }

    public long getLastScreenOffTime(){
        return mTime.getLong(lastScreenOffTime, 0);
    }

    public long getSleepTime(){
        return mTime.getLong(sleepTime, 0);
    }


}
