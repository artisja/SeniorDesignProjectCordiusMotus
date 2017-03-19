package mult_603.seniordesignprojectcordiusmotus;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashEntranceScreen extends Activity {

    ImageView splashIcon;
    TextView titleSplash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_entrance_screen);
        splashIcon = (ImageView) findViewById(R.id.splash_icon);
        titleSplash = (TextView) findViewById(R.id.title_splash);
        SetUpAnimation();
    }

    private void SetUpAnimation() {
        ObjectAnimator scalorDown = ObjectAnimator.ofPropertyValuesHolder(splashIcon,
                PropertyValuesHolder.ofFloat("scaleX",1.2f),
                PropertyValuesHolder.ofFloat("scaleY",1.2f));
        scalorDown.setRepeatCount(30);
        scalorDown.setRepeatMode(ObjectAnimator.REVERSE);
        scalorDown.start();
        final Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        titleSplash.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
                Intent intent = new Intent(getBaseContext(),UserMainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
