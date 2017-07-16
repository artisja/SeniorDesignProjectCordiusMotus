package mult_603.seniordesignprojectcordiusmotus;

import android.*;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This class creates the heart pumping animation that is seen in the starting of our application
 */

public class SplashEntranceScreen extends Activity {

    ImageView splashIcon;
    TextView titleSplash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_entrance_screen);
        splashIcon = (ImageView) findViewById(R.id.splash_icon);
        titleSplash = (TextView) findViewById(R.id.title_splash);
        ActivityCompat.requestPermissions(SplashEntranceScreen.this, new String[]{android.Manifest.permission.CALL_PHONE},0);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            //Log.i(TAG, "The user has granted permission to use the camera ");
        }
        else{
           // Log.i(TAG, "User has not given us permission to use their camera ");
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
            }
        }, 5000);



    }

}
