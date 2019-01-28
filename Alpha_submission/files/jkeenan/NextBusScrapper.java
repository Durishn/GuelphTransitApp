package velocityraptor.guelphtransit;
import java.io.IOException;
import android.os.AsyncTask;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import static android.text.TextUtils.indexOf;
import java.io.*;

/**
 * Created by Jackson Keenan on 2/26/2015.
 * Majority of the time spent on this module was researching Jsoup only to come to the realization it cannot scrape Javascript generated HTML
 * There was multiple instances of this class however I did not save all of them.
 */
public class NextBusScrapper {
    String route;
    String busStop;
    String finalUrl;
    String etaOne;
    String etaTwo;
    String etaThree;

    /*Constructor*/
    NextBusScrapper(String r, String b){
        String baseUrl = "http://www.nextbus.com/#!/guelph/";
        route = r;
        busStop = b;

        String direction = route;                                   //*Need to add a check for busses with non-canonical direction indicators
        direction = direction + "_loop/";
        baseUrl = baseUrl + route + "/" + direction;
        String urlStopTag = busStop;                                //*Need Section to convert busStop to the nextBus URL strings
        baseUrl = baseUrl + urlStopTag;
        finalUrl = baseUrl.toLowerCase();                           //Convert Final URL to lowercase
        System.out.println(finalUrl);
    }

    public String getFirstEta(){
        String docTitle;
        try{
            Document document = Jsoup.connect(finalUrl).get();      //Getting Wrong HTML file
            //System.out.println(document.toString());
            docTitle = document.title();
            System.out.println(docTitle);
            etaOne = docTitle.substring(9,11);
            etaOne = etaOne.trim();
            return etaOne;
        } catch (IOException e){
            e.printStackTrace();
            return "error";
        }
    }
    public String getSecondEta(){
        String docTitle;
        try{
            Document document = Jsoup.connect(finalUrl).get();      //Getting Wrong HTML file
            //System.out.println(document.toString());
            docTitle = document.title();
            int ampIndex = docTitle.indexOf("&");
            etaTwo = docTitle.substring(ampIndex+2,ampIndex+4);
            etaTwo = etaTwo.trim();
            return etaTwo;
        } catch (IOException e){
            e.printStackTrace();
            return "error";
        }
    }

    /*public void getThirdEta(){
        try{
            Document document = Jsoup.connect(finalUrl).get();
            etaThree = document.title();
        } catch (IOException e){
            e.printStackTrace();
        }
    }*/
}