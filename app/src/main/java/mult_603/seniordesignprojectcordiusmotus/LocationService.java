package mult_603.seniordesignprojectcordiusmotus;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.location.Location;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * Created by Wes on 1/21/17.
 */


public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    public static final String TAG = "LocationService";
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private FirebaseDatabase database;
    private LocationManager locationManager;
    public static LocationHolder locationHolder;

    @Override
    public void onCreate(){
        super.onCreate();
        createGoogleAPIClient();
        Log.i(TAG, "On Create");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        // If the google API Client is not connected then connect it
        if(!googleApiClient.isConnected()){
            googleApiClient.connect();
        }
        // Tells the service to run until we call stopSelf
//        return START_STICKY;

        // This will make the service stop when application is terminated
        return START_NOT_STICKY;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "On Connected");
        // May throw a security exception if the user did not give us permission to access the location
        try {
            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if(location != null){
                Log.i(TAG, "Location Lat: " + location.getLatitude());
                Log.i(TAG, "Location Lng: " + location.getLongitude());
            }

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Is GPS enabled
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                Log.i(TAG, "GPS is enabled");
            }
            // Is Network enabled
            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                Log.i(TAG, "Network Provider is enabled");
            }

        }
        catch(SecurityException e){
            Log.i(TAG, "Security Exception was thrown");
            e.printStackTrace();
        }

        // Start updating the location
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "On Connection Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "On Connection Failed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }


    // Stop threads and unregister receivers
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i(TAG, "Location Service has been destroyed");
        stopLocationUpdates();
        stopSelf();
    }

    public void startLocationUpdates(){
        // High accuracy should use GPS to get the location
        locationRequest = new LocationRequest();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setSmallestDisplacement(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // The user has not given us access to get their location
            // May want to request those premissions here
            try {
                int gpsOff = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
                // If this returns zero then GPS is off
                if(gpsOff == 0){
                    Intent turnOnGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(turnOnGPS);
                }
            }catch (Settings.SettingNotFoundException e){
                e.printStackTrace();
            }

            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    public void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    protected synchronized void createGoogleAPIClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public static LocationHolder getLocationHolder(){
        return locationHolder;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "Location Has Changed");
        try {
            Location l = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            Log.i(TAG, "New Location Lat: " + l.getLatitude() + " Lng: " + l.getLongitude());
        }
        catch (SecurityException e){
            Log.i(TAG, "Security Exception Thrown");
            e.printStackTrace();
        }

        Double lat = location.getLatitude();
        Double lng = location.getLongitude();
        locationHolder = new LocationHolder(lat,lng);
        String locationString = locationHolder.toString();
        Log.i(TAG, locationString);

        // Get the instance of the current user
        FirebaseUser currentUser =  FirebaseAuth.getInstance().getCurrentUser();

        // If the current user is not null then we post their location in the database under their unique identifier
        if (currentUser != null) {
            database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference(currentUser.getUid());

            // Checking that the location gets added. We could make the location an object and store it that way
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i(TAG, "Data Changed in Database ");
                    Log.i(TAG, "Reference " + dataSnapshot.getRef());
                    Log.i(TAG, "Key " + dataSnapshot.getKey());
                    Log.i(TAG, "Value " + dataSnapshot.getValue());
                    Log.i(TAG, "Children Count " + dataSnapshot.getChildrenCount());
                    Log.i(TAG, "Children " + dataSnapshot.getChildren());

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Log.i(TAG, "Child " + child);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            String key = reference.getKey();
            Log.i(TAG, "Key " + key);
            reference.child("Location").setValue(locationHolder);

        }
    }
}

