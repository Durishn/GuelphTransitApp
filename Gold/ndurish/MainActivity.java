package velocityraptor.guelphtransit;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import velocityraptor.guelphtransit.interfaces.ScrapeRespCallback;

/**
 * This class defines the Fragment for the main bottom bar, filled with Bus Stops
 *      Authors: Nic Durish, Aidan Maher, Anthony Mazzawi & Jackson Keenan.
 *
 * I have exclusive control over this submission via my password.
 * By including this statement in this header comment, I certify that:
 * 1) I have read and understood the University policy on academic integrity;
 * 2) I have completed the Computing with Integrity Tutorial on Moodle; and
 * I assert that this work is my own. I have appropriately acknowledged any and all material
 * (data, images, ideas or words) that I have used, whether directly quoted or paraphrased.
 * Furthermore, I certify that this assignment was prepared by me specifically for this course.
 */
public class MainActivity extends ActionBarActivity implements ScrapeRespCallback {

    public static int topHeight;

    //getting main context & activity
    public static Context thisContext;
    public Activity thisActivity = this;

    //Fragment for the Bottom Bar (Routes)
    private FragmentRouteBar busBarFragment;
    private FragmentSpecialBar specialBarFragment;

    //List of Routes for
    public static ArrayList<Stop> favoriteList = new ArrayList<>();
    public static ArrayList<Route> routeList = new ArrayList<>();
    public static ArrayList<Stop> locationList = new ArrayList<>();

    //Bottom Panel Control (0=Home, 1=Favourites ,2=My Location, 3=Secondary State(Favourites), 4=Secondary State(Location))
    public static int panelMode = 0;

    //popUp
    public static StopPopup stopPopUp;

    //Map API & Loc Manager [Jackson Keenan]
    public static GoogleMap mainMap;
    public static CameraUpdate mapUpdate;
    private final LatLng LOCATION_GUELPH = new LatLng(43.5500, -80.2500);
    private LocationManager locManager;

    //Database functions
    DBController dbController;

    //Alert Dialogs
    AlertDialogController ADC;

    //Update Schedule variables
    static boolean autoUpdate;
    static String lastUpdateTime, serverTime;

    //Determine if the app has been opened for the first time
    public enum AppStart {
        FIRST_TIME, FIRST_VERSION, NORMAL
    }

    private static final String LAST_APP_VERSION = "last_app_version";
    public FragmentManager fragmentManager = getSupportFragmentManager();

    /***********
     * METHODS *
     ***********/

    @Override
    public void resetPopupWithEtas(String etaOne,String etaTwo){
        stopPopUp.setText(etaOne,etaTwo);
    }

    /**
     * Called when the activity is first created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Determine if the app has been started for the first time or if it's normal */
        switch (checkAppStart()) {
            /* When the phone starts up normally, the phone will get the last time the
               server database has been updated and the time the phone db has been updated */
            case NORMAL:
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                lastUpdateTime = preferences.getString("lastUpdateTime", "");
                new LoadTime().execute();
                break;
            case FIRST_VERSION:
                //show what's new
                break;
            case FIRST_TIME:
                //show a tutorial or help while the dbs load
                break;
            default:
                break;
        }

        // Initialize DBController; if the db is empty, load from server
        dbController = new DBController(getApplicationContext());
        ADC = new AlertDialogController(this);
        if (dbController.getCount() <= 0) {
            new LoadStops(this).execute();
            new LoadTime().execute();
        }

        // Insert the stops into classes
        dbController.insertRoutes(routeList);
        dbController.insertAllStops(routeList);

        // Initialize BottomBar Fragment
        busBarFragment = new FragmentRouteBar();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.bot_bar_frag_view, busBarFragment);
        fragmentTransaction.commit();

        //Set View of Contents
        setContentView(R.layout.activity_main);

        //Map & Location Manager Setup [Jackson Keenan]
        mainMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mapUpdate = CameraUpdateFactory.newLatLngZoom(LOCATION_GUELPH, 12);
        mainMap.animateCamera(mapUpdate);
        locManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //Set Static Context
        thisContext = getApplicationContext();

        //Set Button PanelMode
        panelMode = 0;
        setPanelColour();

    }

    /**
     * Called when the activity is about to become visible
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Android: ", "Activity Started");
    }

    /**
     * Called when the activity has become visible
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Android: ", "Activity Resumed");
        String fav;

        /* Shared preferences are stored in the phone even when the app closes
           when the app starts up auto update will be toggled to whatever has been
           stored, if there is nothing there than it will default to false  */
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        autoUpdate = preferences.getBoolean("autoUpdate", false);
        fav = preferences.getString("favourites", "");

        favoriteList.clear();

        if(!fav.equals(""))
        {
            String tokens[] = fav.split(",");
            for (String token : tokens) {

                favoriteList.add(dbController.getStop(token));
            }
        }

        new LoadTime().execute();
        if(autoUpdate)
        {
            if(!serverTime.equals(lastUpdateTime))
                NotificationStart();
        }

        // Sort the Routes List when the activity resumes
        Collections.sort(routeList, Route.routeSort);
    }

    /**
     * Called when another activity is taking focus
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Android: ", "Activity Paused");

        /* When the user leaves the activities, the preferences will be saved */
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("autoUpdate", autoUpdate);
        editor.putString("lastUpdateTime", lastUpdateTime);

        String fav = favToString();
        if(fav.equals(""))
            editor.putString("favourites", "");
        else
            editor.putString("favourites", fav);
        editor.apply();
    }

    /**
     * Called when the activity is no longer visible
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Android: ", "Activity Stopped");
    }

    /**
     * Called when the activity is being destroyed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Android: ", "Activity Destroyed");
        android.os.Debug.stopMethodTracing();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //Getting Height Of Top Status bar for PopUp placement
        topHeight = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        Rect r = new Rect();
        Window w = getWindow();
        w.getDecorView().getWindowVisibleDisplayFrame(r);
        topHeight = topHeight + r.top;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here.
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.action_schedule:
                intent = new Intent(this, ActivityScheduleBus.class);
                startActivity(intent);
                break;
            case R.id.action_about:
                intent = new Intent(this, ActivityAboutPage.class);
                startActivity(intent);
                break;
            case R.id.action_help:
                intent = new Intent(this, ActivityScreenSlidePager.class);
                startActivity(intent);
                break;
            case R.id.action_clear_favorites:
                favoriteList.clear();
                Toast.makeText(getApplicationContext(), "Your Favorites have been cleared."
                        , Toast.LENGTH_LONG).show();
                homeButtonClick(null);
                break;
            case R.id.action_update_schedules:
                new LoadTime().execute();
                /* Compare the phone db time with the server time
                   if it's different, then it will be updated
                   if not then it will show a pop up saying the db it up to date */
                if (lastUpdateTime.equals(serverTime))
                    ADC.updateDialog(true);
                else {
                    dbController.deleteDB();
                    new LoadStops(this).execute();
                    new LoadTime().execute();
                }
                break;
            case R.id.action_toggle_autoupdate:
                ADC.autoUpdateDialog(autoUpdate);
                break;
            default:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setPanelColour(){

        String homeImage = "@drawable/home_icon";
        String favImage = "@drawable/favorite_icon";
        String locImage = "@drawable/location_icon";

        switch (panelMode){
            case 0:
                homeImage = "@drawable/blue_home_icon";
                break;
            case 1:
                favImage = "@drawable/blue_favorite_icon";
                break;
            case 2:
                locImage = "@drawable/blue_location_icon";
                break;
        }

        //Set home, favorite and location buttons
        int homeImageResource = getResources().getIdentifier(homeImage, null, getPackageName());
        int favImageResource = getResources().getIdentifier(favImage, null, getPackageName());
        int locImageResource = getResources().getIdentifier(locImage, null, getPackageName());
        Drawable homeRes = getResources().getDrawable(homeImageResource);
        Drawable favRes = getResources().getDrawable(favImageResource);
        Drawable locRes = getResources().getDrawable(locImageResource);
        ImageView homeImageView = (ImageView) findViewById(R.id.button_image_home);
        ImageView favImageView = (ImageView) findViewById(R.id.button_image_fav);
        ImageView locImageView = (ImageView) findViewById(R.id.button_image_loc);
        homeImageView.setImageDrawable(homeRes);
        favImageView.setImageDrawable(favRes);
        locImageView.setImageDrawable(locRes);
    }

    // Method called when home button is clicked
    public void homeButtonClick(View view) {

        // Potentially Closing Open PopupDialog
        try {
            busBarFragment.newFragment.stopPopUp.dismiss();
        } catch (Exception noPopUpToClose) {
            noPopUpToClose.printStackTrace();
        }
        try {
            specialBarFragment.stopPopUp.dismiss();
        } catch (Exception noPopUpToClose) {
            noPopUpToClose.printStackTrace();
        }
        try {
            stopPopUp.dismiss();
        } catch (Exception noPopUpToClose) {
            noPopUpToClose.printStackTrace();
        }

        //Reloading Route Bar
        busBarFragment = new FragmentRouteBar();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.bot_bar_frag_view, busBarFragment);
        fragmentTransaction.commit();
        mapUpdate = CameraUpdateFactory.newLatLngZoom(LOCATION_GUELPH, 12);
        mainMap.animateCamera(mapUpdate);
        mainMap.clear();

        panelMode = 0;
        setPanelColour();
    }

    // Method called when favorite button is clicked
    public void favButtonClick(View view) {
        // Potentially Closing Open PopupDialog
        try {
            busBarFragment.newFragment.stopPopUp.dismiss();
        } catch (Exception noPopUpToClose) {
            noPopUpToClose.printStackTrace();
        }
        try {
            specialBarFragment.stopPopUp.dismiss();
        } catch (Exception noPopUpToClose) {
            noPopUpToClose.printStackTrace();
        }
        try {
            stopPopUp.dismiss();
        } catch (Exception noPopUpToClose) {
            noPopUpToClose.printStackTrace();
        }

        if (panelMode != 1) {
            panelMode = 1;
            setPanelColour();
            mainMap.clear();

            // Create fragment and give it an argument specifying the article it should show
            specialBarFragment = new FragmentSpecialBar();
            Bundle args = new Bundle();
            panelMode = 3;
            args.putInt("EXTRA_MODE", 0);
            specialBarFragment.setArguments(args);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // Replace whatever is in the fragment_container view with this fragment,
            transaction.replace(R.id.bot_bar_frag_view, specialBarFragment);
            transaction.commit();

            MainActivity.mainMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    //Dismiss Special Bar Popup, Display new one
                    panelMode = 3;
                    return true;
                }
            });
        }
    }

    // Method called when location button is clicked
    public void locButtonClick(View view) {
        // Potentially Closing Open PopupDialog(s)
        try {
            busBarFragment.newFragment.stopPopUp.dismiss();
        } catch (Exception noPopUpToClose) {
            noPopUpToClose.printStackTrace();
        }
        try {
           specialBarFragment.stopPopUp.dismiss();
        } catch (Exception noPopUpToClose) {
            noPopUpToClose.printStackTrace();
        }
        try {
            stopPopUp.dismiss();
        } catch (Exception noPopUpToClose) {
            noPopUpToClose.printStackTrace();
        }

        if (panelMode != 2) {
            mainMap.clear();
            panelMode = 2;
            setPanelColour();

            // Create fragment and give it an argument specifying the article it should show
            specialBarFragment = new FragmentSpecialBar();
            Bundle args = new Bundle();
            args.putInt("EXTRA_MODE", 1);
            specialBarFragment.setArguments(args);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // Replace whatever is in the fragment_container view with this fragment,
            transaction.replace(R.id.bot_bar_frag_view, specialBarFragment);
            transaction.commit();

            /*Current Location [Jackson Keenan]*/
            //Finding Device Location
            Location myCurrentLoc = locManager.getLastKnownLocation(locManager.getBestProvider(new Criteria(), true));

            //Click Listener for Map Markers (Same as OnClick for bottom bar)
            MainActivity.mainMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    WindowManager wm = (WindowManager) MainActivity.getAppContext().getSystemService(MainActivity.getAppContext().WINDOW_SERVICE);
                    Display display = wm.getDefaultDisplay();
                    final Point size = new Point();
                    display.getSize(size);

                    if(panelMode==4){
                        //Dismiss Special Bar Popup, Display new one
                    }
                    else if(marker.getTitle().contains(",") == true){
                        panelMode = 4;
                        List<String> localStopList = new ArrayList<String>(Arrays.asList(marker.getTitle().split(",")));
                        final CharSequence locStops[] = new CharSequence[localStopList.size()];
                        String overlapStopName = " ";
                        for(int i = 0;i < localStopList.size();i++){
                            for(int j= 0;j < routeList.size();j++){
                                for(int k = 0;k < routeList.get(j).getStopList().size();k++){
                                    if(routeList.get(j).getStopList().get(k).getStopID().equals(localStopList.get(i))){
                                        locStops[i] = "Route " + routeList.get(j).getRouteName() + ": (" + localStopList.get(i) + ")";
                                        overlapStopName = routeList.get(j).getStopList().get(k).getStopName();
                                    }
                                }
                            }
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                        builder.setTitle(overlapStopName);
                        builder.setItems(locStops, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for(int j= 0;j < routeList.size();j++){
                                    for(int k = 0;k < routeList.get(j).getStopList().size();k++) {
                                        if(locStops[which].toString().contains(routeList.get(j).getStopList().get(k).getStopID())){
                                            if(stopPopUp!= null){stopPopUp.dismiss();} //Closing Previous popup
                                            if(specialBarFragment.stopPopUp!= null){
                                                specialBarFragment.stopPopUp.dismiss();} //Closing Previous popup
                                            //Code for getting activity for stopPopUo [Jackson Keenan START]

                                            LayoutInflater popUpLayout = (LayoutInflater)thisActivity.getLayoutInflater();
                                            View popUpView = popUpLayout.inflate(R.layout.popup, null);
                                            stopPopUp = new StopPopup(popUpView, ViewGroup.LayoutParams.WRAP_CONTENT
                                                    , size.x,j,k);
                                            //Scrape next bus times, popup will automatically add
                                            //JSON Parsed times when loadBus class is complete

                                            stopPopUp.showAsDropDown(popUpView, 10, MainActivity.topHeight);
                                            //PopUpWindow: Jackson Keenan [END]

                                            //Stop Location Markers: Jackson Keenan [START]

                                            //Get Stop Long + Lat
                                            Float stopLat = MainActivity.routeList.get(j).getStopList().get(k).getLatitude();
                                            Float stopLong = MainActivity.routeList.get(j).getStopList().get(k).getLongitude();
                                            LatLng stopLatLong = new LatLng(stopLat, stopLong);

                                            //Clear Old Markers
                                            MainActivity.mainMap.clear();

                                            //Add Stop Marker
                                            MainActivity.mainMap.addMarker(new MarkerOptions()
                                                    .position(stopLatLong)
                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                                    .title(MainActivity.routeList.get(j).getStopList().get(k).getStopID()));
                                            MainActivity.mapUpdate = CameraUpdateFactory.newLatLng(stopLatLong);
                                            MainActivity.mainMap.animateCamera(MainActivity.mapUpdate);

                                            //Stop Location Markers: Jackson Keenan [END]
                                        }
                                    }
                                }
                            }
                        });
                        marker.hideInfoWindow();
                        mainMap.animateCamera(mapUpdate);
                        builder.show();
                    }else{
                        panelMode = 4;
                        // No Overlap (single stop / id)
                        for (int i = 0; i < routeList.size(); i++) {
                            for (int j = 0; j < routeList.get(i).getStopList().size(); j++) {
                                if(routeList.get(i).getStopList().get(j).getStopID().equals(marker.getTitle())){
                                    if(stopPopUp!= null){stopPopUp.dismiss();} //Closing Previous popup
                                    //Code for getting activity for stopPopUp [Jackson Keenan START]
                                    LayoutInflater popUpLayout = (LayoutInflater)thisActivity.getLayoutInflater();
                                    View popUpView = popUpLayout.inflate(R.layout.popup, null);
                                    //Jackson Keenan [END]

                                    //Custom popup class for showing popup with information about the stop
                                    stopPopUp = new StopPopup(popUpView, ViewGroup.LayoutParams.WRAP_CONTENT
                                            , size.x,i,j);
                                    //Scrape next bus times, popup will automatically add
                                    //JSON Parsed times when loadBus class is complete


                                    /*Display PopUp*/
                                    stopPopUp.showAsDropDown(popUpView, 10, MainActivity.topHeight);
                                    //PopUpWindow: Jackson Keenan [END]

                                    //Stop Location Markers: Jackson Keenan [START]

                                    //Get Stop Long + Lat
                                    Float stopLat = MainActivity.routeList.get(i).getStopList().get(j).getLatitude();
                                    Float stopLong = MainActivity.routeList.get(i).getStopList().get(j).getLongitude();
                                    LatLng stopLatLong = new LatLng(stopLat, stopLong);

                                    //Clear Old Markers
                                    MainActivity.mainMap.clear();

                                    //Add Stop Marker
                                    MainActivity.mainMap.addMarker(new MarkerOptions()
                                            .position(stopLatLong)
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                            .title(MainActivity.routeList.get(i).getStopList().get(j).getStopID()));
                                    MainActivity.mapUpdate = CameraUpdateFactory.newLatLng(stopLatLong);
                                    MainActivity.mainMap.animateCamera(MainActivity.mapUpdate);
                                    //Stop Location Markers: Jackson Keenan [END]
                                    break;
                                }
                            }
                        }
                    }
                    return true;
                }
            });

            //If location services are available place marker at current location
            if (myCurrentLoc != null) {
                LatLng myLocLatLng = new LatLng(myCurrentLoc.getLatitude(), myCurrentLoc.getLongitude());
                Marker myLoc = mainMap.addMarker(new MarkerOptions().position(myLocLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("You Are Here"));
                MainActivity.mapUpdate = CameraUpdateFactory.newLatLngZoom(myLocLatLng, 13);
                MainActivity.mainMap.animateCamera(MainActivity.mapUpdate);

                locationList.clear();
                //Finding distance between current location and stop location
                for (int i = 0; i < routeList.size(); i++) {
                    for (int j = 0; j < routeList.get(i).getStopList().size(); j++) {
                        double theta = myLocLatLng.longitude - routeList.get(i).getStopList().get(j).getLongitude();
                        double distFromLoc = Math.sin(myLocLatLng.latitude * Math.PI / 180.0) *
                                Math.sin(routeList.get(i).getStopList().get(j).getLatitude() * Math.PI / 180.0) +
                                Math.cos(myLocLatLng.latitude * Math.PI / 180.0) *
                                        Math.cos(routeList.get(i).getStopList().get(j).getLatitude() * Math.PI / 180.0) *
                                        Math.cos(theta * Math.PI / 180.0);
                        distFromLoc = Math.acos(distFromLoc);
                        distFromLoc = (distFromLoc * 180.0 / Math.PI);
                        //Miles => Km
                        distFromLoc = (distFromLoc * 60 * 1.1515) * 1.609344;
                        //If Stop is within radius add to ArrayList<Stop> locationList
                        if(distFromLoc <= 0.5){
                            locationList.add(0, routeList.get(i).getStopList().get(j));
                        }
                    }
                }

                boolean placedStop = false;
                ArrayList<Marker> stopMarker = new ArrayList<>();

                for(int i = 0;i < locationList.size();i++){
                    placedStop = false;

                    //Checks if that LatLng is Already a Pin
                    for(int j = 0;j<stopMarker.size();j++){
                        LatLng tempLatLng = new LatLng(locationList.get(i).getLatitude(), locationList.get(i).getLongitude());

                        double theta = tempLatLng.longitude - stopMarker.get(j).getPosition().longitude;
                        double distFromLoc = Math.sin(tempLatLng.latitude * Math.PI / 180.0) *
                                Math.sin(stopMarker.get(j).getPosition().latitude * Math.PI / 180.0) +
                                Math.cos(tempLatLng.latitude * Math.PI / 180.0) *
                                        Math.cos(stopMarker.get(j).getPosition().latitude * Math.PI / 180.0) *
                                        Math.cos(theta * Math.PI / 180.0);
                        distFromLoc = Math.acos(distFromLoc);
                        distFromLoc = (distFromLoc * 180.0 / Math.PI);
                        //Miles => Km
                        distFromLoc = (distFromLoc * 60 * 1.1515) * 1.609344;

                        if(distFromLoc  <= 0.05){
                            stopMarker.get(j).setTitle(stopMarker.get(j).getTitle() + "," + locationList.get(i).getStopID());
                            placedStop = true;
                        }
                    }
                    if(!placedStop){
                        stopMarker.add(mainMap.addMarker(new MarkerOptions()
                                .position(new LatLng(locationList.get(i).getLatitude(), locationList.get(i).getLongitude()))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.green_dot))
                                .title(locationList.get(i).getStopID())));
                        mainMap.animateCamera(mapUpdate);
                    }

                }
            } else {
                //If the location is not available it defaults to the UC (For Testing)
                LatLng myLocLatLng = new LatLng(43.52099932861328, -80.22380065917969);
                Marker myLoc = mainMap.addMarker(new MarkerOptions().position(myLocLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("You Are Here"));
                MainActivity.mapUpdate = CameraUpdateFactory.newLatLngZoom(myLocLatLng, 13);
                MainActivity.mainMap.animateCamera(MainActivity.mapUpdate);

                locationList.clear();
                //Finding distance between current location and stop location
                for (int i = 0; i < routeList.size(); i++) {
                    for (int j = 0; j < routeList.get(i).getStopList().size(); j++) {
                        double theta = myLocLatLng.longitude - routeList.get(i).getStopList().get(j).getLongitude();
                        double distFromLoc = Math.sin(myLocLatLng.latitude * Math.PI / 180.0) *
                                Math.sin(routeList.get(i).getStopList().get(j).getLatitude() * Math.PI / 180.0) +
                                Math.cos(myLocLatLng.latitude * Math.PI / 180.0) *
                                        Math.cos(routeList.get(i).getStopList().get(j).getLatitude() * Math.PI / 180.0) *
                                        Math.cos(theta * Math.PI / 180.0);
                        distFromLoc = Math.acos(distFromLoc);
                        distFromLoc = (distFromLoc * 180.0 / Math.PI);
                        //Miles => Km
                        distFromLoc = (distFromLoc * 60 * 1.1515) * 1.609344;
                        //If Stop is within radius add to ArrayList<Stop> locationList
                        if(distFromLoc <= 0.5){
                            locationList.add(0, routeList.get(i).getStopList().get(j));
                        }
                    }
                }

                boolean placedStop = false;
                ArrayList<Marker> stopMarker = new ArrayList<>();

                for(int i = 0;i < locationList.size();i++){
                    placedStop = false;

                    //Checks if that LatLng is Already a Pin
                    for(int j = 0;j<stopMarker.size();j++){
                        LatLng tempLatLng = new LatLng(locationList.get(i).getLatitude(), locationList.get(i).getLongitude());

                        double theta = tempLatLng.longitude - stopMarker.get(j).getPosition().longitude;
                        double distFromLoc = Math.sin(tempLatLng.latitude * Math.PI / 180.0) *
                                Math.sin(stopMarker.get(j).getPosition().latitude * Math.PI / 180.0) +
                                Math.cos(tempLatLng.latitude * Math.PI / 180.0) *
                                        Math.cos(stopMarker.get(j).getPosition().latitude * Math.PI / 180.0) *
                                        Math.cos(theta * Math.PI / 180.0);
                        distFromLoc = Math.acos(distFromLoc);
                        distFromLoc = (distFromLoc * 180.0 / Math.PI);
                        //Miles => Km
                        distFromLoc = (distFromLoc * 60 * 1.1515) * 1.609344;

                        if(distFromLoc  <= 0.05){
                            stopMarker.get(j).setTitle(stopMarker.get(j).getTitle() + "," + locationList.get(i).getStopID());
                            placedStop = true;
                        }
                    }
                    if(!placedStop){
                        stopMarker.add(mainMap.addMarker(new MarkerOptions()
                                .position(new LatLng(locationList.get(i).getLatitude(), locationList.get(i).getLongitude()))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.green_dot))
                                .title(locationList.get(i).getStopID())));
                        mainMap.animateCamera(mapUpdate);
                    }

                }
            }
        }
    }

    public static void setTimes(String updateTime, String serverTime) {
        if (updateTime != null)
            lastUpdateTime = updateTime;

        MainActivity.serverTime = serverTime;
    }

    public static Context getAppContext() {
        return thisContext;
    }

    public static void setAutoUpdate(Boolean auto) {
        autoUpdate = auto;
    }

    /**
     * This method will determine if the user has started the app for the first time
     */
    public AppStart checkAppStart() {
        PackageInfo pInfo;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        AppStart appStart = AppStart.NORMAL;

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int lastVersion = sharedPreferences.getInt(LAST_APP_VERSION, -1);
            int currentVersion = pInfo.versionCode;
            appStart = checkAppStart(currentVersion, lastVersion);

            // Update the version in preferences
            sharedPreferences.edit().putInt(LAST_APP_VERSION, currentVersion).apply();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appStart;
    }

    /**
     * This is called by the other checkAppStart function and it simply
     * compares the versions sent to it
     */
    public AppStart checkAppStart(int current, int last) {
        if (last == -1)
            return AppStart.FIRST_TIME;
        else if (last < current)
            return AppStart.FIRST_VERSION;
        else
            return AppStart.NORMAL;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void NotificationStart()
    {
        /* Invoking the default notification service */
        NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setContentTitle("Schedules out of date!");
        mBuilder.setContentText("Touch to update.");
        mBuilder.setSmallIcon(R.drawable.bus_symbol);

      /* Creates an explicit intent for an Activity in your app */
        Intent resultIntent = new Intent(this, NotificationActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NotificationActivity.class);

      /* Adds the Intent that starts the Activity to the top of the stack */
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

      /* notificationID allows you to update the notification later on. */
        int notificationID = 100;
        mNotificationManager.notify(notificationID, mBuilder.build());

    }

    private String favToString()
    {
        String fav = "";

        for(int i = 0; i < favoriteList.size(); i++)
        {
            if(i == 0)
                fav = favoriteList.get(i).getStopID() + ",";
            else
                fav += favoriteList.get(i).getStopID() + ",";
        }
        return fav;
    }
}