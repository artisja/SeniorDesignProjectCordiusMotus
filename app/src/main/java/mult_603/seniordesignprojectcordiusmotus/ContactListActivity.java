package mult_603.seniordesignprojectcordiusmotus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        DatabaseReference databaseReference = firebaseDatabase.getReference("Contact");

        // TODO - Get the contact items from the database
        ArrayList<Contact> contactList = new ArrayList<>();
        Contact contactForList = new Contact("Name: Buzz Killington", "Phone: (777)-777-7777");
        contactList.add(contactForList);
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
