package mult_603.seniordesignprojectcordiusmotus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Wes on 10/13/16.
 */
public class ContactListAdapter extends BaseAdapter implements ListAdapter {
    public final String TAG = ContactListAdapter.class.getSimpleName();
    private ArrayList<Contact> contactArrayList;
    private Map<String, Object> contactMap;
    private Map<Contact, String> contactBackwardsMap;
    private Context context;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference(currentUser.getUid());

    public ContactListAdapter(ArrayList<Contact> contacts, Context c){
        contactArrayList = contacts;
        context = c;
        contactMap = new HashMap<>();
        contactBackwardsMap = new HashMap<>();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String name = (String) ds.child("Name").getValue();
                    String phone = (String) ds.child("Phone").getValue();
                    String email = (String) ds.child("Email").getValue();
                    Contact contact = new Contact(name, phone, email);
                    String key = ds.getKey();

                    Log.i(TAG, "Contact's Key in DB "  + key);
                    Log.i(TAG, "Contact value for key " + contact.toString());

                    // Put contact in the hash map
                    if(key != null && contact != null) {
                        contactMap.put(key, contact);
                        contactBackwardsMap.put(contact, key);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "On Cancelled " + databaseError.getDetails());
            }
        });
    }

    @Override
    public int getCount() {
        return contactArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        Log.i(TAG, "Contact at position: " + position + " Contact: " + contactArrayList.get(position).toString());
        return contactArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        View view = convertView;

        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.contact_row_item, null);
        }

        // Get the contact from the array list
        final int currentPosition = position;
        final Contact contact = contactArrayList.get(position);

        // Set up the views
        TextView contactName = (TextView) view.findViewById(R.id.contact_list_name);
        contactName.setText(contact.getName());

        TextView contactPhone = (TextView) view.findViewById(R.id.contact_list_phone);
        contactPhone.setText(contact.getNumber());

        TextView contactEmail = (TextView) view.findViewById(R.id.contact_list_email);
        contactEmail.setText(contact.getEmail());

        final Button removeContactButton = (Button) view.findViewById(R.id.contact_list_remove_button);
        removeContactButton.setText("Remove");



        final AlertDialog confirmationDialog = new AlertDialog.Builder(context)
                .setTitle("Remove Contact")
                .setMessage("Are you sure you want to delete this contact?")
                // The positive no button does nothing
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "Do NOT Delete this contact: " + contact.toString());
                        dialog.dismiss();

                    }
                })
                // Set the negative button to remove the item from the database
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Need to find the key based on the value
                        Contact c = contactArrayList.get(currentPosition);
                        Log.i(TAG, "Contact c " + c.toString());

                        String contactToDeleteKey = contactBackwardsMap.get(c);
                        Log.i(TAG, "Contact to DELETE key " + contactToDeleteKey);

                        // Remove item from the array list and database
                        contactArrayList.remove(currentPosition);

                        notifyDataSetChanged();

                        String contactKey = "Contact" + currentPosition;


//                        reference.child(contactToDeleteKey).setValue(contactArrayList.get(currentPosition));

                        // Make a map for updating the keys
                        for(int i = 0; i < contactArrayList.size() - 1; i++){
                            String contactStringKey = "Contact" + i;
                            Contact contact1 = contactArrayList.get(i);
                            Log.i(TAG, "Map ");
                            Log.i(TAG, "Contact String Key " + contactStringKey);
                            Log.i(TAG, "Contact at position " + i + " Contact " + contact1.toString());

                            if (contact.equals(null)){
                                contactMap.remove(contactKey);
                            }
                            else {
                                contactMap.put(contactStringKey, contact1);
                            }
                        }
                        reference.updateChildren(contactMap);

                        // Dismiss the dialog box
                        dialog.dismiss();

                    }
                }).create();



        removeContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.show();
            }
        });

        notifyDataSetChanged();

        return view;
    }
}