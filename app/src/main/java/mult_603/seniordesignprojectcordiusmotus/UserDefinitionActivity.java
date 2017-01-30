package mult_603.seniordesignprojectcordiusmotus;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class UserDefinitionActivity extends AppCompatActivity implements Drawer.OnDrawerItemClickListener,
        View.OnClickListener {

    public final String TAG = UserDefinitionActivity.class.getSimpleName();
    private Button medicButton, caliButton, bluetoothButton;
    private ImageButton accountButton;
    private ImageButton homeButton;
    private Intent medicMapIntent;
    private Intent bluetoothActivity;
    public double longitude, latitude;
    private ApplicationController myAppController;
    private PrimaryDrawerItem signOut;
    private PrimaryDrawerItem deleteAccount;
    private PrimaryDrawerItem forgotPassword;
    private PrimaryDrawerItem changePassword;
    private PrimaryDrawerItem changeEmail;
    private PrimaryDrawerItem login;
    private ProfileDrawerItem userProfile;
    private FirebaseUser currentUser;
    public static Drawer navigationDrawer;
    public static AccountHeader currentUserAccountHeader;
    private final String LOGIN_TAG           = "Login";
    private final String CHANGE_EMAIL_TAG    = "ChangeEmail";
    private final String CHANGE_PASSWORD_TAG = "ChangePassword";
    private final String SIGN_OUT_TAG        = "SignOut";
    private final String DELETE_ACCOUNT_TAG  = "DeleteAccount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_definition);
        findViews();
        setUpNavDrawer();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);

        if (toolbar == null) {
            Log.i(TAG, "Toolbar is null ");
        } else {
            Log.i(TAG, "Toolbar is not null ");
        }

        if (myAppController == null) {
            Log.i(TAG, "My Application Controller is null");

        } else {
            Log.i(TAG, "My Application Controller is not null");

            //myAppController.setContext(getApplicationContext());
            //navigationDrawer = myAppController.getUserDrawer(this);
        }



        Intent locationIntent = new Intent(getApplicationContext(), LocationService.class);
        this.startService(locationIntent);

    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i(TAG, "On Resume was called");
        setUpNavDrawer();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        Log.i(TAG, "On Options Item Selected");
        switch (menuItem.getItemId()) {
            case R.id.profile_button:
                Log.i(TAG, "Profile Button Clicked ");
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

//    Use this if we want to use toolbar as an action bar
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu){
//        Log.i(TAG, "On Create Options Menu Called");
//        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
//        return true;
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
//                medicMapIntent.putExtra("Longitude", longitude);
//                medicMapIntent.putExtra("Latitude", latitude);
                startActivity(medicMapIntent);
                break;

            case R.id.bluetooth_Button:
                bluetoothActivity = new Intent(getApplicationContext(), BluetoothActivity.class);
                startActivity(bluetoothActivity);
                break;

            case R.id.user_login_toolbar:
                Log.i(TAG, "Clicked on the account button in the toolbar");
                Animation imageClicked = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_clicked);

                Animation.AnimationListener animationListener = new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        Log.i(TAG, "On Animation Start");
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Log.i(TAG, "On Animation end");
                        navigationDrawer.openDrawer();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        Log.i(TAG, "On Animation repeat");
                    }
                };

                imageClicked.setAnimationListener(animationListener);
                v.startAnimation(imageClicked);
                break;
            /* Do We Need a home button?
            case R.id.home_toolbar_icon:
                Log.i(TAG, "Clicked the home button in the toolbar");
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_clicked));
                break;
                */
        }
    }

    private void setUpNavDrawer() {
        changeEmail         = new PrimaryDrawerItem();
        changePassword      = new PrimaryDrawerItem();
        signOut             = new PrimaryDrawerItem();
        deleteAccount       = new PrimaryDrawerItem();
        userProfile         = new ProfileDrawerItem();
        currentUser         = FirebaseAuth.getInstance().getCurrentUser();


        if(currentUser != null) {
            Log.i(TAG, "Firebase Auth State Listener signed in " + currentUser);
            Log.i(TAG, "Display Name " + currentUser.getDisplayName());
            Log.i(TAG, "Email " + currentUser.getEmail());
            Log.i(TAG, "UUID " + currentUser.getUid());

            // Create User Profile
            userProfile = new ProfileDrawerItem()
                    .withEmail(currentUser.getEmail())
                    .withName(currentUser.getDisplayName())
                    .withIcon(currentUser.getPhotoUrl());

            currentUserAccountHeader = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withHeaderBackground(R.drawable.header_nav_background_adjusted)
                    .addProfiles(userProfile)
                    .withTextColorRes(R.color.colorPrimaryDark)
                    .build();

            // Change email
            changeEmail.withDescription(R.string.change_email)
                    .withDescriptionTextColorRes(R.color.material_drawer_dark_selected)
                    .withSelectedColorRes(R.color.lightGrayWithRed)
                    .withTag("ChangeEmail");

            // Change password
            changePassword.withDescription(R.string.change_password)
                    .withDescriptionTextColorRes(R.color.material_drawer_dark_selected)
                    .withSelectedColorRes(R.color.lightGrayWithRed)
                    .withTag("ChangePassword");

            // Log out drawer item
            signOut.withDescriptionTextColorRes(R.color.wordColorRed)
                    .withDescription("Sign Out")
                    .withSelectedColorRes(R.color.lightGrayWithRed)
                    .withSelectedTextColor(Color.WHITE)
                    .withTag("SignOut");

            // Delete account drawer item
            deleteAccount.withDescriptionTextColorRes(R.color.wordColorRed)
                    .withDescription("Delete Account")
                    .withSelectedColorRes(R.color.lightGrayWithRed)
                    .withSelectedTextColor(Color.WHITE)
                    .withTag("DeleteAccount");

            navigationDrawer = new DrawerBuilder().withActivity(this)
                    .withAccountHeader(currentUserAccountHeader)
                    .withDisplayBelowStatusBar(true)
                    .withActionBarDrawerToggle(true)
                    .withActionBarDrawerToggleAnimated(false)
                    .addDrawerItems(
                            changeEmail,
                            changePassword,
                            new DividerDrawerItem(),
                            signOut,
                            deleteAccount
                    )
                    .withOnDrawerItemClickListener(this)
                    .build();

        }

        else{
            Log.i(TAG, "Firebase Auth State Listener signed out ");

            userProfile = new ProfileDrawerItem()
                    .withEmail("Email")
                    .withName("Name")
                    .withIcon(R.drawable.ic_account);

            currentUserAccountHeader = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withHeaderBackground(R.drawable.header_nav_background_adjusted)
                    .addProfiles(userProfile)
                    .build();

            // Login Drawer Item
            login = new PrimaryDrawerItem()
                    .withDescription("Login")
                    .withDescriptionTextColorRes(R.color.colorAccent)
                    .withTag(LOGIN_TAG);

            navigationDrawer = new DrawerBuilder().withActivity(this)
                    .withAccountHeader(currentUserAccountHeader)
                    .withDisplayBelowStatusBar(true)
                    .addDrawerItems(login)
                    .withOnDrawerItemClickListener(this)
                    .build();
        }
    }

    private void findViews() {
        // Get the application controller and set up the buttons
        myAppController = (ApplicationController) getApplicationContext();
        caliButton      = (Button) findViewById(R.id.cali_Button);
        medicButton     = (Button) findViewById(R.id.medic_Button);
        bluetoothButton = (Button) findViewById(R.id.bluetooth_Button);
        accountButton   = (ImageButton) findViewById(R.id.user_login_toolbar);

        caliButton.setOnClickListener(this);
        medicButton.setOnClickListener(this);
        bluetoothButton.setOnClickListener(this);
        accountButton.setOnClickListener(this);

        latitude =  myAppController.latitude;
        longitude = myAppController.longitude;

        Log.i(TAG, "Longitude " + longitude);
        Log.i(TAG, "Latitude " + latitude);

    }

    // Navigation on drawer item click listener
    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        Log.i(TAG, "Drawer item clicked at position " + position);
        Log.i(TAG, "Drawer Item " + drawerItem);
        Log.i(TAG, "View " + view);
        Log.i(TAG, "Drawer Item Tag " + drawerItem.getTag());
        Log.i(TAG, "Drawer Item Identifier " + drawerItem.getIdentifier());
        final String str = drawerItem.getTag().toString();

        switch (str) {
            case LOGIN_TAG:
                Log.i(TAG, "Login Tag Pressed");
                navigationDrawer.closeDrawer();
                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginIntent);
                break;

            case CHANGE_EMAIL_TAG:
                Log.i(TAG, "Change Email Tag Pressed");
                navigationDrawer.closeDrawer();
                Intent changeEmail = new Intent(getApplicationContext(), ChangeEmailActivity.class);
                startActivity(changeEmail);
                break;

            case CHANGE_PASSWORD_TAG:
                Log.i(TAG, "Change Password Tag Pressed");
                navigationDrawer.closeDrawer();
                //Intent changePassword = new Intent(getApplicationContext(), )

                break;

            case SIGN_OUT_TAG:
                Log.i(TAG, "Sign Out Tag Pressed");
                navigationDrawer.closeDrawer();
                if (currentUser != null) {
                    FirebaseAuth.getInstance().signOut();
                    setUpNavDrawer();
                }
                break;

            case DELETE_ACCOUNT_TAG:
                Log.i(TAG, "Delete Account Tag Pressed");
                navigationDrawer.closeDrawer();
                de.hdodenhof.circleimageview.CircleImageView img = new de.hdodenhof.circleimageview.CircleImageView(this);
                img.setImageURI(currentUser.getPhotoUrl());

                new AlertDialog.Builder(this)
                        .setMessage("Are you sure you want to delete your account with the following Info : " + "\n\nEmail : " + currentUser.getEmail() +
                                "\nUser Name: " + currentUser.getDisplayName())
                        .setCustomTitle(img)
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (currentUser != null) {
                                    Log.i(TAG, "Deleting the user with display name " + currentUser.getDisplayName());
                                    Log.i(TAG, "Deleting the user with email address " + currentUser.getEmail());

                                    currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.i(TAG, "User has been Deleted");
                                            } else {
                                                Log.i(TAG, "Something went wrong deleting the user");
                                            }
                                        }
                                    });
                                }
                                // Set up the navigation drawer since it changed again
                                setUpNavDrawer();
                            }
                        })
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
                break;
        }

        return true;
    }
}
