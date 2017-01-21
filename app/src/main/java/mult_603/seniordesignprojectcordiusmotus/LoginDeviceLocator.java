package mult_603.seniordesignprojectcordiusmotus;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
    public final String TAG = LoginDeviceLocator.class.getSimpleName();
    private TextView deviceLocatorInstructions;
    private EditText deviceLocatorPassword;
    private Button   deviceLocatorLogin;
    FirebaseDatabase database;
    String uuid,child;

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

                final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(v.getContext().INPUT_METHOD_SERVICE);
                final View currentView = v;
                Log.i(TAG, "Reference Key to Database " + ref.getKey());
                Log.i(TAG, "Reference Root " + ref.getRoot());
                Log.i(TAG, "Reference Database " + ref.getDatabase());
                ref.child("Location").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        child = dataSnapshot.getKey().toString();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Toast.makeText(LoginDeviceLocator.this,child, Toast.LENGTH_SHORT).show();
//                ref.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Log.i(TAG, "Datasnapshot Key " + dataSnapshot.getKey());
//                        Log.i(TAG, "Datasnapshot Value " + dataSnapshot.getValue());
//                        Log.i(TAG, "Datasnapshot Children Count " + dataSnapshot.getChildrenCount());
//                        Log.i(TAG, "Datasnapshot Get Reference " + dataSnapshot.getRef());
//
//
//                        if (dataSnapshot.getValue().equals(userInput) || userInput.isEmpty()) {
//                            // Hide the keyboard from view and then present the toast
//                            inputMethodManager.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
//                            Log.i(TAG, "Incorrect Password " + userInput);
//                            Toast.makeText(LoginDeviceLocator.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
//                        }else{
//                            Log.i(TAG, "Correct Password " + userInput);
//                            Intent intent = new Intent(LoginDeviceLocator.this, UserMapsActivity.class);
//                            for (DataSnapshot data:dataSnapshot.getChildren()) {
//                                if(data.getValue().toString().equalsIgnoreCase("Longitude")){
//                                   String [] newStrings = data.getValue().toString().split(":");
//                                }
//                            }
//                            intent.putExtra("Longitutde","Some Location");
//                            startActivity(intent);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
            }
        });
    }

    private void findViews() {
        deviceLocatorLogin        = (Button) findViewById(R.id.device_login_button);
        deviceLocatorPassword     = (EditText) findViewById(R.id.device_edittext_password);
        deviceLocatorInstructions = (TextView) findViewById(R.id.device_login_instructions);
    }
}
