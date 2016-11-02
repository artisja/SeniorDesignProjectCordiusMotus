package mult_603.seniordesignprojectcordiusmotus;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import android.support.v7.app.AppCompatActivity;

public class UserDefinitionActivity extends AppCompatActivity{
    public final String TAG = UserDefinitionActivity.class.getSimpleName();
    private Button medicButton, caliButton, bluetoothButton;
    private Intent medicMapIntent;
    private Intent bluetoothActivity;
    public double longitude, latitude;
    private ApplicationController myAppController;
    private PrimaryDrawerItem primaryDrawerItem;
    private PrimaryDrawerItem logOut;
    private PrimaryDrawerItem deleteAccount;
    private PrimaryDrawerItem forgotPassword;
    private PrimaryDrawerItem changePassword;
    private PrimaryDrawerItem changeEmail;
    private SecondaryDrawerItem secondaryDrawerItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_definition);
        findViews();
        setButtonDestination();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if(toolbar == null){
            Log.i(TAG, "Toolbar is null ");
        }
        else{
            Log.i(TAG, "Toolbar is not null ");
        }

        myAppController = (ApplicationController)getApplicationContext();

        if(myAppController == null){
            Log.i(TAG, "My Application Controller is null");
        }
        else{
            Log.i(TAG, "My Application Controller is not null");

            if(myAppController.currentUser != null){
                Log.i(TAG, "Current User is not null");
            }
            else{
                Log.i(TAG, "Current User is null");
            }
        }

        // Create an account header for the user
        AccountHeader accountForUser = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header_nav_background_adjusted)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withEmail(myAppController.currentUser.getEmail())
                                .withIcon(R.drawable.ic_account)
                                .withName(myAppController.currentUser.getDisplayName())
                )
                .withTextColorRes(R.color.colorPrimaryDark)
                .build();


        // Create the drawer items
        primaryDrawerItem.withDescriptionTextColorRes(R.color.colorAccent)
                    .withDescription(myAppController.currentUser.getDisplayName())
                    .withIcon(R.drawable.ic_account)
                    .withIdentifier(1);

        // Change email

        // Change password

        // Log out drawer item
        logOut.withDescriptionTextColorRes(R.color.wordColorRed)
                .withDescription("Log Out")
                .withSelectedColorRes(R.color.lightGrayWithRed)
                .withSelectedTextColor(Color.WHITE)
                .withIdentifier(2);

        // Delete account drawer item
        deleteAccount.withDescriptionTextColorRes(R.color.wordColorRed)
                .withDescription("Delete Account")
                .withSelectedColorRes(R.color.lightGrayWithRed)
                .withSelectedTextColor(Color.WHITE)
                .withIdentifier(3);

        // Secondary drawer items can be sub menus
        secondaryDrawerItem.withDescription(myAppController.currentUser.getEmail())
                    .withIdentifier(0);

        // Build a navigation drawer for the users login activity
        final Drawer drawer = new DrawerBuilder().withActivity(this)
                    .withAccountHeader(accountForUser)
                    .withDisplayBelowStatusBar(true)
                    .withActionBarDrawerToggle(true)
                    .withActionBarDrawerToggleAnimated(true)
                    .addDrawerItems(primaryDrawerItem,
                            secondaryDrawerItem,
                            new DividerDrawerItem(),
                            logOut,
                            new DividerDrawerItem(),
                            deleteAccount
                    )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            Log.i(TAG, "Drawer item clicked at position " + position);
                            Log.i(TAG, "Drawer Item " + drawerItem);
                            Log.i(TAG, "View " + view);
                            return false;
                        }
                    })
                    .build();

        // Set the toolbar as the action bar before calling anything below
        if(getActionBar() != null){
            Log.i(TAG, "Get Action Bar is not null");
        }
        else{
            Log.i(TAG, "Get Action Bar is null");
        }


        try {
//            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
        }
        catch(NullPointerException n){
            Log.i(TAG, "Null Pointer Exception " + n.getCause());
        }

        toolbar.setLogo(R.drawable.ic_account);
        View v = toolbar.getChildAt(0);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Clicked the logo");
                drawer.openDrawer();
            }
        });


        // Arrow instead of hamburger
//        drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onPostCreate(Bundle saveInstanceState){
        super.onPostCreate(saveInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

    private void setButtonDestination() {
        caliButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserDefinitionActivity.this, "Emergency Contact/Edit screen", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        medicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserDefinitionActivity.this, "Google Maps/medical data view screen", Toast.LENGTH_SHORT).show();
                medicMapIntent = new Intent(getApplicationContext(), LoginDeviceLocator.class);
                medicMapIntent.putExtra("Longitude",longitude);
                medicMapIntent.putExtra("Latitude", latitude);
                startActivity(medicMapIntent);
            }
        });

        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothActivity = new Intent(getApplicationContext(), BluetoothActivity.class);
                startActivity(bluetoothActivity);
            }
        });
    }

    private void findViews() {
        caliButton      = (Button) findViewById(R.id.cali_Button);
        medicButton     = (Button) findViewById(R.id.medic_Button);
        bluetoothButton = (Button) findViewById(R.id.bluetooth_Button);
        primaryDrawerItem = new PrimaryDrawerItem();
        secondaryDrawerItem = new SecondaryDrawerItem();
        logOut = new PrimaryDrawerItem();
        deleteAccount = new PrimaryDrawerItem();
    }

    public void getLocationManager() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }
}
