package mult_603.seniordesignprojectcordiusmotus;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by artisja on 10/1/2016.
 */

public class Contact {
    public static final String TAG = Contact.class.getSimpleName();
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private FirebaseDatabase firebaseDatabase;

    public Contact(){

    }

    public void setContactName(String name){
        this.contactName = name;
    }

    public void setContactPhone(String phone){
        this.contactPhone = phone;
    }

    public void setContactEmail(String email){
        this.contactEmail = email;
    }

    public String getContactName(){
        return contactName;
    }

    public String getContactPhone(){
        return contactPhone;
    }

    public String getContactEmail(){
        return contactEmail;
    }

    // Add a patient object to the Firebase Database using a string reference
    public void addContactToDatabase(Contact contact, String reference){
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference(reference);
        ref.setValue(contact);

        // What if we could call this whenever the users position changes?
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String data = dataSnapshot.getValue(String.class);
                Log.i(TAG, "Contacts data has changed: " + data);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "Contacts data change was cancelled");
            }
        });
    }
}
