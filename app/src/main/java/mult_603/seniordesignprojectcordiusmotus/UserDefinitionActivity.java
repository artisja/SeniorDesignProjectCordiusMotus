package mult_603.seniordesignprojectcordiusmotus;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class UserDefinitionActivity extends AppCompatActivity {

    private Button medicButton, caliButton, bluetoothButton;
    private Intent medicMapIntent;
    private Intent bluetoothActivity;
    public double longitude, latitude;

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
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        medicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserDefinitionActivity.this, "Google Maps/medical data view screen", Toast.LENGTH_SHORT).show();
                medicMapIntent = new Intent(getApplicationContext(), LoginDeviceLocator.class);
                medicMapIntent.putExtra("Longitude",longitude);
                medicMapIntent.putExtra("Latitude", latitude);
                startActivity(medicMapIntent);
            }
        });

        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothActivity = new Intent(getApplicationContext(), BluetoothActivity.class);
                startActivity(bluetoothActivity);
            }
        });
    }

    private void findViews() {
        caliButton = (Button) findViewById(R.id.cali_Button);
        medicButton = (Button) findViewById(R.id.medic_Button);
        bluetoothButton = (Button) findViewById(R.id.bluetooth_Button);
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
}
