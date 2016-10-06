package mult_603.seniordesignprojectcordiusmotus;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TemporaryBluetoothActivity extends AppCompatActivity {
    public static final String TAG = TemporaryBluetoothActivity.class.getSimpleName();
    private final int REQUEST_BLUETOOTH_ENABLED = 2;
    private BluetoothListAdapter bluetoothListAdapter;
    public ListView listView;
    Button refreshButton;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    BluetoothSocket bluetoothSocket;
    HashMap<String, BluetoothDevice> deviceHashMap;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temporary_bluetooth);
        /**
         * GETDEFAULTADAPTER FOR JELLY BEAN AND BELOW
         * ABOVE JELLYBEAN USE GETADAPTER
         */
        listView = (ListView) findViewById(R.id.bluetooth_list);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        refreshButton = (Button) findViewById(R.id.refresh_button);
        deviceHashMap = new HashMap<>();

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothListAdapter.notifyDataSetChanged();
                Log.i(TAG, "Devices: " );
            }
        });

        // Check to see if bluetooth is enabled
        if (bluetoothAdapter == null) {
            Log.i(TAG, "Device does not support bluetooth :(");
        } else {
            Log.i(TAG, "Device supports bluetooth :)");
        }

        // Check to see if we can get permission to use bluetooth
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableByInent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableByInent, REQUEST_BLUETOOTH_ENABLED);
            Log.i(TAG, "User must enable bluetooth");
        } else {
            Log.i(TAG, "User does not need to enable bluetooth");
        }

        // Start discovery
        bluetoothAdapter.startDiscovery();
        final ArrayList<String> deviceList = new ArrayList<>();
        BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if( bluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
                    Log.i(TAG, "Action Bond State Changed");
                }
                if (bluetoothDevice.ACTION_FOUND.equals(action)) {

                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                    final int previousState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
                    String str = "Bluetooth device: " + device.getName() + " at address: " + device.getAddress();

                    if(!deviceList.contains(str)) {
                        deviceList.add(str);
                        deviceHashMap.put(str, device);
                        Log.i(TAG, "Device Found: " + str);
                    }

                    if (state == BluetoothDevice.BOND_BONDED && previousState == BluetoothDevice.BOND_BONDING){
                        Log.i(TAG, "Paired");
                    }
                    else if(state == BluetoothDevice.BOND_NONE && previousState == BluetoothDevice.BOND_BONDED){
                        Log.i(TAG, "Unpaired");
                    }
                }
                else{
                    Log.i(TAG, "Somthing went wrong");
                }
            }
        };


        bluetoothListAdapter = new BluetoothListAdapter(deviceList, getApplicationContext());
        listView.setAdapter(bluetoothListAdapter);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String str = (String) parent.getItemAtPosition(position);
//                BluetoothDevice device = deviceHashMap.get(str);
//                Log.i(TAG, "Got this device from hashmap " + device.getName() + " " + device.getAddress());
//                pairDevice(device);
//            }
//        });

        // Register broadcast receiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothReceiver, filter);

        /**
         * NOTE: Cancel discovery before pairing with a device.
         * Also If already connected to a device,
         * bluetooth discovery can limit your communication abilities
         */
        long leastSig = 1024;
        long mostSig = 10248;
        UUID uuid = new UUID(leastSig, mostSig);
        try {
            BluetoothServerSocket serverSocket = bluetoothAdapter
                    .listenUsingInsecureRfcommWithServiceRecord("CordiusMotus", uuid);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "IO Exception trying to open a server socket for bluetooth");
        }


        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        if (devices.size() > 0) {
            for (BluetoothDevice device : devices) {
                Log.i(TAG, "Paired to a bluetooth enabled device");
                Log.i(TAG, device.getAddress() + " at address: " + device.getName());
            }
        }


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void pairDevice(BluetoothDevice device){
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
            Log.i(TAG, "Paired Device " + device.getName());
        }
        catch (Exception e){
            e.printStackTrace();
            Log.i(TAG, "Error thrown while trying to pair device " + device.getName());
        }
    }

    public void unpairDevice(BluetoothDevice device){
        try{
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object []) null);
            Log.i(TAG, "Unpaired Device " + device.getName());
        }
        catch(Exception e){
            e.printStackTrace();
            Log.i(TAG, "Error thrown while trying to unpair device " + device.getName());
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "TemporaryBluetooth Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://mult_603.seniordesignprojectcordiusmotus/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "TemporaryBluetooth Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://mult_603.seniordesignprojectcordiusmotus/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    private class AcceptThread extends Thread {
        private BluetoothServerSocket bsSocket = null;

        public AcceptThread() {
            // Temp object later assigned to the bluetooth server socket
            BluetoothServerSocket temp = null;
            try {
                UUID uuid = new UUID(1024, 2048);
                temp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("CordiusMotus", uuid);

            } catch (IOException e) {
                e.printStackTrace();
                Log.i(TAG, "I/O Error in Accept Thread");
            }
            bsSocket = temp;
        }

        public void run() {
            BluetoothSocket blueSocket = null;
            while (true) {
                try {
                    blueSocket = bsSocket.accept();

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG, "I/O Exception in run method in accept thread");
                    break;
                }

                // If a connection was accepted
                if (blueSocket != null) {
                    Log.i(TAG, "Socket in run method is not null");
                    // Manage the socket connection
                    try {
                        manageConnectedSocket(blueSocket);
                        bsSocket.close();
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i(TAG, "The socket could not close?");
                    }


                }

            }
        }

        public void Cancel() {
            try {
                bsSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(TAG, "Canceling the socket connection");
            }
        }

        private void manageConnectedSocket(BluetoothSocket socket) {
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = socket.getInputStream();
                tempOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(TAG, "I/O Exception in managing the connection");
            }

        }

    }
}
