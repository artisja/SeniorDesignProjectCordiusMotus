package mult_603.seniordesignprojectcordiusmotus;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {
    public final String TAG = SignUpActivity.class.getSimpleName();
    private EditText setPasswordEdit,setEmailEdit, setUserNameEdit;
    private Button createdPasswordButton;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findViews();
        mFirebaseAuth = FirebaseAuth.getInstance();
        setUpClick();

        // This Auth state listener might have broken some things
        // Or it was me adding too many of the same user accounts in the database
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.i(TAG, "Current User " + user);

                if(user != null){
                    Log.i(TAG, "Current User is not null");
                    String uName    = setUserNameEdit.getText().toString().trim();

                    if(uName != null){
                        Log.i(TAG, "User Name is not null");
                        // Can use this to give them an image with their profile as well
                        // Giving the user a username
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(uName)
                                .build();

                        user.updateProfile(userProfileChangeRequest);
                        Log.i(TAG, "User Get Display Name " + user.getDisplayName());

                    }
                }
            }
        };

        // Add the auth state listener
        mFirebaseAuth.addAuthStateListener(authStateListener);


    }

    @Override
    public void onStop(){
        super.onStop();
        Log.i(TAG, "On Stop Entered");

        // If the auth listener exists remove it
        if(authStateListener != null){
            mFirebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.i(TAG, "On Pause Entered");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i(TAG, "On Resume Entered");
        // If the activity is resumed, add the auth listener
        mFirebaseAuth.addAuthStateListener(authStateListener);
    }

    private void setUpClick() {
        createdPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = setPasswordEdit.getText().toString().trim();
                String email    = setEmailEdit.getText().toString().trim();
                String uName    = setUserNameEdit.getText().toString().trim();
                final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                if(email.isEmpty() || password.isEmpty() || uName.isEmpty()){
                    // Close the keyboard and notify the user that one of the fields was empty
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    Toast.makeText(SignUpActivity.this, "No Password or Email was entered", Toast.LENGTH_SHORT).show();
                }else{
                    // If all the fields have input then create a new user
                    mFirebaseAuth.createUserWithEmailAndPassword(email,password);
                    Boolean truth = mFirebaseAuth.signInWithEmailAndPassword(email,password).isSuccessful();
                    onBackPressed();
                }
            }
        });
    }

    private void findViews() {
        setPasswordEdit = (EditText) findViewById(R.id.input_password_edit);
        setEmailEdit    = (EditText) findViewById(R.id.input_email_edit);
        setUserNameEdit = (EditText) findViewById(R.id.input_username_edit);
        createdPasswordButton = (Button) findViewById(R.id.submit_signup_button);
    }
}
