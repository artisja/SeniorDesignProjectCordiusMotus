package mult_603.seniordesignprojectcordiusmotus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginDeviceLocator extends AppCompatActivity {

    private TextView deviceLocatorInstructions;
    private EditText deviceLocatorPassword;
    private Button deviceLocatorLogin;
    FirebaseDatabase database;
    String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_device_locator);
        findViews();
        setClickHappenings();
    }

    private void setClickHappenings() {
        deviceLocatorLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userInput = deviceLocatorPassword.getText().toString();
                uuid = userInput;
                database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference(uuid);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()==null) {
                            Toast.makeText(LoginDeviceLocator.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                        }else {
                            Intent intent = new Intent(LoginDeviceLocator.this,UserMapsActivity.class);
                            intent.putExtra("Location","Some Location");
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void findViews() {
        deviceLocatorLogin = (Button) findViewById(R.id.device_login_button);
        deviceLocatorPassword = (EditText) findViewById(R.id.device_edittext_password);
        deviceLocatorInstructions = (TextView) findViewById(R.id.device_login_instructions);
    }
}
