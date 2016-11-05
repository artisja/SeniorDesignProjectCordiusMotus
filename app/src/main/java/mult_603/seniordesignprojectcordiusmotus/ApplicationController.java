package mult_603.seniordesignprojectcordiusmotus;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static mult_603.seniordesignprojectcordiusmotus.BluetoothActivity.mHandler;

/**
 * Created by Wes on 10/31/16.
 * This class can be used to give our application global access to the users location
 * Whether or not the user is logged in, Bluetooth etc
 */
public class ApplicationController extends android.app.Application {
    public final String TAG = ApplicationController.class.getSimpleName();
    public FirebaseAuth.AuthStateListener authStateListener;
    public FirebaseAuth firebaseAuth;
    public FirebaseUser currentUser;
    public Handler bluetoothHandler;
    public UserProfileChangeRequest userProfileChangeRequest;
    private DrawerLayout userDrawerLayout;
    private ActionBarDrawerToggle actionBarToggle;
    public double longitude, latitude;
    private ConnectedThread connectedThread;
    public Patient patient;

    private static ApplicationController singleton;

    public ApplicationController getInstance(){
        return singleton;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        singleton = this;
        Log.i(TAG, "On Create was called");


        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        patient = new Patient();
        if(currentUser != null) {
            Log.i(TAG, "Current User " + currentUser);
            Log.i(TAG, "Current User Display Name "  + currentUser.getDisplayName());
            Log.i(TAG, "Current User Email " + currentUser.getEmail());
            Log.i(TAG, "Current User UUID  " + currentUser.getUid());

            // Send the user an email verification
            // This will send them an email every time do I want to do that???
//            currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if(task.isSuccessful()){
//                        Log.i(TAG, "Sent an email to " + currentUser.getEmail());
//                    }
//                }
//            });
        }

        if(firebaseAuth != null){
            Log.i(TAG, "Firebase auth is not null ");
            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    Log.i(TAG, "Firebase Auth State Listener " + firebaseAuth.getCurrentUser());

                }
            };

            // Add auth state listener
            firebaseAuth.addAuthStateListener(authStateListener);
        }
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                byte[] writeBuf = (byte[]) msg.obj;
                int begin = (int) msg.arg1;
                int end = (int) msg.arg2;


                switch (msg.what) {
                    case 1:
                        try {
                            String read = new String(writeBuf, begin, end, "UTF-8").trim();
                            Log.i(TAG, "Handler Message -> " + read);
                        }catch(Exception e){
                            Log.i(TAG, "String conversion exception " + e.getMessage());
                        }

                        break;
                }
            }
        };
        // get the location manager so we can call use latitude and logitude coordinates
        getLocationManager();

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


    public void getLocationManager() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "On Configuration Changed Called");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i(TAG, "On Loe Memory called");
    }


    private class ConnectedThread extends Thread {
        public final BluetoothSocket mmSocket;
        public final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.i(TAG, "ERROR trying to access the input stream " + e.getMessage());
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int begin = 0;
            int bytes = 0;
            while (true) {
                try {
                    bytes += mmInStream.read(buffer, bytes, buffer.length - bytes);
//                        Log.i(TAG, "Buffer Length: " + buffer.length
//                                + "\n" + "Bytes " + bytes);

                    for (int i = begin; i < bytes; i++) {
                        if (buffer[i] == " H".getBytes()[0]) {
                            // Send the heart rate data to the handler.
                            mHandler.obtainMessage(1, begin, bytes, buffer).sendToTarget();
                            if (i == bytes - 1) {
                                bytes = 0;
                                begin = 0;
                            }
                        }
                    }

//                        Log.i(TAG, "Bytes -> " + bytes);
                } catch (IOException e) {
                    Log.i(TAG, "ERROR reading information from buffer " + e.getMessage());
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.i(TAG, "ERROR writing to device " + e.getMessage());
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.i(TAG, "ERROR trying to cancel socket " + e.getMessage());
            }
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(TAG, "On Terminate Called");
        // If the auth state listener is not null then remove it from the firebase auth
        if(authStateListener != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
