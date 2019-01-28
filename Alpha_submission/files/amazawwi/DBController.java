package velocityraptor.guelphtransit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
* DBController controls the database by selecting and 
* inserting information with the SQLite database on 
* the android device
* 
* Examples taken from androidhive.info
*
* @author Anthony Mazzawi
*/
public class DBController extends SQLiteOpenHelper{

    // Database Version
    private static final int DB_VERSION = 1;

    // Database Name
    private static final String DB_NAME = "transitDB";

    // Table Name
    private static final String TABLE_NAME = "Stops";

    // Column Names
    private static final String KEY_ROUTE = "route";
    private static final String KEY_STOPID = "stopID";
    private static final String KEY_STOPNAME = "stopName";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_WEEKTIMES = "weekTimes";
    private static final String KEY_SATTIMES = "satTimes";
    private static final String KEY_SUNTIMES = "sunTimes";

    // Table Create Statement
    private static final String CREATE_TABLE_STOPS = "CREATE TABLE "
            + TABLE_NAME + "(" + KEY_ROUTE + " varchar(100), "
            + KEY_STOPID + " varchar(5) PRIMARY KEY, " + KEY_STOPNAME
            + " varchar(100), " + KEY_LATITUDE + " float, " + KEY_LONGITUDE
            + " float, " + KEY_WEEKTIMES + " varchar(1000), " + KEY_SATTIMES
            + " varchar(100), " + KEY_SUNTIMES + " varchar(1000) " + ")";

    // Count Statement
    private static final String SELECT_COUNT = "SELECT count(*) FROM " + TABLE_NAME;

    // Select Statements
    private static final String SELECT_ALL = "SELECT * FROM " + TABLE_NAME;
    private static final String SELECT_ROUTE = "SELECT " + KEY_ROUTE + " FROM " + TABLE_NAME;

    
    public DBController(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /** 
     *  Creates the table
     *  @param db The database to be created
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //Create required table
        db.execSQL(CREATE_TABLE_STOPS);
    }

    /**
     * Upgrades the current tables
     *
     * @param db Database where the table is stored
     * @param oldVersion The old version of the database
     * @param newVersion The new version of the database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // On upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // create new tables
        onCreate(db);
    }
    
    /**
    * Insert a single stop into the database
    */
    public void insert(String route, String stopID, String stopName, String latitude,
                       String longitude, String weekTimes, String satTimes,
                       String sunTimes)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ROUTE,route);
        values.put(KEY_STOPID, stopID);
        values.put(KEY_STOPNAME, stopName);
        values.put(KEY_LATITUDE, latitude);
        values.put(KEY_LONGITUDE, longitude);
        values.put(KEY_WEEKTIMES, weekTimes);
        values.put(KEY_SATTIMES, satTimes);
        values.put(KEY_SUNTIMES, sunTimes);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    /* Determine how many entries are in the SQLite table */
    public int getCount()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(SELECT_COUNT, null);
        c.moveToFirst();
        db.close();
        return c.getInt(0);
    }

    public void insertRoutes(ArrayList<Route> routeList) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(SELECT_ROUTE, null);

        //Loop through all the entries
        if(c.moveToFirst()) {
            do{

                String route = c.getString(c.getColumnIndex(KEY_ROUTE));

                // Iterate through the route list to determine if the
                // route already exists

                boolean noRoute = true;
                for(int i = 0; i < routeList.size(); i++) {
                    if (routeList.get(i).getRouteName().equals(route))
                        noRoute = false;
                }

                // If the route is not in the list, it is added
                if(noRoute) {
                    Route r = new Route(route);
                    routeList.add(r);
                }


            } while (c.moveToNext());
        }

    }

    /**
    * Insert every stop in the array list of routes into the database
    */
    public void insertAllStops(ArrayList<Route> routeList)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(SELECT_ALL, null);

        //Loop through all the rows
        if(c.moveToFirst())
        {
            do{
                String route = c.getString(c.getColumnIndex(KEY_ROUTE));
                String stopID = c.getString(c.getColumnIndex(KEY_STOPID));
                String stopName = c.getString(c.getColumnIndex(KEY_STOPNAME));
                Float latitude = c.getFloat(c.getColumnIndex(KEY_LATITUDE));
                Float longitude = c.getFloat(c.getColumnIndex(KEY_LONGITUDE));
                String weekTimes = c.getString(c.getColumnIndex(KEY_WEEKTIMES));
                String satTimes = c.getString(c.getColumnIndex(KEY_SATTIMES));
                String sunTimes = c.getString(c.getColumnIndex(KEY_SUNTIMES));

                Stop s = new Stop(stopID, stopName, weekTimes, satTimes,
                        sunTimes, latitude, longitude);

                int routeIndex = 0;
                for(int i = 0; i < routeList.size(); i++) {
                    if (routeList.get(i).getRouteName().equals(route))
                        routeIndex = i;
                }
                routeList.get(routeIndex).addToStopList(s);

            } while (c.moveToNext());
        }
    }

    /** Close the database connection */
    public void closeDB(){
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
