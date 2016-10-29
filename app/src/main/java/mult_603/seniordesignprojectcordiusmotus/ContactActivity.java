package mult_603.seniordesignprojectcordiusmotus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity {
    public final String TAG = ContactActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    public static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button addButton;
    private Button submitButton;
    public static ArrayList<Contact> contactsArray;
    private FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        contactsArray = new ArrayList<Contact>();
        contactsArray.add(new Contact("Example","0898709678", "someone@somewhere.com"));
        findViews();
        setUpClickListener();
    }

    private void setUpClickListener() {

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contactsArray.size()<3){
                    Contact contact = new Contact(null,null,null);
                    contactsArray.add(contact);
                notify();
                }else{
                    Toast.makeText(ContactActivity.this, "No more than three contacts", Toast.LENGTH_SHORT).show();
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactsArray.get(0).setUuid("Pokemon");
                addContactToDatabase(contactsArray,contactsArray.get(0).getUuid());

                Toast.makeText(ContactActivity.this, "Contacts Uploaded", Toast.LENGTH_SHORT).show();
                Intent goingHomeintent = new Intent(ContactActivity.this,UserDefinitionActivity.class);
                startActivity(goingHomeintent);

//                Intent contactListIntent = new Intent(ContactActivity.this, ContactListActivity.class);
//                startActivity(contactListIntent);
            }
        });
    }

    private void findViews() {
        recyclerView = (RecyclerView) findViewById(R.id.contact_recycler_view);
        layoutManager = new LinearLayoutManager(this);
       // adapter = new ContactRecyclerAdapter(contactsArray);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        addButton = (Button) findViewById(R.id.add_button);
        submitButton = (Button) findViewById(R.id.submit_all_button);
    }

    public void addContactToDatabase(ArrayList<Contact> contact, String reference){
        final ArrayList<Contact> contacts = contact;
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference(reference);

        // Attaching a value listener to see what is being sent to the database
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(Contact c: contacts){
                    Log.i(TAG, "Contact "  + c.toString());
                }
                Log.i(TAG, "Data Snapshot key " + dataSnapshot.getKey());
                Log.i(TAG, "Data Snapshot to string " + dataSnapshot.toString());
                Log.i(TAG, "Data Snapshot get children count " + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "Database addition was cancelled");
            }
        });

        ref.setValue(contact);
    }
}
