package mult_603.seniordesignprojectcordiusmotus;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

public class UserMapsActivity extends FragmentActivity implements OnMapReadyCallback,
        OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener{

    private GoogleMap mMap;
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
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(10 * 1000)  // 10 seconds
                .setInterval(1 * 1000);        // 1 second

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

    }

    public void setUpMapMarkerByLocation(LatLng location){
        MarkerOptions options = new MarkerOptions();
        options.position(location)
                .title("My Current Location!")
                .position(location);
        mMap.addMarker(options);
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
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                latitude = lastLocation.getLatitude();
                longitude = lastLocation.getLongitude();
                currentPosition = new LatLng(latitude, longitude);
                setUpMapMarkerByLocation(currentPosition);
                Log.i(TAG, "( Latitude: " + latitude + " Longitude: " + longitude + " )");
                Log.i(TAG, "Location To String: " + lastLocation.toString());
                Toast.makeText(this, "Connected via the on connect method", Toast.LENGTH_LONG).show();
            } else {
                Log.i(TAG, "Location was null");
            }
        }
        else{
            Log.i(TAG, "Could not get the permission to access fine loation");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended. Reconnect");
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLatitude();
        Log.i(TAG, "Location has changed to: " + location);
    }
}