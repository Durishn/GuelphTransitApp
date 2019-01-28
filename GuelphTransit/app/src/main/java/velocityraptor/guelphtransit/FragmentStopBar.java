package velocityraptor.guelphtransit;
import velocityraptor.guelphtransit.interfaces.ScrapeRespCallback;


import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;

import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;



/**
 * This class defines the Fragment for the main bottom bar, filled with Bus Stops
 *      @author: Nic Durish & Jackson Keenan (@mail.uoguelph.ca).
 *
 * I have exclusive control over this submission via my password.
 * By including this statement in this header comment, I certify that:
 * 1) I have read and understood the University policy on academic integrity;
 * 2) I have completed the Computing with Integrity Tutorial on Moodle; and
 * I assert that this cd work is my own. I have appropriately acknowledged any and all material
 * (data, images, ideas or words) that I have used, whether directly quoted or paraphrased.
 * Furthermore, I certify that this assignment was prepared by me specifically for this course.
 */
public class FragmentStopBar extends Fragment{

    public int routePos;
    public Marker stopMarker;
    public StopPopup stopPopUp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_busbar, container, false);
        //Get Bundle for routePos
        Bundle bundle = this.getArguments();
        routePos = bundle.getInt("EXTRA_ROUTE_POS");
        //Adapter to create bus schedule list
        AdapterStopList busListRouteAdapter = new AdapterStopList(getActivity()
                .getApplicationContext(),"botbar",MainActivity.routeList
                .get(routePos).getStopList());

        //Get a reference to the listView & set number of columns
        GridView gridView = (GridView) view.findViewById(R.id.list_view_bus);
        gridView.setNumColumns(MainActivity.routeList.get(routePos).getStopList().size());

        //Set width_param and convert to display independent pixels
        float scale = getActivity().getResources().getDisplayMetrics().density;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(MainActivity
                .routeList.get(routePos).getStopList().size()*100 * scale + 0.5f)
                ,(int)(100 * scale + 0.5f));
        gridView.setLayoutParams(params);
        //Click Listener for Map Markers (Same as OnClick for bottom bar)
        MainActivity.mainMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for(int i = 0;i < MainActivity.routeList.get(routePos).getStopList().size();i++) {
                    if(MainActivity.routeList.get(routePos).getStopList().get(i).getStopID().equals(marker.getTitle())){
                        /*Setup*/
                        if(stopPopUp!= null){stopPopUp.dismiss();} //Closing Previous popup
                            WindowManager wm = (WindowManager) MainActivity.getAppContext().getSystemService(MainActivity.getAppContext().WINDOW_SERVICE);
                            Display display = wm.getDefaultDisplay();
                            Point size = new Point();
                            display.getSize(size);

                            //Code for getting activity for stopPopUp [Jackson Keenan START]
                            LayoutInflater popUpLayout = (LayoutInflater)getActivity().getLayoutInflater();
                            View popUpView = popUpLayout.inflate(R.layout.popup, null);
                            //Jackson Keenan [END]

                            //Custom popup class for showing popup with information about the stop
                            stopPopUp = new StopPopup(popUpView, LayoutParams.WRAP_CONTENT
                                    ,size.x,routePos,i);
                            //Scrape next bus times, popup will automatically add
                            //JSON Parsed times when loadBus class is complete


                            /*Display PopUp*/
                            stopPopUp.showAsDropDown(popUpView, 10, MainActivity.topHeight);
                            //PopUpWindow: Jackson Keenan [END]

                            //Stop Location Markers: Jackson Keenan [START]

                            //Get Stop Long + Lat
                            Float stopLat = MainActivity.routeList.get(routePos).getStopList().get(i).getLatitude();
                            Float stopLong = MainActivity.routeList.get(routePos).getStopList().get(i).getLongitude();
                            LatLng stopLatLong = new LatLng(stopLat, stopLong);

                            //Add Stop Marker
                            if(stopMarker != null) stopMarker.remove();
                            stopMarker = MainActivity.mainMap.addMarker(new MarkerOptions()
                                    .position(stopLatLong)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                    .title(MainActivity.routeList.get(routePos).getStopList().get(i).getStopID()));
                            MainActivity.mapUpdate = CameraUpdateFactory.newLatLng(stopLatLong);
                            MainActivity.mainMap.animateCamera(MainActivity.mapUpdate);
                            //Stop Location Markers: Jackson Keenan [END]
                            break;
                        }
                    }
                return true;
            }
        });

        //Click Listener for Bottom Bar
        OnItemClickListener listener = new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(stopPopUp!= null){stopPopUp.dismiss();}                              //Closing Previous popups
                if(MainActivity.stopPopUp!= null){MainActivity.stopPopUp.dismiss();}
                //Code for getting activity for stopPopUp [Jackson Keenan START]

                WindowManager wm = (WindowManager) MainActivity.getAppContext().getSystemService(MainActivity.getAppContext().WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);

                LayoutInflater popUpLayout = (LayoutInflater)getActivity().getLayoutInflater();
                View popUpView = popUpLayout.inflate(R.layout.popup, null);
                stopPopUp = new StopPopup(popUpView, LayoutParams.WRAP_CONTENT
                        ,size.x,routePos,position);
                //Scrape next bus times, popup will automatically add
                //JSON Parsed times when loadBus class is complete

                stopPopUp.showAsDropDown(popUpView, 10, MainActivity.topHeight);
                //PopUpWindow: Jackson Keenan [END]

                //Stop Location Markers: Jackson Keenan [START]

                //Get Stop Long + Lat
                Float stopLat = MainActivity.routeList.get(routePos).getStopList().get(position).getLatitude();
                Float stopLong = MainActivity.routeList.get(routePos).getStopList().get(position).getLongitude();
                LatLng stopLatLong = new LatLng(stopLat, stopLong);

                //Add Stop Marker
                if(stopMarker != null) stopMarker.remove();
                stopMarker = MainActivity.mainMap.addMarker(new MarkerOptions()
                        .position(stopLatLong)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                        .title(MainActivity.routeList.get(routePos).getStopList().get(position).getStopID()));
                MainActivity.mapUpdate = CameraUpdateFactory.newLatLngZoom(stopLatLong, 13); //Change Zoom to 14 / 15 for release
                MainActivity.mainMap.animateCamera(MainActivity.mapUpdate);

                //Stop Location Markers: Jackson Keenan [END]
            }
        };

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                boolean repeatFav = false;
                Stop selectedStop = MainActivity.routeList.get(routePos).getStopList().get(position);
                for (int i=0; i<MainActivity.favoriteList.size(); i++){
                    if(MainActivity.favoriteList.get(i).getStopID() == selectedStop.getStopID()){
                        Toast.makeText(getActivity().getApplicationContext(), "Stop " + selectedStop.getStopID()
                                + " is already in Favorites.", Toast.LENGTH_SHORT).show();
                        repeatFav = true;
                    }
                }
                if (!repeatFav) {
                    MainActivity.favoriteList.add(0, selectedStop);
                    Toast.makeText(getActivity().getApplicationContext(), "Stop " + selectedStop.getStopID()
                            + " has been added to Favorites.", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        //Set listeners and adapters for the gridView
        gridView.setOnItemClickListener(listener);
        gridView.setAdapter(busListRouteAdapter);
        return view;
    }

}
