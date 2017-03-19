package mult_603.seniordesignprojectcordiusmotus;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;

public class UserMainActivity extends AppCompatActivity implements
        View.OnClickListener {
    public final String TAG = UserMainActivity.class.getSimpleName();
    private Button medicButton, caliButton;
    private Intent medicMapIntent;
    private AccountHeader headerResult;
    private Drawer drawerResult;
    private NavigationDrawerHandler navHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_definition);
        findViews();

        // Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Set up the navigation handler
        navHandler = new NavigationDrawerHandler(this, savedInstanceState, getApplicationContext(), toolbar);
        headerResult = navHandler.setAccountHeader(this, savedInstanceState, getApplicationContext());
        drawerResult = navHandler.setUserDrawer(this, headerResult, toolbar);
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i(TAG, "On Resume was called");

        // Get the updated header and drawer
        headerResult = navHandler.getHeader();
        drawerResult = navHandler.getDrawer();
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem menuItem) {
//        Log.i(TAG, "On Options Item Selected");
//        switch (menuItem.getItemId()) {
//            case R.id.profile_button:
//                Log.i(TAG, "Profile Button Clicked ");
//                return true;
//        }
//        return super.onOptionsItemSelected(menuItem);
//    }

    @Override
    public void onPostCreate(Bundle saveInstanceState) {
        super.onPostCreate(saveInstanceState);
        Log.i(TAG, "On Post Create");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "On Configuration Changed");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cali_Button:
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.medic_Button:
                medicMapIntent = new Intent(getApplicationContext(), UserMapsActivity.class);
                startActivity(medicMapIntent);
                break;

        }
    }

    private void findViews() {
        // Get the application controller and set up the buttons
        caliButton      = (Button) findViewById(R.id.cali_Button);
        medicButton     = (Button) findViewById(R.id.medic_Button);

        caliButton.setOnClickListener(this);
        medicButton.setOnClickListener(this);
    }
}
