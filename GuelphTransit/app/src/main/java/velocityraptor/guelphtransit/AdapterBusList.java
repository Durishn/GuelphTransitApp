package velocityraptor.guelphtransit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * This class defines the Adapter which injects strings for bus ID and Name into a layout
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
public class AdapterBusList extends ArrayAdapter<Route> {

    private String layout;

    public AdapterBusList(Context context, String layout, ArrayList<Route> routeList) {
        super(context, 0, routeList);
        this.layout = layout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Route route = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {

            //Call respective layout based on string input
            switch (layout){
                case "botbar":
                    convertView = LayoutInflater.from(getContext()).inflate(
                            R.layout.icon_botbar_bus, parent, false);
                    break;
                case "list":
                    convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.icon_list_bus, parent, false);
                    break;
            }
        }

        // Lookup view for data population
        assert convertView != null;
        TextView tvName = (TextView) convertView.findViewById(R.id.bus_id);
        TextView tvHome = (TextView) convertView.findViewById(R.id.bus_route);

        // Populate the data into the template view using the data object
        tvName.setText(route.getRouteName());
        tvHome.setText(route.getRouteNameString());

        // Return the completed view to render on screen
        return convertView;
        }
}
