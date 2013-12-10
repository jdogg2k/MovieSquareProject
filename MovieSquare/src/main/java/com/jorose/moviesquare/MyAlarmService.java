package com.jorose.moviesquare;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;



public class MyAlarmService extends Service
{

    private MovieShowings showings;

    @Override
    public IBinder onBind(Intent arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @SuppressWarnings("static-access")
    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);

        Global global = ((Global)getApplicationContext());

        String mName = global.get_checkinMovieName();

        Intent intent1 = new Intent(this.getApplicationContext(),EditMovie.class);
        intent1.putExtra("mName", mName);


        PendingIntent pendingNotificationIntent = PendingIntent.getActivity( this.getApplicationContext(),0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);

        final int soundResId = R.raw.tada;
        final String packageName = this.getApplicationContext().getPackageName();


        // Base notification
        NotificationCompat.Builder b = new NotificationCompat.Builder(this);
        b.setSmallIcon(R.drawable.ic_launcher);
        b.setContentTitle("Curtain Closed?");
        b.setContentText(mName);
        b.setTicker("Curtain Closed?");
        b.setWhen(System.currentTimeMillis());
        b.setAutoCancel(true);
        b.setSound(Uri.parse("android.resource://" + packageName + "/" + soundResId));
        b.setContentIntent(pendingNotificationIntent);

        Resources res = this.getResources();

        // BigPictureStyle
        NotificationCompat.BigPictureStyle s = new NotificationCompat.BigPictureStyle();
        s.bigPicture(BitmapFactory.decodeResource(res, R.drawable.theater));
        s.setBigContentTitle("How was " + mName + "?");
        s.setSummaryText("Leave your 0-5 star rating!");
        b.setStyle(s);
        Notification n = b.build();

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(3,n);
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}
