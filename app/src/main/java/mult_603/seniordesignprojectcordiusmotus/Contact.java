package mult_603.seniordesignprojectcordiusmotus;

/**
 * Created by artisja on 10/1/2016.
 */
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Contact {

    private String name,number, email;
    public final String TAG = Contact.class.getSimpleName();

    public Contact(String newName,String newNumber, String newEmail){
        name = newName;
        number = newNumber;
        email = newEmail;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getEmail(){
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void addContactToDatabase(Contact contact, String reference){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference(reference);
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
