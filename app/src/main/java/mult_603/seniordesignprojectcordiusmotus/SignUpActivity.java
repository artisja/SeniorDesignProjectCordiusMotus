package mult_603.seniordesignprojectcordiusmotus;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private EditText setPasswordEdit,setEmailEdit;
    private Button createdPasswordButton;
    private FirebaseAuth mFirebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findViews();
        mFirebaseAuth = FirebaseAuth.getInstance();
        setUpClick();
    }

    private void setUpClick() {
        createdPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String password = setPasswordEdit.getText().toString().trim();
                String email = setEmailEdit.getText().toString().trim();
                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(SignUpActivity.this, "NO no silly", Toast.LENGTH_SHORT).show();
                }else{

                    mFirebaseAuth.createUserWithEmailAndPassword(email,password);
                    Boolean truth = mFirebaseAuth.signInWithEmailAndPassword(email,password).isSuccessful();
                    onBackPressed();
                }
            }
        });
    }

    private void findViews() {
        setPasswordEdit = (EditText) findViewById(R.id.set_password_edit);
        setEmailEdit = (EditText) findViewById(R.id.input_email_edit);
        createdPasswordButton = (Button) findViewById(R.id.submit_signup_button);
    }
}
