package mult_603.seniordesignprojectcordiusmotus;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 *  The Icons used in this Map activity come from flaticon.com
 *  The Temperature icon was made by EpicCoders in Tools and Utensils
 *  The location icon wass made by Freepik in Maps and Flags
 *  The people icon was made by Freepik in People
 *  The heart icon was made by Freepik in Interface
 */

public class UserMapsActivity extends FragmentActivity implements OnMapReadyCallback,
        OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener , ActivityCompat.OnRequestPermissionsResultCallback{

    private GoogleMap mMap;
    private RelativeLayout personFrameLayout;
    private TextView personText;
    private TextView personHeartRate;
    private TextView personLocation;
    private static final int REQUEST_LOCATION = 2;
    private final static int CONNETION_TIMEOUT = 5000;
    public static final String TAG = UserMapsActivity.class.getSimpleName();
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    public Location location;
    public LatLng currentPosition;
    public double latitude;
    public double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_maps);

        // Set up the api client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create location request the time interval directly affects power usage and we want the highest accuracy
        // Priority high accuracy paired with fine location in the manifest file can find a users location within
        // the accuracy of a few feet
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(5 * 1000)  // 5 seconds
                .setInterval(10 * 1000);        // 10 second

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Set up the parts of the relative layout on the map
        personFrameLayout = (RelativeLayout) findViewById(R.id.person_fragment);
        personText = (TextView) findViewById(R.id.person_text);
        personHeartRate = (TextView) findViewById(R.id.person_heart_rate);
        personLocation = (TextView) findViewById(R.id.person_location);

        // Make the relative layout invisible
        personFrameLayout.setVisibility(View.GONE);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Set Map Click Listener
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(personFrameLayout.getVisibility() == View.VISIBLE){
                    personFrameLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    public void setUpMapMarkerByLocation(final LatLng location){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location)
                .title("My Current Location!")
                .position(location);
        mMap.addMarker(markerOptions);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Geocoder gc = new Geocoder(getApplicationContext(), Locale.getDefault());
                LatLng currentLatLng = marker.getPosition();
                Address currentAddress = new Address(Locale.getDefault());
                String streetAddress =  new String();
                String cityAddress =    new String();
                String countryAddress = new String();

                try {
                    List<Address> addressList = gc.getFromLocation(currentLatLng.latitude, currentLatLng.longitude, 1);
                    for(int i = 0; i < addressList.size(); i++){
                        currentAddress = addressList.get(i);
                        streetAddress = currentAddress.getAddressLine(0);
                        cityAddress = currentAddress.getAddressLine(1);
                        countryAddress = currentAddress.getAddressLine(2);
                        Log.i(TAG, i + ". " + streetAddress + " " + cityAddress + " " + countryAddress);
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }

                marker.showInfoWindow();
                String locationString = "Location (" + currentLatLng.latitude + " , " + currentLatLng.longitude + ")\n"
                        + "Street: " + streetAddress + "\n"
                        + "City: " + cityAddress + " , "
                        + countryAddress;

                personFrameLayout.setBackgroundColor(Color.BLACK);
                personText.setText("Person: This is me");
                personText.setTextColor(Color.WHITE);
                personHeartRate.setText("Heart Rate: Excellent");
                personHeartRate.setTextColor(Color.WHITE);
                personLocation.setText(locationString);
                personLocation.setTextColor(Color.WHITE);

                // Make the relative layout visible
                personFrameLayout.setVisibility(View.VISIBLE);
                return true;
            }
        });
        try {
            mMap.setMyLocationEnabled(true);
        }catch(SecurityException se){
            Log.i(TAG, "Security Exception: " + se.getLocalizedMessage());
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
    }

    // Used if the user leaves the app and comes back
    @Override
    protected void onResume(){
        super.onResume();
        googleApiClient.connect();
        Log.i(TAG, "On Resume was called");
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(googleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
            googleApiClient.disconnect();
            Log.i(TAG, "Disconnected in on Pause");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection Failed");
        if(connectionResult.hasResolution()){
            // Try to resolve the connection failure
            try {
                connectionResult.startResolutionForResult(this, CONNETION_TIMEOUT);
                Log.i(TAG, "Connection failed attempting to resolve");
            }
            catch (IntentSender.SendIntentException si){
                si.printStackTrace();
            }
        }
        else{
            Log.i(TAG, "CONNECTION FAILED ERROR: " + connectionResult.getErrorMessage()
                    + " Error Code: " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connected");
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Log.i(TAG, "DID show the request permission rationale");
            }
            else{
                Log.i(TAG, "DID NOT show the request permission rationale");
            }
        }
        else{
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                Intent intent = getIntent();
                latitude = intent.getDoubleExtra("Longitude",lastLocation.getLatitude());
                longitude = intent.getDoubleExtra("Latitude",lastLocation.getLongitude());
                currentPosition = new LatLng(latitude, longitude);
                setUpMapMarkerByLocation(currentPosition);
                Log.i(TAG, "( Latitude: " + latitude + " Longitude: " + longitude + " )");
            } else {
                Log.i(TAG, "Location was null");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                       String[] permissions,
                                       int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if(grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the location API
                Log.i(TAG, "On Request Permission Results -> Permission Granted");
            }
            else {
                // Permission may have been denied
                Log.i(TAG, "On Request Permission Results -> Permission Denied");
            }
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended. Reconnect");
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState){
        super.onSaveInstanceState(saveInstanceState);
        //Save if the map is requesting updates boolean
        //Save the latitude and longitude
        //Save the last time the location was updated
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLatitude();
        Log.i(TAG, "Location has changed to: " + location);
    }
}
