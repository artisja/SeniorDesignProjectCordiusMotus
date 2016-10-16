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

import java.util.ArrayList;
import java.util.List;

public class ContactListActivity extends AppCompatActivity {
    public final String TAG = ContactListActivity.class.getSimpleName();
    private ListView contactListView;
    private TextView contactName;
    private TextView contactPhone;
    private Button removeButton;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        findViews();
        firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Contact");

        final ArrayList<Contact> contactList = new ArrayList<>();

        String dbKey = databaseReference.getKey();
        Log.i(TAG, "Database Key " + dbKey);

        // TODO Does this work ???
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "Database Snapshot get value " + dataSnapshot.getValue());

                for(DataSnapshot contactSnapshot: dataSnapshot.getChildren()){
                    Log.i(TAG, "Contact Children " + dataSnapshot.getChildrenCount());
                    Log.i(TAG, "Name Key ");
                    String name = (String) contactSnapshot.child("Name").getValue();
                    String phone = (String) contactSnapshot.child("Phone").getValue();
                    String email = (String) contactSnapshot.child("Email").getValue();
                    Log.i(TAG, "String name " + name + "\nString number " + phone + "\nString email " + email);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "Database Error occurred " + databaseError.getDetails());
            }
        });

        // TODO - Get the contact items from the database
        //Contact contactForList = new Contact("Blah", "777-777-7777");
        //contactList.add(contactForList);
        ContactListAdapter contactAdapter = new ContactListAdapter(contactList, getApplicationContext());
        contactListView.setAdapter(contactAdapter);


    }

    public void findViews(){
        contactListView = (ListView) findViewById(R.id.contact_list);
        contactName = (TextView) findViewById(R.id.contact_list_name);
        contactPhone = (TextView) findViewById(R.id.contact_list_phone);
        removeButton = (Button) findViewById(R.id.contact_list_remove_button);
    }

}
