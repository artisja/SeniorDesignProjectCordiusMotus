package mult_603.seniordesignprojectcordiusmotus;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;

public class ForgotPasswordActivity extends AppCompatActivity {
    public final String TAG = ForgotPasswordActivity.class.getSimpleName();
    private ApplicationController appController;
    private EditText emailEditText;
    private Button passwordResetButton;
    private Button backButton;
    private FirebaseUser currentUser;
    private FirebaseAuth auth;
    private AccountHeader headerResult;
    private Drawer drawerResult;
    private NavigationDrawerHandler navHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Set up the navigation handler
        navHandler = new NavigationDrawerHandler(this, savedInstanceState, getApplicationContext(), toolbar);
        headerResult = navHandler.setAccountHeader(this, savedInstanceState, getApplicationContext());
        drawerResult = navHandler.setUserDrawer(this, headerResult, toolbar);

        setUpViews();
        setOnClickListeners();
    }

    private void setOnClickListeners(){
        passwordResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Password reset button clicked");
                if(currentUser != null) {
                    Log.i(TAG, "Current User is not null " + currentUser.getEmail());
                }
                else{
                    Log.i(TAG, "Current user is null ");
                }
                    final String emailAddress = emailEditText.getText().toString().trim();

                    if(emailAddress.isEmpty() == false) {
                        Log.i(TAG, "Email Address is not empty " + emailAddress);

                        auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "We have sent an email to reset password to " + emailAddress);
                                    Toast.makeText(getApplicationContext(), "We have sent and email to " + emailAddress, Toast.LENGTH_LONG).show();
                                } else {
                                    Log.i(TAG, "Failed to send an email to reset password to " + emailAddress);
                                    Toast.makeText(getApplicationContext(), "Password reset email failed to send", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    else{
                        Log.i(TAG, "Email Address is empty " + emailAddress);
                    }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Back button clicked");
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
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

    private void setUpViews(){
        appController = (ApplicationController) getApplicationContext();
        emailEditText = (EditText) findViewById(R.id.email_forgot_password);
        passwordResetButton = (Button) findViewById(R.id.password_reset_button);
        backButton = (Button) findViewById(R.id.forgot_password_back_button);
        currentUser = appController.currentUser;
        auth        = appController.firebaseAuth;
    }
}
