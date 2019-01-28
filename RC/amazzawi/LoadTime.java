package velocityraptor.guelphtransit;

import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class loads the JSON Time object from the url via an asynctask which
 * doesn't run on the main thread.
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
public class LoadTime extends AsyncTask<String, String, String> {

    //JSON Variables
    JSONParser jParser = new JSONParser();
    JSONArray time = null;

    // URL where the JSON time string is stored
    private static final String URL = "http://131.104.49.61/androidConnect/getUpdateTime.php";

    //JSON node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_TIME = "Time";
    private static final String TAG_UPDATE = "update";

    /**
     * This method will run in the background instead of the main thread
     * Will load in the time and store it for the main activity
     * @param args .
     * @return null
     */
    @Override
    protected String doInBackground(String... args) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<>();

        //Getting JSON String From URL
        JSONObject json = jParser.makeHttpRequest(URL, "GET", params);

        //Temp String
        String serverTime;

        try{
            //Check for success tag
            int success = json.getInt(TAG_SUCCESS);

            //Stops found, get array of stops
            if(success == 1){

                // Retrieve the specific array from the JSON Object
                time = json.getJSONArray(TAG_TIME);

                JSONObject c = time.getJSONObject(0);
                serverTime = c.getString(TAG_UPDATE);

                // Set the server time on the phone
                MainActivity.setTimes(null, serverTime);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
