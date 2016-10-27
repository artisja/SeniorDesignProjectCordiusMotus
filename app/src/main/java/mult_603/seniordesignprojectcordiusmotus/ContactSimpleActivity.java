package mult_603.seniordesignprojectcordiusmotus;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ContactSimpleActivity extends AppCompatActivity {
    public final String TAG = ContactSimpleActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_simple);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // May want to add email address to contacts
        final EditText contactName = (EditText) findViewById(R.id.contact_simple_name);
        final EditText contactPhone = (EditText) findViewById(R.id.contact_simple_phone);
        final EditText contactEmail = (EditText) findViewById(R.id.contact_simple_email);

        // Get database reference
        FirebaseDatabase fdb = FirebaseDatabase.getInstance();
        final DatabaseReference dbref = fdb.getReference("Contact");

        // Add value listener so we can make sure we are getting the right data in the database
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "On Data Changed ");
                Log.i(TAG, "Database Snapshot " + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "Database operation canceled");
            }
        });

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_button);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactSimpleActivity.this, ContactListActivity.class);
                String name = contactName.getText().toString();
                String phone = contactPhone.getText().toString();
                String email = contactEmail.getText().toString();
//                Contact contact1 = new Contact(name, phone, email);

                // Set database value
                dbref.child("Name").setValue(name);
                dbref.child("Phone").setValue(phone);
                dbref.child("Email").setValue(email);
//                dbref.setValue(contact1);
                try {

                    SmsManager smsManager = SmsManager.getDefault();
                    Toast.makeText(ContactSimpleActivity.this, phone, Toast.LENGTH_SHORT).show();
                    smsManager.sendTextMessage(phone, null, "Your pornhub subsricption has been sent.", null, null);
//               Intent sendIntent = new Intent(Intent.ACTION_VIEW);
//               sendIntent.putExtra("sms_body", "You got a pretty kind of dirty face.");
//               sendIntent.setType("vnd.android-dir/mms-sms");
//               startActivity(sendIntent);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(ContactSimpleActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
                // Start new activity
                Intent contactList = new Intent(ContactSimpleActivity.this, ContactListActivity.class);
                startActivity(contactList);
            }
        });
    }
}
