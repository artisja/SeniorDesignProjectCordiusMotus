//package mult_603.seniordesignprojectcordiusmotus;
//
//import android.app.Activity;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothSocket;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.v7.app.AlertDialog;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Set;
//import java.util.UUID;
//
///**
// * Created by Wes on 11/15/16.
// */
//public class BluetoothData extends Activity {
//
//    public static void getHandler(Handler handler){
//        mHandler = handler;
//    }
//
//    public static void onDisconnect(){
//        if(connectedThread != null){
//            connectedThread.cancel();
//            connectedThread = null;
//        }
//    }
//
//    static Handler mHandler = new Handler();
//
//    static ConnectedThread connectedThread;
//    public static final String TAG = BluetoothData.class.getSimpleName();
//    public static final UUID DEVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
//    public static final int SUCCESSFUL_CONNECTION = 0;
//    public static final int READING_MESSAGE = 1;
//    public static final int REQUEST_BLUETOOTH_ENABLED = 2;
//    BluetoothListAdapter listAdapter;
//    ListView deviceListView;
//    private BluetoothListAdapter  bluetoothListAdapter;
//    BroadcastReceiver             broadcastReceiver;
//    BluetoothAdapter              bluetoothAdapter;
//    IntentFilter                  filter;
//    private ListView              listView;
//    private Button                refreshButton;
//    private Button                chartButton;
//    private ArrayList<BluetoothDevice> deviceList;
//    private IntentFilter          foundFilter;
//    private BluetoothDevice       bluetoothDevice;
//    private Intent                enableBluetoothIntent;
//    private Set<BluetoothDevice>  bondedDevices;
//    private BluetoothDevice       connectedDevice;
//    private ProgressBar           bluetoothProgressBar;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_bluetooth);
//        init();
//    }
//
//    @Override
//    protected void onPause(){
//        super.onPause();
//        Log.i(TAG, "On Pause Entered ");
//        unregisterReceiver(broadcastReceiver);
//    }
//
//    @Override
//    protected void onResume(){
//        super.onResume();
//        Log.i(TAG, "On Resume Entered ");
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode , Intent data){
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(resultCode == RESULT_CANCELED){
//            Log.i(TAG, "Result Canceled ");
//            Log.i(TAG, "Bluetooth must be enabled to continue");
//        }
//    }
//
//    private void getBondedDevice(){
////        if(bluetoothAdapter.isDiscovering()){
////            bluetoothAdapter.cancelDiscovery();
////        }
//
//        bondedDevices = bluetoothAdapter.getBondedDevices();
//        if (bondedDevices.size() > 0) {
//            for (BluetoothDevice device : bondedDevices) {
//                Log.i(TAG, "Bonded Device: " + device.getAddress() + " , " + " Name : " + device.getName());
//
//                if(!deviceList.contains(device)){
//                    deviceList.add(device);
//                    connectedDevice = device;
//                }
//            }
//        }
//    }
//
////    @Override
////    protected void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3){
////        if(bluetoothAdapter.isDiscovering()){
////            bluetoothAdapter.cancelDiscovery();
////        }
////
////        // Get a paired device and add a connected thread ...
////        if(listAdapter.getItem(arg2) == "Paired"){
////            BluetoothDevice device = deviceList.get(arg2);
////            ConnectThread connectThread = new ConnectThread(device);
////            connectThread.start();
////        }
////
////    }
//
//    // Set Up the views here !!
//    private void init() {
//        bluetoothAdapter        = bluetoothAdapter.getDefaultAdapter();
//        filter                  = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        listView                = (ListView) findViewById(R.id.bluetooth_list);
//        refreshButton           = (Button) findViewById(R.id.refresh_button);
//        chartButton             = (Button) findViewById(R.id.bluetooth_chart_button);
//        foundFilter             = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        enableBluetoothIntent   = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//        bluetoothProgressBar    = (ProgressBar) findViewById(R.id.bluetooth_search_progress);
//        deviceList              = new ArrayList<>();
//
//        // Set the Chart button on click listener
//        chartButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent chartActivity = new Intent(getApplicationContext(), BluetoothChartActivity.class);
//                startActivity(chartActivity);
//            }
//        });
//
//        // Set the refresh button on click listener
//        refreshButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                bluetoothListAdapter.notifyDataSetChanged();
//
//                if (bluetoothListAdapter.getCount() == 0) {
//                    // Notify the user that no devices were found
//
//                    new AlertDialog.Builder(v.getContext())
//                            .setTitle("Bluetooth Devices")
//                            .setMessage("No Bluetooth Devices were found.")
//                            .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//                                }
//                            })
//                            .create()
//                            .show();
//                    Log.i(TAG, "No Bluetooth Devices were found");
//                } else {
//                    Log.i(TAG, "Bluetooth Devices were found ");
//                }
//            }
//        });
//        broadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                String action = intent.getAction();
//                if(BluetoothDevice.ACTION_FOUND.equals(action)){
//                    Log.i(TAG, "Action Found ");
//                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
//                    // Add the device to the list view
//
//                }
//                else if(bluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
//                    Log.i(TAG, "Action Discovery Started ");
//                }
//
//                else if(bluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
//                    Log.i(TAG, "Action Discovery Finished ");
//
//                }
//                else if(bluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
//                    Log.i(TAG, "Action State Changed");
//                    if(bluetoothAdapter.STATE_OFF == bluetoothAdapter.getState()){
//                        Log.i(TAG, "Bluetooth is off turn it back on");
//                    }
//                }
//
//            }
//        };
//
//    }
//
//    // Bluetooth
//    public void enableBluetooth(){
//        if(bluetoothAdapter == null){
//            Log.i(TAG, "Device does not support bluetooth :( ");
//            // If the user does not have a bluetooth device we should notify them
//            new AlertDialog.Builder(getApplicationContext())
//                    .setTitle("Bluetooth Compatibility")
//                    .setMessage("This device does not support bluetooth")
//                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    })
//                    .create()
//                    .show();
//        } else {
//            Log.i(TAG, "Device supports bluetooth :) ");
//
//            // Check to see if we can get permission to use bluetooth from the user
//            if (!bluetoothAdapter.isEnabled()) {
//                Intent enableByIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableByIntent, REQUEST_BLUETOOTH_ENABLED);
//                Log.i(TAG, "User must enable bluetooth");
//            } else {
//                Log.i(TAG, "User has enabled bluetooth");
//            }
//
//            // Start discovering devices and get the ones that are paired
//            discoverDevices();
//            getPairedDevices();
//        }
//    }
//
//    private void getPairedDevices(){
//        bondedDevices = bluetoothAdapter.getBondedDevices();
//        if (bondedDevices.size() > 0) {
//            for (BluetoothDevice device : bondedDevices) {
//                Log.i(TAG, "Bonded Device: " + device.getAddress() + " , " + " Name : " + device.getName());
//
//                if(!deviceList.contains(device)){
//                    deviceList.add(device);
//                    //connectedDevice = device;
//                }
//            }
//        }
//    }
//
//    private void discoverDevices(){
//        bluetoothAdapter.cancelDiscovery();
//        bluetoothAdapter.startDiscovery();
//    }
//
//
//
//    private class ConnectThread extends Thread {
//        private final BluetoothSocket mmSocket;
//        private final BluetoothDevice mmDevice;
//
//        public ConnectThread(BluetoothDevice device) {
//            // Use a temporary object that is later assigned to mmSocket,
//            // because mmSocket is final
//            BluetoothSocket tmp = null;
//            mmDevice = device;
//
//            // Get a BluetoothSocket to connect with the given BluetoothDevice
//            try {
//                // MY_UUID is the app's UUID string, also used by the server code
//                tmp = device.createRfcommSocketToServiceRecord(DEVICE_UUID);
//            } catch (IOException e) { }
//            mmSocket = tmp;
//        }
//
//        public void run() {
//            // Cancel discovery because it will slow down the connection
//            bluetoothAdapter.cancelDiscovery();
//
//            try {
//                // Connect the device through the socket. This will block
//                // until it succeeds or throws an exception
//                mmSocket.connect();
//            } catch (IOException connectException) {
//                // Unable to connect; close the socket and get out
//                try {
//                    mmSocket.close();
//                } catch (IOException closeException) { }
//                return;
//            }
//
//            // Do work to manage the connection (in a separate thread)
//            //manageConnectedSocket(mmSocket);
//            mHandler.obtainMessage(SUCCESSFUL_CONNECTION, mmSocket).sendToTarget();
//        }
//
//        /** Will cancel an in-progress connection, and close the socket */
//        public void cancel() {
//            try {
//                mmSocket.close();
//            } catch (IOException e) { }
//        }
//    }
//
//    static class ConnectedThread extends Thread {
//        private final BluetoothSocket mmSocket;
//        private final InputStream mmInStream;
//        private final OutputStream mmOutStream;
//
//        public ConnectedThread(BluetoothSocket socket) {
//            mmSocket = socket;
//            InputStream tmpIn = null;
//            OutputStream tmpOut = null;
//
//            // Get the input and output streams, using temp objects because
//            // member streams are final
//            try {
//                tmpIn = socket.getInputStream();
//                tmpOut = socket.getOutputStream();
//            } catch (IOException e) { }
//
//            mmInStream = tmpIn;
//            mmOutStream = tmpOut;
//        }
//
//        public void run() {
//            byte[] buffer = new byte[1024];  // buffer store for the stream
//            int bytes; // bytes returned from read()
//
//            // Keep listening to the InputStream until an exception occurs
//            while (true) {
//                try {
//                    try{
//                        sleep(30);
//                    }catch(InterruptedException e){
//                        Log.i(TAG, "Interrupted Exception " + e.getMessage());
//                        e.printStackTrace();
//                    }
//                    // Read from the InputStream
//                    bytes = mmInStream.read(buffer);
//
//                    // Send the obtained bytes to the UI activity
//                    mHandler.obtainMessage(READING_MESSAGE, bytes, -1, buffer)
//                            .sendToTarget();
//                } catch (IOException e) {
//                    break;
//                }
//            }
//        }
//
//        /* Call this from the main activity to send data to the remote device */
//        public void write(byte[] bytes) {
//            try {
//                mmOutStream.write(bytes);
//            } catch (IOException e) { }
//        }
//
//        /* Call this from the main activity to shutdown the connection */
//        public void cancel() {
//            try {
//                mmSocket.close();
//            } catch (IOException e) { }
//        }
//    }
//
//}
