package com.example.parentalcontrol.service;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.ResultReceiver;
import android.util.Log;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ServiceUtils;
import com.blankj.utilcode.util.Utils;
import com.example.parentalcontrol.AppLockActivity;
import com.example.parentalcontrol.utils.Constants;
import com.example.parentalcontrol.utils.TinyDB;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;


//This is a service class which runs in background
public class CheckRunningAppService extends Service {
    public static String currentApp, prevApp;
    public static boolean pauseService = false;
    public static final int RESULT_OK = -1;
    /**
     * Key used in the intent extras for the result receiver.
     */
    public static final String KEY_RECEIVER = "KEY_RECEIVER";
    /**
     * Key used in the result bundle for the message.
     */
    public static final String KEY_MESSAGE = "KEY_MESSAGE";
    Timer timer;
    TinyDB tinyDB;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ClearFromRecentService", "Service Started");
        Utils.init(this.getApplication());
        timer = new Timer();
        tinyDB = new TinyDB(this.getApplicationContext());
        final Long startTime=tinyDB.getLong(Constants.START_TIME_AppLock,0);
        final Long endTime=tinyDB.getLong(Constants.END_TIME_AppLock,0);
        final ArrayList<String> lockedAppsPackage=tinyDB.getListString(Constants.SHARED_PREFS_AppLock);
//        prevApp = currentApp;
//        currentApp = getForegroundApp();
//        ToastUtils.showShort("onStarCommand");
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (!tinyDB.getBoolean(Constants.APP_LOCK_STATUS)){
                    //Stops service
                    ServiceUtils.stopService(CheckRunningAppService.class);
                }

//                if (/*!isScreenOn() || */!tinyDB.getBoolean("applock"))
//                {
//                    ServiceUtils.stopService(checkRunningAppsService.class);
//                    return;
//                }
                prevApp = currentApp;
                currentApp = getForegroundApp();
                if (currentApp==null || prevApp==null) return;
                //checks if the opened app package is in our locked packages
                for (int i = 0; i<lockedAppsPackage.size();i++){
                    Log.e("lockedappspackages",lockedAppsPackage.get(i));
                    if (currentApp.equals(lockedAppsPackage.get(i))) {
//                        Log.e("package","is equal");
//                        Log.e("starttimemillis",startTime+"");
//                        Log.e("endtimemillis",endTime+"");
                        final Calendar currentTime=Calendar.getInstance();
                      //  Log.e("endtimemillis",currentTime.getTimeInMillis()+"");
                        if (currentTime.getTimeInMillis()>startTime && currentTime.getTimeInMillis()<endTime){
                            Log.e("time","inside lock time");
                        startMessageActivity();
                        break;
                        }
                    }
                }
                Long currentTime=Calendar.getInstance().getTimeInMillis();
                if (currentTime>endTime){
                    tinyDB.putBoolean(Constants.APP_LOCK_STATUS,false);
                }
            }
        }, 0,10);  // every 1 seconds
        return START_STICKY;
    }

    //this opens the lockeed app screen
    /**
     * Starts an activity for retrieving a message.
     */
    private void startMessageActivity() {
        Log.e("inside","startmesssage");
        Intent intent = new Intent(this, AppLockActivity.class);
        // Pack the parcelable receiver into the intent extras so the
        // activity can access it.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        intent.putExtra(KEY_RECEIVER, new MessageReceiver());
//        intent.putExtra("pkgname",currentApp);
//        pauseService = true;

//        Log.e("P C SECONDLAST",prevApp+"   "+currentApp);

        prevApp = currentApp;
        currentApp = getPackageName();

//        Log.e("P C LAST",prevApp+"   "+currentApp);
        startActivity(intent);
    }
    //getting current opened app

    public String getForegroundApp() {
        String currentApp = "NULL";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }

        return currentApp;
    }

    @Override
    public void onDestroy() {
//        super.onDestroy();
        if (!tinyDB.getBoolean(Constants.APP_LOCK_STATUS)) {
            ServiceUtils.stopService(CheckRunningAppService.class);
        }
//        if (timer!=null) timer.cancel();
//        ServiceUtils.stopService(CheckRunningAppService.class);
//        timer = null;
//        Log.d("ClearFromRecentService", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ClearFromRecentService", "END");
        //Code here
//        startService(new Intent(getBaseContext(), FirebaseMessagingService.class));
//        startService(new Intent(getBaseContext(), FirebaseInstanceIDService.class));
//        ToastUtils.showShort("task Removed");

        //starts service again if user tries to remove app from memeory

        if (tinyDB==null)
        {
            tinyDB = new TinyDB(this);
        }
        if (tinyDB.getBoolean(Constants.APP_LOCK_STATUS)) {
            ServiceUtils.startService(CheckRunningAppService.class);
        }
        else
        {
            onDestroy();
        }
//        stopSelf();
    }

    /**
     * Used by an activity to send a result back to our service.
     */
    class MessageReceiver extends ResultReceiver {

        public MessageReceiver() {
            // Pass in a handler or null if you don't care about the thread
            // on which your code is executed.
            super(null);
        }

        /**
         * Called when there's a result available.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            // Define and handle your own result codes
            if (resultCode != RESULT_OK) {
                return;
            }

            // Let's assume that a successful result includes a message.
            String message = resultData.getString(KEY_MESSAGE);

            if (message != null && message.equals("sendIn")) {

//                ToastUtils.showShort("onreciveResult");
//                currentApp = getForegroundApp();
//                currentApp = prevApp;

                pauseService = false;
            }
            else
            {
                pauseService = false;

            }
            // Now you can do something with it.
        }

    }
}