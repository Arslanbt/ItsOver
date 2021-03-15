package com.example.parentalcontrol.utils;

import android.app.Application;

import java.util.ArrayList;

//Different variables of our app

public class Variables extends Application {
    private ArrayList<String> globalPackagesArrayList=new ArrayList<>();

    public ArrayList<String> getGlobalPackagesArrayList() {
        return globalPackagesArrayList;
    }

    public void setGlobalPackagesArrayList(ArrayList<String> globalPackagesArrayList) {
        this.globalPackagesArrayList = globalPackagesArrayList;
    }
}
