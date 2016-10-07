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
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class TemporaryBluetoothActivity extends AppCompatActivity {
    public static final String TAG = TemporaryBluetoothActivity.class.getSimpleName();
    private final int REQUEST_BLUETOOTH_ENABLED = 2;
    private BluetoothListAdapter bluetoothListAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver bluetoothReceiver;
    private ListView listView;
    private Button refreshButton;
    private ArrayList<BluetoothDevice> deviceList;
    private IntentFilter foundFilter;
    private BluetoothDevice bluetoothDevice;
    private Intent enableBluetoothIntent;
    private Set<BluetoothDevice> bondedDevices;
    private ProgressDialog progressDialog;
    private boolean isPaired = false;
    private Handler h;
    private final int RECIEVE_MESSAGE = 1;
    private StringBuilder sb;
    private String address = "20:15:07:13:94:86";
    private BluetoothSocket btSocket;
    private final UUID CONNECTION_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ConnectedThread connectedThread;
    private ConnectThread connectThread;
    private Handler mHandler;


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

        bluetoothReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (bluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                    Log.i(TAG, "Action Bond State Changed");
                }

                if (bluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                    Log.i(TAG, "Action Discovery Started");
                }

                if (bluetoothDevice.ACTION_FOUND.equals(action)) {

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

                    // Device is unpairing
                    else if (state == BluetoothDevice.BOND_NONE && previousState == BluetoothDevice.BOND_BONDED) {
                        Log.i(TAG, "Unpaired in bluetooth receiver");
                    }

                } else {
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
    private void findViews() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listView = (ListView) findViewById(R.id.bluetooth_list);
        refreshButton = (Button) findViewById(R.id.refresh_button);
        foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        deviceList = new ArrayList<>();
        sb = new StringBuilder();

        // Set the refresh button on click listener
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                byte[] writeBuf = (byte[]) msg.obj;
                int begin = (int) msg.arg1;
                int end = (int) msg.arg2;

                switch (msg.what) {
                    case 1:
                        String writeMessage = new String(writeBuf);
                        writeMessage = writeMessage.substring(begin, end);
                        break;
                }
            }
        };
    }


    private void enableBluetooth() {
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
    private void getBondedDevices() {
        bondedDevices = bluetoothAdapter.getBondedDevices();
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice device : bondedDevices) {
                Log.i(TAG, "Bonded Device: " + device.getAddress() + " , " + " Name : " + device.getName());
                if(!deviceList.contains(device)){
                    deviceList.add(device);
                }
                connectThread = new ConnectThread(device);
                connectThread.start();
//                connectedThread = new ConnectedThread(btSocket);
//                connectedThread.start();
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

    // Unpair a previously paired bluetooth device
    public void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
            Log.i(TAG, "Unpaired Device " + device.getName());
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Error thrown while trying to unpair device " + device.getName());
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
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
                }
                return;
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

        private class ConnectedThread extends Thread {
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
                }
                mmInStream = tmpIn;
                mmOutStream = tmpOut;
            }

            public void run() {
                byte[] buffer = new byte[1024];
                int begin = 0;
                int bytes = 0;
                while (true) {
                    try {
                        bytes += mmInStream.read(buffer, bytes, buffer.length - bytes);
                        for (int i = begin; i < bytes; i++) {
                            if (buffer[i] == "#".getBytes()[0]) {
                                mHandler.obtainMessage(1, begin, i, buffer).sendToTarget();
                                begin = i + 1;
                                if (i == bytes - 1) {
                                    bytes = 0;
                                    begin = 0;
                                }
                            }
                        }
                    } catch (IOException e) {
                        break;
                    }
                }
            }

            public void write(byte[] bytes) {
                try {
                    mmOutStream.write(bytes);
                } catch (IOException e) {
                }
            }

            public void cancel() {
                try {
                    mmSocket.close();
                } catch (IOException e) {
                }
            }
    }
}