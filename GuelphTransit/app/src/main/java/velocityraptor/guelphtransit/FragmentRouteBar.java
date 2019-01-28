package velocityraptor.guelphtransit;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This class defines the Fragment for the main bottom bar, filled with Bus Routes
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
public class FragmentRouteBar extends Fragment {
    public FragmentStopBar newFragment;

    private int pinResourceID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_busbar, container, false);

        //Adapter to create bus schedule list
        AdapterBusList busListRouteAdapter = new AdapterBusList(getActivity()
                .getApplicationContext(), "botbar", MainActivity.routeList);

        //Get a reference to the listView & set number of columns
        GridView gridView = (GridView) view.findViewById(R.id.list_view_bus);
        gridView.setNumColumns(MainActivity.routeList.size());

        //Set width_param and convert to display independent pixels
        float scale = getActivity().getResources().getDisplayMetrics().density;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(MainActivity
                .routeList.size()*100 * scale + 0.5f), (int)(100 * scale + 0.5f));
        gridView.setLayoutParams(params);

        //Create Click Listener
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Create fragment and give it an argument specifying the article it should show
                newFragment = new FragmentStopBar();
                Bundle args = new Bundle();
                args.putInt("EXTRA_ROUTE_POS", position);
                MainActivity.panelMode = 3;
                ((MainActivity) getActivity()).setPanelColour();
                newFragment.setArguments(args);

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.bot_bar_frag_view, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();

                //Jackson Keenan [Start]
                MainActivity.mainMap.clear();
                Float maxLat = (float) 0.00;
                Float minLat = (float) 100.00;
                Float maxLong = (float) -120.00;
                Float minLong = (float) 0.00;

                Polyline routeLine = MainActivity.mainMap.addPolyline(new PolylineOptions().width(5).color(0x7F0000FF));
                List<LatLng> routePoints = new ArrayList<>();

                ArrayList<String> timeList;
                String listIterator;
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat tempDay = new SimpleDateFormat("EE");
                String dayOfWeek = tempDay.format(cal.getTime());
                SimpleDateFormat dateTime = new SimpleDateFormat("HHmm");
                String currentTime = dateTime.format(cal.getTime());

                for (int i = 0; i < MainActivity.routeList.get(position).getStopList().size(); i++) {
                    Float stopLat = MainActivity.routeList.get(position).getStopList().get(i).getLatitude();
                    Float stopLong = MainActivity.routeList.get(position).getStopList().get(i).getLongitude();
                    if (stopLat < minLat) {
                        minLat = stopLat;
                    } else if (stopLat > maxLat) {
                        maxLat = stopLat;
                    }
                    if (stopLong < minLong) {
                        minLong = stopLong;
                    } else if (stopLong > maxLong) {
                        maxLong = stopLong;
                    }

                    //Add Stop Markers
                    LatLng stopLatLong = new LatLng(stopLat, stopLong);
                    routePoints.add(i, stopLatLong);


                    /*Place specific coloured dot based on time [WILLIAM MAHER]*/


                        /*Calculating schedule Times*/
                    switch (dayOfWeek) {
                        case "Sat":
                            timeList = MainActivity.routeList.get(position).getStopList().get(i)
                                    .getSatTimes();
                            break;
                        case "Sun":
                            timeList = MainActivity.routeList.get(position).getStopList().get(i)
                                    .getSunTimes();
                            break;
                        default:
                            timeList = MainActivity.routeList.get(position).getStopList().get(i)
                                    .getWeekTimes();
                            break;
                    }

                    int stopInt=-1;
                    for (int j = 0;j<timeList.size();j++){
                        listIterator = timeList.get(j);
                        listIterator = listIterator.replace(":", "");
                       // Log.i("log", currentTime.toString());
                        if(Integer.parseInt(currentTime) < Integer.parseInt(listIterator)){
                            stopInt = j;
                            break;
                        }
                    }

                    String stopTime=timeList.get(stopInt).replace(":","");
                    currentTime=currentTime.replace(":","");
                    //currTime - stopTime

                    int difference = Integer.parseInt(stopTime)-Integer.parseInt(currentTime);
                    //Log.i("log","Difference at"+ MainActivity.routeList.get(position).getStopList().get(i).getStopID()
                      //     +"is "+ difference + "Currtime: "+currentTime);
                    //Based on difference in time, choose different pin colour
                    if(difference >= 15){
                        pinResourceID = R.drawable.green_dot;
                    }else if (difference >=5 && difference <15){
                        pinResourceID = R.drawable.yellow_dot;
                    }else if(difference < 5){
                        pinResourceID = R.drawable.red_dot;
                    }else{
                        pinResourceID = R.drawable.red_dot;
                    }
                    //WILLIAM MAHER [END]
                    MainActivity.mainMap.addMarker(new MarkerOptions()
                            .position(stopLatLong)
                            .icon(BitmapDescriptorFactory.fromResource(pinResourceID))
                            .title(MainActivity.routeList.get(position).getStopList().get(i).getStopID()));
                }

                routeLine.setPoints(routePoints);
                routeLine.setGeodesic(false);
                Float latCenter = maxLat - ((maxLat - minLat) / 2);
                Float longCenter = maxLong - ((maxLong - minLong) / 2);
                LatLng routeLatLong = new LatLng(latCenter, longCenter);
                MainActivity.mapUpdate = CameraUpdateFactory.newLatLngZoom(routeLatLong, 12);
                MainActivity.mainMap.animateCamera(MainActivity.mapUpdate);
                //Jackson Keenan [Start]
            }
        });

        //Set listeners and adapters for the gridView
        gridView.setAdapter(busListRouteAdapter);
        return view;
    }
}
