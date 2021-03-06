
package velocityraptor.guelphtransit;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;

/**
 * This class defines the Activity which builds a stops scheduled times page, given
 * the route position and stop position.
 *      @author: Nic Durish.
 *
 * I have exclusive control over this submission via my password.
 * By including this statement in this header comment, I certify that:
 * 1) I have read and understood the University policy on academic integrity;
 * 2) I have completed the Computing with Integrity Tutorial on Moodle; and
 * I assert that this work is my own. I have appropriately acknowledged any and all material
 * (data, images, ideas or words) that I have used, whether directly quoted or paraphrased.
 * Furthermore, I certify that this assignment was prepared by me specifically for this course.
 */
public class ActivityScheduledTimes extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set reference on content and unpack bundles
        setContentView(R.layout.page_schedule_times);
        Bundle extras = getIntent().getExtras();
        Integer stopPos = extras.getInt("EXTRA_STOP_POS");
        Integer routePos = extras.getInt("EXTRA_ROUTE_POS");
        String routeNum = MainActivity.routeList.get(routePos).getRouteName();
        String routeName = MainActivity.routeList.get(routePos).getRouteNameString();

        //Retrieve Stop
        Stop stop = MainActivity.routeList.get(routePos).getStopList().get(stopPos);

        //Set Texts for given boxes
        TextView txt = (TextView)findViewById(R.id.route_id);
        txt.setText("Route " + routeNum +":");
        txt = (TextView)findViewById(R.id.route_name);
        txt.setText(routeName);
        txt = (TextView)findViewById(R.id.stop_id);
        txt.setText("Stop " + stop.getStopID() + ":");
        txt = (TextView)findViewById(R.id.stop_name);
        txt.setText(stop.getStopName());
        txt = (TextView)findViewById(R.id.sched_coords);
        txt.setText((int) stop.getLatitude() + ", " + (int) stop.getLongitude());

        //Adapter to create bus schedule lists
        ArrayAdapter<String> weekAdapter = new ArrayAdapter <> (this
                , R.layout.textview_times, stop.getWeekTimes());
        ArrayAdapter<String> satAdapter = new ArrayAdapter <> (this
                , R.layout.textview_times, stop.getSatTimes());
        ArrayAdapter<String> sunAdapter = new ArrayAdapter <> (this
                , R.layout.textview_times, stop.getSunTimes());

        //Get a reference to the listViews
        ListView listView = (ListView) this.findViewById(R.id.stop_times_week_list);
        listView.setAdapter(weekAdapter);
        listView = (ListView) this.findViewById(R.id.stop_times_sat_list);
        listView.setAdapter(satAdapter);
        listView = (ListView) this.findViewById(R.id.stop_times_sun_list);
        listView.setAdapter(sunAdapter);


        /* This code references code provided by User: Bhavin on May 17 '12 at 10:54
         * , the code can be found at: http://stackoverflow.com/questions/10634231/
         * how-to-display-current-time-that-changes-dynamically-for-every-second-in-android
         * Description: Begin Dynamic Clock Thread */
        Thread myThread;
        Runnable myRunnableThread = new CountDownRunner();
        myThread= new Thread(myRunnableThread);
        myThread.start();
    }

    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try{
                    TextView txtCurrentTime= (TextView)findViewById(R.id.clock_current);
                    Date dt = new Date();
                    int hours = dt.getHours();
                    int minutes = dt.getMinutes();
                    int seconds = dt.getSeconds();
                    String curTime = hours + ":" + minutes + ":" + seconds;
                    txtCurrentTime.setText(curTime);
                }catch (Exception ignored) {}
            }
        });
    }

    class CountDownRunner implements Runnable {
        // @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    doWork();
                    Thread.sleep(1000); // Pause of 1 Second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}

