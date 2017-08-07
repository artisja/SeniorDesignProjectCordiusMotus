package mult_603.seniordesignprojectcordiusmotus;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class UserContactConfirmRequest extends Fragment {
    private FirebaseUser currentUser;
    private Button confirmButton;
    private EditText phoneNumberEdit;
    private EditText hashEdit;
    private UserTypes selectedType;
    private DatabaseReference dbReference;
    private View view;

    public UserContactConfirmRequest() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_user_contact_confirm_request, container, false);
        setUpViews();
        SetOnClicklistener();
        return view;
    }

    private void SetOnClicklistener() {
     confirmButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             dbReference = FirebaseDatabase.getInstance().getReference("Universal Contact List").child(phoneNumberEdit.getText().toString()).child(hashEdit.getText().toString());
             dbReference.child("approved").setValue(true);
             currentUser = FirebaseAuth.getInstance().getCurrentUser();
             DatabaseReference approveCurrentRef = FirebaseDatabase.getInstance().getReference(currentUser.getUid().toString()).child("CurrentUser");
             approveCurrentRef.child("approved").setValue(true);
             //add to user dictionary
             approveCurrentRef = FirebaseDatabase.getInstance().getReference("UserDictionary").child("PATIENT").child(hashEdit.getText().toString()).child("approved");
             approveCurrentRef.setValue(true);
             DatabaseReference changeRefData = FirebaseDatabase.getInstance().getReference();

             Toast.makeText(getContext(), "You are confirmed as Contact", Toast.LENGTH_LONG).show();
         }
     });

        //add contact info to database
        //change corresponding user to approve
    }

    private void setUpViews() {
        phoneNumberEdit = (EditText) view.findViewById(R.id.edit_confirm_number);
        hashEdit = (EditText) view.findViewById(R.id.edit_confirm_hash);
        confirmButton = (Button) view.findViewById(R.id.confirm_contact_button);
    }


}
