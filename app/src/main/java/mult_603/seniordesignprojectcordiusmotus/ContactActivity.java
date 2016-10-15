package mult_603.seniordesignprojectcordiusmotus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import static mult_603.seniordesignprojectcordiusmotus.UserMapsActivity.TAG;

public class ContactActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    public static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button addButton;
    private Button submitButton;
    private ArrayList<Contact> contactsArray;
    private FirebaseDatabase firebaseDatabase;
    public static boolean isSubmitPressed =true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        contactsArray = new ArrayList<Contact>();
        contactsArray.add(new Contact("",""));
        findViews();
        setUpClickListener();

    }

    private void setUpClickListener() {

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contactsArray.size()<3){
                    Contact contact = new Contact(null,null);
                    contactsArray.add(contact);
                    adapter.notifyDataSetChanged();
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
//                Intent goingHomeintent = new Intent(ContactActivity.this,UserDefinitionActivity.class);
//                startActivity(goingHomeintent);
                Intent contactListIntent = new Intent(ContactActivity.this, ContactListActivity.class);
                startActivity(contactListIntent);
            }
        });
    }

    private void findViews() {
        recyclerView = (RecyclerView) findViewById(R.id.contact_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        adapter = new ContactRecyclerAdapter(contactsArray);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        addButton = (Button) findViewById(R.id.add_button);
        submitButton = (Button) findViewById(R.id.submit_all_button);
    }

    public void addContactToDatabase(ArrayList<Contact> contact, String reference){
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference(reference);
        ref.setValue(contact);
    }
}
