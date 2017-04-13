package mult_603.seniordesignprojectcordiusmotus;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

/**
 * Created by Wes on 10/31/16.
 *
 */
public class ApplicationController extends Application {

    public final String TAG = ApplicationController.class.getSimpleName();
    public FirebaseAuth firebaseAuth;
    public FirebaseUser currentUser;
    private static ApplicationController singleton;
    public static Intent locationIntent;
    public static HashMap<String, DeviceUser> deviceUserHashMap;

    public ApplicationController getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        Log.i(TAG, "On Create Called -> Initializing firebase starting location service");

        // Initialize fire base for our application set persistence for offline access to database
        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // Can Start the location service when the application starts
        locationIntent = new Intent(getApplicationContext(), LocationService.class);
        startService(locationIntent);

        // Debugging play alert sound
//        playAlertSound();

    }

    public void playAlertSound(){
        int MAX_VOLUME = 100;
        float soundVolume = 90;
        float volume = (float) (1 - (Math.log(MAX_VOLUME - soundVolume) / Math.log(MAX_VOLUME)));
        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alert);
        mediaPlayer.setVolume(volume, volume);
        mediaPlayer.start();
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
