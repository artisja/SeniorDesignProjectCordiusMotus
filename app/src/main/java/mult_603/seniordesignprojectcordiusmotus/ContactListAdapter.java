package mult_603.seniordesignprojectcordiusmotus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Wes on 10/13/16.
 */
public class ContactListAdapter extends BaseAdapter implements ListAdapter {
    public final String TAG = ContactListAdapter.class.getSimpleName();
    private ArrayList<Contact> contactArrayList;
    private Context context;

    public ContactListAdapter(ArrayList<Contact> contacts, Context c){
        contactArrayList = contacts;
        context = c;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final int listPosition = position;

        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.contact_row_item, null);
        }

        // Get the contact from the array list
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
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "Do NOT Delete this contact: " + contact.toString());
                        dialog.dismiss();

                    }
                })
                // TODO This probablly needs to be changed to delete specific contacts based on name etc.
                // TODO Notify the contact list that this value has been removed
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "Delete this contact " + contact.toString());
                        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Contact");
                        dRef.removeValue();
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
