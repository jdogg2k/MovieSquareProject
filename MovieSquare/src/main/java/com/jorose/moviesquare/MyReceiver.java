package com.jorose.moviesquare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MyReceiver extends BroadcastReceiver
{
    private MySQLiteHelper db;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean notificationsSet = prefs.getBoolean("pref_key_notifications", true);
        if (notificationsSet){
            Intent service1 = new Intent(context, MyAlarmService.class);
            context.startService(service1);
        }
    }
}
