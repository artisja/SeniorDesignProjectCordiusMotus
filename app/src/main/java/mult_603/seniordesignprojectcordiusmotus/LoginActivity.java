package mult_603.seniordesignprojectcordiusmotus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.tapadoo.alerter.Alerter;
import com.tapadoo.alerter.OnShowAlertListener;

public class LoginActivity extends AppCompatActivity {
    public final String TAG = LoginActivity.class.getSimpleName();
    private Button signUpButton, loginButton, forgotPasswordButton, mapsButton;
    private EditText passwordEditText,emailEditText;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private ApplicationController appController;
    private AccountHeader headerResult;
    private Drawer drawerResult;
    private NavigationDrawerHandler navHandler;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
        setUpClicks();

        // Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Set up the navigation handler
        navHandler = new NavigationDrawerHandler(this, savedInstanceState, getApplicationContext(), toolbar);
        headerResult = navHandler.setAccountHeader(this, savedInstanceState, getApplicationContext());
        drawerResult = navHandler.setUserDrawer(this, headerResult, toolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if(currentUser != null){
                    Log.i(TAG, "Current User is not null in login activity");
                    signUpButton.setTextColor(Color.GRAY);
                    signUpButton.setEnabled(false);
                }
                else{
                    Log.i(TAG, "Current User is null in the login activity");
                    signUpButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.wordColorRed));
                    signUpButton.setEnabled(true);
                }
            }
        };

        // Add the auth state listener
        firebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    // Set up the button on click listeners
    private void setUpClicks() {

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Login Button Was Clicked");
                final String typedEmail    = emailEditText.getText().toString().trim();
                final String typedPassword = passwordEditText.getText().toString().trim();

                // Create a progress dialog to tell the user how long the process will take
                progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppThemeDialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();

                loginUser(v, typedEmail, typedPassword);
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

        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Map Button Was Clicked");
                Intent intent = new Intent(LoginActivity.this, UserMapsActivity.class);
                startActivity(intent);
            }
        });
    }

    // Method to log the user in Use this to show a progress dialog while authentication is processing

    private void loginUser(View view, String typedEmail, String typedPassword){
        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        final View currentView = view;

        // If the password and email are null then don't do anything.
        if(typedEmail.isEmpty() && typedPassword.isEmpty()){
            inputMethodManager.hideSoftInputFromWindow(currentView.getWindowToken(), 0);

            // Notify the user that both email and password fields are empty
            Alerter.create(LoginActivity.this)
                    .setTitle("Error Occurred")
                    .setText("Error: " + "Email and Password are empty")
                    .setBackgroundColor(R.color.colorPrimaryDark)
                    .enableIconPulse(true)
                    .setOnShowListener(new OnShowAlertListener() {
                        @Override
                        public void onShow() {
                            progressDialog.cancel();
                        }
                    })
                    .show();
        }

        // If password or email is null then don't do anything.
        else if(typedEmail.isEmpty() || typedPassword.isEmpty()){
            inputMethodManager.hideSoftInputFromWindow(currentView.getWindowToken(), 0);

            // Notify the user that the email or password field is empty
            Alerter.create(LoginActivity.this)
                    .setTitle("Error Occurred")
                    .setText("Error: " + "Email or Password is empty")
                    .setBackgroundColor(R.color.colorPrimaryDark)
                    .enableIconPulse(true)
                    .setOnShowListener(new OnShowAlertListener() {
                        @Override
                        public void onShow() {
                            progressDialog.cancel();
                        }
                    })
                    .show();
        }

        // Use Firebase to sign into the application
        else {
            firebaseAuth.signInWithEmailAndPassword(typedEmail, typedPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                    // If logging in is unsuccessful then hide keyboard and display toast
                    if (!task.isSuccessful()) {
                        // Hide the keyboard
                        inputMethodManager.hideSoftInputFromWindow(currentView.getWindowToken(), 0);

                        // Throw the exception that occurred
                        try{
                            throw task.getException();
                        }
                        catch(Exception e) {
                            progressDialog.cancel();
                            Alerter.create(LoginActivity.this)
                                    .setTitle("Error Occurred")
                                    .setText("Error: " + e.getMessage())
                                    .setBackgroundColor(R.color.colorPrimaryDark)
                                    .enableIconPulse(true)
                                    .setOnShowListener(new OnShowAlertListener() {
                                        @Override
                                        public void onShow() {
                                            progressDialog.cancel();
                                        }
                                    })
                                    .show();
                        }
                    } else {
                        Log.i(TAG, "Successfully logging user in");
                        firebaseUser = firebaseAuth.getCurrentUser();

                        progressDialog.cancel();

                        // Go to the user tab activity
                        Intent intent = new Intent(LoginActivity.this, UserTabActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    // Disable going backwards from the login screen this exits application
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Log.i(TAG, "On Back Pressed From Login Activity");
        moveTaskToBack(true);
    }

    @Override
    public void onStop(){
        super.onStop();

        // If the auth listener exists remove it
        if(mAuthStateListener != null){
            firebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        headerResult = navHandler.getHeader();
        drawerResult = navHandler.getDrawer();

        // Add the auth state listener back
        firebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void findViews() {
        loginButton          = (Button) findViewById(R.id.login_button);
        signUpButton         = (Button) findViewById(R.id.sign_up_button);
        mapsButton           = (Button) findViewById(R.id.user_map_button);
        forgotPasswordButton = (Button) findViewById(R.id.forgot_password_button);
        emailEditText        = (EditText) findViewById(R.id.email_edit);
        passwordEditText     = (EditText) findViewById(R.id.password_edit);
        appController        = (ApplicationController) getApplicationContext();
        firebaseDatabase     = FirebaseDatabase.getInstance();
    }
}