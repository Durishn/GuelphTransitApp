package velocityraptor.guelphtransit.listeners;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import velocityraptor.guelphtransit.MainActivity;
import velocityraptor.guelphtransit.R;
import velocityraptor.guelphtransit.StopPopup;
import velocityraptor.guelphtransit.interfaces.ScrapeRespCallback;

/**
 * Created by William Aidan Maher on 27/03/15.
 * Originally attempted to refactor code from FragmentStopBar but was unsuccessful
 * This code is not used at all.
 * Most code belongs to Jackson Keenan, class Skeleton belongs to William Aidan Maher
 */
public class MarkerListener implements OnMarkerClickListener,ScrapeRespCallback{
    private Marker stopMarker;
    private StopPopup stopPopup;
    private int position;
    private int routePos;
    private Fragment f;

    public MarkerListener(int routePosIn,Fragment fragIn){
        super();
        routePos=routePosIn;
        f=fragIn;

    }

    @Override
    public boolean onMarkerClick(Marker marker){
        for(int i = 0;i < MainActivity.routeList.size();i++) {
            if(MainActivity.routeList.get(routePos).getStopList().get(i).getStopID().equals(marker.getTitle())){
                position=i;
                break;
            }
        }
        LayoutInflater popUpLayout = (LayoutInflater)f.getActivity().getLayoutInflater();
        View popupView = popUpLayout.inflate(R.layout.popup, null);
        ImageButton clsPopUp = (ImageButton)popupView.findViewById(R.id.dismiss);
        if(stopPopup!= null){stopPopup.dismiss();} //Closing Previous popup
        //stopPopup=new StopPopup(popupView,routePos,position);
         /*PopUp: Movement*/
        popupView.setOnTouchListener(new View.OnTouchListener(){
            int strX,
                    strY,
                    offX,
                    offY;
            @Override
            public boolean onTouch(View v, MotionEvent event){
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        strX = (int) event.getX();
                        strY = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        offX = (int)event.getRawX() - strX;
                        offY = (int)event.getRawY() - strY;
                        stopPopup.update(offX, offY, -1, -1, true);
                        break;
                }
                return true;
            }
        });
        /*Set listener for closing PopUp*/
        clsPopUp.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v){
                stopPopup.dismiss();
            }
        });
        //Stop Location Markers: Jackson Keenan [START]
        //Get Stop Long + Lat
        Float stopLat = MainActivity.routeList.get(routePos).getStopList().get(position).getLatitude();
        Float stopLong = MainActivity.routeList.get(routePos).getStopList().get(position).getLongitude();
        LatLng stopLatLong = new LatLng(stopLat, stopLong);

        //Clear Old Markers
        MainActivity.mainMap.clear();

        //Add Stop Marker
        stopMarker = MainActivity.mainMap.addMarker(new MarkerOptions()
                .position(stopLatLong)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                .title(MainActivity.routeList.get(routePos).getStopList().get(position).getStopID()));
        MainActivity.mapUpdate = CameraUpdateFactory.newLatLngZoom(stopLatLong, 12); //Change Zoom to 14 / 15 for release
        MainActivity.mainMap.animateCamera(MainActivity.mapUpdate);

        //Stop Location Markers: Jackson Keenan [END]
            return false;
    }

    @Override
    public void resetPopupWithEtas(String eta1,String eta2){
        //stopPopup.setText(eta1,eta2);
    }

}
