package mult_603.seniordesignprojectcordiusmotus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class ContactListActivity extends AppCompatActivity {
    public final String TAG = ContactListActivity.class.getSimpleName();
    private ListView contactListView;
    private TextView contactName;
    private TextView contactPhone;
    private TextView contactEmail;
    private Button   removeButton;
    private FirebaseDatabase   firebaseDatabase;
    private FirebaseAuth       firebaseAuth;
    private ContactListAdapter contactListAdapter;
    private ArrayList<Contact> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        findViews();

        // Get the users portion of the database using their uuid
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        final DatabaseReference databaseReference = firebaseDatabase.getReference(currentUser.getUid());

        Log.i(TAG, "Current User "       + currentUser);
        Log.i(TAG, "Current User Email " + currentUser.getEmail());
        Log.i(TAG, "Current User UUID "  + currentUser.getUid());
        Log.i(TAG, "Current User User Name " + currentUser.getDisplayName());
        Log.i(TAG, "Database Key " + databaseReference.getKey());

        // Set the contact List Adapter
        contactListAdapter = new ContactListAdapter(contactList, ContactListActivity.this);
        contactListView.setAdapter(contactListAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot contact: dataSnapshot.getChildren()){
                    // Get the key of the child
                    String key = contact.getKey();
                    Log.i(TAG, "Contact Database Key : " + key);
                    Contact newContact = contact.getValue(Contact.class);
                    Log.i(TAG, "Contact From Database " + newContact.toString());

                    // If the contact is not contained in the array list then add it
                    if(!contactList.contains(newContact)) {
                        contactList.add(newContact);
                    }

                    // Notify the adapter that the contacl list has changed
                    contactListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "Database Error occurred " + databaseError.getDetails());
            }
        });


    }

    public void findViews(){
        contactListView   = (ListView) findViewById(R.id.contact_list);
        contactName       = (TextView) findViewById(R.id.contact_list_name);
        contactPhone      = (TextView) findViewById(R.id.contact_list_phone);
        contactEmail      = (TextView) findViewById(R.id.contact_list_email);
        removeButton      = (Button) findViewById(R.id.contact_list_remove_button);
        firebaseDatabase  = FirebaseDatabase.getInstance();
        firebaseAuth      = FirebaseAuth.getInstance();
        contactList       = new ArrayList<>();
    }
}
