package mult_603.seniordesignprojectcordiusmotus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

/**
 * Created by Wes on 3/13/17.
 * This Fragment is for displaying a list of emergency contacts to the user so that they can edit them if they wish
 */
public class UserContactListActivityFragment extends Fragment {
    private final String TAG = UserContactListActivityFragment.class.getSimpleName();
    private View view;
    private ListView contactListView;
    private TextView contactName;
    private TextView contactPhone;
    private TextView contactEmail;
    private Button   removeButton;
    private FirebaseDatabase   firebaseDatabase;
    private FirebaseAuth       firebaseAuth;
    private ContactListAdapter contactListAdapter;
    private ArrayList<Contact> contactList;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    private String hasher,confirmed;
    private Contact newContact;


    // Empty Public Constructor
    public UserContactListActivityFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.activity_contact_list, container, false);
        findViews(view);
        return view;
    }

    public void findViews(View view){
        contactListView   = (ListView) view.findViewById(R.id.contact_list);
        contactName       = (TextView) view.findViewById(R.id.contact_list_name);
        contactPhone      = (TextView) view.findViewById(R.id.contact_list_phone);
        contactEmail      = (TextView) view.findViewById(R.id.contact_list_email);
        removeButton      = (Button)   view.findViewById(R.id.contact_list_remove_button);
        firebaseDatabase  = FirebaseDatabase.getInstance();
        firebaseAuth      = FirebaseAuth.getInstance();
        contactList       = new ArrayList<>();

        // Get the users portion of the database using their uuid
        currentUser = firebaseAuth.getCurrentUser();
        databaseReference = firebaseDatabase.getReference(currentUser.getUid()).child("Contacts");

        getUserUUID();
        // Set the contact List Adapter
        contactListAdapter = new ContactListAdapter(contactList, view.getContext());
        contactListView.setAdapter(contactListAdapter);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot contact: dataSnapshot.getChildren()){
                    // Get the key of the child
                    String key = contact.getKey();
                    Log.i(TAG, "Contact Database Key : " + key);
                    newContact = contact.getValue(Contact.class);
                    Log.i(TAG, "Contact From Database " + newContact.toString());
                    DatabaseReference contactCheckRef= FirebaseDatabase.getInstance().getReference("Universal Contact List");
                    // If the contact is not contained in the array list then add it
                    checkContactConfirmed(newContact.getNumber().toString(),contactCheckRef);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "Database Error occurred " + databaseError.getDetails());
            }
        });
    }

    private void getUserUUID() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(currentUser.getUid().toString()).child("CurrentUser").child("shortHash");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    UserContactListActivityFragment.this.hasher = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkContactConfirmed(String phoneNumber,DatabaseReference databaseReference) {
        getUserUUID();
       databaseReference = databaseReference.child(phoneNumber).child(hasher).child("approved");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserContactListActivityFragment.this.confirmed = dataSnapshot.getValue().toString();
                if(!contactList.contains(newContact) && confirmed.equalsIgnoreCase("true")) {
                    contactList.add(newContact);
                }
                // Notify the adapter that the contacl list has changed
                contactListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
