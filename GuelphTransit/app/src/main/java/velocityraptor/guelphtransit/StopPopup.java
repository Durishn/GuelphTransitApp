package velocityraptor.guelphtransit;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.view.View.OnTouchListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.jar.Attributes;

import velocityraptor.guelphtransit.interfaces.ScrapeRespCallback;


/**
 * Created by William Maher on 27/03/15.
 * Most code inside is originally from Jackson
 * But variable declarations and last method (setText) made by William Aidan Maher
 * Which was refactored here since the popup is called by 2 different listeners
 * @author William Maher
 */
public class StopPopup extends PopupWindow {

    private TextView popUpFirstTime;
    private TextView popUpText;
    private TextView popUpFirstOtherTimes;
    private String schedOne = "";
    private String schedTwo = "";
    private String schedThree = "";
    //PopUpWindow: Jackson Keenan [START]


    public StopPopup(View popupView, int height,int width,int routePosIn,int positionIn) {
        //Aidan Maher [Start]
        //Pass to superclass, necessary vars to instantiate correctly
        super(popupView,width,height);
        LoadNextBus l = new LoadNextBus(routePosIn,positionIn,this);
        l.execute();
        //Aidan Maher[End]
        //PopUpWindow: Jackson Keenan [START]


        String listIterator;
        ArrayList<String> timeList;

        int stopInt = 0;
        ImageButton clsPopUp = (ImageButton)popupView.findViewById(R.id.dismiss);
        /*Populate Popup*/
        popUpText = (TextView) popupView.findViewById(R.id.popUpTextViewTitle);
        popUpFirstTime = (TextView) popupView.findViewById(
                R.id.popUpTextViewFirstTime);
        popUpFirstOtherTimes = (TextView) popupView.findViewById(
                R.id.popUpTextViewTimes);
        String popUpRouteTitle = MainActivity.routeList.get(routePosIn).getRouteName();
        String popUpStopTitle = MainActivity.routeList.get(routePosIn).getStopList()
                .get(positionIn).getStopName();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat tempDay = new SimpleDateFormat("EE");
        String dayOfWeek = tempDay.format(cal.getTime());
        SimpleDateFormat dateTime = new SimpleDateFormat("HHmm");
        String currentTime = dateTime.format(cal.getTime());

                        /*Calculating schedule Times*/
        switch (dayOfWeek) {
            case "Sat":
                timeList = MainActivity.routeList.get(routePosIn).getStopList().get(positionIn)
                        .getSatTimes();
                break;
            case "Sun":
                timeList = MainActivity.routeList.get(routePosIn).getStopList().get(positionIn)
                        .getSunTimes();
                break;
            default:
                timeList = MainActivity.routeList.get(routePosIn).getStopList().get(positionIn)
                        .getWeekTimes();
                break;
        }

        for (int j = 0;j<timeList.size();j++){
            listIterator = timeList.get(j);
            listIterator = listIterator.replace(":", "");
            if(Integer.parseInt(currentTime) < Integer.parseInt(listIterator)){
                stopInt = j;
                break;
            }
        }
                        /*Setting Schedule times*/
        schedOne = timeList.get(stopInt);
        if(stopInt <= timeList.size()-2){
            schedTwo = timeList.get(stopInt+1);
        } else{schedTwo = "- -";}
        if(stopInt <= timeList.size()-3){
            schedThree = timeList.get(stopInt+2);
        } else{schedThree = "- -";}

                        /*Setting Text In popUp*/

        popUpText.setText(" " + popUpRouteTitle + ": " + popUpStopTitle);
        popUpFirstTime.setText(" " + schedOne + "\t\t\t\t--Loading Eta");
        popUpFirstOtherTimes.setText("  " + schedTwo+"\t\t\t\t--Loading Eta\n  "+schedThree);


                        /*Display PopUp*/
        //showAsDropDown(popupView, 10, 250);

                    /*Closing PopUp*/
        clsPopUp.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v){
                dismiss();
            }
        });

                /*PopUp: Movement*/
        popupView.setOnTouchListener(new OnTouchListener() {
            int strX,strY,offX,offY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        strX = (int) event.getX();
                        strY = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        offX = (int) event.getRawX() - strX;
                        offY = (int) event.getRawY() - strY;
                        update(offX, offY, -1, -1, true);
                        break;
                }
                return true;
            }
        });
        //PopUpWindow: Jackson Keenan [END]

    }

    /*Setting Text In popUp When scraping is finished Written by William Aidan Maher*/
    public void setText(String eta1, String eta2){
        if(eta1=="NextBus Time unavailable"&&eta2=="NextBus Time unavailable"){
            popUpFirstTime.setText(" " + schedOne + "\t"+eta1);
            popUpFirstOtherTimes.setText("  " + schedTwo +"\t\t"+ eta2 + "\n  " + schedThree);
        }else {
            popUpFirstTime.setText(" " + schedOne + "\t\t\t\t1st GPS ETA @ this stop: " + eta1);
            popUpFirstOtherTimes.setText("  " + schedTwo + "\t\t\t\t2nd GPS ETA @ this stop:  " + eta2 + "\n  " + schedThree);
        }

    }


}
