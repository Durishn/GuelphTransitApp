package velocityraptor.guelphtransit;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
* Class to parse the JSON obtained and turn it into 
* something useable
* 
* Examples taken from androidhive.info
*
* @author Anthony Mazzawi
*/

public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    /** Constructor */
    public JSONParser() {
    }

    /**
    * Make a request and obtain JSON from the url, will potentially be
    * able to POST in the near future instead of only being able to GET
    *
    * @param url The url where json is stored
    * @param params Parameters of HTTP
    * @return Return the json object which java can use
    */
    public JSONObject makeHttpRequest(String url, List<NameValuePair> params){
        // Make HTTP request
        try{
            DefaultHttpClient httpClient = new DefaultHttpClient();
            String paramString = URLEncodedUtils.format(params, "utf-8");
            url += "?" + paramString;
            HttpGet httpGet = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        }
        catch (UnsupportedEncodingException e){e.printStackTrace();}
        catch (ClientProtocolException e){e.printStackTrace();}
        catch (IOException e){e.printStackTrace();}

        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                sb.append(line).append("\n");
            is.close();
            json = sb.toString();
        }
        catch (Exception e){
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        //try to parse the string to a JSON object
        try{
            jObj = new JSONObject(json);
        }
        catch (JSONException e){
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        //Return JSON String
        return jObj;
    }
}