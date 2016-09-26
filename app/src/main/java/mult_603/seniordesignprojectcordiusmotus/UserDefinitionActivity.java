package mult_603.seniordesignprojectcordiusmotus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class UserDefinitionActivity extends AppCompatActivity {

    private Button medicButton,caliButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_definition);
        findViews();
        setButtonDestination();
    }

    private void setButtonDestination() {
        caliButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserDefinitionActivity.this, "Emergency Contact/Edit screen", Toast.LENGTH_SHORT).show();
            }
        });

        medicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserDefinitionActivity.this, "Google Maps/medical data view screen", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void findViews() {
     caliButton = (Button) findViewById(R.id.cali_Button);
     medicButton = (Button) findViewById(R.id.medic_Button);
    }

}
