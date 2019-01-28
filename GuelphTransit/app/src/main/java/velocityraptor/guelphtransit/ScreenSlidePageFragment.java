package velocityraptor.guelphtransit;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by NicsMac on 15-04-14.
 */
public final class ScreenSlidePageFragment extends Fragment {

    int imageResourceId;

    /*public ScreenSlidePageFragment(int i){
        imageResourceId = i;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_help_slide_page, container, false);

        ImageView image = (ImageView) v.findViewById(R.id.help_pager_image);
        image.setImageResource(getArguments().getInt("msg"));

        return v;
        }


    public static ScreenSlidePageFragment newInstance(int i) {

        ScreenSlidePageFragment f = new ScreenSlidePageFragment();
        Bundle b = new Bundle();
        b.putInt("msg", i);

        f.setArguments(b);

        return f;
    }

}
