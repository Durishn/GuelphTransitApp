package velocityraptor.guelphtransit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.PopupWindow;
import android.widget.Button;
import android.widget.ImageButton;
import android.view.ViewGroup.LayoutParams;
import android.view.MotionEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * This class defines the Fragment for the main bottom bar, filled with Bus Stops
<<<<<<< HEAD
 *      @author: Nic Durish & Jackson Keenan.
=======
 *      @author: Nic Durish & Jackson Keenan (@mail.uoguelph.ca).
>>>>>>> 223ead000c1650345274366126af7332fbbda478
 *
 * I have exclusive control over this submission via my password.
 * By including this statement in this header comment, I certify that:
 * 1) I have read and understood the University policy on academic integrity;
 * 2) I have completed the Computing with Integrity Tutorial on Moodle; and
 * I assert that this work is my own. I have appropriately acknowledged any and all material
 * (data, images, ideas or words) that I have used, whether directly quoted or paraphrased.
 * Furthermore, I certify that this assignment was prepared by me specifically for this course.
 */
public class FragmentBotbarStop extends Fragment{

    public int routePos;

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

        //Create Click Listener
        OnItemClickListener listener = new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //PopUpWindow: Jackson Keenan [START]
                String etaOne = "";
                String etaTwo = "";
                String etaThree = "";
                String listIterator;
                ArrayList<String> timeList;
                int stopInt = 0;

                /*Setup*/
                LayoutInflater popUpLayout = (LayoutInflater)getActivity().getLayoutInflater();
                View popUpView = popUpLayout.inflate(R.layout.popup, null);
                final PopupWindow stopPopUp = new PopupWindow(popUpView, LayoutParams.WRAP_CONTENT
                        ,LayoutParams.WRAP_CONTENT);
                ImageButton clsPopUp = (ImageButton)popUpView.findViewById(R.id.dismiss);

                /*Populate Popup*/
                TextView popUpText = (TextView) popUpView.findViewById(R.id.popUpTextViewTitle);
                TextView popUpFirstTime = (TextView) popUpView.findViewById(
                        R.id.popUpTextViewFirstTime);
                TextView popUpFirstOtherTimes = (TextView) popUpView.findViewById(
                        R.id.popUpTextViewTimes);
                String popUpRouteTitle = MainActivity.routeList.get(routePos).getRouteName();
                String popUpStopTitle = MainActivity.routeList.get(routePos).getStopList()
                        .get(position).getStopName();
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat tempDay = new SimpleDateFormat("EE");
                String dayOfWeek = tempDay.format(cal.getTime());
                SimpleDateFormat dateTime = new SimpleDateFormat("HHmm");
                String currentTime = dateTime.format(cal.getTime());

                /*Calculating ETAs*/
                switch (dayOfWeek) {
                    case "Sat":
                        timeList = MainActivity.routeList.get(routePos).getStopList().get(position)
                                .getSatTimes();
                        break;
                    case "Sun":
                        timeList = MainActivity.routeList.get(routePos).getStopList().get(position)
                                .getSunTimes();
                        break;
                    default:
                        timeList = MainActivity.routeList.get(routePos).getStopList().get(position)
                                .getWeekTimes();
                        break;
                }
                for (int i = 0;i<timeList.size();i++){
                    listIterator = timeList.get(i);
                    listIterator = listIterator.replace(":", "");
                    if(Integer.parseInt(currentTime) < Integer.parseInt(listIterator)){
                        stopInt = i;
                        break;
                    }
                }

                /*Setting ETAs*/
                etaOne = timeList.get(stopInt);
                if(stopInt <= timeList.size()-2){
                    etaTwo = timeList.get(stopInt+1);
                } else{etaTwo = "- -";}
                if(stopInt <= timeList.size()-3){
                    etaThree = timeList.get(stopInt+2);
                } else{etaThree = "- -";}

                /*Setting Text In popUp*/
                popUpText.setText(popUpRouteTitle + ":\n" + popUpStopTitle);
                popUpFirstTime.setText(etaOne);
                popUpFirstOtherTimes.setText(etaTwo+"\n"+etaThree);

                /*Display PopUp*/
                stopPopUp.showAsDropDown(popUpView, 50, 550);

                /*Closing PopUp*/
                clsPopUp.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        stopPopUp.dismiss();
                    }
                });

                /*PopUp: Movement*/
                popUpView.setOnTouchListener(new OnTouchListener(){
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
                                stopPopUp.update(offX, offY, -1, -1, true);
                                break;
                        }
                        return true;
                    }
                });

                //PopUpWindow: Jackson Keenan [END]
            }
        };

        //Set listeners and adapters for the gridView
        gridView.setOnItemClickListener(listener);
        gridView.setAdapter(busListRouteAdapter);
        return view;
    }
}
