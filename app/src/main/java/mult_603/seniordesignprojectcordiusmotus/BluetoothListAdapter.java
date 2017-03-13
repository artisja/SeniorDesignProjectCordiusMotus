package mult_603.seniordesignprojectcordiusmotus;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Wes on 10/5/16.
 */
public class BluetoothListAdapter extends BaseAdapter implements ListAdapter {
    public final String TAG = BluetoothListAdapter.class.getSimpleName();
    private ArrayList<BluetoothDevice> bluetoothNameList;
    private Context context;

    public BluetoothListAdapter(ArrayList<BluetoothDevice> bluetoothNameList, Context context){
        this.bluetoothNameList = bluetoothNameList;
        this.context = context;
    }
    @Override
    public int getCount() {
        return bluetoothNameList.size();
    }

    @Override
    public Object getItem(int position) {
        Log.i(TAG, "Device Name: " + bluetoothNameList.get(position) + " at position: " + position);
        return bluetoothNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // These items do not have an id if they had an id we would call get item . get id
        Log.i(TAG, "Have not yet set up an item id");
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final int listPosition = position;

        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.bluetooth_row_item, null);
        }

        // Device name text view
        TextView deviceName = (TextView) view.findViewById(R.id.bluetooth_device_name);

        // Get the bluetooth device from the list and set the text fields based on name and address
        final BluetoothDevice device = bluetoothNameList.get(position);
        deviceName.setText("Name: " + device.getName());

        // Device address text view
        TextView deviceAddress = (TextView) view.findViewById(R.id.bluetooth_device_address);
        deviceAddress.setText("Address: " + device.getAddress());

        // Device pair button
        final Button devicePairButton = (Button) view.findViewById(R.id.bluetooth_device_pair_button);

        // Device is bonded
        if(device.getBondState() == 12){
            Log.i(TAG, "Device: " + device + " bond state: " + device.getBondState() + " PAIRED");
            devicePairButton.setText("Unpair");
        }
        // Device is not bonded
        else if (device.getBondState() == 10){
            devicePairButton.setText("Pair Device");
            Log.i(TAG, "Device: " + device + " bond state: " + device.getBondState() + " UNPAIRED");
        }

        devicePairButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserBluetoothListFragment tmp = new UserBluetoothListFragment();
                BluetoothDevice bluetoothDevice = bluetoothNameList.get(listPosition);

                // Attempt to pair or unpair device
                if (devicePairButton.getText() == "Unpair"){
                    Log.i(TAG, "Attempt to unpair device from list " + bluetoothDevice.getAddress());
                    tmp.unpairDevice(bluetoothDevice);
                    devicePairButton.setText("Pair Device");
                }
                else{
                    Log.i(TAG, "Attempt to pair device from list " + bluetoothDevice.getAddress());
                    tmp.pairDevice(bluetoothDevice);
                    devicePairButton.setText("Unpair");
                }
            }
        });
        // Return the view
        return view;
    }
}
