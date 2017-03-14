package mult_603.seniordesignprojectcordiusmotus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.tapadoo.alerter.Alerter;

public class LoginActivity extends AppCompatActivity {
    public final String TAG = LoginActivity.class.getSimpleName();
    private Button signUpButton, loginButton, forgotPasswordButton;
    private EditText passwordEditText,emailEditText;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private ApplicationController appController;
    private AccountHeader headerResult;
    private Drawer drawerResult;
    private TextView warningMsg;
    private NavigationDrawerHandler navHandler;

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
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null){
            Log.i(TAG, "Current User Display Name " + firebaseUser.getDisplayName());
            Log.i(TAG, "Current User Email " + firebaseUser.getEmail());
            Log.i(TAG, "Current User UUID" + firebaseUser.getUid());

            // If the user is already logged in the they don't need to sign in
            signUpButton.setTextColor(Color.GRAY);
            signUpButton.setEnabled(false);
        }
    }

    // Set up the button on click listeners
    private void setUpClicks() {

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String   typedEmail    = emailEditText.getText().toString().trim();
                final String   typedPassword = passwordEditText.getText().toString().trim();
                final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                final View currentView = v;

                // If the password and email are null then don't do anything.
                if(typedEmail.isEmpty() && typedPassword.isEmpty()){
                    inputMethodManager.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
                    Toast.makeText(getApplicationContext(), "Email and Password are empty", Toast.LENGTH_SHORT).show();
                }

                // If password or email is null then don't do anything.
                else if(typedEmail.isEmpty() || typedPassword.isEmpty()){
                    inputMethodManager.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
                    Toast.makeText(getApplicationContext(), "Email or Password is empty", Toast.LENGTH_SHORT).show();
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

                                // Throw the exception that occured
                                try{
                                    throw task.getException();
                                }
                                catch(Exception e) {
                                    Alerter.create(LoginActivity.this)
                                            .setTitle("Error Occured")
                                            .setText("Error: " + e.getMessage())
                                            .setBackgroundColor(R.color.colorPrimaryDark)
                                            .enableIconPulse(true)
                                            .show();
                                }
                            } else {
                                Log.i(TAG, "Successfully logging user in");
                                firebaseUser = firebaseAuth.getCurrentUser();

                                // If firebase user is not null
                                if(firebaseUser != null){
                                    // Set the user name to be a key to getting uuid for map lookups
                                    DeviceUser deviceUser = new DeviceUser(firebaseUser.getEmail(), firebaseUser.getDisplayName());
                                    deviceUser.setUuid(firebaseUser.getUid());

                                    DatabaseReference dbRef = firebaseDatabase.getReference("UserDictionary");
                                    dbRef.child(firebaseUser.getDisplayName()).setValue(deviceUser);
                                }

                                Intent intent = new Intent(LoginActivity.this, UserTabActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
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

    @Override
    public void onResume(){
        super.onResume();
        headerResult = navHandler.getHeader();
        drawerResult = navHandler.getDrawer();
    }

    private void findViews() {
        loginButton          = (Button) findViewById(R.id.login_button);
        signUpButton         = (Button) findViewById(R.id.sign_up_button);
        forgotPasswordButton = (Button) findViewById(R.id.forgot_password_button);
        emailEditText        = (EditText) findViewById(R.id.email_edit);
        passwordEditText     = (EditText) findViewById(R.id.password_edit);
        appController        = (ApplicationController) getApplicationContext();
        warningMsg           = (TextView) findViewById(R.id.warning_message);
        firebaseDatabase     = FirebaseDatabase.getInstance();

        // Warn the user to only use the login if they are wearing a device
        warningMsg.setText("Please only create a login if you are currently using our heart rate monitor device");
    }
}