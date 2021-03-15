package com.example.parentalcontrol;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.parentalcontrol.model.AppModel;
import com.example.parentalcontrol.utils.AppData;
import com.example.parentalcontrol.utils.Constants;
import com.example.parentalcontrol.utils.PermissionClass;
import com.example.parentalcontrol.utils.adapter.ApplicationAdapter;

import android.app.ProgressDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import me.ithebk.barchart.BarChart;
import me.ithebk.barchart.BarChartModel;
//this screen is for showing usage cahrt to user
public class AppsUsageChart extends AppCompatActivity implements AdapterView.OnItemClickListener, ApplicationAdapter.PackagesList {
    PackageManager packageManager;
    RecyclerView appList;
    ApplicationAdapter adapter;
    Long currentTime;
    ArrayList<AppModel> appModels=new ArrayList<>();
    private UsageStatsManager usageStatsManager;
    private String strLog="";
    private ArrayList<Long> secondsArray=new ArrayList<>();
    private long maxSeconds;
    List<PackageInfo> packageList1 = new ArrayList<PackageInfo>();
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_usage_chart);
        PermissionClass.checkPermission(AppsUsageChart.this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //in background getting data of user usage
        new Dialog().execute();
    }
    //checks if not system package
    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
        PackageInfo packageInfo = (PackageInfo) parent
                .getItemAtPosition(position);
        AppData appData = (AppData) getApplicationContext();
        appData.setPackageInfo(packageInfo);
//        Intent appInfo = new Intent(getApplicationContext(), ApkInfo.class);
//        startActivity(appInfo);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void fireIntent(ArrayList<String> packages) {

    }
    //background task for getting usage stats
    class Dialog extends AsyncTask<Void, Void, Void> implements ApplicationAdapter.PackagesList {
        ProgressDialog progressDialog = new ProgressDialog(AppsUsageChart.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Please Wait");
            progressDialog.setTitle("Getting Usage Data...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected Void doInBackground(Void... params) {
            packageManager = getPackageManager();
            List<PackageInfo> packageList = packageManager
                    .getInstalledPackages(PackageManager.GET_PERMISSIONS);
            usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            Calendar beginCal = Calendar.getInstance();
            beginCal.setTime(new Date());
            beginCal.add(Calendar.DAY_OF_MONTH, -1);
//        beginCal.set(Calendar.MONTH, 2);
//        beginCal.set(Calendar.YEAR, 2020);

            Calendar endCal = Calendar.getInstance();
            Map<String, UsageStats> queryUsageStats = usageStatsManager.queryAndAggregateUsageStats(beginCal.getTimeInMillis(), endCal.getTimeInMillis());
            Log.e("time","sttartTime"+beginCal.getTimeInMillis()+" end time"+endCal.getTimeInMillis());
            for(PackageInfo pi : packageList) {
                boolean b = isSystemPackage(pi);
                if(!b) {
                    AppModel appModel=new AppModel();
                    for (Map.Entry<String, UsageStats> entry : queryUsageStats.entrySet()) {
                        PackageManager packageManager = AppsUsageChart.this.getPackageManager();
                        if (entry.getKey().equals(pi.packageName)) {
                            Log.e("list", entry.getKey() + "");
                            Log.e("Log", entry.getValue().getTotalTimeInForeground()/1000 + "");
                            long longVal = entry.getValue().getTotalTimeInForeground() / 1000;
                            int hours = (int) longVal / 3600;
                            int remainder = (int) longVal - hours * 3600;
                            int mins = remainder / 60;
                            remainder = remainder - mins * 60;
                            int secs = remainder;
                            strLog = hours + " " + mins + " " + secs;
                            Log.e("time", hours + " " + mins + " " + secs);
                            appModel.setAppName(packageManager.getApplicationLabel(pi.applicationInfo).toString());
                            appModel.setAppImage(packageManager.getApplicationIcon(pi.applicationInfo));
                            appModel.setAppPackage(pi.packageName);
                            secondsArray.add(longVal);
                            appModel.setLngSeconds(longVal);
                            appModel.setHours(hours+"");
                            appModel.setMinutes(mins+"");
                            appModel.setSeconds(secs+"");
                            appModels.add(appModel);
                            packageList1.add(pi);
                            break;
                        }
                    }
                }
            }
            maxSeconds=Collections.max(secondsArray);

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //remove progress dialog when got data
            if (progressDialog != null)
            {
                progressDialog.dismiss();
                appList =findViewById(R.id.appsUsageList);
                appList.setLayoutManager(new LinearLayoutManager(AppsUsageChart.this));
                appList.setHasFixedSize(true);
                adapter = new ApplicationAdapter(AppsUsageChart.this, packageList1,appModels,maxSeconds, packageManager,getIntent().getStringExtra(Constants.SOURCE_ACTIVITY),this);
                appList.setAdapter(adapter);
            }
        }

        @Override
        public void fireIntent(ArrayList<String> packages) {

        }
    }
}