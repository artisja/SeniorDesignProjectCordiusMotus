package mult_603.seniordesignprojectcordiusmotus;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class TemporaryBluetoothActivity extends AppCompatActivity{
    public static final String TAG = TemporaryBluetoothActivity.class.getSimpleName();
    private final int REQUEST_BLUETOOTH_ENABLED = 2;
    private BluetoothListAdapter bluetoothListAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private ListView listView;
    private Button refreshButton;
    private ArrayList<BluetoothDevice> deviceList;
    private IntentFilter foundFilter;
    private BluetoothDevice bluetoothDevice;
    private long leastSig = 1024;
    private long mostSig = 10248;
    private Intent enableBluetoothIntent;
    private Set<BluetoothDevice> bondedDevices;
    private UUID uuid;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temporary_bluetooth);

        // Set up the initial views and resources
        findViews();

        // Try to get bluetooth access
        enableBluetooth();

        // Start discovery
        bluetoothAdapter.startDiscovery();

        BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if( bluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
                    Log.i(TAG, "Action Bond State Changed");
                }

                if (bluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                    Log.i(TAG, "Action Discovery Started");
                }

                if (bluetoothDevice.ACTION_FOUND.equals(action)) {

                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                    final int previousState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                    // If the device is not in the list then add it
                    if(!deviceList.contains(device)) {
                        deviceList.add(device);
                        Log.i(TAG, "Device Found: " + device.getName() + " , " + device.getAddress());
                    }

                    // Is the state of the device bonded or bonding?
                    if (state == BluetoothDevice.BOND_BONDED && previousState == BluetoothDevice.BOND_BONDING){
                        Log.i(TAG, "Paired in bluetooth receiver");
                    }
                    else if(state == BluetoothDevice.BOND_NONE && previousState == BluetoothDevice.BOND_BONDED){
                        Log.i(TAG, "Unpaired in bluetooth receiver");
                    }
                }
                else{
                    Log.i(TAG, "Something went wrong with bluetooth action found");
                }
            }
        };

        bluetoothListAdapter = new BluetoothListAdapter(deviceList, getApplicationContext());
        listView.setAdapter(bluetoothListAdapter);

        // Register broadcast receiver
        registerReceiver(bluetoothReceiver, foundFilter);

        // Get bonded devices
        getBondedDevices();
    }

    // Set up the variables for this class
    private void findViews(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listView = (ListView) findViewById(R.id.bluetooth_list);
        refreshButton = (Button) findViewById(R.id.refresh_button);
        foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        deviceList = new ArrayList<>();
        uuid = new UUID(mostSig, leastSig);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothListAdapter.notifyDataSetChanged();

                if(bluetoothListAdapter.getCount() == 0){
                    //Toast.makeText(getApplicationContext(), "No devices found", Toast.LENGTH_LONG).show();
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Bluetooth Devices")
                            .setMessage("No Bluetooth Devices were found.")
                            .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .create()
                            .show();
                    Log.i(TAG, "No Bluetooth Devices were found");
                }
                else {
                    Log.i(TAG, "Bluetooth Devices were found ");
                }
            }
        });
    }

    private void enableBluetooth(){
        // Check to see if bluetooth is enabled
        if (bluetoothAdapter == null) {
            Log.i(TAG, "Device does not support bluetooth :( ");
            // If the user does not have a bluetooth device we should notify them
            new AlertDialog.Builder(getApplicationContext())
                    .setTitle("Bluetooth Compatibility")
                    .setMessage("This device does not support bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .create()
                    .show();
        } else {
            Log.i(TAG, "Device supports bluetooth :)");
        }

        // Check to see if we can get permission to use bluetooth
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableByInent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableByInent, REQUEST_BLUETOOTH_ENABLED);
            Log.i(TAG, "User must enable bluetooth");
        } else {
            Log.i(TAG, "User does not want to enable bluetooth");
        }
    }

    // Get a list of all devices printed to the console
    private void getBondedDevices(){
        bondedDevices = bluetoothAdapter.getBondedDevices();
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice device : bondedDevices) {
                Log.i(TAG, "Bonded Device: " + device.getAddress() + " , " + " Name : " + device.getName());
            }
        }
    }

    // This gets called when the pair device button is clicked.
    public void pairDevice(BluetoothDevice device){
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
            Log.i(TAG, "Paired Device " + device.getName() + " in pair device method");
        }
        catch (Exception e){
            e.printStackTrace();
            Log.i(TAG, "Error thrown while trying to pair device " + device.getName());
        }
    }

    // Unpair a previously paired bluetooth device
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
    
    /**
     * The below string was found at envato tuts plus
     */

    public class ConnectionThread extends Thread {
        private BluetoothSocket bluetoothSocket;

        public ConnectionThread() {
        }

        public boolean connectThread(BluetoothDevice device, UUID uuid) {
            BluetoothSocket tempSocket = null;

            try {
                tempSocket = device.createRfcommSocketToServiceRecord(uuid);

            } catch (IOException e) {
                e.printStackTrace();
                Log.i(TAG, "IO Exception could not create thread");
                return false;
            }

            try{
                bluetoothSocket.connect();
            }
            catch (IOException e){
                e.printStackTrace();
                Log.i(TAG, "IO Exception failed to connect thread");
                try{
                    bluetoothSocket.close();
                }
                catch (IOException i){
                    i.printStackTrace();
                    Log.i(TAG, "IO Exception could not close connection thread");
                    return false;
                }
            }
            return true;
        }

        public boolean cancel(){
            try{
                bluetoothSocket.close();
            }
            catch (IOException e){
                Log.i(TAG, "IO Exception could not close connection thread");
                return false;
            }
            return true;
        }
    }


    /**
     *  Bluetooth Server Connection Thread class
     */
    public class BluetoothServerConnectionThread extends Thread {
        private BluetoothSocket bluetoothSocket;
        public final String TAG = BluetoothServerConnectionThread.class.getSimpleName();
        // Empty Constructor
        public BluetoothServerConnectionThread(){ }

        public void acceptConnection(BluetoothAdapter bluetoothAdapter, UUID uuid){
            BluetoothServerSocket bluetoothServerSocket = null;
            try{
                bluetoothServerSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("CordiusMotus", uuid);
            }
            catch (IOException e){
                e.printStackTrace();
                Log.i(TAG, "IO Exception while trying to accept connection server socket");
            }

            while (true){
                try{
                    bluetoothSocket = bluetoothServerSocket.accept();
                }
                catch(IOException e){
                    e.printStackTrace();
                    Log.i(TAG, "IO Exception when trying to accept bluetooth server socket");
                    break;
                }

                if(bluetoothSocket != null){
                    try {
                        bluetoothServerSocket.close();
                    }
                    catch(IOException e){
                        e.printStackTrace();
                        Log.i(TAG, "IO Exception when trying to close bluetooth server socket");
                    }
                    break;
                }
            }
        }

        public void closeConnection(){
            try{
                bluetoothSocket.close();
            }
            catch(IOException e){
                e.printStackTrace();
                Log.i(TAG, "IO Exception when trying to close connection");
            }
        }
    }


    public class ManageConnectedThread extends Thread {
        public final String TAG = ManageConnectedThread.class.getSimpleName();

        // Empty Constructor
        public ManageConnectedThread(){ }

        public void sendDate(BluetoothSocket socket, int data) throws IOException{
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4);
            outputStream.write(data);
            OutputStream socketOutput = socket.getOutputStream();
            socketOutput.write(outputStream.toByteArray());
        }

        public int receiveData(BluetoothSocket socket) throws IOException{
            byte[] buffer = new byte[4];
            ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer);
            InputStream input = socket.getInputStream();
            inputStream.read(buffer);
            return input.read();
        }
    }
}