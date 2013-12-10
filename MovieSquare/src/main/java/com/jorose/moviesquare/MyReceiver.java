package com.jorose.moviesquare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver
{
    private MySQLiteHelper db;

    @Override
    public void onReceive(Context context, Intent intent)
    {

            Intent service1 = new Intent(context, MyAlarmService.class);
            context.startService(service1);

    }
}
