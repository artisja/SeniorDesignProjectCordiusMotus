package mult_603.seniordesignprojectcordiusmotus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.telephony.SmsManager;
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

/**
 * Created by Wes on 10/13/16.
 */
public class ContactListAdapter extends BaseAdapter implements ListAdapter {
    public final String          TAG = ContactListAdapter.class.getSimpleName();
    private ArrayList<Contact>   contactArrayList;
    private Context              context;
    private FirebaseUser         currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference    reference = FirebaseDatabase.getInstance().getReference(currentUser.getUid());
    private ArrayList<String>    keyList;

    public ContactListAdapter(ArrayList<Contact> contacts, Context c){
        keyList = new ArrayList<>();
        contactArrayList = contacts;
        context = c;

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Log.i(TAG, "Data Snapshot Key   " + ds.getKey());
                    Log.i(TAG, "Data Snapshot Ref   "   + ds.getRef());
                    Log.i(TAG, "Data Snapshot Value " + ds.getValue());

                    // If the item is not in the list then add it
                    if(!(keyList.contains(ds.getKey()))) {
                        keyList.add(ds.getKey());
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
                        // Get the key for the contact from the database
                        String key = keyList.get(currentPosition);

                        Contact contactToRemove = contactArrayList.get(currentPosition);
                        String contactToRemovePhone = contactToRemove.getNumber();
                        String contactToRemoveUName = contactToRemove.getName();
                        String contactToRemoveEmail = contactToRemove.getEmail();

                        Log.i(TAG, "Reference " + reference.getKey());
                        Log.i(TAG, "Key to Delete       " + key);
                        Log.i(TAG, "Reference to Delete " + reference.child(key));

                        SmsManager smsManager = SmsManager.getDefault();

                        // Create the text message string
                        String message = "You have been Removed as an emergency contact for User Name: "
                                + contactToRemoveUName
                                + " with Email Address: "
                                + contactToRemoveEmail;

                        smsManager.sendTextMessage(contactToRemovePhone, null,message, null, null);
                        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                        sendIntent.putExtra("sms_body", message);
                        sendIntent.setType("vnd.android-dir/mms-sms");
                        context.startActivity(sendIntent);

                        // Remove contact from the array list and remove the key from the list of keys
                        contactArrayList.remove(currentPosition);
                        keyList.remove(currentPosition);


                        // Notify the data set has changed
                        notifyDataSetChanged();

                        // Remove the item from the database
                        reference.child(key).removeValue();

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

        return view;
    }
}