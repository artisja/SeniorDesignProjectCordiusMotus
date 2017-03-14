package mult_603.seniordesignprojectcordiusmotus;

import android.Manifest;
import android.bluetooth.BluetoothClass;
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
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.SearchView;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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

    public static final String TAG = UserMapsActivity.class.getSimpleName();
    private final static String PATIENT_MARKER_TAG = "PatientMarkerTag";
    private final static String EMERGENCY_CONTACT_TAG = "EmergencyMarkerTag";
    private static final int REQUEST_LOCATION = 2;
    private final static int CONNETION_TIMEOUT = 5000;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    public Location location;
    public LatLng currentPosition;
    public double latitude;
    public double longitude;
    private FirebaseUser currentUser;
    private SearchView mapSearchView;
    private LocationHolder patientsLocationHolder;
    private Marker patientMarker;
    private Marker emergencyContactMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_maps);

        // Set up the search view and its listeners
        mapSearchView = (SearchView) findViewById(R.id.searchView);
        mapSearchView.setQueryHint("Patients UUID");
        mapSearchView.setOnQueryTextListener(this);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        // Set up the api client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Set up the patient marker
        MarkerOptions patientMarkerOptions = new MarkerOptions();
        MarkerOptions contactMarkerOptions = new MarkerOptions();



        // Create location request the time interval directly affects power usage and we want the highest accuracy
        // Priority high accuracy paired with fine location in the manifest file can find a users location within
        // the accuracy of a few feet
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(1 * 1000)   // 1 second
                .setInterval(5 * 1000);        // 5 seconds

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
        // Set Map Click Listener
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });
        // Set the maps info window and marker click listener
        mMap.setInfoWindowAdapter(this);
        mMap.setOnMarkerClickListener(this);
    }

    public void setUpMapMarkerByLocation(final LatLng location){
        // We have a user Their marker is blue
        // Going from red to blue
        if(currentUser != null){
            Log.i(TAG, "User is not null");
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(location)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            patientMarker = mMap.addMarker(markerOptions);
            patientMarker.setTag(PATIENT_MARKER_TAG);
            Log.i(TAG, "Created a map marker for a user");

        }
        // The user of our application is not a registered patient their marker is red
        else {
            Log.i(TAG, "User is null");
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(location)
                    .title("My Current Location!");

            emergencyContactMarker = mMap.addMarker(markerOptions);
            emergencyContactMarker.setTag(EMERGENCY_CONTACT_TAG);
            Log.i(TAG, "Created a map marker for a non user");
        }




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
        // Do not have Permission to access fine location
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request access to fine location
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Log.i(TAG, "DID show the request permission rationale");
            }
            else{
                Log.i(TAG, "DID NOT show the request permission rationale");
            }
        }
        // We have permission to access the user's fine location
        else{
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            // Last Location is not null
            if (lastLocation != null) {
                Intent intent = getIntent();
                latitude =  intent.getDoubleExtra("Longitude", lastLocation.getLatitude());
                longitude = intent.getDoubleExtra("Latitude", lastLocation.getLongitude());
                currentPosition = new LatLng(latitude, longitude);
                setUpMapMarkerByLocation(currentPosition);
                Log.i(TAG, "( Latitude: " + latitude + " Longitude: " + longitude + " )");
            }
            // Last location is null
            else {
                Log.i(TAG, "Location was null");
                LocationManager locationManager = (LocationManager) getApplicationContext()
                        .getSystemService(Context.LOCATION_SERVICE);

                boolean isNetworkEnabled = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);

                boolean isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);

                Log.i(TAG, "Is Network Enabled " + isNetworkEnabled);
                Log.i(TAG, "Is GPS Enabled "     + isGPSEnabled);

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

        // Send the lat and long to update the map marker
        LatLng newLatLng = new LatLng(latitude, longitude);
        setUpMapMarkerByLocation(newLatLng);
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

        // Add support for multiple markers at once w/ user info
        if(marker.getTag().equals(PATIENT_MARKER_TAG)){
            Log.i(TAG, "Clicked the patients marker");
        }
        else if(marker.getTag().equals(EMERGENCY_CONTACT_TAG)){
            Log.i(TAG, "Clicked the emergency contact tag");
        }

        if(currentUser != null) {
            Log.i(TAG, "Clicked a emergency contacts marker");

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

        // The current user is null we want to find a path from them to a registered user
        else{
            CircleImageView circleImageView = new CircleImageView(getApplicationContext());
            circleImageView.setBorderColor(Color.BLUE);
            circleImageView.setImageResource(R.drawable.account_black_48);
            circleImageView.setBorderWidth(2);
            linearLayout.addView(circleImageView);

            LinearLayout subLayout = new LinearLayout(UserMapsActivity.this);
            LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            subLayout.setOrientation(LinearLayout.VERTICAL);
            subLayout.setLayoutParams(subParams);

            TextView currentLocText = new TextView(UserMapsActivity.this);
            currentLocText.setText("Current Location");
            currentLocText.setGravity(Gravity.CENTER_HORIZONTAL);
            currentLocText.setTextColor(Color.BLACK);
            subLayout.addView(currentLocText);

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

    // Create a request string to send to google
    private void getDirectionsFromGoogle(LatLng source, LatLng destination){
        // Example -> https://maps.googleapis.com/maps/api/directions/json?origin=37.540791,-77.469917&destination=37.5450506,-77.4483879
        String apiCallString = "https://maps.googleapis.com/maps/api/directions/json?origin="
                                + source.latitude
                                + ","
                                + source.longitude
                                + "$destination="
                                + destination.latitude
                                + ","
                                + destination.longitude;


    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.i(TAG, "Clicked Info Window");
    }


    // Search View Methods
    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.i(TAG, "Text Query: " + query);
        final FirebaseDatabase fdb = FirebaseDatabase.getInstance();
        DatabaseReference fdbRef = fdb.getReference("UserDictionary").child(query);
        Log.i(TAG, "Db Reference: " + fdbRef);

        // TODO broke this damnit
        fdbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "On Data Changed");
                for(DataSnapshot user: dataSnapshot.getChildren()){
                    // Get the location based on the text entry
                    DeviceUser dev = user.getValue(DeviceUser.class);
                    Log.i(TAG, "Device User -> " + dev.toString());
                    DatabaseReference locationRef = fdb.getReference(dev.getUuid());

                    locationRef.child("Location").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot location: dataSnapshot.getChildren()) {
                                LocationHolder locationHolder = location.getValue(LocationHolder.class);
                                Log.i(TAG, "Location Holder Obj from DB " + locationHolder.toString());

                                if (locationHolder.hasLatitude() && locationHolder.hasLongitude()) {
                                    Log.i(TAG, "Location Holder has latitude and longitude");
                                    Log.i(TAG, "Location Holder has latitude " + locationHolder.getLatitude());
                                    Log.i(TAG, "Location Holder has longitude " + locationHolder.getLongitude());
                                    // Set the location Holder to a static object
                                    patientsLocationHolder = locationHolder;
                                    LatLng patientsLatLng = new LatLng(patientsLocationHolder.getLatitude(), patientsLocationHolder.getLongitude());
                                    // Draw the route to the patient
                                    drawRoute(patientsLatLng);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "Database Error");
            }
        });

        return false;
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        Log.i(TAG, "On Query Change: " + newText);
        return false;
    }

    public LatLng calculateMidPoint(LatLng origin, LatLng destination){
        LatLng midPoint;
        Double originLat = origin.latitude;
        Double originLng = origin.longitude;
        Double destLat   = destination.latitude;
        Double destLng   = destination.longitude;

        // Calculate the midpoint and return it
        Double newLat = (originLat + destLat) / 2;
        Double newLng = (originLng + destLng) / 2;
        midPoint = new LatLng(newLat, newLng);
        return midPoint;
    }

    private void drawRoute(LatLng finishLocation){
        // Get the view in order to hide the keyboard
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(R.id.map)).getChildAt(0);
        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(viewGroup.getWindowToken(), 0);

        // Get url from current position to the finish location

        String url = getDirectionsUrl(currentPosition, finishLocation);
        DownloadTask dl = new DownloadTask();
        dl.execute(url);

    }

    public String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }
    /** A method to download json data from url */
    public String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.i("ExceptionDownloadingURL", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

//    // Create multiple markers
//    private class CreateBubbleMarkers extends  AsyncTask<String, Void, String>{
//        @Override
//        protected  String doInBackground(String... url){
//
//        }
//    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionParser parser = new DirectionParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(12.0f);
                lineOptions.color(R.color.colorPrimaryDark);
                LatLng patientsLatLng = new LatLng(patientsLocationHolder.getLatitude(), patientsLocationHolder.getLongitude());
                markerOptions.position(patientsLatLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        .title("Patients Location");

                patientMarker = mMap.addMarker(markerOptions);
                patientMarker.setTag(PATIENT_MARKER_TAG);

                // Calculate midpoint and zoom out a bit
                LatLng centerMap = calculateMidPoint(currentPosition, patientsLatLng);
                CameraUpdate cmu = CameraUpdateFactory.newLatLngZoom(centerMap, 15.0f);
                mMap.moveCamera(cmu);
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }
}
