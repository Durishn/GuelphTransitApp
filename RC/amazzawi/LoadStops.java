package velocityraptor.guelphtransit;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class loads the JSON from the url provided via an asynctask which
 * doesn't run on the main thread.  It parses the information and stores
 * it on the phone.
 *      Author: Anthony Mazzawi
 *
 * I have exclusive control over this submission via my password.
 * By including this statement in this header comment, I certify that:
 * 1) I have read and understood the University policy on academic integrity;
 * 2) I have completed the Computing with Integrity Tutorial on Moodle; and
 * I assert that this work is my own. I have appropriately acknowledged any and all material
 * (data, images, ideas or words) that I have used, whether directly quoted or paraphrased.
 * Furthermore, I certify that this assignment was prepared by me specifically for this course.
 */
public class LoadStops extends AsyncTask<String, String, String>{

    private ProgressDialog pDialog;
    private DBController dbController;
    private AlertDialogController ADC;

    // JSON Variables
    JSONParser jParser = new JSONParser();
    JSONArray stops = null;
    JSONArray express = null;
    JSONArray time = null;

    // URL with the JSON Bus information
    private static final String URL = "http://131.104.49.61/androidConnect/getRoutes.php";

    //JSON node names
    private static final String TAG_SUCCESS = "success";

    //JSON array names
    private static final String TAG_ROUTES = "Routes";
    private static final String TAG_EXPRESS = "Express";
    private static final String TAG_TIME = "Time";

    //Json value names
    private static final String TAG_ROUTE = "route";
    private static final String TAG_STOPID = "stopID";
    private static final String TAG_STOPNAME = "stopName";
    private static final String TAG_LATITUDE = "Latitude";
    private static final String TAG_LONGITUDE = "Longitude";
    private static final String TAG_TIMELIST = "timeList";
    private static final String TAG_DAY = "day";
    private static final String TAG_UPDATE = "update";

    /**
     * Constructor which creates a popup dialog and construct two objects
     * to store the information and create a new alertdialog
     * @param context Context currently active
     */
    public LoadStops(Context context)
    {
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading all stops... Please wait");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);

        this.dbController = new DBController(context);
        this.ADC = new AlertDialogController(context);
    }

    /**
     * This method is executed directly before doInBackground
     * It will show the dialog created in the constructor
     */
    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        pDialog.show();
    }

    /**
     * This method will load the JSON Object from the url and parse it into the
     * sqLite db that's on the phone.  Will also store the time of the server db
     * onto main activity.
     * @param args .
     * @return null
     */
    @Override
    protected String doInBackground(String... args)
    {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();

        // Getting JSON String From URL
        JSONObject json = jParser.makeHttpRequest(URL, "GET", params);

        try{
            // Check for success tag
            int success = json.getInt(TAG_SUCCESS);

            //Stops found, get array of stops
            if(success == 1)
            {
                // Get the different JSON Arrays from the Object
                stops =  json.getJSONArray(TAG_ROUTES);
                express = json.getJSONArray(TAG_EXPRESS);
                time = json.getJSONArray(TAG_TIME);

                // Temp variables
                int check = 0;
                String route, stopID, stopName, day;
                String latitude, longitude, times, updateTime;
                String weekTimes = null;
                String satTimes = null;
                String sunTimes = null;

                //loop through every stop, every 3 entries are the same stop
                for (int i = 0; i < stops.length(); i++)
                {
                    JSONObject c = stops.getJSONObject(i);

                    //Store each item into a variable
                    route = c.getString(TAG_ROUTE);
                    stopID = c.getString(TAG_STOPID);
                    stopName = c.getString(TAG_STOPNAME);
                    latitude = c.getString(TAG_LATITUDE);
                    longitude = c.getString(TAG_LONGITUDE);
                    times = c.getString(TAG_TIMELIST);
                    day = c.getString(TAG_DAY);

                    // Each stop has three different times depending on the day
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

                    // If check is 3, it means this is the third time the stop has gone through
                    if (check == 3)
                    {
                        // Insert into the database
                        dbController.insert(route, stopID, stopName, latitude, longitude,
                                weekTimes, satTimes, sunTimes);
                        check = 0;

                    }
                }

                // Since the express only run on one day, they are done separately
                for(int i = 0; i < express.length(); i++)
                {
                    JSONObject c = express.getJSONObject(i);

                    // Store each item into a variable
                    route = c.getString(TAG_ROUTE);
                    stopID = c.getString(TAG_STOPID);
                    stopName = c.getString(TAG_STOPNAME);
                    latitude = c.getString(TAG_LATITUDE);
                    longitude = c.getString(TAG_LONGITUDE);
                    times = c.getString(TAG_TIMELIST);

                    // Insert into the database
                    dbController.insert(route, stopID, stopName, latitude, longitude, times, null, null);
                }

                // Update the time
                JSONObject c = time.getJSONObject(0);
                updateTime = c.getString(TAG_UPDATE);

                /* Since this class is only used when the database is updated
                   that means the db is up to date and both times will be the same */
                MainActivity.setTimes(updateTime, updateTime);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method runs immediately after doInBackground and will
     * dismiss the dialog currently open and will open a new dialog.
     * @param unused .
     */
    @Override
    protected void onPostExecute(String unused)
    {
        pDialog.dismiss();
        ADC.updateDialog(false);
    }
}