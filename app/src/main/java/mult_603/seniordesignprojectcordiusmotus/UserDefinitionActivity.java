package mult_603.seniordesignprojectcordiusmotus;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
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

public class UserDefinitionActivity extends AppCompatActivity implements Drawer.OnDrawerItemClickListener{
    public final String TAG = UserDefinitionActivity.class.getSimpleName();
    private Button medicButton, caliButton, bluetoothButton;
    private Intent medicMapIntent;
    private Intent bluetoothActivity;
    public double longitude, latitude;
    private ApplicationController myAppController;
    private PrimaryDrawerItem primaryDrawerItem;
    private PrimaryDrawerItem signOut;
    private PrimaryDrawerItem deleteAccount;
    private PrimaryDrawerItem forgotPassword;
    private PrimaryDrawerItem changePassword;
    private PrimaryDrawerItem changeEmail;
    private SecondaryDrawerItem secondaryDrawerItem;
    public static Drawer navigationDrawer;
    public static AccountHeader currentUserNavHeader;

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

        // Build a navigation drawer for the users login activity
        navigationDrawer = new DrawerBuilder().withActivity(this)
                    .withAccountHeader(currentUserNavHeader)
                    .withDisplayBelowStatusBar(true)
                    .withActionBarDrawerToggle(true)
                    .withActionBarDrawerToggleAnimated(true)
                    .addDrawerItems(//primaryDrawerItem,
                            //secondaryDrawerItem,
                            //new DividerDrawerItem(),
                            changeEmail,
                            new DividerDrawerItem(),
                            changePassword,
                            new DividerDrawerItem(),
                            signOut,
                            new DividerDrawerItem(),
                            deleteAccount
                    )
                    .withOnDrawerItemClickListener(this)
                    .build();

        // Set the logo on the toolbar to be able to open the navigation drawer
//        toolbar.setLogo(R.drawable.ic_account);
//        View v = toolbar.getChildAt(0);
//        v.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i(TAG, "Clicked the logo");
//                navigationDrawer.openDrawer();
//            }
//        });

        ImageButton accountButton = (ImageButton) findViewById(R.id.user_login_toolbar);
        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Clicked on the account button in the toolbar");
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_clicked));
                navigationDrawer.openDrawer();
            }
        });

        ImageButton homeButton = (ImageButton) findViewById(R.id.home_toolbar_icon);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Clicked the home button in the toolbar");
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_clicked));
            }
        });
    }

    @Override
    public void onPostCreate(Bundle saveInstanceState){
        super.onPostCreate(saveInstanceState);
        Log.i(TAG, "On Post Create");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "On Configuration Changed");
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
        // Get the application controller to have access to the global firebase instance
        myAppController = (ApplicationController)getApplicationContext();
        caliButton      = (Button) findViewById(R.id.cali_Button);
        medicButton     = (Button) findViewById(R.id.medic_Button);
        bluetoothButton = (Button) findViewById(R.id.bluetooth_Button);
        primaryDrawerItem   = new PrimaryDrawerItem();
        secondaryDrawerItem = new SecondaryDrawerItem();
        changeEmail         = new PrimaryDrawerItem();
        changePassword      = new PrimaryDrawerItem();
        signOut = new PrimaryDrawerItem();
        deleteAccount       = new PrimaryDrawerItem();
        latitude     = myAppController.latitude;
        longitude    = myAppController.longitude;

        Log.i(TAG, "Longitude " + longitude);
        Log.i(TAG, "Latitude " + latitude);

        if(myAppController.currentUser == null){
            currentUserNavHeader = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withHeaderBackground(R.drawable.header_nav_background_adjusted)
                    .build();

        }
        else {
            // Create an account header for the user
            currentUserNavHeader = new AccountHeaderBuilder()
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

            // Secondary drawer items can be sub menus
            secondaryDrawerItem.withDescription(myAppController.currentUser.getEmail())
                    .withIdentifier(0);

            // Create the drawer items
            primaryDrawerItem.withDescriptionTextColorRes(R.color.colorAccent)
                    .withDescription(myAppController.currentUser.getDisplayName())
                    .withIcon(R.drawable.ic_account)
                    .withIdentifier(1);
        }

        // Change email
        changeEmail.withDescription(R.string.change_email)
                .withDescriptionTextColorRes(R.color.material_drawer_dark_selected)
                .withSelectedColorRes(R.color.lightGrayWithRed)
                .withIdentifier(2);


        // Change password
        changePassword.withDescription(R.string.change_password)
                .withDescriptionTextColorRes(R.color.material_drawer_dark_selected)
                .withSelectedColorRes(R.color.lightGrayWithRed)
                .withIdentifier(3);

        // Log out drawer item
        signOut.withDescriptionTextColorRes(R.color.wordColorRed)
                .withDescription("Sign Out")
                .withSelectedColorRes(R.color.lightGrayWithRed)
                .withSelectedTextColor(Color.WHITE)
                .withIdentifier(4);

        // Delete account drawer item
        deleteAccount.withDescriptionTextColorRes(R.color.wordColorRed)
                .withDescription("Delete Account")
                .withSelectedColorRes(R.color.lightGrayWithRed)
                .withSelectedTextColor(Color.WHITE)
                .withIdentifier(5);
    }

    // Navigation on drawer item click listener
    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        Log.i(TAG, "Drawer item clicked at position " + position);
        Log.i(TAG, "Drawer Item " + drawerItem);
        Log.i(TAG, "View " + view);

        switch(position){
            case 0:
                Log.i(TAG, "Case 0 position " + position + " Drawer Item " + drawerItem);
                break;
            case 1:
                Log.i(TAG, "Case 1 position " + position + " Drawer Item " + drawerItem);
                break;
            case 2:
                Log.i(TAG, "Case 2 position " + position + " Drawer Item " + drawerItem);
                break;
            case 3:
                Log.i(TAG, "Case 3 position " + position + " Drawer Item " + drawerItem);
                break;
            case 4:
                Log.i(TAG, "Case 4 position " + position + " Drawer Item " + drawerItem);
                break;

            // Sign out case
            case 5:
                Log.i(TAG, "Case 5 position " + position + " Drawer Item " + drawerItem);
                // If the user is logged in then sign them out
                if(myAppController.currentUser != null){
                    myAppController.firebaseAuth.signOut();
                }
                break;

            case 6:
                Log.i(TAG, "Case 6 position " + position + " Drawer Item " + drawerItem);
                break;
            case 7:
                Log.i(TAG, "Case 7 position " + position + " Drawer Item " + drawerItem);
                break;
            case 8:
                Log.i(TAG, "Case 8 position " + position + " Drawer Item " + drawerItem);
                break;
        }
        return false;
    }
}
