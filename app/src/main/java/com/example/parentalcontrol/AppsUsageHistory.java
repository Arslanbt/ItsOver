package com.example.parentalcontrol;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;
import java.util.List;
//we are not using this file currently.
public class AppsUsageHistory extends AppCompatActivity {
    private UsageStatsManager usageStatsManager;
    private List<UsageStats> queryUsageStats;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_usage_history);
        final UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar beginCal = Calendar.getInstance();
        beginCal.set(Calendar.DAY_OF_MONTH, 6);
        beginCal.set(Calendar.MONTH, 2);
        beginCal.set(Calendar.YEAR, 2020);

        Calendar endCal = Calendar.getInstance();
        final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, beginCal.getTimeInMillis(), endCal.getTimeInMillis());
        System.out.println("results for " + beginCal.getTime().toGMTString() + " - " + endCal.getTime().toGMTString());
        for (UsageStats app : queryUsageStats) {
            Log.e("appusage",app.getPackageName() + " | " + (float) (app.getTotalTimeInForeground() / 1000));
             long longVal = app.getTotalTimeInForeground()/1000;
            int hours = (int) longVal / 3600;
            int remainder = (int) longVal - hours * 3600;
            int mins = remainder / 60;
            remainder = remainder - mins * 60;
            int secs = remainder;
            Log.e("totaltime",hours+" "+mins+" "+secs);
        }
    }
}