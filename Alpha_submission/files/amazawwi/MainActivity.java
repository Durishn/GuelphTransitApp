package velocityraptor.guelphtransit;

import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class defines the Fragment for the main bottom bar, filled with Bus Stops
 *      @author: Nic Durish, Aidan Maher, Anthony Mazzawi & Jackson Keenan.
 *
 * I have exclusive control over this submission via my password.
 * By including this statement in this header comment, I certify that:
 * 1) I have read and understood the University policy on academic integrity;
 * 2) I have completed the Computing with Integrity Tutorial on Moodle; and
 * I assert that this work is my own. I have appropriately acknowledged any and all material
 * (data, images, ideas or words) that I have used, whether directly quoted or paraphrased.
 * Furthermore, I certify that this assignment was prepared by me specifically for this course.
 */
public class MainActivity extends ActionBarActivity {

    //List of Routes
    public static ArrayList<Route> routeList = new ArrayList<>();

    //Database functions
    DBController dbController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize DBController; if the db is empty, load from server
        dbController = new DBController(getApplicationContext());
        if(dbController.getCount() <= 0)
            new loadStops().execute();

        // Insert the stops into classes
        dbController.insertRoutes(routeList);
        dbController.insertAllStops(routeList);

        Collections.sort(routeList, new Comparator() {

            public int compare(Object o1, Object o2) {
                Route p1 = (Route) o1;
                Route p2 = (Route) o2;

                char[] letters = new char[]{'A', 'B', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

                String routeName1 = p1.getRouteName();
                String routeName2 = p2.getRouteName();

                // Boolean to see if the toures have letter (like 1A, 3B)
                boolean hasLetter1 = false;
                boolean hasLetter2 = false;

                // These will be the index in the array, so what letter/number it is
                int index1 = 0;
                int index2 = 0;
                int index3 = 0;
                int index4 = 0;

                // Iterate through the char array to determine which letter the stop route has
                for (int i = 0; i < letters.length; i++) {
                    if (routeName1.charAt(0) == letters[i]) {
                        index1 = i;
                    }
                    if (routeName2.charAt(0) == letters[i]) {
                        index2 = i;
                    }

                    if (routeName1.length() < 2) {
                        index3 = 0;
                    } else if (routeName1.charAt(1) == letters[i]) {

                        if (((routeName1.charAt(1)) == 'A') || (routeName1.charAt(1) == 'B')) {
                            hasLetter1 = true;
                        }
                        index3 = -1;
                    }

                    if (routeName2.length() < 2) {
                        index4 = 0;
                    } else if (routeName2.charAt(1) == letters[i]) {
                        if (((routeName2.charAt(1)) == 'A') || (routeName2.charAt(1) == 'B')) {
                            hasLetter2 = true;
                        }
                        index4 = -1;
                    }
                }

                /* If the bus has a letter, it means the route is something like 1a or 2b
                which means it will be at the front of the list */
                if (hasLetter1 && !hasLetter2) {
                    return -1;
                } else if (!hasLetter1 && hasLetter2) {
                    return 1;
                } else if ((routeName1.length() < routeName2.length())) {

                    return -1;
                } else if ((index1 < index2) && (index3 < index4)) {
                    return -1;

                } else
                    return 0;
            }


        });

        FragmentBotbarBus fragment = new FragmentBotbarBus();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.bot_bar_frag_view, fragment);
        fragmentTransaction.commit();

        //Set View of Contents
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_schedule:
                newActivityScheduleBus(findViewById(android.R.id.content));
                break;
            case R.id.action_settings:
                break;
            default:
                Toast.makeText(getApplicationContext(), "Sorry, This Feature Isn't Implemented Yet",
                        Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to Start Bus Schedule Activity
    public void newActivityScheduleBus(View view) {
        Intent intent = new Intent(this, ActivityScheduleBus.class);
        startActivity(intent);
    }

    // Method called when home button is clicked
    public void homeButtonClick(View view) {
        FragmentBotbarBus fragment = new FragmentBotbarBus();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.bot_bar_frag_view, fragment);
        fragmentTransaction.commit();
    }

    // Method called when favorite button is clicked
    public void favButtonClick(View view) {Toast.makeText(getApplicationContext()
            , "Sorry, This Feature Isn't Implemented Yet",Toast.LENGTH_LONG).show();}

    // Method called when location button is clicked
    public void locButtonClick(View view) {Toast.makeText(getApplicationContext()
            , "Sorry, This Feature Isn't Implemented Yet",Toast.LENGTH_LONG).show();}

    /* This class loads the stops from the server
         Must use AsyncTask, because you cannot run this thread on the main activity */
    private class loadStops extends AsyncTask<String, String, String> {

        //url to retrieve bus schedule
        private static final String URL = "http://131.104.49.61/androidConnect/getRoutes.php";

        //JSON node names
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_ROUTES = "Routes";
        private static final String TAG_ROUTE = "route";
        private static final String TAG_STOPID = "stopID";
        private static final String TAG_STOPNAME = "stopName";
        private static final String TAG_LATITUDE = "Latitude";
        private static final String TAG_LONGITUDE = "Longitude";
        private static final String TAG_TIMELIST = "timeList";
        private static final String TAG_DAY = "day";

        ProgressDialog pDialog;
        JSONParser jParser = new JSONParser();
        JSONArray stops = null;

        /* Before starting background thread show progress dialog */
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading all stops. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /* Getting all stops from url */
        @Override
        protected String doInBackground(String... args){

            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            //Getting JSON String From URL
            JSONObject json = jParser.makeHttpRequest(URL, params);

            try{

                //Check for success tag
                int success = json.getInt(TAG_SUCCESS);

                //Stops found, get array of stops
                if(success == 1){

                    stops =  json.getJSONArray(TAG_ROUTES);
                    int check = 0;
                    String route;
                    String stopID;
                    String stopName;
                    String latitude;
                    String longitude;
                    String times;
                    String weekTimes = null;
                    String satTimes = null;
                    String sunTimes = null;
                    String day;

                    //loop through every stop, every 3 entries are the same stop
                    for (int i = 0; i < stops.length(); i++) {
                        JSONObject c = stops.getJSONObject(i);

                        //Store each item into a variable
                        route = c.getString(TAG_ROUTE);
                        stopID = c.getString(TAG_STOPID);
                        stopName = c.getString(TAG_STOPNAME);
                        latitude = c.getString(TAG_LATITUDE);
                        longitude = c.getString(TAG_LONGITUDE);
                        times = c.getString(TAG_TIMELIST);
                        day = c.getString(TAG_DAY);

                        switch (day) {
                            case "Week":
                                weekTimes = times;
                                break;
                            case "Sat":
                                satTimes = times;
                                break;
                            case "Sun":
                                sunTimes = times;
                                break;
                        }
                        check++;

                        if (check == 3) {
                            dbController.insert(route, stopID, stopName, latitude, longitude,
                                    weekTimes, satTimes, sunTimes);
                            check = 0;
                        }
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /* After completing background task, dismiss the progress dialog */
        protected void onPostExecute(String fileURL) {
            // dismiss the dialog after after getting all stops
            pDialog.dismiss();
        }
    }
}
