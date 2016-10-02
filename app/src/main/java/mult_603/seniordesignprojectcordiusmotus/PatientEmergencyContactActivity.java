package mult_603.seniordesignprojectcordiusmotus;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.w3c.dom.Text;
import java.util.zip.Inflater;
import android.widget.LinearLayout.LayoutParams;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PatientEmergencyContactActivity extends AppCompatActivity {
    public static final String TAG = PatientEmergencyContactActivity.class.getSimpleName();
    private Button saveContactButton;
    private TextView contactName;
    private TextView contactPhone;
    private TextView contactEmail;
    private LinearLayout container;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_emergency_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        container = (LinearLayout) findViewById(R.id.add_contact_layout);
        LinearLayoutCompat.LayoutParams lparams = new LinearLayoutCompat
                .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);

        addButton = (Button) findViewById(R.id.add_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AddContactView view = new AddContactView(getApplicationContext(), null);
//                container.addView(view);
                Log.i(TAG, "Add button was clicked");
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View child = getLayoutInflater().inflate(R.layout.add_contact, null);
//                FrameLayout container = (FrameLayout) findViewById(R.id.container_for_contacts);
//                container.addView(child);
//                AddContactView v = new AddContactView(getApplicationContext(), null);
//                container.addView(v);
                Log.i(TAG, "Floating Action Button was pressed");
            }
        });

        contactName = (TextView) findViewById(R.id.contact_name);
        contactEmail = (TextView) findViewById(R.id.contact_email);
        contactPhone = (TextView) findViewById(R.id.contact_phone);
        saveContactButton = (Button) findViewById(R.id.contact_save_button);

        saveContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact c = new Contact();
                String name = contactName.getText().toString();
                String phone = contactPhone.getText().toString();
                String email = contactEmail.getText().toString();

                c.setContactName(name);
                c.setContactPhone(phone);
                c.setContactEmail(email);
                Log.i(TAG, "This is a contact: " + c);
//                c.addContactToDatabase(c, "Contact");

            }
        });
    }

}
