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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BluetoothChartActivity extends AppCompatActivity {

    public static final String TAG = BluetoothChartActivity.class.getSimpleName();
    private LineChart  lineChart;
    List<Entry> entries = new ArrayList<>();

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            int i = 0;

            switch(msg.what){
                case BluetoothActivity.SUCCESSFUL_CONNECTION:
                    BluetoothActivity.connectedThread = new BluetoothActivity.ConnectedThread((BluetoothSocket) msg.obj);
                    Log.i(TAG, "Connected Successfully Starting thread ");
                    BluetoothActivity.connectedThread.start();
                    break;

                case BluetoothActivity.READING_MESSAGE:
                    Log.i(TAG, "Reading Message ");
                    byte[] read = (byte[]) msg.obj;

                    String pUPattern = "Pu = \\d+";
                    String pDPattern = "PD = \\d+";
                    String tPattern  = "T = \\d+";
                    String vPattern  = "V = \\d+";

                    Pattern pu = Pattern.compile(pUPattern);
                    Pattern pd = Pattern.compile(pDPattern);
                    Pattern t  = Pattern.compile(tPattern);
                    Pattern v  = Pattern.compile(vPattern);

                    String strings = new String(read);
                    Matcher puMatch = pu.matcher(strings);
                    Matcher pdMatch = pd.matcher(strings);
                    Matcher tMatch  = t.matcher(strings);
                    Matcher vMatch  = v.matcher(strings);

                    String puString = new String();
                    String pdString = new String();
                    String tString  = new String();
                    String vString  = new String();

                    double puDouble = 0.0;
                    double pdDouble = 0.0;
                    double tDouble  = 0.0;
                    double vDouble  = 0.0;

                    // Split up the String
                    if(puMatch.find()){
                        puString = puMatch.group(0);
                        Log.i(TAG, "PU Match " + puString);
                    }

                    if(pdMatch.find()){
                        pdString = pdMatch.group(0);
                        Log.i(TAG, "PD Match " + pdString);
                    }

                    if(tMatch.find()){
                        tString = tMatch.group(0);
                        Log.i(TAG, "T  Match " + tString);
                    }

                    if(vMatch.find()){
                        vString = vMatch.group(0);
                        Log.i(TAG, "V  Match " + vString );
                    }

                    Log.i(TAG, "Strings " + strings);


                    // If there is a number then add it to the graph
                    try{
                        vString = vString.replace("V = ", "");
                        vDouble = Double.parseDouble(vString);
                        Log.i(TAG, "Parsed Double -> " + vDouble);
                        entries.add(new Entry((float) i, (float) vDouble));
                        i ++;
                    }catch(Exception e){
                        Log.i(TAG, "Exception occured while trying to parse double " + e.getMessage());
                        e.printStackTrace();
                    }


                    // If its outside of the view then reset the view
                    if(i > 30){
                        i = 0;
                    }

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_chart);

        //Get Handler
        //BluetoothActivity.getHandler();
        BluetoothActivity.setHandler(mHandler);

        // Create the line chart
        lineChart = (LineChart) findViewById(R.id.bluetooth_chart);



        // Turn the data from bluetooth into entries
//        entries.add(new Entry((float) Math.random() , (float) Math.random() ));
//        entries.add(new Entry((float) Math.random() , (float) Math.random() ));
//        entries.add(new Entry((float) Math.random() , (float) Math.random() ));


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
