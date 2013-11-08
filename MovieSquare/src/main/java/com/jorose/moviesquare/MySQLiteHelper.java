package com.jorose.moviesquare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Movies table name
    private static final String TABLE_MOVIES= "movies";

    // Movies Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_FAN_ID = "fan_id";
    private static final String KEY_THEATER_ID = "theater_id";
    private static final String KEY_RATING = "rating";
    private static final String KEY_DATESTR = "date_str";

    private static final String[] COLUMNS = {KEY_ID,KEY_TITLE,KEY_FAN_ID,KEY_THEATER_ID,KEY_RATING,KEY_DATESTR};

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "MovieDB";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create movie table
        String CREATE_MOVIE_TABLE = "CREATE TABLE movies ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, "+
                "fan_id TEXT, "+
                "theater_id TEXT, "+
                "rating REAL, "+
                "date_str TEXT )";

        // create movies table
        db.execSQL(CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older movies table if existed
        db.execSQL("DROP TABLE IF EXISTS movies");

        // create fresh movies table
        this.onCreate(db);
    }

    public void addMovie(Movie movie){
        //for logging
        Log.d("addMovie", movie.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, movie.getTitle()); // get title
        values.put(KEY_FAN_ID, movie.getFan_id()); // get fandangoID
        values.put(KEY_THEATER_ID, movie.getTheater_id()); // get theater
        values.put(KEY_RATING, movie.getRating()); // get ratingNum
        values.put(KEY_DATESTR, movie.getDateStr()); // get Date

        // 3. insert
        if (db != null) {
            db.insert(TABLE_MOVIES, // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/ values = column values
        }

        // 4. close
        db.close();
    }

    public Movie getMovie(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                null; // h. limit
        if (db != null) {
            cursor = db.query(TABLE_MOVIES, // a. table
                    COLUMNS, // b. column names
                    " id = ?", // c. selections
                    new String[] { String.valueOf(id) }, // d. selections args
                    null, // e. group by
                    null, // f. having
                    null, // g. order by
                    null);
        }

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build movie object
        Movie movie = new Movie();
        movie.setId(Integer.parseInt(cursor.getString(0)));
        movie.setTitle(cursor.getString(1));
        movie.setFan_id(cursor.getString(2));
        movie.setTheater_id(cursor.getString(3));
        movie.setRating(Float.parseFloat(cursor.getString(4)));
        movie.setDateStr(cursor.getString(5));

        //log
        Log.d("getMovie("+id+")", movie.toString());

        // 5. return movie
        return movie;
    }

    public List<Movie> getAllMovies() {
        List<Movie> movies = new LinkedList<Movie>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_MOVIES;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build movie and add it to list
        Movie movie = null;
        if (cursor.moveToFirst()) {
            do {
                movie = new Movie();
                movie.setId(Integer.parseInt(cursor.getString(0)));
                movie.setTitle(cursor.getString(1));
                movie.setFan_id(cursor.getString(2));
                movie.setTheater_id(cursor.getString(3));
                movie.setRating(Float.parseFloat(cursor.getString(4)));
                movie.setDateStr(cursor.getString(5));

                // Add movie to movies
                movies.add(movie);
            } while (cursor.moveToNext());
        }

        Log.d("getAllMovies()", movies.toString());

        // return movies
        return movies;
    }

    public int updateMovie(Movie movie) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, movie.getTitle()); // get title
        values.put(KEY_FAN_ID, movie.getFan_id()); // get fandangoID
        values.put(KEY_THEATER_ID, movie.getTheater_id()); // get theater
        values.put(KEY_RATING, movie.getRating()); // get ratingNum
        values.put(KEY_DATESTR, movie.getDateStr()); // get Date

        // 3. updating row
        int i = 0; //selection args
        if (db != null) {
            i = db.update(TABLE_MOVIES, //table
                    values, // column/value
                    KEY_ID+" = ?", // selections
                    new String[] { String.valueOf(movie.getId()) });

            db.close();
        }

        return i;

    }

    public void deleteMovie(Movie movie) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        if (db != null) {
            db.delete(TABLE_MOVIES, //table name
                    KEY_ID+" = ?",  // selections
                    new String[] { String.valueOf(movie.getId()) }); //selections args
            db.close();
        }

        //log
        Log.d("deleteMovie", movie.toString());

    }
}
