package velocityraptor.guelphtransit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * This class defines the Adapter which injects strings for stop ID and Name into a layout
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
public class AdapterStopList extends ArrayAdapter<Stop> {
    private String layout;

    public AdapterStopList(Context context, String layout, ArrayList<Stop> stopList) {
        super(context, 0, stopList);
        this.layout = layout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Stop stop = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {

            //Call respective layout based on string input
            switch (layout){
                case "list":
                    convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.icon_list_stop, parent, false);
                    break;
                case "botbar":
                    convertView = LayoutInflater.from(getContext()).inflate(
                            R.layout.icon_botbar_stop, parent, false);
                    break;
            }
        }

        // Lookup view for data population
        assert convertView != null;
        TextView tvName = (TextView) convertView.findViewById(R.id.bus_stop_id);
        TextView tvHome = (TextView) convertView.findViewById(R.id.bus_stop_name);

        // Populate the data into the template view using the data object
        tvName.setText(stop.getStopID());
        tvHome.setText(stop.getStopName());

        // Return the completed view to render on screen
        return convertView;
    }
}
