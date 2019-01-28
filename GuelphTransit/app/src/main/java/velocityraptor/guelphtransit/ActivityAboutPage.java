package velocityraptor.guelphtransit;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by NicsMac on 15-04-14.
 */
public class ActivityAboutPage extends ActionBarActivity {

    private int counter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_about);

        counter = 0;

        ImageView image = (ImageView) findViewById(R.id.vrlogo);


        image.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                counter++;

                switch(counter)
                {
                    case 2:
                        Toast.makeText(getApplicationContext(), "THREE more clicks", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(getApplicationContext(), "TWO more clicks", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(getApplicationContext(), "ONE more click", Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        Intent i = new Intent();
                        i.setComponent(new ComponentName("velocityraptor.guelphtransit", "velocityraptor.guelphtransit.LoginActivity"));
                        i.setAction("android.intent.action.MAIN");
                        i.addCategory("android.intent.category.LAUNCHER");
                        i.addCategory("android.intent.category.DEFAULT");
                        v.getContext().startActivity(i);
                        break;

                }

            }

        });
}

    @Override
    protected void onResume() {
        super.onResume();
        counter = 0;
    }
}

