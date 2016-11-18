package mult_603.seniordesignprojectcordiusmotus;

import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BluetoothChartActivity extends AppCompatActivity {

    public static final String TAG = BluetoothChartActivity.class.getSimpleName();
    private LinearLayout    background;
    private LineChart       lineChart;
    private LineData        lineData;
    private static int      xView = 100;
    static double           iteration = 1.0;
    ArrayList<Entry>        entries = new ArrayList<>();

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

                    //Log.i(TAG, "Strings " + strings);

                    // If there is a number then add it to the graph
                    try{
                        Log.i(TAG, "VString -- > " + vString);
                        vString = vString.replace("V = ", "");
                        vDouble = Double.parseDouble(vString);
                        Log.i(TAG, "Iteration --> " + iteration);
                        Log.i(TAG, "V Double --> " + vDouble);

                        // Create an entry
                        Entry a = new Entry((float) iteration, (float) vDouble);

                        // Add entry to the chart
                        addEntryToChart((float) iteration, (float) vDouble);
                        Log.i(TAG, "Entries Contains a " + entries.contains(a));

                        if(iteration >= xView){
                            Log.i(TAG, "Set the iterations back to zero");
                            iteration = 0;
//                            lineChart.notifyDataSetChanged();
//                            lineChart.invalidate();
                        }
                        else{
                            Log.i(TAG, "Update the number of iterations");
                            iteration += 1;
                        }

                    }catch(Exception e){
                        Log.i(TAG, "Exception occured while trying to parse double " + e.getMessage());
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_chart);
        Log.i(TAG, "On Create was called");

        // Set Handler
        BluetoothActivity.setHandler(mHandler);

        // Set the background color
        background = (LinearLayout) findViewById(R.id.chart_bg);
        background.setBackgroundColor(Color.BLACK);

        // Create the line chart
        lineChart = (LineChart) findViewById(R.id.bluetooth_chart);
        Description description = new Description();
        description.setText("Heart Rate Data For User");
        description.setTextColor(Color.WHITE);
        lineChart.setBackgroundColor(Color.LTGRAY);
        lineChart.setDescription(description);
        lineChart.setNoDataText("No Bluetooth Heart Rate Data to Display");
        lineChart.setNoDataTextColor(Color.WHITE);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setGridBackgroundColor(Color.MAGENTA);
        lineChart.setDrawMarkers(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawBorders(true);
//        lineChart.setMaxVisibleValueCount(100);

        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setFormLineWidth(5f);
        legend.setTextColor(Color.WHITE);
        legend.setMaxSizePercent(25);
        legend.setDrawInside(true);
        legend.setFormSize(25);
        legend.setDirection(Legend.LegendDirection.RIGHT_TO_LEFT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setAxisLineWidth(3f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setDrawGridLines(false);
        yAxis.setTextColor(Color.WHITE);
        yAxis.setAxisLineColor(Color.WHITE);
        yAxis.setAxisLineWidth(3f);
        yAxis.setAxisMaximum(50f);

        YAxis yAxis2 = lineChart.getAxisRight();
        yAxis2.setEnabled(false);

        lineData = new LineData();
        lineData.setValueTextColor(Color.WHITE);
        lineChart.setData(lineData);

        lineChart.invalidate();

        // Logcat output bad for the processing but may be necessary
        //lineChart.setLogEnabled(true);

    }


    // Add an entry to the chart
    private void addEntryToChart(float x, float y){
        LineData data = lineChart.getData();
        Log.i(TAG, "Adding Entry");

        if(data != null){
            Log.i(TAG, "Data is not null");
            LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);

            if(set == null){
                Log.i(TAG, "Set is null");
                set = createSet();
                data.addDataSet(set);
            }

            // Add a value
            Entry e = new Entry(x , y);
            data.addEntry(e, 0);

            // Notify the data set that is has changed
            data.notifyDataChanged();

            // Enable the chart to know when the value has changed
            lineChart.notifyDataSetChanged();

            // Limit the number of visible entries
            lineChart.setVisibleXRangeMaximum(xView);

            lineChart.setVisibleXRange((float) 0.0, (float) 100.0);
            lineChart.setVisibleYRange((float) 0.0, (float) 1000.0, YAxis.AxisDependency.LEFT);

            // Move to the last x value
            //lineChart.moveViewToX(data.getEntryCount());

            // Refresh chart
            lineChart.invalidate();

            // If the data values are greater than the maximum x value clear the list
            if(data.getEntryCount() >= xView){
                data.clearValues();
            }
            data.notifyDataChanged();
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i(TAG, "On Resume Called");
        // Real time data
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run(){
                // Add data
                Log.i(TAG, "Running Thread");


                    try {
                        // Pause between adds
                        Thread.sleep(600);
                    }
                    catch(InterruptedException e){
                        Log.i(TAG, "Interrupted Exception " + e.getMessage());
                        e.printStackTrace();
                    }
                }
           // }
        });

        thread.start();

    }

    // Create Set
    private LineDataSet createSet(){
        Log.i(TAG, "Creating Set");
        LineDataSet lineDataSet = new LineDataSet(null , "Heart Rate Data");
        lineDataSet.setDrawCircles(true);
        lineDataSet.setCubicIntensity(0.2f);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setColor(Color.GREEN);
        lineDataSet.setCircleColor(Color.RED);
        lineDataSet.setLineWidth(4f);
        lineDataSet.setCircleHoleRadius(8f);
        lineDataSet.setFillAlpha(65);
        lineDataSet.setFillColor(Color.WHITE);
        lineDataSet.setHighLightColor(Color.LTGRAY);
//        lineDataSet.setValueTextColor(Color.YELLOW);
//        lineDataSet.setValueTextSize(10f);
        return lineDataSet;
    }
}
