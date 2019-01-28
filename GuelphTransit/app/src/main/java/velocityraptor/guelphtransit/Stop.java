package velocityraptor.guelphtransit;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by Anthony Mazzawi on 2/23/15.
 * Class Skeleton Created by William (Aidan) Maher
 *
 * This is class for representing the stops on the map/UI
 * which is used in Routes (Routes are composed of stops).
 */
public class Stop {

    private String stopID, stopName;
    private ArrayList<String> weekTimes, satTimes, sunTimes;
    private float latitude, longitude;

    //Written by William (Aidan) Maher and Anthony Mazzawi
    /** Constructor with array lists for time */
    public Stop(String stopID, String stopName, ArrayList<String> weekTimes, ArrayList<String> satTimes, ArrayList<String> sunTimes,
                float latitude, float longitude) {
        this.stopName = stopName;
        this.stopID = stopID;
        this.weekTimes = new ArrayList<>();
        this.satTimes = new ArrayList<>();
        this.sunTimes = new ArrayList<>();

        for (String s :weekTimes) {this.weekTimes.add(s);}
        for (String s :satTimes) {this.satTimes.add(s);}
        for (String s:sunTimes) {this.sunTimes.add(s);}

        this.latitude = latitude;
        this.longitude = longitude;
    }

    /** Constructor with strings for times */
    public Stop(String stopID, String stopName, String weekTimes, String satTimes,
                String sunTimes, float latitude, float longitude){
        this.stopID = stopID;
        this.stopName = stopName;
        this.latitude = latitude;
        this.longitude = longitude;

        this.weekTimes = new ArrayList<>();
        this.satTimes = new ArrayList<>();
        this.sunTimes = new ArrayList<>();

        StringTokenizer st1 = new StringTokenizer(weekTimes, " ");
        while(st1.hasMoreTokens()){
            String time = st1.nextToken();
            this.weekTimes.add(time);
        }

        if(satTimes != null)
        {
            StringTokenizer st2 = new StringTokenizer(satTimes, " ");
            while(st2.hasMoreTokens())
            {
                String time = st2.nextToken();
                this.satTimes.add(time);
            }
        }
        else
            this.satTimes = null;

        if(sunTimes != null)
        {
            StringTokenizer st3 = new StringTokenizer(sunTimes, " ");
            while (st3.hasMoreTokens())
            {
                String time = st3.nextToken();
                this.sunTimes.add(time);
            }
        }
        else
            this.sunTimes = null;
    }
    /*
    Getters and setters, hopefully self documenting
    Written by Anthony and Aidan
    */

    public String getStopID() {return this.stopID;}

    /**
     * Get the stop name
     */
    public String getStopName() {return this.stopName;}

    /** 
    * Get the Week Times
    * @return Week Times in an Array List
    */
    public ArrayList<String> getWeekTimes() { return this.weekTimes;}

    /** 
    * Get the Saturday Times
    * @return Saturday Times in an Array List
    */
    public ArrayList<String> getSatTimes() { return this.satTimes; }

    /** 
    * Get the  Sunday Times
    * @return Sunday Times in an Array List
    */
    public ArrayList<String> getSunTimes() { return this.sunTimes;}

    /** 
     * Return the latitude
     * @return The latitude of the bus Stop
     */
    public float getLatitude() {return this.latitude;}

    /** 
    * Get the longitude
    */
    public float getLongitude() {return this.longitude;}
}
