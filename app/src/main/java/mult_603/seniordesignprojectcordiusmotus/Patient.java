package mult_603.seniordesignprojectcordiusmotus;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;

/**
 * Created by Wes on 10/1/16.
 */
public class Patient implements User {

    public String patientUserName;
    private double heartRate;
    private double temperature;
    private String uuid;
    public static final String TAG = Patient.class.getSimpleName();

    public Patient(){

    }

    public double getHeartRate(){
        return heartRate;
    }

    public double getTemperature(){
        return temperature;
    }

    public String getPatientUserName(){
        return patientUserName;
    }

    public String getUuid(){
        return uuid;
    }


    // Add a patient object to the Firebase Database using a string reference
    public void addPatientToDatabase(Patient patient, String reference){
        FirebaseDatabase fireDb = FirebaseDatabase.getInstance();
        DatabaseReference ref = fireDb.getReference(reference);
        ref.setValue(patient);

        // What if we could call this whenever the users position changes?
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String data = dataSnapshot.getValue(String.class);
                Log.i(TAG, "Persons data has changed: " + data);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "Persons data change was cancelled");
            }
        });
    }

    public void setHeartRate(double hr){
        this.heartRate = hr;
    }

    public void setTemperature(double temp){
        this.temperature = temp;
    }

    public void setPatientUserName(String uName){
        this.patientUserName = uName;
    }

    public void setUuid(String id){
        this.uuid = id;
    }

    @Override
    public String toString(){
        String str = patientUserName + " " + heartRate + " " + temperature + " ";
        return str;
    }

}
