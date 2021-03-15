package com.example.parentalcontrol.model;

import android.graphics.drawable.Drawable;
//Model class for Application usage history
public class AppModel {
    private String appName;
    private String appPackage;
    private Drawable appImage;
    private long lngSeconds;
    private String hours="0";
    private String minutes="0";
    private String seconds="0";
    private boolean ischecked=false;

    public long getLngSeconds() {
        return lngSeconds;
    }

    public void setLngSeconds(long lngSeconds) {
        this.lngSeconds = lngSeconds;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public String getSeconds() {
        return seconds;
    }

    public void setSeconds(String seconds) {
        this.seconds = seconds;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public Drawable getAppImage() {
        return appImage;
    }

    public void setAppImage(Drawable appImage) {
        this.appImage = appImage;
    }

    public boolean isIschecked() {
        return ischecked;
    }

    public void setIschecked(boolean ischecked) {
        this.ischecked = ischecked;
    }
}
