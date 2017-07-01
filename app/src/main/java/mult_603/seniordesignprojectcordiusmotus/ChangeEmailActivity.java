package mult_603.seniordesignprojectcordiusmotus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;

/**
 * Allows the user to change their email address that is tied to their account.
 * This is done using Firebase
 * This still needs to be implemented
 */
public class ChangeEmailActivity extends AppCompatActivity {
    public static final String TAG = ChangeEmailActivity.class.getSimpleName();
    private TextView changeEmailHeader;
    private TextView changeEmailDescription;
    private EditText changeEmailEditText;
    private Button   changeEmailButton;
    private Button   backButton;
    private FirebaseUser currentUser;
    private NavigationDrawerHandler navHandler;
    private AccountHeader headerResult;
    private Drawer drawerResult;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        // Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Set up the navigation handler
        navHandler = new NavigationDrawerHandler(this, savedInstanceState, getApplicationContext(), toolbar);
        headerResult = navHandler.setAccountHeader(this, savedInstanceState, getApplicationContext());
        drawerResult = navHandler.setUserDrawer(this, headerResult, toolbar);

        // Find the views
        findViews();

        changeEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Change Email Button was pressed");

                if(currentUser != null){
                    Log.i(TAG, "Current User is not null");
                    Log.i(TAG, "Current User's Email " + currentUser.getEmail());
                    Log.i(TAG, "Current User's Display Name " + currentUser.getDisplayName());
                }
                else{
                    Log.i(TAG, "Current User is null");
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Back Button was pressed");
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        headerResult = navHandler.getHeader();
        drawerResult = navHandler.getDrawer();
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.i(TAG, "On Start Called");
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.i(TAG, "On Stop Called");
        if(authListener != null){
            auth.removeAuthStateListener(authListener);
        }
    }

    private void findViews(){
        changeEmailHeader      = (TextView) findViewById(R.id.change_email_text_view);
        changeEmailDescription = (TextView) findViewById(R.id.change_email_message_text_view);
        changeEmailEditText    = (EditText) findViewById(R.id.change_email_edit_text);
        changeEmailButton      = (Button)   findViewById(R.id.change_email_reset_button);
        backButton             = (Button)   findViewById(R.id.change_email_back_button);
        currentUser            = FirebaseAuth.getInstance().getCurrentUser();
    }
}
