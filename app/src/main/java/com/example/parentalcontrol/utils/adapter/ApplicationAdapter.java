package com.example.parentalcontrol.utils.adapter;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parentalcontrol.R;
import com.example.parentalcontrol.model.AppModel;
import com.example.parentalcontrol.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//adapter class which shows data in recyclerview on both screens(lock apps screen,usage graph screen)

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationVH> {
    List<PackageInfo> packageList;
    Activity context;
    PackageManager packageManager;
    ArrayList<String> packages=new ArrayList<>();
    PackagesList packagesList;
    ArrayList<AppModel> appModels;
    String sourceActivity;
    long maxseconds;
    public ApplicationAdapter(Activity context, List<PackageInfo> packageList, ArrayList<AppModel> appModels, long maxSeconds, PackageManager packageManager, String stringExtra, PackagesList packagesList) {
        super();

        this.context            = context;
        this.packageList        = packageList;
        this.packageManager     = packageManager;
        this.packagesList       = packagesList;
        this.appModels          = appModels;
        this.sourceActivity     = stringExtra;
        this.maxseconds         = maxSeconds;

    }

    @NonNull
    @Override
    public ApplicationVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ApplicationVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_application, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull final ApplicationVH holder, int position) {
       // final PackageInfo packageInfo = packageList.get(position);
        final AppModel appModel=appModels.get(position);
//        Drawable appIcon = packageManager
//                .getApplicationIcon(packageInfo.applicationInfo);
//        String appName = packageManager.getApplicationLabel(
//                packageInfo.applicationInfo).toString();
        Drawable appIcon=appModel.getAppImage();
        final String appName=appModel.getAppName();
       // appIcon.setBounds(0, 0, 40, 40);
       // holder.applicationName.setCompoundDrawables(appIcon, null, null, null);
      //  holder.applicationName.setCompoundDrawablePadding(15);
        holder.applicationName.setText(appName);
        holder.applicationImage.setImageDrawable(appIcon);
        if (appModel.getHours().equals("0") && appModel.getMinutes().equals("0")){
            holder.appLogs.setText(appModel.getSeconds()+"s");
        }
        else if (appModel.getHours().equals("0")){
            holder.appLogs.setText(appModel.getMinutes()+"m"+appModel.getSeconds()+"s");
        }
        else {
            holder.appLogs.setText(appModel.getHours()+"h"+appModel.getMinutes()+"m"+appModel.getSeconds()+"s");
        }

        //setting random color for each graph

        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        holder.progressBar.setProgressTintList(ColorStateList.valueOf(color));
        holder.progressBar.setProgress((int)appModel.getLngSeconds());
        holder.applicationCheckBox.setOnCheckedChangeListener(null);
        holder.applicationCheckBox.setChecked(appModel.isIschecked());
        holder.applicationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                appModel.setIschecked(isChecked);
                if (isChecked){
                   // Constants.globalInterface.getGlobalPackagesArrayList().add(packageInfo.packageName);
                    Constants.globalInterface.getGlobalPackagesArrayList().add(appModel.getAppPackage());
//                    holder.applicationCheckBox.setChecked(true);
                    appModel.setIschecked(true);
                }
                else {
//                    Constants.globalInterface.getGlobalPackagesArrayList().remove(packageInfo.packageName);
                    Constants.globalInterface.getGlobalPackagesArrayList().remove(appModel.getAppPackage());
//                    holder.applicationCheckBox.setChecked(false);
                    appModel.setIschecked(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }
    public class ApplicationVH extends RecyclerView.ViewHolder{
        TextView applicationName,appLogs;
        ImageView applicationImage;
        CheckBox applicationCheckBox;
        RelativeLayout appsUsageLayout;
        ProgressBar progressBar;
        public ApplicationVH(@NonNull View itemView) {
            super(itemView);
            applicationName=itemView.findViewById(R.id.appname);
            applicationImage=itemView.findViewById(R.id.appImage);
            applicationCheckBox=itemView.findViewById(R.id.appCheckBox);
            appLogs=itemView.findViewById(R.id.appLog);
            appsUsageLayout=itemView.findViewById(R.id.AppsUsageLayout);
            progressBar=itemView.findViewById(R.id.progressBar);
            progressBar.setMax((int)maxseconds);
            if (sourceActivity.equals(Constants.LOCK_APPS_ACTIVITY)){
                appsUsageLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
            else if (sourceActivity.equals(Constants.APPS_USAGE_ACTIVITY)){
                applicationCheckBox.setVisibility(View.GONE);
            }
        }
    }
    public interface PackagesList {
        void fireIntent(ArrayList<String> packages);
    }
}