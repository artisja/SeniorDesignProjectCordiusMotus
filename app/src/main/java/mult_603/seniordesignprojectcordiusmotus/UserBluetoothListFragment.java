package mult_603.seniordesignprojectcordiusmotus;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Wes on 3/13/17.
 */
public class UserBluetoothListFragment extends Fragment {
    private static final String TAG = UserBluetoothListFragment.class.getSimpleName();
    public static void setHandler(Handler handler){
        mHandler = handler;
    }
    public static Handler getHandler(){
        return mHandler;
    }
    public static void onDisconnect(){
        if(connectedThread != null){
            connectedThread.cancel();
            connectedThread = null;
        }
    }
    static Handler mHandler = new Handler();
    static ConnectThread connectThread;
    static ConnectedThread connectedThread;
    public static final UUID DEVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    public static final int SUCCESSFUL_CONNECTION = 0;
    public static final int READING_MESSAGE       = 1;
    private final int REQUEST_BLUETOOTH_ENABLED   = 2;
    private BluetoothListAdapter        bluetoothListAdapter;
    private BluetoothAdapter            bluetoothAdapter;
    private ListView                    listView;
    private Button                      refreshButton;
    private ArrayList<BluetoothDevice>  deviceList;
    private IntentFilter                intentFilter;
    private Intent                      enableBluetoothIntent;
    private Set<BluetoothDevice>        bondedDevices;
    private BluetoothDevice             connectedDevice;
    private View                        view;
    private ProgressDialog              progressDialog;

    private final BroadcastReceiver broadReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    Log.i(TAG, "Action Bond State Changed ");
                    break;

                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    Log.i(TAG, "Action ACL Connected");
                    // Create a thread between the device and the application
                    if (connectedDevice != null && connectThread == null) {
                        connectThread = new ConnectThread(connectedDevice);
                        connectThread.start();
                        Log.i(TAG, "Connect Thread "      + connectThread.getName()  + "\n"
                                + "Connect Thread State " + connectThread.getState() + "\n"
                                + "Connect Thread Id "    + connectThread.getId());
                    }
                    break;

                case BluetoothDevice.ACTION_FOUND:
                    Log.i(TAG, "Action Found ");
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                    final int previousState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                    // If the device is not in the list then add it
                    if (!deviceList.contains(device)) {
                        deviceList.add(device);
                        Log.i(TAG, "Device Found: " + device.getName() + " , " + device.getAddress());
                    }

                    // Is the state of the device bonded or bonding?
                    if (state == BluetoothDevice.BOND_BONDED && previousState == BluetoothDevice.BOND_BONDING) {
                        Log.i(TAG, "Paired in bluetooth receiver");
                    }

                    // Device is un-pairing
                    else if (state == BluetoothDevice.BOND_NONE && previousState == BluetoothDevice.BOND_BONDED) {
                        Log.i(TAG, "Unpaired in bluetooth receiver");
                    }
                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.i(TAG, "Action Discovery Started ");
                    // Create a progress dialog to tell the user how long the process will take
//                    progressDialog = new ProgressDialog(getActivity(), R.style.AppThemeDialog);
//                    progressDialog.setIndeterminate(true);
//                    progressDialog.setMessage("Authenticating...");
//                    progressDialog.show();
                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.i(TAG, "Action Discovery Finished ");
//                    progressDialog.cancel();
                    break;

                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    Log.i(TAG, "Action State Changed ");
                    break;
            }
        }
    };

    // Empty constructor
    public UserBluetoothListFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        bluetoothAdapter      = BluetoothAdapter.getDefaultAdapter();
        enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        deviceList            = new ArrayList<>();

        intentFilter           = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);

        // Register receivers
        getActivity().registerReceiver(broadReceiver, intentFilter);
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i(TAG, "On Resume Called. Fragment Active.");
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.i(TAG, "On Start Called. Fragment Visible.");
        connectTheThread();
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.i(TAG, "On Pause. User is leaving the Fragment");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i(TAG, "On Destroy. Unregister receiver here");
        getActivity().unregisterReceiver(broadReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent i){
        super.onActivityResult(requestCode, resultCode, i);
        Log.i(TAG, "On Activity Result Called in Fragment");
        Log.i(TAG, "Request Code: " + requestCode);
        Log.i(TAG, "Result Code: " + resultCode);
        Log.i(TAG, "Intent: " + i);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.activity_bluetooth, container, false);
        listView         = (ListView) view.findViewById(R.id.bluetooth_list);
        refreshButton    = (Button) view.findViewById(R.id.refresh_button);


        // Set the refresh button on click listener
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter != null) {
                    bluetoothListAdapter.notifyDataSetChanged();

                    if (bluetoothListAdapter.getCount() == 0) {
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
                    } else {
                        Log.i(TAG, "Bluetooth Devices were found ");
                    }
                }
                else{
                    Log.i(TAG, "Bluetooth Adapter is null");
                    new android.support.v7.app.AlertDialog.Builder(view.getContext())
                            .setTitle("Bluetooth Compatibility")
                            .setMessage("Unfortunately This device does not support bluetooth")
                            .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                }
            }
        });

        setUpBluetooth();
        return view;
    }

    public void setUpBluetooth(){
        // Try to get bluetooth access
        enableBluetooth();

        // If the bluetooth adapter got set then proceed
        if(bluetoothAdapter!= null) {

            // Start discovery
            bluetoothAdapter.startDiscovery();

            // Set up the adapter and the list view
            bluetoothListAdapter = new BluetoothListAdapter(deviceList, view.getContext());
            listView.setAdapter(bluetoothListAdapter);

            connectTheThread();
        }
    }

    private void connectTheThread(){
        // If the connected device is null then look for bonded devices first and connect to them
        if(connectedDevice == null){
            getBondedDevices();
        }
        // Create a thread between the device and the application
        if (connectedDevice != null) {
            connectThread = new ConnectThread(connectedDevice);
            connectThread.start();
            Log.i(TAG, "Connect Thread " + connectThread.getName() + "\n"
                    +  "Connect Thread State " + connectThread.getState() + "\n"
                    +  "Connect Thread Id " + connectThread.getId());
        }
    }

    private void enableBluetooth() {
        // The device doesn't support bluetooth
        if (bluetoothAdapter == null) {
            Log.i(TAG, "Device does not support bluetooth :( ");
            // If the user does not have a bluetooth device we should notify them
            new android.support.v7.app.AlertDialog.Builder(view.getContext())
                    .setTitle("Bluetooth Compatibility")
                    .setMessage("This device does not support bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }
        // Bluetooth adapter is not null
        else {
            Log.i(TAG, "Device supports bluetooth :)");
            // Check to see if we can get permission to use bluetooth as long as the adapter isn't null
            if (!bluetoothAdapter.isEnabled()){
                Intent enableByIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableByIntent, REQUEST_BLUETOOTH_ENABLED);
                Log.i(TAG, "User must enable bluetooth");
            } else {
                Log.i(TAG, "User has enabled bluetooth");
            }
        }
    }

    // Get a list of all devices printed to the console
    private void getBondedDevices() {
        bondedDevices = bluetoothAdapter.getBondedDevices();
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice device : bondedDevices) {
                Log.i(TAG, "Bonded Device: " + device.getAddress() + " , " + " Name : " + device.getName());
                if(!deviceList.contains(device)){
                    deviceList.add(device);
                }

                if(device.getName().equals("HC-05")){
                    Log.i(TAG, "Found HC-05! Thanks MJ");
                    connectedDevice = device;
                    Log.i(TAG, "Connected Device: " + device.getName() + " UUID: " + device.getUuids());
                }
            }
        }
    }

    // This gets called when the pair device button is clicked.
    public void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
            Log.i(TAG, "Paired Device " + device.getName() + " in pair device method");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Error thrown while trying to pair device " + device.getName());
        }
    }

    // Un-pair a previously paired bluetooth device
    public void unpairDevice(BluetoothDevice device) {
        try{
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
            Log.i(TAG, "Unpaired Device " + device.getName());
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Error thrown while trying to unpair device " + device.getName());
        }
    }

    // This thread creates a socket and a connected thread and instantiates it.
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;
            try {
                tmp = device.createRfcommSocketToServiceRecord(DEVICE_UUID);
            } catch (IOException e) {
                Log.i(TAG, "ERROR trying to create socket to service record " + e.getMessage());
            }
            mmSocket = tmp;
        }

        public void run() {
            bluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();

            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.i(TAG, "ERROR closing the socket connection " + closeException.getMessage());
                }
                return;
            }

            //mHandler.obtainMessage(SUCCESSFUL_CONNECTION, mmSocket).sendToTarget();

            // Connect the socket and get information
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.start();

            Log.i(TAG, "Connected Thread       " + connectedThread.getName() + "\n"
                    +  "Connected Thread State " + connectedThread.getState() + "\n"
                    +  "Connected Thread Id    " + connectedThread.getId());


        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.i(TAG, "ERROR closing the socket in the cancel method " + e.getMessage());
            }
        }
    }

    static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
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
            Log.i(TAG, "Running the Connected Thread ");
            BufferedReader bufferedInputStream = new BufferedReader(new InputStreamReader(mmInStream));

            while (true) {
                try {
                    // Read the stream line by line and send the info to the target.
                    String line = bufferedInputStream.readLine();
                    Log.i(TAG, "Bluetooth Info -> " + line);
                    mHandler.obtainMessage(READING_MESSAGE, line).sendToTarget();
                }
                catch (IOException e) {
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
}
