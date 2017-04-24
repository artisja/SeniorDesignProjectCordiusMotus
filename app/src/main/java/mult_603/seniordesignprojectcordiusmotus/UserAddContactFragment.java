package mult_603.seniordesignprojectcordiusmotus;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tapadoo.alerter.Alerter;
import com.tapadoo.alerter.OnHideAlertListener;
import com.twilio.client.Twilio;
import com.twilio.client.TwilioClientService;

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
    private InputMethodManager inputMethodManager;
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

        // hide the keyboard
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);


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
                final String name  = contactName.getText().toString().trim();
                final String phone = contactPhone.getText().toString().trim();
                final String email = contactEmail.getText().toString().trim();

                // Set database value if the information in the text fields is not empty
                if((!name.isEmpty() && !phone.isEmpty() && !email.isEmpty())) {
                    Log.i(TAG, "Name , Email, Phone Number is not empty");
                    Contact newContact = new Contact(name, phone, email);
                    dbref.child("Contacts").push().setValue(newContact);

                    // Present a message letting the user know that the contact was added to the database
                    Alerter.create(getActivity())
                            .setIcon(R.drawable.account_black_48)
                            .setBackgroundColor(R.color.colorPrimaryDark)
                            .setTitle("Contact Added")
                            .setText("Contact \nName: " + name
                                    + "\nPhone: " + phone
                                    + "\nEmail: " + email
                                    + " has been added.")
                            .enableIconPulse(true)
                            .setOnHideListener(new OnHideAlertListener() {
                                @Override
                                public void onHide() {
                                    // When the alert goes away fire the email and text
                                    getDeviceUserShortHash(phone, email);
                                }
                            })
                            .show();
                }
                // Name, Email or Phone number is empty
                else if(name.isEmpty() || phone.isEmpty() || email.isEmpty()){
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

    // Get the users short hash code and send it to the emergency contact
    private void getDeviceUserShortHash(String phone, String email){
        final String userPhone = phone;
        final String userEmail = email;

        // Get the short hash from the database
        DatabaseReference firebaseUserRef = firebaseDatabase.getReference("UserDictionary");
        firebaseUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnap: dataSnapshot.getChildren()){
                    Log.i(TAG, "DataSnapshot child " + dataSnap);

                    final DeviceUser deviceUser = dataSnap.getValue(DeviceUser.class);
                    Log.i(TAG, "Device User -> " + deviceUser);

                    if(deviceUser.getUuid().equals(currentUser.getUid())){
                        Log.i(TAG, "Device user uuid == current user uuid");

                        // Get the short hash and send it to the emergency contact via email or phone
                        String shortHash = deviceUser.getShortHash();
                        LocationHolder locationHolder = LocationService.getLocationHolder();
                        String lat = Double.toString(locationHolder.getLatitude());
                        String lng = Double.toString(locationHolder.getLongitude());

                        Log.i(TAG, "Location Holder from Service: " + locationHolder.toString());
                        sendNotificationToContact(userPhone, userEmail, shortHash, lat, lng);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "Database Error: " + databaseError.getMessage());
            }
        });
    }

    // Old way that involved using firebase messaging api
    // Notify the emergency contact
    private void sendNotificationToContact(String phone, String email, String shortHash, String latitude, String longitude){
        // Try to send a message to the current users emergency contact
        try {
            SmsManager smsManager = SmsManager.getDefault();
            String message = "You have been added as an emergency contact for User "
                    + email
                    + "\n This is your UUID for the user's location -> "
                    + shortHash;
            String uri = "http://maps.google.com/maps?saddr=" + latitude + "," + longitude;

            Intent sendMessageIntent = new Intent(Intent.ACTION_SEND);
            StringBuffer buffer = new StringBuffer();
            buffer.append(Uri.parse(uri));
            message += buffer;
            sendMessageIntent.setType("vnd.android-dir/mms-sms");
            smsManager.sendTextMessage(phone, null, message, null, null);
            startActivity(sendMessageIntent);

//            Intent emailIntent = new Intent(Intent.ACTION_SEND);
//            emailIntent.setType("text/html");
//            emailIntent.putExtra(Intent.EXTRA_EMAIL,"artisja@vcu.edu");
//            emailIntent.putExtra(Intent.EXTRA_SUBJECT,"Cardian Emergency Contact");
//            emailIntent.putExtra(Intent.EXTRA_TEXT, message);
//            startActivity(Intent.createChooser(emailIntent,"Send Email"));

        }catch (Exception e){
            e.printStackTrace();
            Alerter.create(getActivity())
                    .setTitle("Error Occurred")
                    .setText("Error: " + e.getMessage())
                    .setBackgroundColor(R.color.colorPrimaryDark)
                    .enableIconPulse(true)
                    .show();
        }
    }

    // TODO going into production we need to create our own server with this

    private void sendSMSToContact(String phone, String email, String shortHash){
        final String ACCOUNT_SID = "AC49b7f3bab850868ca8733f927dd8a95c";
        final String AUTH_TOKEN = "your_auth_token";


    }

}
