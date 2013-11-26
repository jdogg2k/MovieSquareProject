package com.jorose.moviesquare;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.foursquare.android.nativeoauth.FoursquareCancelException;
import com.foursquare.android.nativeoauth.FoursquareDenyException;
import com.foursquare.android.nativeoauth.FoursquareInvalidRequestException;
import com.foursquare.android.nativeoauth.FoursquareOAuth;
import com.foursquare.android.nativeoauth.FoursquareOAuthException;
import com.foursquare.android.nativeoauth.FoursquareUnsupportedVersionException;
import com.foursquare.android.nativeoauth.model.AccessTokenResponse;
import com.foursquare.android.nativeoauth.model.AuthCodeResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends FragmentActivity {

    public final static String SELECTED_VENUE_ID = "com.jorose.moviesquare.VENUE_ID";
    public final static String SELECTED_VENUE_NAME = "com.jorose.moviesquare.VENUE_NAME";
    public final static String SELECTED_VENUE_LAT = "com.jorose.moviesquare.VENUE_LAT";
    public final static String SELECTED_VENUE_LNG= "com.jorose.moviesquare.VENUE_LNG";
    String jsonResult;
    GetChildList childList;
    String selVenueID;
    String selVenueName;
    String selVenueLat;
    String selVenueLng;
    Double curLat;
    Double curLong;
    ActionMode mActionMode;
    MovieHelper mHelper;
    SimpleAdapter movieAdapter;
    View editMovieLayout;
    MySQLiteHelper db;

    ListView lv;

    private static final int REQUEST_CODE_FSQ_CONNECT = 200;
    private static final int REQUEST_CODE_FSQ_TOKEN_EXCHANGE = 201;

    private String[] mMenuTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private FrameLayout frame;

    private Movie selectedMovie;
    private String sortStr = "id";
    private String sortDir = "DESC";

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private LayoutInflater inflater;

    private static final LatLng SYDNEY = new LatLng(-33.88,151.21);
    private static final LatLng MOUNTAIN_VIEW = new LatLng(37.4, -122.1);

    private GoogleMap mMap;
    private int menuPos = 0;

    public FragmentManager fManager ;

    SupportMapFragment mMapFragment;


    /**
     * Obtain your client id and secret from:
     * https://foursquare.com/developers/apps
     */
    public static final String CLIENT_ID = "ZAJDKMJ13CTC25VCZFYTMQCV3YMMZXSQFUZIOEMUOVVRFQGG";
    public static final String CLIENT_SECRET = "43T3FFM5KWHRXUP1M0BJVUV3ZJNAIYC2WVECDUJJJRHAG2EZ";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //todo - TAKE THIS OFF AND DO background processing properly
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTitle = mDrawerTitle = getTitle();
        mMenuTitles = getResources().getStringArray(R.array.menu_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        frame = (FrameLayout) findViewById(R.id.content_frame);

        inflater = getLayoutInflater();

        //FrameLayout frame = (FrameLayout) findViewById(R.id.content_frame);
        //inflater.inflate(R.layout.venue_list, frame);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mMenuTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            try {
                selectItem(0, this.findViewById(R.id.content_frame));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        mHelper = new MovieHelper();
        fManager = getSupportFragmentManager();
        db = new MySQLiteHelper(frame.getContext());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getTitle().toString().contains("Sort by")){
            if (sortDir.equals("DESC")){
                sortDir = "ASC";
            } else {
                sortDir = "DESC";
            }
        }

        if (item.getTitle().toString().equals("Sort by Title")){
            sortStr = "title";
            try {
                populateMyMovies(lv);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return true;
        }

        if (item.getTitle().toString().equals("Sort by Rating")){
            sortStr = "rating";
            try {
                populateMyMovies(lv);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return true;
        }

        if (item.getTitle().toString().equals("Sort by Theater")){
            sortStr = "theater_id";
            try {
                populateMyMovies(lv);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            try {
                selectItem(position, view);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position, View v) throws ParseException {
        FragmentManager fm = getSupportFragmentManager();

        frame.removeAllViews();
        int tView = R.layout.venue_list;
        if (position == 1){
           if (fm.getFragments() != null){
               fm.getFragments().clear();
           }
            tView = R.layout.movie_map;
        }

        if (position == 2){
            tView = R.layout.about_layout;
        }

        inflater.inflate(tView, frame);

        if (position == 1){
            MapPageFragment fragment = (MapPageFragment) fm.getFragments().get(0);
            FragmentManager cm = fragment.getChildFragmentManager();
            SupportMapFragment mapFragment = (SupportMapFragment) cm.findFragmentById(R.id.map);
            mMap = mapFragment.getMap();
            launchMap();
        }

        if (position == 0) {
            if (ensureUi()){
                childList = new GetChildList();
                childList.execute();
            }
        }

        if (position == 2){
            try {
                String versionName = v.getContext().getPackageManager().getPackageInfo(v.getContext().getPackageName(), 0).versionName;
                TextView tv = (TextView) findViewById(R.id.appVersion);
                tv.setText("Version: " + versionName + " (Beta)");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        menuPos = position;

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mMenuTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private class MyMovieBinder implements SimpleAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {
            if(view.getId() == R.id.my_movie_rating){
                float ratingValue = (Float) data;
                RatingBar ratingBar = (RatingBar) view;
                ratingBar.setRating(ratingValue);
                return true;
            }
            if(view.getId() == R.id.my_movie_icon){
                String venueType = (String) data;
                ImageView iv = (ImageView) view;
                if (venueType.equals("AMC")){
                    iv.setImageResource(R.drawable.amc);
                } else if (venueType.equals("Regal")){
                    iv.setImageResource(R.drawable.regal);
                } else {
                    iv.setImageResource(R.drawable.video);
                }
                return true;
            }
            return false;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_menu, menu);
    }

    private void populateMyMovies(ListView tLV) throws ParseException {
        List<Movie> movies = db.getAllMovies(sortStr, sortDir);

        List<Map<String, String>> list = new ArrayList<Map<String,String>>();

        if (movies.size() < 1){

            toastMessage(MainActivity.this, getString(R.string.no_movie_notice));

        } else {

            for (Movie i : movies) {
                Map map = new HashMap();
                map.put("id", i.getId());
                map.put("title", i.getTitle());

                String target = i.getDateStr();
                DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
                DateFormat newf = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.ENGLISH);
                Date mDate =  df.parse(target);
                String newDate = newf.format(mDate);

                map.put("date", newDate);
                map.put("rating", i.getRating());
                map.put("lat", String.valueOf(i.getVenue().getLat()));
                map.put("lng", String.valueOf(i.getVenue().getLng()));
                map.put("type", i.getVenue().getVenue_type());
                map.put("venue_name", i.getVenue().getVenue_name());
                list.add(map);
            }

            movieAdapter = new SimpleAdapter(this, list, R.layout.my_movies_layout, new String[] { "title", "date", "rating", "type" },
                    new int[] { R.id.my_movie_title, R.id.my_movie_date, R.id.my_movie_rating, R.id.my_movie_icon });
            movieAdapter.setViewBinder(new MyMovieBinder());
            tLV.setAdapter(movieAdapter);
            tLV.setLongClickable(true);

            tLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Map item = (Map)parent.getAdapter().getItem(position);
                    LatLng latlong = new LatLng(Double.valueOf(item.get("lat").toString()), Double.valueOf(item.get("lng").toString()));

                    mMap.clear();
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(latlong)
                            .title(item.get("title").toString())
                            .snippet(item.get("venue_name").toString()));


                    // Zoom in, animating the camera.
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 13), 800, null);
                    marker.showInfoWindow();
                }
            });
        }

    }

    private void launchMap() throws ParseException {

        lv = (ListView) frame.findViewById(R.id.myMovieView);
        registerForContextMenu(lv);

        populateMyMovies(lv);

        mMap.setMyLocationEnabled(true);
        LatLng curLatLong = new LatLng(curLat, curLong);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curLatLong, 11), 400, null);
    }

    @Override
    public void setTitle(CharSequence title) {
       mTitle = title;
       getActionBar().setTitle(mTitle);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        String thisTitle = getActionBar().getTitle().toString();
        if(thisTitle.equals("My Movies")){
            menu.findItem(R.id.action_sort).setVisible(true);

        }else{
            menu.findItem(R.id.action_sort).setVisible(false);
        }
        return true;
    }



    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int hitMovie = info.position;
        HashMap hMap = (HashMap) lv.getItemAtPosition(hitMovie);
        String selMovieNumber = hMap.get("id").toString();
        switch (item.getItemId()) {
            case R.id.edit_movie:
                String selMovieName = hMap.get("title").toString();
                String selRating = hMap.get("rating").toString();
                String selDate = hMap.get("date").toString();
                editMovie(selMovieNumber, selMovieName, selRating, selDate);
                return true;
            case R.id.delete_movie:
                mHelper.RemoveMovie(selMovieNumber, lv.getContext());
                try {
                    populateMyMovies(lv);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void editMovie(String mNum, String title, String rating, String date){
        LinearLayout viewGroup = (LinearLayout) findViewById(R.id.popupLinearLayout);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        editMovieLayout = layoutInflater.inflate(R.layout.edit_movie, viewGroup);

        selectedMovie = db.getMovie(Integer.parseInt(mNum));

        int popupWidth = 800;
        int popupHeight = 900;

        TextView mName = (TextView) editMovieLayout.findViewById(R.id.editMovieName);
        mName.setText(title);

        final RatingBar mRating = (RatingBar) editMovieLayout.findViewById(R.id.editRating);
        mRating.setRating(Float.parseFloat(rating));

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow();
        popup.setContentView(editMovieLayout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);
        //popup.setAnimationStyle(R.style.PopupWindowAnimation);

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(editMovieLayout, Gravity.CENTER, 0, 0);


        ImageButton close = (ImageButton) editMovieLayout.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popup.dismiss();

            }
        });

        Button save = (Button) editMovieLayout.findViewById(R.id.saveMovieButton);
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectedMovie.setRating(mRating.getRating());
                db.updateMovie(selectedMovie);
                try {
                    populateMyMovies(lv);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                popup.dismiss();

            }
        });
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
        }
    }

    public void showDatePickerDialog(View v) {
        //DialogFragment newFragment = new DatePickerFragment();
        //newFragment.show
    }

    private class GetChildList extends AsyncTask<String, Void, String>{

        private String currentDateandTime = "20130715";  //yyyymmdd

        @Override
        protected String doInBackground(String... params) {
            // TODO Check for location via GPS OR WIFI

            //LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            //Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double[] latlong = getGPS();
            curLat = latlong[0];
            curLong = latlong[1];

            String latLong = Double.toString(curLat) + "," + Double.toString(curLong);

            DefaultHttpClient httpclient = new DefaultHttpClient();
            final HttpParams httpParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
            HttpConnectionParams.setSoTimeout(httpParams, 30000);

            HttpGet httppost = new HttpGet("https://api.foursquare.com/v2/venues/search?intent=checkin&ll="+latLong+"&client_id="+CLIENT_ID+"&client_secret="+CLIENT_SECRET+"&v="+currentDateandTime+"&categoryId=4bf58dd8d48988d17f941735&limit=20"); //

            try{

                HttpResponse response = httpclient.execute(httppost);  //response class to handle responses
                jsonResult = inputStreamToString(response.getEntity().getContent()).toString();

                /** The parsing of the xml data is done in a non-ui thread */
                ListViewLoaderTask listViewLoaderTask = new ListViewLoaderTask();

                /** Start parsing xml data */
                listViewLoaderTask.execute(jsonResult);
            }
            catch(ConnectTimeoutException e){
                Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_LONG).show();
            }
            catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return jsonResult;
        }

        private double[] getGPS() {
            LocationManager lm = (LocationManager) getSystemService(
                    Context.LOCATION_SERVICE);
            List<String> providers = lm.getProviders(true);

            Location l = null;

            for (int i=providers.size()-1; i>=0; i--) {
                l = lm.getLastKnownLocation(providers.get(i));
                if (l != null) break;
            }

            double[] gps = new double[2];
            if (l != null) {
                gps[0] = l.getLatitude();
                gps[1] = l.getLongitude();
            }

            return gps;
        }

        protected void onPostExecute(String Result){
            try{

                //Toast.makeText(getApplicationContext(), "R E S U L T :"+jsonResult, Toast.LENGTH_LONG).show();
                System.out.println(jsonResult);
                //showing result

            }catch(Exception E){
                Toast.makeText(getApplicationContext(), "Error:"+E.getMessage(), Toast.LENGTH_LONG).show();
            }

        }

        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            try {
                while ((rLine = rd.readLine()) != null) {
                    answer.append(rLine);
                }
            }

            catch (IOException e) {
                e.printStackTrace();
            }
            return answer;
        }

        private class ListViewLoaderTask extends AsyncTask<String, Void, SimpleAdapter>{

            JSONObject jObject;
            /** Doing the parsing of xml data in a non-ui thread */
            @Override
            protected SimpleAdapter doInBackground(String... strJson) {
                try{
                    jObject = new JSONObject(strJson[0]);
                    VenueJSONParser venueJsonParser = new VenueJSONParser();
                    venueJsonParser.parse(jObject);
                }catch(Exception e){
                    Log.d("JSON Exception1",e.toString());
                }

                VenueJSONParser venueJsonParser = new VenueJSONParser();

                List<HashMap<String, String>> venues = null;

                try{
                    /** Getting the parsed data as a List construct */
                    venues = venueJsonParser.parse(jObject);
                }catch(Exception e){
                    Log.d("Exception",e.toString());
                }

                /** Keys used in Hashmap */
                String[] from = {"name","location","checkIn"};

                /** Ids of views in listview_layout */
                int[] to = { R.id.venue_name,R.id.venue_location,R.id.checkInCount};

                /** Instantiating an adapter to store each items
                 *  R.layout.listview_layout defines the layout of each item
                 */
                SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), venues, R.layout.venue_lv_layout, from, to);

                return adapter;
            }

            /** Invoked by the Android system on "doInBackground" is executed completely */
            /** This will be executed in ui thread */
            @Override
            protected void onPostExecute(SimpleAdapter adapter) {

                /** Getting a reference to listview of main.xml layout file */
                final ListView listView = (ListView) findViewById(R.id.venueList);

                listView.setBackgroundColor(Color.rgb(223, 223, 224));

                /** Setting the adapter containing the country list to listview */
                listView.setAdapter(adapter);

                listView.setClickable(true);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                        HashMap hm = (HashMap) listView.getItemAtPosition(position);

                        selVenueID = hm.get("id").toString();
                        selVenueName = hm.get("name").toString();
                        selVenueLat = hm.get("lat").toString();
                        selVenueLng = hm.get("lng").toString();
                        showMovies(listView);

                    }
                });

            }
        }

    }

    /** Called when the user clicks the Send button */
    public void showMovies(View view) {
        Global global = ((Global)getApplicationContext());
        Intent intent = new Intent(this, MovieShowings.class);
        intent.putExtra(SELECTED_VENUE_ID, selVenueID);
        intent.putExtra(SELECTED_VENUE_NAME, selVenueName);
        intent.putExtra(SELECTED_VENUE_LAT, selVenueLat);
        intent.putExtra(SELECTED_VENUE_LNG, selVenueLng);
        global.set_venue(selVenueID);
        startActivity(intent);
    }

    public void emailAuthor(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "jorose@gmail.com" });
        intent.putExtra(Intent.EXTRA_SUBJECT, "Movie Square");
        startActivity(Intent.createChooser(intent, ""));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private boolean ensureUi() {

        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        String tToken = settings.getString("4sqToken", "none");

        if (tToken != "none") {
            ExampleTokenStore.get().setToken(tToken);
        }

        boolean isAuthorized = !TextUtils.isEmpty(ExampleTokenStore.get().getToken());

        if (!isAuthorized){
            Intent intent = FoursquareOAuth.getConnectIntent(MainActivity.this, CLIENT_ID);

            // If the device does not have the Foursquare app installed, we'd
            // get an intent back that would open the Play Store for download.
            // Otherwise we start the auth flow.
            if (FoursquareOAuth.isPlayStoreIntent(intent)) {
                Intent fourIntent = new Intent(MainActivity.this, DownloadFoursquare.class);
                startActivity(fourIntent);
                return false;
            } else {
                startActivityForResult(intent, REQUEST_CODE_FSQ_CONNECT);
                return true;
            }
        } else {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("4sqToken", ExampleTokenStore.get().getToken().toString());
            editor.commit();
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_FSQ_CONNECT:
                onCompleteConnect(resultCode, data);
                break;

            case REQUEST_CODE_FSQ_TOKEN_EXCHANGE:
                onCompleteTokenExchange(resultCode, data);
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onCompleteConnect(int resultCode, Intent data) {
        AuthCodeResponse codeResponse = FoursquareOAuth.getAuthCodeFromResult(resultCode, data);
        Exception exception = codeResponse.getException();

        if (exception == null) {
            // Success.
            String code = codeResponse.getCode();
            performTokenExchange(code);

        } else {
            if (exception instanceof FoursquareCancelException) {
                // Cancel.
                toastMessage(this, "Canceled");

            } else if (exception instanceof FoursquareDenyException) {
                // Deny.
                toastMessage(this, "Denied");

            } else if (exception instanceof FoursquareOAuthException) {
                // OAuth error.
                String errorMessage = exception.getMessage();
                String errorCode = ((FoursquareOAuthException) exception).getErrorCode();
                toastMessage(this, errorMessage + " [" + errorCode + "]");

            } else if (exception instanceof FoursquareUnsupportedVersionException) {
                // Unsupported Fourquare app version on the device.
                toastError(this, exception);

            } else if (exception instanceof FoursquareInvalidRequestException) {
                // Invalid request.
                toastError(this, exception);

            } else {
                // Error.
                toastError(this, exception);
            }
        }
    }

    private void onCompleteTokenExchange(int resultCode, Intent data) {
        AccessTokenResponse tokenResponse = FoursquareOAuth.getTokenFromResult(resultCode, data);
        Exception exception = tokenResponse.getException();

        if (exception == null) {
            String accessToken = tokenResponse.getAccessToken();
            // Success.
            //toastMessage(this, "Access token: " + accessToken);

            // Persist the token for later use. In this example, we save
            // it to shared prefs.
            ExampleTokenStore.get().setToken(accessToken);

            // Refresh UI.
            ensureUi();

        } else {
            if (exception instanceof FoursquareOAuthException) {
                // OAuth error.
                String errorMessage = ((FoursquareOAuthException) exception).getMessage();
                String errorCode = ((FoursquareOAuthException) exception).getErrorCode();
                toastMessage(this, errorMessage + " [" + errorCode + "]");

            } else {
                // Other exception type.
                toastError(this, exception);
            }
        }
    }

    private void performTokenExchange(String code) {
        Intent intent = FoursquareOAuth.getTokenExchangeIntent(this, CLIENT_ID, CLIENT_SECRET, code);
        startActivityForResult(intent, REQUEST_CODE_FSQ_TOKEN_EXCHANGE);
    }

    public static void toastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void toastError(Context context, Throwable t) {
        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
    }

}
