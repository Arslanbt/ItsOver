package com.example.parentalcontrol;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.parentalcontrol.model.AppModel;
import com.example.parentalcontrol.utils.PermissionClass;
import com.example.parentalcontrol.utils.adapter.ApplicationAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ChartsActivity extends AppCompatActivity {
    PackageManager packageManager;
    private UsageStatsManager usageStatsManager;
    private ArrayList<Long> secondsArray=new ArrayList<>();
    private long maxSeconds;
    List<PackageInfo> packageList1 = new ArrayList<PackageInfo>();
    private String strLog="";
    ArrayList<AppModel> appModels=new ArrayList<>();


    BarChart barChart;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);

        barChart = findViewById(R.id.charts);
        PermissionClass.checkPermission(ChartsActivity.this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //in background getting data of user usage
        new Dialog().execute();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
    }

    class Dialog extends AsyncTask<Void, Void, Void> implements ApplicationAdapter.PackagesList {
        ProgressDialog progressDialog = new ProgressDialog(ChartsActivity.this);
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
                        PackageManager packageManager = ChartsActivity.this.getPackageManager();
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
            maxSeconds= Collections.max(secondsArray);

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //remove progress dialog when got data
            if (progressDialog != null)
            {
                progressDialog.dismiss();
//                appList =findViewById(R.id.appsUsageList);
//                appList.setLayoutManager(new LinearLayoutManager(AppsUsageChart.this));
//                appList.setHasFixedSize(true);
//                adapter = new ApplicationAdapter(AppsUsageChart.this, packageList1,appModels,maxSeconds, packageManager,getIntent().getStringExtra(Constants.SOURCE_ACTIVITY),this);
//                appList.setAdapter(adapter);


                setUpChart();
            }
        }

        @Override
        public void fireIntent(ArrayList<String> packages) {

        }
    }

    private void setUpChart() {
        BarDataSet barDataSet = new BarDataSet(barEntries(), "Apps usage");

        BarData barData = new BarData(barDataSet);

        barChart.setData(barData);



        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(getAPPNames()));
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.setVisibleXRangeMaximum(3);
        barChart.invalidate();



    }


    private List<BarEntry> barEntries(){
        List<BarEntry> arrayList = new ArrayList<>();

        for (int i = 0 ; i < appModels.size(); i++) {
            AppModel appModel = appModels.get(i);

            int value = Integer.parseInt(appModel.getMinutes()) + (Integer.parseInt(appModel.getHours()) * 60);
//            int value = (Integer.parseInt(appModel.getMinutes()) * 60) + Integer.parseInt(appModel.getSeconds())
//            + (Integer.parseInt(appModel.getHours()) * 60 * 60);
            arrayList.add(new BarEntry(i, value));

        }
        return arrayList;
    }


    private List<String> getAPPNames(){
        List<String> labels = new ArrayList<>();

        for (int i = 0 ; i < appModels.size(); i++) {
            AppModel appModel = appModels.get(i);
            labels.add(appModel.getAppName());

        }
        return labels;
    }
}
