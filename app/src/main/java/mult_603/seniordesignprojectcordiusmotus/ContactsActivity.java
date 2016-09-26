package mult_603.seniordesignprojectcordiusmotus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ContactsActivity extends AppCompatActivity {

    TextInputRow emergencyContact,emergencyContactTwo,emergencyContactThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        findViews();
    }

    private void findViews() {
        emergencyContact = (TextInputRow) findViewById(R.id.emergency_contact_one);
        emergencyContactTwo = (TextInputRow) findViewById(R.id.emergency_contact_two);
        emergencyContactThree = (TextInputRow) findViewById(R.id.emergency_contact_three);
    }
}
