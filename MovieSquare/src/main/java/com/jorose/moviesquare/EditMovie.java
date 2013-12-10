package com.jorose.moviesquare;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.ParseException;

/**
 * Created by jrose on 12/6/13.
 */
public class EditMovie extends Activity {

    MySQLiteHelper db;
    Movie selectedMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the message from the intent
        Intent intent = getIntent();
        setContentView(R.layout.edit_my_movie);

        db = new MySQLiteHelper(getApplicationContext());

        String m = getIntent().getStringExtra("mName");

        editMovie(m);
    }

    public void editMovie(String title){

        selectedMovie = db.getMovieFromTitle(title);

        TextView mName = (TextView) findViewById(R.id.editMyMovieName);
        mName.setText(title);

        final RatingBar mRating = (RatingBar) findViewById(R.id.editMyRating);
        mRating.setRating(selectedMovie.getRating());

        Button save = (Button) findViewById(R.id.saveMovieButton);
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectedMovie.setRating(mRating.getRating());
                db.updateMovie(selectedMovie);
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
