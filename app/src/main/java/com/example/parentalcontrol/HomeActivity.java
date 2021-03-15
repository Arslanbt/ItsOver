package com.example.parentalcontrol;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.blankj.utilcode.util.ServiceUtils;
import com.example.parentalcontrol.model.AppModel;
import com.example.parentalcontrol.service.CheckRunningAppService;
import com.example.parentalcontrol.utils.AppData;
import com.example.parentalcontrol.utils.Constants;
import com.example.parentalcontrol.utils.PermissionClass;
import com.example.parentalcontrol.utils.TinyDB;
import com.example.parentalcontrol.utils.adapter.ApplicationAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
    //activity for app locking where user slect app to block
public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, ApplicationAdapter.PackagesList {
    PackageManager packageManager;
    RecyclerView appList;
    ApplicationAdapter adapter;
    Button btnDone,stop;
    private TinyDB tinyDB;
    Long currentTime;
    ArrayList<AppModel> appModels=new ArrayList<>();
    private UsageStatsManager usageStatsManager;
    private String strLog="";
    private ArrayList<Long> secondsArray=new ArrayList<>();
    List<PackageInfo> packageList1 = new ArrayList<PackageInfo>();
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        PermissionClass.checkPermission(HomeActivity.this);
        btnDone=findViewById(R.id.btnDone);
        stop=findViewById(R.id.btnStopService);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tinyDB=new TinyDB(HomeActivity.this);
      btnDone.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              if (Constants.globalInterface.getGlobalPackagesArrayList().size()>0) {
                  for (int i = 0; i < Constants.globalInterface.getGlobalPackagesArrayList().size(); i++) {
                      Log.e("packages", Constants.globalInterface.getGlobalPackagesArrayList().get(i));
                  }
                  tinyDB.putListString(Constants.SHARED_PREFS_AppLock,Constants.globalInterface.getGlobalPackagesArrayList());
                  Constants.globalInterface.getGlobalPackagesArrayList().clear();
                  startActivity(new Intent(HomeActivity.this,SelectTime.class));
                  onStartService();
              }
              else {
                  Toast.makeText(HomeActivity.this, "Select at least 1 app", Toast.LENGTH_SHORT).show();
              }
          }
      });
      stop.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              tinyDB.clear();
              ServiceUtils.stopService(CheckRunningAppService.class);
              onServiceStop();
          }
      });
      //getting user usage data in background
        new Dialog().execute();
    }

    private void onStartService() {
        stop.setVisibility(View.VISIBLE);
        btnDone.setVisibility(View.GONE);
    }

    private void onServiceStop() {
        stop.setVisibility(View.GONE);
        btnDone.setVisibility(View.VISIBLE);
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        tinyDB=new TinyDB(HomeActivity.this);
        currentTime=Calendar.getInstance().getTimeInMillis();
        Long startTime=tinyDB.getLong(Constants.START_TIME_AppLock,0);
        Long endTime=tinyDB.getLong(Constants.END_TIME_AppLock,0);
        if (tinyDB.getBoolean(Constants.APP_LOCK_STATUS)){
            onStartService();
        }
        else if (!tinyDB.getBoolean(Constants.APP_LOCK_STATUS)){
            onServiceStop();
        }
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
    public void fireIntent(ArrayList<String> packages) {
        for (int i=0;i<packages.size();i++){
            Log.e("packages",packages.get(i));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    //getting user usage data in background
    class Dialog extends AsyncTask<Void, Void, Void> implements ApplicationAdapter.PackagesList {
        ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this);
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
            beginCal.add(Calendar.DAY_OF_MONTH, -5);
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
                        PackageManager packageManager = HomeActivity.this.getPackageManager();
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

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (progressDialog != null)
            {
                progressDialog.dismiss();
                appList =findViewById(R.id.applist);
                appList.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                appList.setHasFixedSize(true);
                adapter = new ApplicationAdapter(HomeActivity.this, packageList1,appModels, 0, packageManager,getIntent().getStringExtra(Constants.SOURCE_ACTIVITY),this);
                appList.setAdapter(adapter);
            }
        }

        @Override
        public void fireIntent(ArrayList<String> packages) {

        }
    }
}