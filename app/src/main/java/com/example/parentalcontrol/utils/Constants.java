package com.example.parentalcontrol.utils;

public interface Constants {
    //constants of our app
    public static Variables globalInterface = new Variables();
    public static String SHARED_PREFS_NAME="loginPrefs";
    public static String SHARED_PREFS_AppLock="lockPrefs";
    public static String START_TIME_AppLock="startTime";
    public static String END_TIME_AppLock="endTime";
    public static String APP_LOCK_STATUS="appLockStatus";
    String SOURCE_ACTIVITY="sourceActivity";
    String LOCK_APPS_ACTIVITY="lockAppsActivity";
    String APPS_USAGE_ACTIVITY="apps_usage_activity";
}
