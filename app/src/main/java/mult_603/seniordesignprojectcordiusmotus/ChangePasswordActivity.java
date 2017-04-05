package mult_603.seniordesignprojectcordiusmotus;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.tapadoo.alerter.Alerter;
import com.tapadoo.alerter.OnShowAlertListener;

public class ChangePasswordActivity extends AppCompatActivity {
    private static final String TAG = ChangePasswordActivity.class.getSimpleName();
    private NavigationDrawerHandler navHandler;
    private AccountHeader headerResult;
    private FirebaseUser currentUser;
    private Drawer drawerResult;
    private TextView changePasswordHeader;
    private TextView changePasswordMessage;
    private EditText changePasswordEmail;
    private EditText oldPassword;
    private EditText newPassword;
    private Button submitButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Set up the navigation handler
        navHandler = new NavigationDrawerHandler(this, savedInstanceState, getApplicationContext(), toolbar);
        headerResult = navHandler.setAccountHeader(this, savedInstanceState, getApplicationContext());
        drawerResult = navHandler.setUserDrawer(this, headerResult, toolbar);

        findViews();

        // Reset the users password based on whether or not the email and old password are correct
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String oldEmail = changePasswordEmail.getText().toString().trim();
                final String oldPass  = oldPassword.getText().toString().trim();
                final String newPass  = newPassword.getText().toString().trim();

                // Create a progress dialog to show to the user
                progressDialog = new ProgressDialog(ChangePasswordActivity.this, R.style.AppThemeDialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Changing Password...");
                progressDialog.show();

                // Change the user's password
                changeUsersPassword(oldEmail, oldPass, newPass);

            }
        });
    }

    private void changeUsersPassword(String oldEmail, String oldPass, String newPassword){
        Log.i(TAG, "Attempting to change the users password ");
        final String newPass = newPassword;

        if (oldEmail.isEmpty() && oldPass.isEmpty() && newPass.isEmpty()) {
            Log.i(TAG, "All Fields Blank");
            Alerter.create(ChangePasswordActivity.this)
                    .setBackgroundColor(R.color.colorPrimaryDark)
                    .setText("Error All Text Fields are empty")
                    .setTitle("Error")
                    .setDuration(3000)
                    .enableIconPulse(true)
                    .setOnShowListener(new OnShowAlertListener() {
                        @Override
                        public void onShow() {
                            progressDialog.cancel();
                        }
                    })
                    .show();
        }
        else if(oldEmail.isEmpty() || oldPass.isEmpty() || newPass.isEmpty()){
            Alerter.create(ChangePasswordActivity.this)
                    .setBackgroundColor(R.color.colorPrimaryDark)
                    .setText("Error one of the below fields is empty")
                    .setTitle("Error")
                    .setDuration(3000)
                    .enableIconPulse(true)
                    .setOnShowListener(new OnShowAlertListener() {
                        @Override
                        public void onShow() {
                            progressDialog.cancel();
                        }
                    })
                    .show();
        }
        else {
            // If the user is not signed in then re-authenticate them

            AuthCredential credential = EmailAuthProvider.getCredential(oldEmail, oldPass);
            currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    // Successfully re-authenticated the user to reset their password
                    if (task.isSuccessful()) {

                        // Update the users password
                        currentUser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // Successfully updated the users password
                                if (task.isSuccessful()) {
                                    Alerter.create(ChangePasswordActivity.this)
                                            .enableIconPulse(true)
                                            .setDuration(3000)
                                            .setTitle("Success")
                                            .setText("Updated your password successfully")
                                            .setBackgroundColor(R.color.colorPrimaryDark)
                                            .setOnShowListener(new OnShowAlertListener() {
                                                @Override
                                                public void onShow() {
                                                    progressDialog.cancel();
                                                }
                                            })
                                            .show();
                                } else {
                                    try {
                                        throw task.getException();
                                    } catch (Exception e) {
                                        Log.i(TAG, "Error updating password " + e.getMessage());
                                        Alerter.create(ChangePasswordActivity.this)
                                                .setText("Error: " + e.getMessage())
                                                .setTitle("Error")
                                                .setDuration(3000)
                                                .enableIconPulse(true)
                                                .setOnShowListener(new OnShowAlertListener() {
                                                    @Override
                                                    public void onShow() {
                                                        progressDialog.cancel();
                                                    }
                                                })
                                                .show();
                                    }
                                }
                            }
                        });
                    } else {
                        Alerter.create(ChangePasswordActivity.this)
                                .setBackgroundColor(R.color.colorPrimaryDark)
                                .setText("The user name and email were not recognized. Please create a login")
                                .setTitle("Error")
                                .setDuration(3000)
                                .enableIconPulse(true)
                                .setOnShowListener(new OnShowAlertListener() {
                                    @Override
                                    public void onShow() {
                                        progressDialog.cancel();
                                    }
                                })
                                .show();
                    }
                }
            });
        }
    }

    public void findViews(){
        changePasswordHeader  = (TextView) findViewById(R.id.change_password);
        changePasswordMessage = (TextView) findViewById(R.id.change_password_message);
        changePasswordEmail   = (EditText) findViewById(R.id.change_password_email);
        oldPassword  = (EditText) findViewById(R.id.old_password);
        newPassword  = (EditText) findViewById(R.id.new_password);
        submitButton = (Button) findViewById(R.id.submit_change_password_button);
    }

    @Override
    public void onResume(){
        super.onResume();
        headerResult = navHandler.getHeader();
        drawerResult = navHandler.getDrawer();
    }
}
