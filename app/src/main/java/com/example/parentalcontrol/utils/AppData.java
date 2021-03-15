package com.example.parentalcontrol.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;

public class AppData extends Application {
//getting user usage access permission
    PackageInfo packageInfo;

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
    }

    public void checkPermission(final Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (UStats.getUsageStatsList(this).isEmpty()){
                showUsageDialog(activity);
                return;
            }
        }
        PermissionUtils.permission(PermissionConstants.STORAGE)
                .callback(new PermissionUtils.SimpleCallback() {
                    @Override
                    public void onGranted() {

                        //   screenCaptureObserver();
                    }

                    @Override
                    public void onDenied() {
                        ToastUtils.showShort("Can't work without permissions");
                        activity.finish();
                    }
                }).request();
    }
    void showUsageDialog(final Activity activity) {

        new MaterialDialog.Builder(this)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        startActivity(intent);

                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ToastUtils.showShort("Can't work without permissions");
                        activity.finish();

                    }
                })
                .title("Accept Usage Access Permission")
                .content("We need this permission to start exam")
                .positiveText("Grant Access")
                .negativeText("Later")
                .theme(Theme.LIGHT)
                .contentGravity(GravityEnum.START)
                .titleGravity(GravityEnum.START)
                .show();
    }
}