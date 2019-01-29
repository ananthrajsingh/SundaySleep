package com.ananthrajsingh.sundaysleep;


import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;



/**
 * Created by ananthrajsingh on 26/01/19
 */
public class SleepService extends Service {
    private static final String TAG = SleepService.class.getSimpleName();
    public int mCounter;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private SleepReceiver mSleepReceiver;
    public static final String SCREEN_OFF_ACTION = "android.intent.action.SCREEN_OFF";
    public static final String SCREEN_ON_ACTION = "android.intent.action.SCREEN_ON";



    @Override
    public void onCreate() {
        super.onCreate();

        startTimer();

        boolean isServiceRunning = false;
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SleepService.class.getName().equals(service.service.getClassName())) {
                isServiceRunning = true;
            }
        }
        if (isServiceRunning){
            Log.e("SleepService", "Service ALREADY RUNNING,NOT starting");
        }
        if (!isServiceRunning){
            Log.e("SleepService", "Service not already running starting again...");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                startOwnForeground();
                Log.e("SleepService", "onCreate startOwnForeGround");

            }
            else{
                startForeground(1, new Notification());

                Log.e("SleepService", "onCreate startForeground");

            }
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SCREEN_OFF_ACTION);
        intentFilter.addAction(SCREEN_ON_ACTION);
        mSleepReceiver = new SleepReceiver();
        try{
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mSleepReceiver);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }

        Log.e("SleepService", "RECEIVER REGISTERED");
        getApplicationContext().registerReceiver(mSleepReceiver, intentFilter);



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("SleepService", "Running...");
        /*
         * This commented out part was intended to run every 3 seconds, we no longer need it.
         */
//        Set<JobRequest> jobRequestsImmediate = JobManager.instance().getAllJobRequestsForTag(TAG_I);
//
//        if (jobRequestsImmediate == null || jobRequestsImmediate.isEmpty()) {
//            SleepJob.runJobImmediately();
//        }
//        final Handler handler = new Handler();
//        final int delay = 1000 * 3; //milliseconds
//
//        handler.postDelayed(new Runnable(){
//            public void run(){
//                //do something
//                Log.e("SleepService", "Hello!");
//                Set<JobRequest> jobRequestsImmediate = JobManager.instance().getAllJobRequestsForTag(TAG_I);
//
//                if (jobRequestsImmediate == null || jobRequestsImmediate.isEmpty()) {
//                    SleepJob.runJobImmediately();
//                }
//                handler.postDelayed(this, delay);
//            }
//        }, delay);
        /*
         * Now we need this. We simply need to call once, as this service will be triggered
         * at regular intervals by the Alarm Manager.
         */
//        Set<JobRequest> jobRequestsImmediate = JobManager.instance().getAllJobRequestsForTag(TAG_I);
//
//        if (jobRequestsImmediate == null || jobRequestsImmediate.isEmpty()) {
//            SleepJob.runJobImmediately();



        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopTimerTask();
        Intent broadcastIntent = new Intent(this, RestartServiceReceiver.class);
        this.sendBroadcast(broadcastIntent);
//        try {
//            SleepReceiver receiver = new SleepReceiver();
//            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        }
        Log.e(TAG, "In onTaskRemoved");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimerTask();

//        try {
//            SleepReceiver receiver = new SleepReceiver();
//            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        }
        Log.e(TAG, "onDestroy of Service called! Sending Broadcast for restart...");
        Intent broadcastIntent = new Intent(this, RestartServiceReceiver.class);
        this.sendBroadcast(broadcastIntent);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setImportance(NotificationManager.IMPORTANCE_LOW);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setContentText("Hello")
//                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setBadgeIconType(Notification.BADGE_ICON_SMALL)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .build();
//        Context context = getApplicationContext();
//
//        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
//                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
//                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setLargeIcon(largeIcon(context))
//                .setContentTitle())
//                .setContentText("Content")
//                .setStyle(new NotificationCompat.BigTextStyle().bigText("big"))
//                .addAction(ignoreReminderAction(context))
//                .setDefaults(Notification.DEFAULT_VIBRATE)
//                .setContentIntent(contentIntent(context))
//                .setAutoCancel(true);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
//        }
//
//        NotificationManager notificationManager = (NotificationManager)
//                context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        startForeground(431, notification);
    }

    public void startTimer() {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            public void run() {
                Log.e("Count", "=========  "+ (mCounter++));
            }
        };
        mTimer.schedule(mTimerTask, 1000, 1000); //
    }

    public void stopTimerTask() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

//    private static Bitmap largeIcon(Context context){
//
//        Resources res = context.getResources();
//        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.ic_launcher_background);
//        return largeIcon;
//    }
//    private static Notification.Action ignoreReminderAction(Context context){
//        Intent ignoreReminderIntent = new Intent(context, MainActivity.class);
//        ignoreReminderIntent.setAction("dismiss");
//        PendingIntent ignoreReminderPendingIntent = PendingIntent.getService(
//                context,
//                453,
//                ignoreReminderIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT
//        );
//        Notification.Action ignoreReminderAction = new Notification.Action(R.drawable.ic_launcher_background,
//                "No, Thanks",
//                ignoreReminderPendingIntent
//        );
//        return ignoreReminderAction;
//    }
//    private static PendingIntent contentIntent(Context context){
//        Intent startActivityIntent = new Intent(context, MainActivity.class);
//        /*
//         * This takes a unique id for this intent, intent which must be triggered by this
//         * pending intent and FLAG_UPDATE_CURRENT, so that if the intent is created again, keep the
//         * intent but update the data
//         */
//        return PendingIntent.getActivity(context,
//                465,
//                startActivityIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//    }

}
