package mult_603.seniordesignprojectcordiusmotus;

import android.content.res.Configuration;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Created by Wes on 10/31/16.
 * This class can be used to give our application global access to the users location
 * Whether or not the user is logged in, Bluetooth etc
 */
public class ApplicationController extends android.app.Application {
    public final String TAG = ApplicationController.class.getSimpleName();
    public FirebaseAuth.AuthStateListener authStateListener;
    public FirebaseAuth firebaseAuth;
    public FirebaseUser currentUser;
    public Handler bluetoothHandler;
    public UserProfileChangeRequest userProfileChangeRequest;
    private DrawerLayout userDrawerLayout;
    private ActionBarDrawerToggle actionBarToggle;


    private static ApplicationController singleton;

    public ApplicationController getInstance(){
        return singleton;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        singleton = this;
        Log.i(TAG, "On Create was called");


        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if(currentUser != null) {
            Log.i(TAG, "Current User " + currentUser);
            Log.i(TAG, "Current User Display Name "  + currentUser.getDisplayName());
            Log.i(TAG, "Current User Email " + currentUser.getEmail());
            Log.i(TAG, "Current User UUID  " + currentUser.getUid());

            // Send the user an email verification
            // This will send them an email every time do I want to do that???
//            currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if(task.isSuccessful()){
//                        Log.i(TAG, "Sent an email to " + currentUser.getEmail());
//                    }
//                }
//            });
        }

        if(firebaseAuth != null){
            Log.i(TAG, "Firebase auth is not null ");
            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    Log.i(TAG, "Firebase Auth State Listener " + firebaseAuth.getCurrentUser());



                }
            };

            // Add auth state listener
            firebaseAuth.addAuthStateListener(authStateListener);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "On Configuration Changed Called");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i(TAG, "On Loe Memory called");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(TAG, "On Terminate Called");

        // If the auth state listener is not null then remove it from the firebase auth
        if(authStateListener != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
