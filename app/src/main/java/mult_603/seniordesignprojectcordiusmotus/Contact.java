package mult_603.seniordesignprojectcordiusmotus;

/**
 * Created by artisja on 10/1/2016.
 */

public class Contact {

    private String name,number;

    public Contact(String newName,String newNumber){
        name = newName;
        number = newNumber;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

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
