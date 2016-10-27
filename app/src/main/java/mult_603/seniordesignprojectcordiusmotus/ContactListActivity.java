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
    private Button removeButton;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
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

        Log.i(TAG, "Current User " + currentUser);
        Log.i(TAG, "Current User Email " + currentUser.getEmail());
        Log.i(TAG, "Current User UUID " + currentUser.getUid());

        String dbKey = databaseReference.getKey();
        Log.i(TAG, "Database Key " + dbKey);

        // TODO how to update these stupid keys
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for(DataSnapshot children: dataSnapshot.getChildren()){
                    String key = children.getKey();

                    Log.i(TAG, "Database Snapshot get value " + children.getValue());
                    Log.i(TAG, "Contact Key " + key);

                    String name = (String) children.child("Name").getValue();
                    String phone = (String) children.child("Phone").getValue();
                    String email = (String) children.child("Email").getValue();

                    Log.i(TAG, "Name -> " + name);
                    Log.i(TAG, "Phone -> " + phone);
                    Log.i(TAG, "Email -> " + email);

                    if(!(name == null && phone == null && email == null)) {
                        Contact contact = new Contact(name, phone, email);
                        contactList.add(contact);
                    }
                    else{
                        databaseReference.child(key).removeValue();
                    }

                }
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
        firebaseAuth = FirebaseAuth.getInstance();
        contactList = new ArrayList<>();
    }
}
