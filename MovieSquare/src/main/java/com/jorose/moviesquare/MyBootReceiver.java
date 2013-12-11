package com.jorose.moviesquare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MyBootReceiver extends BroadcastReceiver
{
    private MySQLiteHelper db;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean notificationsSet = prefs.getBoolean("pref_key_notifications", true);
        if (notificationsSet){
            Global global = ((Global)context.getApplicationContext());

            db = new MySQLiteHelper(context);


            Movie m = db.getMostRecentMovie();
            if (m.getRating() == 0.0){ //check if latest movie is rated
                global.set_checkinMovieName(m.getTitle());
                global.set_checkinMovieID(Integer.toString(m.getId()));
                Intent service1 = new Intent(context, MyAlarmService.class);
                context.startService(service1);
            }

        }
    }
}
