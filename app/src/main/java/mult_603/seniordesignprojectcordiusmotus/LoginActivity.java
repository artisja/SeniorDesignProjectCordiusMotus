package mult_603.seniordesignprojectcordiusmotus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
              final String   typedEmail = emailEditText.getText().toString().trim();
              final String   typedPassword = passwordEditText.getText().toString().trim();

                firebaseAuth.signInWithEmailAndPassword(typedEmail,typedPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"fuck",Toast.LENGTH_LONG).show();
                        }else{
                            Intent intent = new Intent(LoginActivity.this,ContactsActivity.class);
                            startActivity(intent);
                        }
                    }
                });

            }
        });

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
        submitLoginButton = (Button) findViewById(R.id.submit_login_button);
    }


}