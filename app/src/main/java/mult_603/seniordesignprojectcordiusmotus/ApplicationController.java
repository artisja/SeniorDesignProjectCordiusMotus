package mult_603.seniordesignprojectcordiusmotus;

import android.*;
import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Wes on 10/31/16.
 * This class can be used to give our application global access to the users location
 * Whether or not the user is logged in, Bluetooth etc
 */
public class ApplicationController extends android.app.Application implements AccountHeader.OnAccountHeaderListener,
        FirebaseAuth.AuthStateListener {

    public final String TAG = ApplicationController.class.getSimpleName();
    public FirebaseAuth.AuthStateListener authStateListener;
    public FirebaseAuth firebaseAuth;
    public FirebaseUser currentUser;
    public ProfileDrawerItem profileDrawerItem;
    public Drawer userDrawer;
    private Context context;

    private PrimaryDrawerItem signOut;
    private PrimaryDrawerItem deleteAccount;
    private PrimaryDrawerItem forgotPassword;
    private PrimaryDrawerItem changePassword;
    private PrimaryDrawerItem changeEmail;
    private PrimaryDrawerItem login;

    private final String LOGIN_TAG = "Login";
    private final String CHANGE_EMAIL_TAG = "ChangeEmail";
    private final String CHANGE_PASSWORD_TAG = "ChangePassword";
    private final String SIGN_OUT_TAG = "SignOut";
    private final String DELETE_ACCOUNT_TAG = "DeleteAccount";

    public ProfileSettingDrawerItem profileSettingDrawerItem;
    public AccountHeaderBuilder profileAccountHeader;
    public double longitude, latitude;
    public Patient patient;
    public AccountHeader.OnAccountHeaderListener accountHeaderListener;

    private static ApplicationController singleton;

    public ApplicationController getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        FirebaseApp.initializeApp(this);

        Log.i(TAG, "On Create was called");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        patient = new Patient();

        firebaseAuth.addAuthStateListener(this);

        changeEmail = new PrimaryDrawerItem();
        changePassword = new PrimaryDrawerItem();
        signOut = new PrimaryDrawerItem();
        deleteAccount = new PrimaryDrawerItem();
        login = new PrimaryDrawerItem();

    }

//
//
//    public void getLocationManager() {
//        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        LocationListener locationListener = new LocationListener() {
//
//            @Override
//            public void onLocationChanged(Location location) {
//                longitude = location.getLongitude();
//                latitude = location.getLatitude();
//                Log.i(TAG, "Longitude " + longitude + " , " + latitude);
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//                Log.i(TAG, "On Status Changed");
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//                Log.i(TAG, "On Provider Enabled -> " + provider);
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//                Log.i(TAG, "On Provider Disabled " + provider);
//            }
//        };
//
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
////            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
////
////            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
////                Log.i(TAG, "DID show the request permission rationale");
////            }
////            else{
////                Log.i(TAG, "DID NOT show the request permission rationale");
////            }
//
//            return;
//        }
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
//    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "On Configuration Changed Called");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i(TAG, "On Low Memory called");
    }

    // Profile Changed Listener
    @Override
    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
        Log.i(TAG, "On Profile Changed was called");
        Log.i(TAG, "Profile Name " + profile.getName());
        Log.i(TAG, "Profile Email " + profile.getEmail());
        return true;
    }

    // Auth State Changed listener
    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        Log.i(TAG, "On Auth State Changed ");
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            Log.i(TAG, "Firebase Auth State Listener signed in " + currentUser);
            Log.i(TAG, "Display Name " + currentUser.getDisplayName());
            Log.i(TAG, "Email " + currentUser.getEmail());
            Log.i(TAG, "UUID " + currentUser.getUid());

            // Create User Profile
            profileDrawerItem = new ProfileDrawerItem()
                    .withEmail(currentUser.getEmail())
                    .withName(currentUser.getDisplayName())
                    .withIcon(currentUser.getPhotoUrl());

            // Change email
            changeEmail.withDescription(R.string.change_email)
                    .withDescriptionTextColorRes(R.color.material_drawer_dark_selected)
                    .withSelectedColorRes(R.color.lightGrayWithRed)
                    .withIdentifier(2)
                    .withTag("ChangeEmail");

            // Change password
            changePassword.withDescription(R.string.change_password)
                    .withDescriptionTextColorRes(R.color.material_drawer_dark_selected)
                    .withSelectedColorRes(R.color.lightGrayWithRed)
                    .withIdentifier(3)
                    .withTag("ChangePassword");

            // Log out drawer item
            signOut.withDescriptionTextColorRes(R.color.wordColorRed)
                    .withDescription("Sign Out")
                    .withSelectedColorRes(R.color.lightGrayWithRed)
                    .withSelectedTextColor(Color.WHITE)
                    .withIdentifier(4)
                    .withTag("SignOut");

            // Delete account drawer item
            deleteAccount.withDescriptionTextColorRes(R.color.wordColorRed)
                    .withDescription("Delete Account")
                    .withSelectedColorRes(R.color.lightGrayWithRed)
                    .withSelectedTextColor(Color.WHITE)
                    .withIdentifier(5)
                    .withTag("DeleteAccount");

        } else {
            Log.i(TAG, "Firebase Auth State Listener signed out ");

            profileDrawerItem = new ProfileDrawerItem()
                    .withEmail("Email")
                    .withName("Name")
                    .withIcon(R.drawable.ic_account);

            // Login Drawer Item
            login = new PrimaryDrawerItem()
                    .withDescription("Login")
                    .withDescriptionTextColorRes(R.color.colorAccent)
                    .withIdentifier(1)
                    .withTag(LOGIN_TAG);
        }
    }
}

//    public void setContext(Context c){
//        context = c;
//    }

//    public AccountHeader getAccountHeader(Activity activity){
//        return accountHeader = new AccountHeaderBuilder()
//                .withActivity(activity)
//                .withHeaderBackground(R.drawable.header_nav_background_adjusted)
//                .addProfiles(profileDrawerItem)
//                .withTextColorRes(R.color.colorPrimaryDark)
//                .build();
//    }

//    public Drawer getUserDrawer(Activity activity){
//        if(currentUser != null) {
//            return userDrawer = new DrawerBuilder()
//                    .withActivity(activity)
//                    .withAccountHeader(getAccountHeader(activity))
//                    .addDrawerItems(
//                                    changeEmail,
//                                    changePassword,
//                                    signOut,
//                                    deleteAccount)
//                    .withOnDrawerItemClickListener(this)
//                    .build();
//        }
//        else{
//            return userDrawer = new DrawerBuilder()
//                    .withActivity(activity)
//                    .withAccountHeader(getAccountHeader(activity))
//                    .addDrawerItems(
//                                    login)
//                    .withOnDrawerItemClickListener(this)
//                    .build();
//        }
//    }

    // Drawer Item Click Listener
//    @Override
//    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
//        Log.i(TAG, "Drawer item clicked at position " + position);
//        Log.i(TAG, "Drawer Item " + drawerItem);
//        Log.i(TAG, "View " + view);
//        Log.i(TAG, "Drawer Item Tag " + drawerItem.getTag());
//        Log.i(TAG, "Drawer Item Identifier " + drawerItem.getIdentifier());
//        final String str = drawerItem.getTag().toString();
//
//        switch (str) {
//            case LOGIN_TAG:
//                Log.i(TAG, "Login Tag Pressed");
//                userDrawer.closeDrawer();
//                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
//                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(loginIntent);
//                break;
//
//            case CHANGE_EMAIL_TAG:
//                Log.i(TAG, "Change Email Tag Pressed");
//                userDrawer.closeDrawer();
//                Intent changeEmail = new Intent(getApplicationContext(), ChangeEmailActivity.class);
//                changeEmail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(changeEmail);
//                break;
//
//            case CHANGE_PASSWORD_TAG:
//                Log.i(TAG, "Change Password Tag Pressed");
//                //Intent changePassword = new Intent(getApplicationContext(), )
//
//                break;
//
//            case SIGN_OUT_TAG:
//                Log.i(TAG, "Sign Out Tag Pressed");
//                if (currentUser != null) {
//                    FirebaseAuth.getInstance().signOut();
//                }
//                break;
//
//            case DELETE_ACCOUNT_TAG:
//                Log.i(TAG, "Delete Account Tag Pressed");
//                de.hdodenhof.circleimageview.CircleImageView img = new de.hdodenhof.circleimageview.CircleImageView(this);
//                img.setImageURI(currentUser.getPhotoUrl());
//
//                new AlertDialog.Builder(this)
//                        .setMessage("Are you sure you want to delete your account with the following Info : " + "\n\nEmail : " + currentUser.getEmail() +
//                                "\nUser Name: " + currentUser.getDisplayName())
//                        .setCustomTitle(img)
//                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (currentUser != null) {
//                                    Log.i(TAG, "Deleting the user with display name " + currentUser.getDisplayName());
//                                    Log.i(TAG, "Deleting the user with email address " + currentUser.getEmail());
//
//                                    currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful()) {
//                                                Log.i(TAG, "User has been Deleted");
//                                            } else {
//                                                Log.i(TAG, "Something went wrong deleting the user");
//                                            }
//                                        }
//                                    });
//                                }
//                            }
//                        })
//                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .create()
//                        .show();
//                break;
//        }
//
//        return true;
//    }
