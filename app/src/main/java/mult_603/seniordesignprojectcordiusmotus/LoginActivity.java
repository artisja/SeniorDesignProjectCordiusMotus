package mult_603.seniordesignprojectcordiusmotus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import static com.google.firebase.auth.FirebaseAuth.*;

public class LoginActivity extends AppCompatActivity {

    private Button signUpButton,loginButton,submitLoginButton;
    private EditText passwordEditText,emailEditText;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
        setUpClicks();
        firebaseAuth = getInstance();
        if (emailEditText.getText().toString().equals("") || passwordEditText.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"Please do not leave fields unfilled.",Toast.LENGTH_LONG);
        }else{
            loginButton.performClick();
        }
        if (firebaseUser != null){
            emailEditText.setText(firebaseUser.getEmail());
        }
    }


    private void setUpClicks() {

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String typedEmail = emailEditText.getText().toString().trim();
                final String typedPassword = passwordEditText.getText().toString().trim();
                final InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                final View vInternal = v;
                // If the password and email are null then don't do anything.
                if(typedEmail.isEmpty() && typedPassword.isEmpty()){
                    // Hide the keyboard deliver toast
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    Toast.makeText(getApplicationContext(), "Email and Password are empty", Toast.LENGTH_SHORT).show();
                }
                // If password or email is null then don't do anything.
                else if(typedEmail.isEmpty() || typedPassword.isEmpty()){
                    // Hide the keyboard deliver toast
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    Toast.makeText(getApplicationContext(), "Email or Password is empty", Toast.LENGTH_SHORT).show();
                }
                // Use Firebase to sign into the application
                else {
                    firebaseAuth.signInWithEmailAndPassword(typedEmail, typedPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                            // Login was unsuccessful
                            if (!task.isSuccessful()) {
                                // Hide keyboard and show toast
                                imm.hideSoftInputFromWindow(vInternal.getWindowToken(), 0);
                                Toast.makeText(getApplicationContext(), "Login Unsuccessful", Toast.LENGTH_LONG).show();
                            }
                            // Login was a success
                            else {
                                Intent intent = new Intent(LoginActivity.this, PatientEmergencyContactActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }

            }
        });
        // User clicked sign up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void findViews() {
        loginButton = (Button) findViewById(R.id.login_button);
        signUpButton = (Button) findViewById(R.id.sign_up_button);
        emailEditText = (EditText) findViewById(R.id.email_edit);
        passwordEditText = (EditText) findViewById(R.id.password_edit);
    }
}
