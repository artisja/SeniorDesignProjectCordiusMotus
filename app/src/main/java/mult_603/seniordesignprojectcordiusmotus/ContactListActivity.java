package mult_603.seniordesignprojectcordiusmotus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ContactListActivity extends AppCompatActivity {
    public final String TAG = ContactListActivity.class.getSimpleName();
    private ListView contactListView;
    private TextView contactName;
    private TextView contactPhone;
    private TextView contactEmail;
    private Button removeButton;
    private FirebaseDatabase firebaseDatabase;
    private ContactListAdapter contactListAdapter;
    private ArrayList<Contact> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        findViews();

        final DatabaseReference databaseReference = firebaseDatabase.getReference("Contact");

        String dbKey = databaseReference.getKey();
        Log.i(TAG, "Database Key " + dbKey);

        // TODO This needs to be more dynamic
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "Database Snapshot get value " + dataSnapshot.getValue());

                String name = (String) dataSnapshot.child("Name").getValue();
                String phone = (String) dataSnapshot.child("Phone").getValue();
                String email = (String) dataSnapshot.child("Email").getValue();

                Log.i(TAG, "Name -> " + name);
                Log.i(TAG, "Phone -> " + phone);
                Log.i(TAG, "Email -> " + email);
                Contact contact = new Contact(name, phone, email);
                contactList.add(contact);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "Database Error occurred " + databaseError.getDetails());
            }
        });
        contactListAdapter = new ContactListAdapter(contactList, ContactListActivity.this);
        contactListView.setAdapter(contactListAdapter);


    }

    public void findViews(){
        contactListView = (ListView) findViewById(R.id.contact_list);
        contactName = (TextView) findViewById(R.id.contact_list_name);
        contactPhone = (TextView) findViewById(R.id.contact_list_phone);
        contactEmail = (TextView) findViewById(R.id.contact_list_email);
        removeButton = (Button) findViewById(R.id.contact_list_remove_button);
        firebaseDatabase = FirebaseDatabase.getInstance();
        contactList = new ArrayList<>();
    }
}
