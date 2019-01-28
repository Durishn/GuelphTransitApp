/*Stop.java
Skeleton originally made by William (Aidan) Maher
Anthony, Aidan worked overall on the file
* This is class for representing the stops on the map/UI
* which is used in Routes (Routes are composed of stops).
*/

package velocityraptor.guelphtransit;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by Anthony on 2/23/15.
 * Class Skeleton Created by William (Aidan) Maher
 */
public class Stop {

    private String stopID;
    private String stopName;
    private ArrayList<String> weekTimes;
    private ArrayList<String> satTimes;
    private ArrayList<String> sunTimes;
    private float latitude;
    private float longitude;

    //Written by William (Aidan) Maher and Anthony Mazzawi
    /** Constructor with array lists for times */
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

        StringTokenizer st2 = new StringTokenizer(satTimes, " ");
        while(st2.hasMoreTokens()){
            String time = st2.nextToken();
            this.satTimes.add(time);
        }

        StringTokenizer st3 = new StringTokenizer(sunTimes, " ");
        while(st3.hasMoreTokens()){
            String time = st3.nextToken();
            this.sunTimes.add(time);
        }
    }
    /*
    Getters and setters, hopefully self documenting
    Written by Anthony and Aidan
    */

    /** 
    * Get the stop Name
    * @return Bus stop name
    */
    public String getStopName() { return this.stopName; }

    /** 
    * Set the Stop Name
    * @param stopName bus Stop Name
    */
    public void setStopName(String stopName){this.stopName = stopName;}

    /** 
    * Get the Stop ID
    * @return Bus stop ID
    */
    public String getStopID() {return this.stopID;}

    /** 
    * Set the stop ID of a bus Stop
    * @param stopID stopID of stop
    */
    public void setStopID(String stopID) {this.stopID = stopID;}

    /** 
    * Get the Week Times
    * @return Week Times in an Array List
    */
    public ArrayList<String> getWeekTimes() { return this.weekTimes;}

    /** 
    * Set the Week Times
    * @param weekTimes In an ArrayList
    */
    public void setWeekTimes(ArrayList<String> weekTimes){
        //Clear the arrayList
        this.weekTimes.clear();
        for(String s:weekTimes) {this.weekTimes.add(new String(s));}
    }

    /** 
    * Set the WeekTimes
    * NOT COMPLETE
    * @param weekTimes In a String
    */
    public void setWeekTimes(String weekTimes){
        //Clear the arrayList
        this.weekTimes.clear();
    }

    /** 
    * Get the Saturday Times
    * @return Saturday Times in an Array List
    */
    public ArrayList<String> getSatTimes() { return this.satTimes; }

    /** 
    * Set the Saturday Times
    * @param satTimes In an ArrayList
    */
    public void setSatTimes(ArrayList<String> satTimes){
        //Clear the arrayList
        this.satTimes.clear();
        for(String s:satTimes) {this.satTimes.add(new String(s));}
    }

    /** 
    * Set the Saturday Times
    * NOT COMPLETE
    * @param satTimes In a String
    */
    public void setSatTimes(String satTimes){
        //Clear the arrayList
        this.satTimes.clear();
    }

    /** 
    * Get the  Sunday Times
    * @return Sunday Times in an Array List
    */
    public ArrayList<String> getSunTimes() { return this.sunTimes;}

    /** 
    * Set the Sunday Times
    * @param sunTimes In an ArrayList
    */
    public void setSunTimes(ArrayList<String> sunTimes){
        //Clear the arrayList
        this.sunTimes.clear();
        for(String s:sunTimes) {this.sunTimes.add(new String(s));}
    }

    /** 
    * Set the Sunday Times
    * NOT COMPLETE
    * @param weekTimes In a String
    */
    public void setSunTimes(String sunTimes){
        //Clear the arrayList
        this.sunTimes.clear();
    }

    /** 
     * Return the latitude
     * @return The latitude of the bus Stop
     */
    public float getLatitude() {return this.latitude;}

    /** 
    * Set the latitude
    * @param latitude The latitude of the bus stop.
    */
    public void setLatitude(float latitude) {this.latitude = latitude;}

    /** 
    * Get the longitude
    * @param longitude The longitude of the bus stop.
    */
    public float getLongitude() {return this.longitude;}

    /** 
    * Set the longitude
    * @param longitude The longitude of the bus stop.
    */
    public void setLongitude(float longitude) {this.longitude = longitude;}

}
