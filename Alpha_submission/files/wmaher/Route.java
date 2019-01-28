/*Route.java
@author: Aidan Maher, Anthony Mazzawi, Nic Durish and Jackson Keenan
Skeleton originally made by William (Aidan) Maher
Anthony, Nick and Aidan worked overall on the file
* This is the class representing routes, composed of stops
* which is pulled and populated from our database.
*/

package velocityraptor.guelphtransit;

import java.util.ArrayList;

/**
 * Class for Bus Routes
 */
public class Route {
    private String routeName;
    private String routeNameString;
    private ArrayList<Stop> stopList;

    /**
     * Constructors
     */
    public Route(String routeName) {
        this.routeName = routeName;
        generateRouteNameString();
        this.stopList = new ArrayList<>();
    }

    public Route(String routeName, ArrayList<Stop> stopList) {
        this.routeName = routeName;
        generateRouteNameString();
        this.stopList = new ArrayList<>();
        for (Stop s : stopList) {
            this.stopList.add(new Stop(s.getStopName(), s.getStopID(),
                    s.getWeekTimes(), s.getSatTimes(), s.getSunTimes(),
                    s.getLatitude(), s.getLongitude()));
        }
    }

    /**
     * Setters
     */
    public void setRouteName(String routeName) {
        this.routeName = routeName;
        generateRouteNameString();}

    public void setRouteNameString(String routeNameString) {
        this.routeNameString = routeNameString;}

    public void setStopList(ArrayList<Stop> stopList){

        this.stopList.clear();
        for(Stop s:stopList)
        {
            this.stopList.add(new Stop(s.getStopName(),s.getStopID(),
                    s.getWeekTimes(), s.getSatTimes(), s.getSunTimes(),
                    s.getLatitude(), s.getLongitude()));
        }
    }

    /**
     * Getters
     */
    public String getRouteName() { return this.routeName;}
    public String getRouteNameString() { return this.routeNameString;}
    public ArrayList<Stop> getStopList(){return this.stopList;}

    /**
     * Extra Methods
     */
    //Generate routeNameString based on preloaded strings for routeName
    public void generateRouteNameString(){
        switch(routeName){
            case "1A": this.routeNameString = "College Edinburgh"; break;
            case "1B": this.routeNameString = "College Edinburgh"; break;
            case "2A": this.routeNameString = "West Loop"; break;
            case "2B": this.routeNameString = "West Loop"; break;
            case "3A": this.routeNameString = "East Loop"; break;
            case "3B": this.routeNameString = "East Loop"; break;
            case "4": this.routeNameString = "York"; break;
            case "5": this.routeNameString = "Gordon"; break;
            case "6": this.routeNameString = "Harvard Ironwood"; break;
            case "7": this.routeNameString = "Kortright Downey"; break;
            case "8": this.routeNameString = "Stone Road Mall"; break;
            case "9": this.routeNameString = "Waterloo"; break;
            case "10": this.routeNameString = "Imperial"; break;
            case "11": this.routeNameString = "Willow West"; break;
            case "12": this.routeNameString = "General Hospital"; break;
            case "13": this.routeNameString = "Victoria Rd Rec Centre"; break;
            case "14": this.routeNameString = "Grange"; break;
            case "15": this.routeNameString = "University College"; break;
            case "16": this.routeNameString = "Southgate"; break;
            case "20": this.routeNameString = "NorthWest Industrial"; break;
            case "50": this.routeNameString = "Stone Road Express"; break;
            case "56": this.routeNameString = "Victoria Express"; break;
            case "57": this.routeNameString = "Harvard Express"; break;
            default: this.routeNameString = "Unknown Route"; break;
        }
    }
    
    /**
    * Add a stop to the stop list if all the parameters of a stop were given
    * @param stopID ID number of bus stop
    * @param stopName Name of the bus stop
    * @param weekTimes Times the bus arrives during the week
    * @param satTimes Times the bus arrives on Saturday
    * @param sunTimes Times the bus arrives on Sunday
    */
    public void addToStopList(String stopID, String stopName, String weekTimes, String satTimes
            , String sunTimes, float latitude, float longitude) {
        Stop s = new Stop(stopID, stopName, weekTimes, satTimes,sunTimes, latitude, longitude);
        this.stopList.add(s);
    }
    
    /**
    * Add a stop to the list when an object is given.
    * @param s Stop object
    */
    public void addToStopList(Stop s){ this.stopList.add(s); }

}
