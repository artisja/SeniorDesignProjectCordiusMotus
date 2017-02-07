package mult_603.seniordesignprojectcordiusmotus;

import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
/**
 * Created by Wes on 10/31/16.
 *
 */
public class ApplicationController extends android.app.Application {

    public final String TAG = ApplicationController.class.getSimpleName();
    public FirebaseAuth firebaseAuth;
    public FirebaseUser currentUser;
    private static ApplicationController singleton;
    public static Intent locationIntent;

    public ApplicationController getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;

        // Initialize firebase for our application
        FirebaseApp.initializeApp(this);

        Log.i(TAG, "Initializing firebase");
        // Can Start the location service when the application starts
        locationIntent = new Intent(getApplicationContext(), LocationService.class);
        startService(locationIntent);

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
