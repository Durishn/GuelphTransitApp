package velocityraptor.guelphtransit;

import android.app.FragmentManager;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import velocityraptor.guelphtransit.interfaces.ScrapeRespCallback;


/**
 * This class defines the Fragment for the main bottom bar, filled with Bus Stops
 *      @author: Nic Durish & Jackson Keenan (@mail.uoguelph.ca).
 *
 * I have exclusive control over this submission via my password.
 * By including this statement in this header comment, I certify that:
 * 1) I have read and understood the University policy on academic integrity;
 * 2) I have completed the Computing with Integrity Tutorial on Moodle; and
 * I assert that this work is my own. I have appropriately acknowledged any and all material
 * (data, images, ideas or words) that I have used, whether directly quoted or paraphrased.
 * Furthermore, I certify that this assignment was prepared by me specifically for this course.
 */
public class FragmentSpecialBar extends Fragment{

    public int fragMode;
    public Marker stopMarker;
    public PopupWindow stopPopUp;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_busbar, container, false);

        //Get Bundle for routePos
        Bundle bundle = this.getArguments();
        fragMode = bundle.getInt("EXTRA_MODE");
        GridView gridView;
        AdapterStopList busListRouteAdapter;

        //Adapter to create bus schedule list
        if (fragMode == 0) {
            busListRouteAdapter = new AdapterStopList(getActivity()
                    .getApplicationContext(), "botbar", MainActivity.favoriteList);

            //Get a reference to the listView & set number of columns
            gridView = (GridView) view.findViewById(R.id.list_view_bus);
            gridView.setNumColumns(MainActivity.favoriteList.size());

            //Set width_param and convert to display independent pixels
            float scale = getActivity().getResources().getDisplayMetrics().density;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (MainActivity
                    .favoriteList.size() * 100 * scale + 0.5f)
                    , (int) (100 * scale + 0.5f));
            gridView.setLayoutParams(params);
        }
        else {
            busListRouteAdapter = new AdapterStopList(getActivity()
                    .getApplicationContext(), "botbar", MainActivity.locationList);

            //Get a reference to the listView & set number of columns
            gridView = (GridView) view.findViewById(R.id.list_view_bus);
            gridView.setNumColumns(MainActivity.locationList.size());

            //Set width_param and convert to display independent pixels
            float scale = getActivity().getResources().getDisplayMetrics().density;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (MainActivity
                    .locationList.size() * 100 * scale + 0.5f)
                    , (int) (100 * scale + 0.5f));
            gridView.setLayoutParams(params);
        }

        //Click Listener for Bottom Bar
        OnItemClickListener listener = new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(stopPopUp!= null){stopPopUp.dismiss();} //Closing Previous popup
                MainActivity.mainMap.clear();

                //Favourite View
                if(MainActivity.panelMode==1 || MainActivity.panelMode==3) {
                    MainActivity.panelMode = 3;
                    //Get Stop Long + Lat
                    Float stopLat = MainActivity.favoriteList.get(position).getLatitude();
                    Float stopLong = MainActivity.favoriteList.get(position).getLongitude();
                    LatLng stopLatLong = new LatLng(stopLat, stopLong);

                    //Add Stop Marker
                    if(stopMarker != null) stopMarker.remove();
                    stopMarker = MainActivity.mainMap.addMarker(new MarkerOptions()
                            .position(stopLatLong)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                            .title(MainActivity.favoriteList.get(position).getStopID()));
                    MainActivity.mapUpdate = CameraUpdateFactory.newLatLngZoom(stopLatLong, 13); //Change Zoom to 14 / 15 for release
                    MainActivity.mainMap.animateCamera(MainActivity.mapUpdate);

                    for (int i = 0; i < MainActivity.routeList.size(); i++) {
                        for (int j = 0; j < MainActivity.routeList.get(i).getStopList().size(); j++) {
                            if(MainActivity.routeList.get(i).getStopList().get(j).getStopID().equals(MainActivity.favoriteList.get(position).getStopID())){
                                if(MainActivity.stopPopUp!= null){MainActivity.stopPopUp.dismiss();} //Closing Previous popup
                                if(stopPopUp!= null){stopPopUp.dismiss();} //Closing Previous popup
                                //Code for getting activity for stopPopUp [Jackson Keenan START]

                                WindowManager wm = (WindowManager) MainActivity.getAppContext().getSystemService(MainActivity.getAppContext().WINDOW_SERVICE);
                                Display display = wm.getDefaultDisplay();
                                Point size = new Point();
                                display.getSize(size);

                                LayoutInflater popUpLayout = (LayoutInflater)getActivity().getLayoutInflater();
                                View popUpView = popUpLayout.inflate(R.layout.popup, null);
                                //Jackson Keenan [END]

                                //Custom popup class for showing popup with information about the stop
                                stopPopUp = new StopPopup(popUpView, ViewGroup.LayoutParams.WRAP_CONTENT
                                        , size.x,i,j);
                                //Scrape next bus times, popup will automatically add
                                //JSON Parsed times when loadBus class is complete


                                    /*Display PopUp*/
                                stopPopUp.showAsDropDown(popUpView, 10, MainActivity.topHeight);
                                //PopUpWindow: Jackson Keenan [END]

                                break;
                            }
                        }
                    }
                }

                //Location View
                else if(MainActivity.panelMode==2 || MainActivity.panelMode==4){
                    MainActivity.panelMode = 4;
                    //Get Stop Long + Lat
                    Float stopLat = MainActivity.locationList.get(position).getLatitude();
                    Float stopLong = MainActivity.locationList.get(position).getLongitude();
                    LatLng stopLatLong = new LatLng(stopLat, stopLong);

                    //Add Stop Marker
                    if(stopMarker != null) stopMarker.remove();
                    stopMarker = MainActivity.mainMap.addMarker(new MarkerOptions()
                            .position(stopLatLong)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                            .title(MainActivity.locationList.get(position).getStopID()));
                    MainActivity.mapUpdate = CameraUpdateFactory.newLatLngZoom(stopLatLong, 13); //Change Zoom to 14 / 15 for release
                    MainActivity.mainMap.animateCamera(MainActivity.mapUpdate);

                    for (int i = 0; i < MainActivity.routeList.size(); i++) {
                        for (int j = 0; j < MainActivity.routeList.get(i).getStopList().size(); j++) {
                            if(MainActivity.routeList.get(i).getStopList().get(j).getStopID().equals(MainActivity.locationList.get(position).getStopID())){
                                if(MainActivity.stopPopUp!= null){MainActivity.stopPopUp.dismiss();} //Closing Previous popup
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
                                stopPopUp = new StopPopup(popUpView, ViewGroup.LayoutParams.WRAP_CONTENT
                                        , size.x,i,j);
                                //Scrape next bus times, popup will automatically add
                                //JSON Parsed times when loadBus class is complete


                                    /*Display PopUp*/
                                stopPopUp.showAsDropDown(popUpView, 10, MainActivity.topHeight);
                                //PopUpWindow: Jackson Keenan [END]

                                break;
                            }
                        }
                    }
                }
            }
        };

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (fragMode == 0){
                    Stop selectedStop = MainActivity.favoriteList.get(position);
                    Toast.makeText(getActivity().getApplicationContext(), "Stop " + selectedStop.getStopID()
                            + " has been removed from your Favorites.", Toast.LENGTH_SHORT).show();
                    MainActivity.favoriteList.remove(position);
                    ((MainActivity)getActivity()).favButtonClick(getView());
                }
                else {
                    boolean repeatFav = false;
                    Stop selectedStop = MainActivity.locationList.get(position);
                    for (int i = 0; i < MainActivity.favoriteList.size(); i++) {
                        if (MainActivity.favoriteList.get(i).getStopID() == selectedStop.getStopID()) {
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
