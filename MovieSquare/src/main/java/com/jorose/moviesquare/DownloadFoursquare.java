package com.jorose.moviesquare;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by jrose on 11/26/13.
 */
public class DownloadFoursquare extends Activity
    {


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.download_foursquare);

        }

        /** Called when the user clicks the Send button */
        public void getFoursquare(View view) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=com.joelapenna.foursquared"));
            startActivity(intent);
        }

}
