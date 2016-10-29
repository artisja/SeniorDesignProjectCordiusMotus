package mult_603.seniordesignprojectcordiusmotus;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ContactSimpleActivity extends AppCompatActivity {
    public final String       TAG = ContactSimpleActivity.class.getSimpleName();
    private String            contactStringKey;
    private FirebaseDatabase  firebaseDatabase;
    private FirebaseAuth      firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_simple);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // May want to add email address to contacts
        final EditText contactName       = (EditText) findViewById(R.id.contact_simple_name);
        final EditText contactPhone      = (EditText) findViewById(R.id.contact_simple_phone);
        final EditText contactEmail      = (EditText) findViewById(R.id.contact_simple_email);
        final Button   contactListButton = (Button)   findViewById(R.id.contact_simple_list_button);

        // Get database reference and authentication reference
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth     = FirebaseAuth.getInstance();

        // Get the current user and their part of the database
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        final DatabaseReference dbref  = firebaseDatabase.getReference(currentUser.getUid());


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_button);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the text from the input fields to send to the database
                String name  = contactName.getText().toString();
                String phone = contactPhone.getText().toString();
                String email = contactEmail.getText().toString();

                // Set database value if the information in the text fields is not empty
                if( !(name.isEmpty() || phone.isEmpty() || email.isEmpty())) {
                    Log.i(TAG, "Name , Email, Phone Number is not empty");
                    Contact newContact = new Contact(name, phone, email);
                    dbref.push().setValue(newContact);

                    // Present a message letting the user know that the contact was added to the database
                    Snackbar snackbar = Snackbar.make(view, "Contact Added", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                // Name, Email or Phone number is empty
                else{
                    Log.i(TAG, "Name , Email, Phone Number is empty");
                    Snackbar snackbar = Snackbar.make(view, "Name, Phone, or Emal is Empty", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }

                // Set all the text fields back to empty so the user can add other contacts
                contactName.setText("");
                contactPhone.setText("");
                contactEmail.setText("");
            }
        });

        contactListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start contact list activity
                Intent contactList = new Intent(ContactSimpleActivity.this, ContactListActivity.class);
                startActivity(contactList);
            }
        });
    }
}