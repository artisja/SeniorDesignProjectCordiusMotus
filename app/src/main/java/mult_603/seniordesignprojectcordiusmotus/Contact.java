package mult_603.seniordesignprojectcordiusmotus;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static mult_603.seniordesignprojectcordiusmotus.UserMapsActivity.TAG;

/**
 * Created by artisja on 10/1/2016.
 */

public class Contact implements User{


    private String name,number,patientUuid;;

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


    @Override
    public String getUuid() {
        return patientUuid;
    }

    @Override
    public void setUuid(String id) {
        patientUuid=id;
    }
}
