package velocityraptor.guelphtransit;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

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
public class FragmentBotbarBus extends Fragment {

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
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Create fragment and give it an argument specifying the article it should show
                FragmentBotbarStop newFragment = new FragmentBotbarStop();
                Bundle args = new Bundle();
                args.putInt("EXTRA_ROUTE_POS", position);
                newFragment.setArguments(args);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.bot_bar_frag_view, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        };

        //Set listeners and adapters for the gridView
        gridView.setOnItemClickListener(listener);
        gridView.setAdapter(busListRouteAdapter);
        return view;
    }
}
