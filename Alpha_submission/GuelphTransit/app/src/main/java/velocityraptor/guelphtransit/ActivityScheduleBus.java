package velocityraptor.guelphtransit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This class defines the Activity which builds a bus/stp list page.
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
    public class ActivityScheduleBus extends ActionBarActivity implements AdapterView.OnItemClickListener {

        private String selectedRoute = "Transit Schedule";
        private int selectedRoutePos = 0;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.page_listview_sched);

            //Adapter to create bus schedule list
            AdapterBusList busListRouteAdapter = new AdapterBusList(
                    this, "list", MainActivity.routeList);

            //Get a reference to the listView
            ListView listView = (ListView) this.findViewById(R.id.list_view_sched);
            listView.setOnItemClickListener(this);
            listView.setAdapter(busListRouteAdapter);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                // Respond to the action bar's Up/Home button
                case android.R.id.home:
                    if (!selectedRoute.equals("Transit Schedule")){
                        selectedRoute = "Transit Schedule";
                        selectedRoutePos = 0;
                        setTitle(selectedRoute);

                        //Adapter to create bus schedule list
                        AdapterBusList busListRouteAdapter = new AdapterBusList(
                                this, "list", MainActivity.routeList);
                        //Get a reference to the listView
                        ListView listView = (ListView) this.findViewById(R.id.list_view_sched);
                        listView.setOnItemClickListener(this);
                        listView.setAdapter(busListRouteAdapter);
                    }
                    else
                        NavUtils.navigateUpFromSameTask(this);
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (selectedRoute.equals("Transit Schedule")){
                TextView txt = (TextView) view.findViewById(R.id.bus_route);
                selectedRoute = txt.getText().toString();
                selectedRoutePos = position;
                setTitle("Route "+ MainActivity.routeList.get(selectedRoutePos).getRouteName()
                        + ": " + selectedRoute);

                //Adapter to create bus schedule list
                AdapterStopList busStopListAdapter = new AdapterStopList(
                        this, "list", MainActivity.routeList.get(position).getStopList());

                //Get a reference to the listView
                ListView listView = (ListView) this.findViewById(R.id.list_view_sched);
                listView.setOnItemClickListener(this);
                listView.setAdapter(busStopListAdapter);
            }
            else{

                //Call New Activity
                Intent intent = new Intent(this, ActivityScheduledTimes.class);
                Bundle extras = new Bundle();
                extras.putInt("EXTRA_STOP_POS", position);
                extras.putInt("EXTRA_ROUTE_POS", selectedRoutePos);
                intent.putExtras(extras);
                startActivity(intent);
            }
        }
    }