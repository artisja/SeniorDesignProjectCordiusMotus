package mult_603.seniordesignprojectcordiusmotus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tapadoo.alerter.Alerter;

/**
 * Created by Wes on 3/13/17.
 */
public class UserAddContactFragment extends Fragment {
    private static final String TAG = UserAddContactFragment.class.getSimpleName();
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private EditText contactName;
    private EditText contactPhone;
    private EditText contactEmail;
    private FloatingActionButton fab;
    private DatabaseReference dbref;
    private FirebaseUser currentUser;
    private View view;

    // Blank public constructor
    public UserAddContactFragment(){

    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.activity_contact_simple, container, false);

        // May want to add email address to contacts
        contactName       = (EditText) view.findViewById(R.id.contact_simple_name);
        contactPhone      = (EditText) view.findViewById(R.id.contact_simple_phone);
        contactEmail      = (EditText) view.findViewById(R.id.contact_simple_email);
        fab = (FloatingActionButton)   view.findViewById(R.id.add_button);

        // Get database reference and authentication reference
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth     = FirebaseAuth.getInstance();

        // Get the current user and their part of the database
        currentUser = firebaseAuth.getCurrentUser();
        dbref  = firebaseDatabase.getReference(currentUser.getUid());


        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the text from the input fields to send to the database
                String name  = contactName.getText().toString().trim();
                String phone = contactPhone.getText().toString().trim();
                String email = contactEmail.getText().toString().trim();

                // Set database value if the information in the text fields is not empty
                if( !(name.isEmpty() && phone.isEmpty() && email.isEmpty())) {
                    Log.i(TAG, "Name , Email, Phone Number is not empty");
                    Contact newContact = new Contact(name, phone, email);
                    dbref.child("Contacts").push().setValue(newContact);

                    // Try to send a message to the current users emergency contact
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        String message = "You have been added as an emergency contact for User "
                                + currentUser.getEmail()
                                + "\n This is your UUID for the device location -> "
                                + currentUser.getUid();

                        smsManager.sendTextMessage(phone, null,message, null, null);
                        Intent sendMessageIntent = new Intent(Intent.ACTION_VIEW);
                        sendMessageIntent.setType("vnd.android-dir/mms-sms");

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/html");
                        intent.putExtra(Intent.EXTRA_EMAIL,"artisja@vcu.edu");
                        intent.putExtra(Intent.EXTRA_SUBJECT,"Cordis Motus Emergency Contact");
                        intent.putExtra(Intent.EXTRA_TEXT,message);
                        startActivity(Intent.createChooser(intent,"Send Email"));
                    }catch (Exception e){
                        e.printStackTrace();
                        Alerter.create(getActivity())
                                .setTitle("Error Occured")
                                .setText("Error: " + e.getMessage())
                                .setBackgroundColor(R.color.colorPrimaryDark)
                                .enableIconPulse(true)
                                .show();
                    }

//                    // Present a message letting the user know that the contact was added to the database
//                    Alerter.create(ContactSimpleActivity.this)
//                            .setIcon(R.drawable.account_black_48)
//                            .setBackgroundColor(R.color.colorPrimaryDark)
//                            .setTitle("Contact Added")
//                            .setText("Contact with name: " + name + " has been added.")
//                            .enableIconPulse(true)
//                            .show();
                }
                // Name, Email or Phone number is empty
                else{
                    Log.i(TAG, "Name , Email, Phone Number is empty");

                    Alerter.create(getActivity())
                            .setTitle("Error Occured")
                            .setText("Name, Phone Number or Email is empty for the current contact.")
                            .setBackgroundColor(R.color.colorPrimaryDark)
                            .enableIconPulse(true)
                            .show();
                }

                // Set all the text fields back to empty so the user can add other contacts
                contactName.setText("");
                contactPhone.setText("");
                contactEmail.setText("");
            }
        });

        return view;
    }

}
