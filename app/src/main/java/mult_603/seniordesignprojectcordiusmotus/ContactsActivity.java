package mult_603.seniordesignprojectcordiusmotus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ContactsActivity extends AppCompatActivity {

    TextInputRow emergencyContact,emergencyContactTwo,emergencyContactThree;
    Button submitContactButton;
    ContactsList contactsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        findViews();
        buttonActions();
        contactsList = new ContactsList();
    }

    private void buttonActions() {
        submitContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emergencyContact.switchViews();
                emergencyContactTwo.switchViews();
                emergencyContactThree.switchViews();
                submitContactButton.setVisibility(View.GONE);

            }
        });
    }

    private void findViews() {
        emergencyContact = (TextInputRow) findViewById(R.id.emergency_contact_one);
        emergencyContactTwo = (TextInputRow) findViewById(R.id.emergency_contact_two);
        emergencyContactThree = (TextInputRow) findViewById(R.id.emergency_contact_three);
        submitContactButton = (Button) findViewById(R.id.submit_contacts_button);
    }
}
