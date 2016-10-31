package mult_603.seniordesignprojectcordiusmotus;

import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Wes on 10/31/16.
 * This class can be used to give our application global access to the users location
 * Whether or not the user is logged in, Bluetooth etc
 */
public class ApplicationController extends android.app.Application {
    public final String TAG = ApplicationController.class.getSimpleName();
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private static ApplicationController singleton;

    public ApplicationController getInstance(){
        return singleton;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        singleton = this;

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if(currentUser != null) {
            Log.i(TAG, "Current User " + currentUser);
            Log.i(TAG, "Current User Display Name "  + currentUser.getDisplayName());
            Log.i(TAG, "Current User Email " + currentUser.getEmail());
            Log.i(TAG, "Current User UUID  " + currentUser.getUid());
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
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
