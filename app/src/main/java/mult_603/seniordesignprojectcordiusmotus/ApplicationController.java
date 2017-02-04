package mult_603.seniordesignprojectcordiusmotus;

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
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
 *
 */
public class ApplicationController extends android.app.Application {

    public final String TAG = ApplicationController.class.getSimpleName();
    public FirebaseAuth firebaseAuth;
    public FirebaseUser currentUser;
    private static ApplicationController singleton;

    public ApplicationController getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;

        // Initialize firebase for our application
        FirebaseApp.initializeApp(this);

        Log.i(TAG, "On Create was called");

        // Can Start the location service when the application starts
        Intent locationIntent = new Intent(getApplicationContext(), LocationService.class);
        this.startService(locationIntent);

    }

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
}


    // Profile Changed Listener
//    @Override
//    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
//        Log.i(TAG, "On Profile Changed was called");
//        Log.i(TAG, "Profile Name " + profile.getName());
//        Log.i(TAG, "Profile Email " + profile.getEmail());
//        return true;
//    }

    // Auth State Changed listener
//    @Override
//    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//        Log.i(TAG, "On Auth State Changed ");
//        currentUser = firebaseAuth.getCurrentUser();
//
//        if (currentUser != null) {
//            Log.i(TAG, "Firebase Auth State Listener signed in " + currentUser);
//            Log.i(TAG, "Display Name " + currentUser.getDisplayName());
//            Log.i(TAG, "Email " + currentUser.getEmail());
//            Log.i(TAG, "UUID " + currentUser.getUid());
//
//            // Create User Profile
//            profileDrawerItem = new ProfileDrawerItem()
//                    .withEmail(currentUser.getEmail())
//                    .withName(currentUser.getDisplayName())
//                    .withIcon(currentUser.getPhotoUrl());
//
//            // Change email
//            changeEmail.withDescription(R.string.change_email)
//                    .withDescriptionTextColorRes(R.color.material_drawer_dark_selected)
//                    .withSelectedColorRes(R.color.lightGrayWithRed)
//                    .withIdentifier(2)
//                    .withTag("ChangeEmail");
//
//            // Change password
//            changePassword.withDescription(R.string.change_password)
//                    .withDescriptionTextColorRes(R.color.material_drawer_dark_selected)
//                    .withSelectedColorRes(R.color.lightGrayWithRed)
//                    .withIdentifier(3)
//                    .withTag("ChangePassword");
//
//            // Log out drawer item
//            signOut.withDescriptionTextColorRes(R.color.wordColorRed)
//                    .withDescription("Sign Out")
//                    .withSelectedColorRes(R.color.lightGrayWithRed)
//                    .withSelectedTextColor(Color.WHITE)
//                    .withIdentifier(4)
//                    .withTag("SignOut");
//
//            // Delete account drawer item
//            deleteAccount.withDescriptionTextColorRes(R.color.wordColorRed)
//                    .withDescription("Delete Account")
//                    .withSelectedColorRes(R.color.lightGrayWithRed)
//                    .withSelectedTextColor(Color.WHITE)
//                    .withIdentifier(5)
//                    .withTag("DeleteAccount");
//
//        } else {
//            Log.i(TAG, "Firebase Auth State Listener signed out ");
//
//            profileDrawerItem = new ProfileDrawerItem()
//                    .withEmail("Email")
//                    .withName("Name")
//                    .withIcon(R.drawable.ic_account);
//
//            // Login Drawer Item
//            login = new PrimaryDrawerItem()
//                    .withDescription("Login")
//                    .withDescriptionTextColorRes(R.color.colorAccent)
//                    .withIdentifier(1)
//                    .withTag(LOGIN_TAG);
//        }
//    }
//}
