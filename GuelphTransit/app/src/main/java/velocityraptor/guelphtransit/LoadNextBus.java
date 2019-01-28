package velocityraptor.guelphtransit;
import velocityraptor.guelphtransit.interfaces.ScrapeRespCallback;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by William Aidan Maher on 26/03/15.
 * This class is for sending a POST request to our server
 * to scrape nextBus times identified by an ID
 */
public class LoadNextBus extends AsyncTask<String,String,String> {
    //NextBus times from server:William Aidan Maher [START]

        private String etaOne="null";
        private String etaTwo="null";
        private int stopPos;
        private int routePos;
        private StopPopup stop;

        public LoadNextBus(int routePosInput,int stopPosInput,StopPopup s){
            super();
            routePos=routePosInput;
            stopPos=stopPosInput;
            stop=s;

        }

        @Override
        protected String doInBackground(String... args) {

            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            //Get the stopID from list to send to server
            String stopID = MainActivity.routeList.get(routePos).getStopList().get(stopPos).getStopID();
            if(stopID.length()==3){
                stopID = "0"+stopID;
            }
            //Log.v("log", "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$1" + stopID);

            postParams.add(new BasicNameValuePair("stopID", stopID.toString()));

            final String postURL = "http://131.104.49.61/androidConnect/getNextBus.php";

            JSONParser jParser = new JSONParser();
            JSONObject postResp = new JSONObject();
            Log.v("log", "making POST request to: " + postURL);
            postResp = jParser.makeHttpRequest(postURL, "POST", postParams);
            if(postResp!=null) {
                //Log.v("log", "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$1" + postResp.toString());
                try {
                    etaOne = postResp.getString("ETA1");
                    etaTwo = postResp.getString("ETA2");
                }catch(JSONException j){

                }
            }else{
                //Log.v("log","$$$$$$$$$$NULL postResp");
            }

            return "";
        }
        /*When JSON request complete, change text in popup*/
        @Override
        protected void onPostExecute(String s){
            super.onPostExecute("");
            if((etaOne=="null"&&etaTwo=="null")||(etaOne=="0"&&etaTwo=="0")){
                etaOne="NextBus Time unavailable";
                etaTwo="NextBus Time unavailable";
            }
            if(Integer.parseInt(etaOne)>=50){
                etaTwo=etaOne;
                etaOne = "Nextbus ETA off, check schedule to the left";
            }
            if(stop!=null) {
                stop.setText(etaOne, etaTwo);
            }

        }

    //NextBus times from server:William Aidan Maher [END]
}
