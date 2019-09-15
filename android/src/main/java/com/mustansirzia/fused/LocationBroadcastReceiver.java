package com.mustansirzia.fused;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import com.facebook.react.HeadlessJsTaskService;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;


import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.List;

public class LocationBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "FUSED_LOCATION";
    private ReactContext reactContext;

    @Override
    public void onReceive(Context context, final Intent intent) {
        Log.d("sdasd", "onReceive: BROADCAST RECIBIDO!!!");
        String s1=Boolean.toString(isAppOnForeground((context)));
        Log.i(TAG, s1);
        /**
         This part will be called everytime network connection is changed
         e.g. Connected -> Not Connected
         **/
        if (!isAppOnForeground((context))) {
            Log.i(TAG, "helloThere");
            /**
             We will start our service and send extra info about
             network connections
             **/

            String someData = intent.getStringExtra("my-extra-data");
            boolean hasInternet = isNetworkAvailable(context);
            Intent serviceIntent = new Intent(context, LocationService.class);
            serviceIntent.putExtra("LOCATION_EVENT", someData);
            serviceIntent.putExtra("hasInternet", hasInternet);
            context.startService(serviceIntent);
            HeadlessJsTaskService.acquireWakeLockNow(context);

        }
    }

    private boolean isAppOnForeground(Context context) {
        /**
         We need to check if app is in foreground otherwise the app will crash.
         http://stackoverflow.com/questions/8489993/check-android-application-is-in-foreground-or-not
         **/
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses =
                activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance ==
                    ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }


}
