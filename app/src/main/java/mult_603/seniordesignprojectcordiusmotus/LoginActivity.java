package mult_603.seniordesignprojectcordiusmotus;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.google.firebase.auth.FirebaseAuth.*;

public class LoginActivity extends AppCompatActivity {
    public final String TAG = LoginActivity.class.getSimpleName();
    private Button signUpButton, loginButton, submitLoginButton, forgotPasswordButton;
    private EditText passwordEditText, emailEditText;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ApplicationController appController;
    private LocationHolder locationHolder = new LocationHolder();
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationHolder.setLatitude(String.valueOf(location.getLatitude()));
                locationHolder.setLongitude(String.valueOf(location.getLongitude()));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
        findViews();
        setUpClicks();

        firebaseUser = appController.currentUser;
        firebaseAuth = appController.firebaseAuth;
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // Get the current user and their part of the database
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//        if (currentUser.getUid()!=null) {
//            final DatabaseReference dbref = firebaseDatabase.getReference(currentUser.getUid());
//        }
        locationHolder = new LocationHolder();

        if (firebaseUser != null) {
            Log.i(TAG, "Current User Display Name " + firebaseUser.getDisplayName());
            Log.i(TAG, "Current User Email " + firebaseUser.getEmail());
            Log.i(TAG, "Current User UUID" + firebaseUser.getUid());

            // If the user is already logged in the they don't need to sign in
            signUpButton.setTextColor(Color.GRAY);
            signUpButton.setEnabled(true);
        }
    }

    // Set up the button on click listeners
    private void setUpClicks() {

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String typedEmail = emailEditText.getText().toString().trim();
                final String typedPassword = passwordEditText.getText().toString().trim();
                final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                final View currentView = v;

                // If the password and email are null then don't do anything.
                if ((typedEmail.isEmpty() || typedPassword.isEmpty())) {
                    inputMethodManager.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
                    Toast.makeText(getApplicationContext(), "Email and Password are empty", Toast.LENGTH_SHORT).show();
                }
                // Use Firebase to sign into the application
                else if(firebaseAuth.signInWithEmailAndPassword(typedEmail.trim(), typedPassword.trim()).isSuccessful()){
                    firebaseAuth.signInWithEmailAndPassword(typedEmail.trim(), typedPassword.trim());
                    DatabaseReference dbref = firebaseDatabase.getReference(firebaseAuth.getCurrentUser().getUid().toString());
                    addLocationToDatabase(locationHolder);
                    Intent intent = new Intent(getApplicationContext(),ContactSimpleActivity.class);
                    startActivity(intent);
                }

            }
        });

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Forgot Password Button was clicked");
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Sign Up Button was clicked");
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    public void addLocationToDatabase(LocationHolder locationHolder){
        FirebaseDatabase fireDb = FirebaseDatabase.getInstance();
        DatabaseReference ref = fireDb.getReference(appController.firebaseAuth.getCurrentUser().getUid());
        ref.setValue(locationHolder);
        // What if we could call this whenever the users position changes?
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LocationHolder data = dataSnapshot.getValue(LocationHolder.class);
                Log.i(TAG, "Persons data has changed: " + data);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "Persons data change was cancelled");
            }
        });
    }

    private void findViews() {
        loginButton          = (Button) findViewById(R.id.login_button);
        signUpButton         = (Button) findViewById(R.id.sign_up_button);
        forgotPasswordButton = (Button) findViewById(R.id.forgot_password_button);
        emailEditText        = (EditText) findViewById(R.id.email_edit);
        passwordEditText     = (EditText) findViewById(R.id.password_edit);
        appController        = (ApplicationController) getApplicationContext();
    }
}