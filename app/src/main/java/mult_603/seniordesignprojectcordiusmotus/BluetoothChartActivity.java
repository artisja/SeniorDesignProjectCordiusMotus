package mult_603.seniordesignprojectcordiusmotus;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class BluetoothChartActivity extends AppCompatActivity {
    public static final String TAG = BluetoothChartActivity.class.getSimpleName();
    private LineChart  lineChart;
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch(msg.what){
                case BluetoothActivity.SUCCESSFUL_CONNECTION:
                    BluetoothActivity.connectedThread = new BluetoothActivity.ConnectedThread((BluetoothSocket) msg.obj);
                    Log.i(TAG, "Connected Successfully Starting thread ");
                    BluetoothActivity.connectedThread.start();
                    break;

                case BluetoothActivity.READING_MESSAGE:
                    Log.i(TAG, "Reading Message ");
                    byte[] read = (byte[]) msg.obj;
                    String str = new String(read, 0, 6);
                    String nxt = new String(read, 6, 12);
                    String aft = new String(read, 12, 18);
                    Log.i(TAG, "Strings " + str + " " + nxt + " " + aft);

                    // Split up the String

                    // If there is a numeber then add it to the graph

                    // If its outside of the view then reset the view

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_chart);

        //Get Handler
        BluetoothActivity.getHandler();

        // Create the line chart
        lineChart = (LineChart) findViewById(R.id.bluetooth_chart);

        List<Entry> entries = new ArrayList<>();

        // Turn the data from bluetooth into entries
        entries.add(new Entry((float) Math.random() , (float) Math.random() ));
        entries.add(new Entry((float) Math.random() , (float) Math.random() ));
        entries.add(new Entry((float) Math.random() , (float) Math.random() ));


        // Add List Entry to Line Data Set Object
        LineDataSet lineDataSet = new LineDataSet(entries, "Example HR");
        lineDataSet.setColor(R.color.wordColorRed);
        lineDataSet.setCircleHoleRadius(4f);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleColor(R.color.colorPrimaryDark);
        lineDataSet.setValueTextColor(R.color.wordColorRed);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        // For Dynamic Data Notify dataset changed must be called before invalidate
        //lineDataSet.notifyDataSetChanged();
        //lineChart.notifyDataSetChanged();

        // Refresh chart
        //lineChart.invalidate();

        // Logcat output bad for the processing but may be necessary
        //lineChart.setLogEnabled(true);

        lineChart.setNoDataText("No Bluetooth Heart Rate Data to Display");
        lineChart.setNoDataTextColor(R.color.wordColorRed);


    }
}
