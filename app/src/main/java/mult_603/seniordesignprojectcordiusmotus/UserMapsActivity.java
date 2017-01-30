package mult_603.seniordesignprojectcordiusmotus;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.GoogleMap.OnIndoorStateChangeListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.widget.SearchView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

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
        LocationListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnCircleClickListener,
        GoogleMap.OnMarkerClickListener,
        OnIndoorStateChangeListener,
        GoogleMap.InfoWindowAdapter,
        GoogleMap.OnInfoWindowClickListener,
        SearchView.OnQueryTextListener{

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
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_maps);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

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
                .setFastestInterval(1 * 1000)   // 5 seconds
                .setInterval(5 * 1000);        // 10 second

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Set up the parts of the relative layout on the map
//        personFrameLayout = (RelativeLayout) findViewById(R.id.person_fragment);
//        personText = (TextView) findViewById(R.id.person_text);
//        personHeartRate = (TextView) findViewById(R.id.person_heart_rate);
//        personLocation = (TextView) findViewById(R.id.person_location);
//
//        // Make the relative layout invisible
//        personFrameLayout.setVisibility(View.GONE);
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
//                if(personFrameLayout.getVisibility() == View.VISIBLE){
//                    personFrameLayout.setVisibility(View.GONE);
//                }
            }
        });
        mMap.setInfoWindowAdapter(this);
    }

    public void setUpMapMarkerByLocation(final LatLng location){
        // WE have a user
        if(currentUser != null){
            Log.i(TAG, "User is not null");
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(location)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            mMap.addMarker(markerOptions);
            Log.i(TAG, "Created a map marker for a user");

        }
        // The user of our application is not a registered patient
        else {
            Log.i(TAG, "User is null");
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(location)
                    .title("My Current Location!");
            mMap.addMarker(markerOptions);
            Log.i(TAG, "Created a map marker for a non user");
        }

        // Set the on marker listener
        mMap.setOnMarkerClickListener(this);


        try {
            // This puts a button on the map to find the current location
            mMap.setMyLocationEnabled(true);
            mMap.setIndoorEnabled(true);
            mMap.setBuildingsEnabled(true);
            mMap.setOnIndoorStateChangeListener(this);
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

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

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
                latitude = intent.getDoubleExtra("Longitude", lastLocation.getLatitude());
                longitude = intent.getDoubleExtra("Latitude", lastLocation.getLongitude());
                currentPosition = new LatLng(latitude, longitude);
                setUpMapMarkerByLocation(currentPosition);
                Log.i(TAG, "( Latitude: " + latitude + " Longitude: " + longitude + " )");
            } else {
                Log.i(TAG, "Location was null");
                LocationManager locationManager = (LocationManager) getApplicationContext()
                        .getSystemService(Context.LOCATION_SERVICE);

                boolean isNetworkEnabled = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);

                boolean isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);

                Log.i(TAG, "Is Network Enabled " + isNetworkEnabled);
                Log.i(TAG, "Is GPS Enabled " + isGPSEnabled);


            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
        Log.i(TAG, "On Save Instance State");
        //Save if the map is requesting updates boolean
        //Save the latitude and longitude
        //Save the last time the location was updated
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude  = location.getLatitude();
        longitude = location.getLatitude();
        Log.i(TAG, "Location has changed to: " + location);
        Toast.makeText(getApplicationContext(), "Location " + location, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCircleClick(Circle circle) {
        Log.i(TAG, "On Circle Clicked Listener");
    }

    // If the user clicks the marker icon
    @Override
    public boolean onMarkerClick(Marker marker) {
//        Geocoder gc = new Geocoder(getApplicationContext(), Locale.getDefault());
//        LatLng currentLatLng = marker.getPosition();
//        Address currentAddress = new Address(Locale.getDefault());
//        String streetAddress  = new String();
//        String cityAddress    = new String();
//        String countryAddress = new String();
//
//        try {
//            List<Address> addressList = gc.getFromLocation(currentLatLng.latitude, currentLatLng.longitude, 1);
//            for(int i = 0; i < addressList.size(); i++){
//                currentAddress = addressList.get(i);
//                streetAddress = currentAddress.getAddressLine(0);
//                cityAddress = currentAddress.getAddressLine(1);
//                countryAddress = currentAddress.getAddressLine(2);
//                Log.i(TAG, i + ". " + streetAddress + " " + cityAddress + " " + countryAddress);
//            }
//        }catch(IOException e){
//            e.printStackTrace();
//        }
//         marker.showInfoWindow();
//        String locationString = "Location (" + currentLatLng.latitude + " , "
//                + currentLatLng.longitude + ")\n"
//                + "Street: " + streetAddress + "\n"
//                + "City: " + cityAddress + " , "
//                + countryAddress;
//        personFrameLayout.setBackgroundColor(Color.BLACK);
//        personText.setText("Person: This is me");
//        personText.setTextColor(Color.WHITE);
//        personHeartRate.setText("Heart Rate: Excellent");
//        personHeartRate.setTextColor(Color.WHITE);
//        personLocation.setText(locationString);
//        personLocation.setTextColor(Color.WHITE);
//         // Make the relative layout visible
//        personFrameLayout.setVisibility(View.VISIBLE);


        // Show the custom info window instead of the black frame layout from before
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onIndoorBuildingFocused() {
        Log.i(TAG, "On Indoor Building Focused");
    }

    @Override
    public void onIndoorLevelActivated(IndoorBuilding indoorBuilding) {
        Log.i(TAG, "On Indoor Level Activated");
        Log.i(TAG, "Building Level Index " + indoorBuilding.getActiveLevelIndex());
    }

    // Custom Info View For Map Marker

    @Override
    public View getInfoWindow(Marker marker) {
        return createInfoWindow(marker);
    }

    @Override
    public View getInfoContents(Marker marker) {
        return createInfoWindow(marker);
    }

    private View createInfoWindow(Marker marker){
        LinearLayout linearLayout = new LinearLayout(UserMapsActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(15, 15, 15, 15);
        int[] colors = {Color.WHITE, Color.WHITE};
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        gd.setCornerRadius(25.0f);
        linearLayout.setBackground(gd);
        linearLayout.setLayoutParams(params);

        if(currentUser != null) {
            CircleImageView circleImageView = new CircleImageView(getApplicationContext());
            circleImageView.setBorderColor(Color.BLUE);
            circleImageView.setImageURI(currentUser.getPhotoUrl());
            circleImageView.setBorderWidth(2);
            linearLayout.addView(circleImageView);

            LinearLayout subLayout = new LinearLayout(UserMapsActivity.this);
            LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            subLayout.setOrientation(LinearLayout.VERTICAL);
            subLayout.setLayoutParams(subParams);

            TextView userName = new TextView(UserMapsActivity.this);
            userName.setText(currentUser.getDisplayName());
            userName.setGravity(Gravity.CENTER_HORIZONTAL);
            userName.setTextColor(Color.BLACK);
            subLayout.addView(userName);

            TextView userEmail = new TextView(UserMapsActivity.this);
            userEmail.setText(currentUser.getEmail());
            userEmail.setGravity(Gravity.CENTER_HORIZONTAL);
            userEmail.setTextColor(Color.BLACK);
            subLayout.addView(userEmail);

            TextView userLat = new TextView(UserMapsActivity.this);
            String latString = "Latitude: " + latitude;
            userLat.setText(latString);
            userLat.setGravity(Gravity.CENTER_HORIZONTAL);
            userLat.setTextColor(Color.BLACK);
            subLayout.addView(userLat);

            TextView userLng = new TextView(UserMapsActivity.this);
            String lngString = "Longitude: " + longitude;
            userLng.setText(lngString);
            userLng.setGravity(Gravity.CENTER_HORIZONTAL);
            userLng.setTextColor(Color.BLACK);
            subLayout.addView(userLng);

            try {
                Geocoder gc = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addressList = gc.getFromLocation(latitude, longitude, 1);
                TextView userAddress = new TextView(UserMapsActivity.this);
                Address userCurrentAddress = addressList.get(0);
                String userStreetAddress = userCurrentAddress.getAddressLine(0);
                String userCityAddress   = userCurrentAddress.getAddressLine(1);
                String userCountryAddress= userCurrentAddress.getAddressLine(2);
                String userAddressString = "Address: " + userStreetAddress +
                "\n" + userCityAddress + " " + userCountryAddress;
                userAddress.setText(userAddressString);
                userAddress.setTextColor(Color.BLACK);
                userAddress.setGravity(Gravity.CENTER_HORIZONTAL);
                subLayout.addView(userAddress);
            }
            catch(IOException io){
                Log.i(TAG, "Exception thrown trying to resolve address for info window");
                io.printStackTrace();
            }

            linearLayout.addView(subLayout);
        }

        return linearLayout;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.i(TAG, "Clicked Info Window");
    }


    // Search View Methods

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.i(TAG, "Text Query: " + query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.i(TAG, "On Query Change: " + newText);
        return false;
    }
}
