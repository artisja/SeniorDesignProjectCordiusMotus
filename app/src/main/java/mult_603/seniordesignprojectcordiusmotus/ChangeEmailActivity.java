package mult_603.seniordesignprojectcordiusmotus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangeEmailActivity extends AppCompatActivity {
    public static final String TAG = ChangeEmailActivity.class.getSimpleName();
    private TextView changeEmailHeader;
    private TextView changeEmailDescription;
    private EditText changeEmailEditText;
    private Button   changeEmailButton;
    private Button   backButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private ApplicationController appController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

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

    private void findViews(){
        changeEmailHeader      = (TextView) findViewById(R.id.change_email_text_view);
        changeEmailDescription = (TextView) findViewById(R.id.change_email_message_text_view);
        changeEmailEditText    = (EditText) findViewById(R.id.change_email_edit_text);
        changeEmailButton      = (Button)   findViewById(R.id.change_email_reset_button);
        backButton             = (Button)   findViewById(R.id.change_email_back_button);
        appController          = (ApplicationController) getApplicationContext();
        //firebaseAuth           = FirebaseAuth.getInstance();
        currentUser            = appController.currentUser;
    }
}
