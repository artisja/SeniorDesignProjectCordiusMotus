package mult_603.seniordesignprojectcordiusmotus;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Wes on 10/5/16.
 */
public class BluetoothListAdapter extends BaseAdapter implements ListAdapter {
    public final String TAG = BluetoothListAdapter.class.getSimpleName();
    private ArrayList<String> bluetoothNameList;
    private Context context;

    public BluetoothListAdapter(ArrayList<String> bluetoothNameList, Context context){
        this.bluetoothNameList = bluetoothNameList;
        this.context = context;
    }
    @Override
    public int getCount() {
        Log.i(TAG, "Count of the List of devices is: " + bluetoothNameList.size());
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
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.bluetooth_row_item, null);
            Log.i(TAG, "Set up a view that was initially null");
        }

        // Device name text view
        TextView deviceName = (TextView) view.findViewById(R.id.bluetooth_device_name);
        deviceName.setText(bluetoothNameList.get(position));

        // Device address text view
        TextView deviceAddress = (TextView) view.findViewById(R.id.bluetooth_device_address);


        // Device pair button
        Button devicePairButton = (Button) view.findViewById(R.id.bluetooth_device_pair_button);

        devicePairButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Device Pair Button was clicked", Toast.LENGTH_LONG).show();
                Log.i(TAG, "Device Pair Button was clicked");
            }
        });

        return view;
    }
}
