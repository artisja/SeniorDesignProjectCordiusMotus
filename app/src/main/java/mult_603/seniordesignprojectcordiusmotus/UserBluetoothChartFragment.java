package mult_603.seniordesignprojectcordiusmotus;

import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by Wes on 3/13/17.
 */
public class UserBluetoothChartFragment extends Fragment {
    private static final String TAG = UserBluetoothChartFragment.class.getSimpleName();
    private LinearLayout background;
    private LineChart lineChart;
    private LineData  lineData;
    private static int    xMax = 100;
    private View view;
    static double         iteration = 1.0;
    ArrayList<Entry> entries = new ArrayList<>();
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);

            switch(msg.what){
                case UserBluetoothListFragment.SUCCESSFUL_CONNECTION:
                    UserBluetoothListFragment.connectedThread = new UserBluetoothListFragment.ConnectedThread((BluetoothSocket) msg.obj);
                    Log.i(TAG, "Connected Successfully Starting thread... ");
                    UserBluetoothListFragment.connectedThread.start();
                    Log.i(TAG, "Bluetooth Activity Connected Thread " + UserBluetoothListFragment.connectedThread.getName()
                            +   " Bluetooth Activity Connected Thread State " + UserBluetoothListFragment.connectedThread.getState()
                            +   " Bluetooth Activity Connected Thread Id    " + UserBluetoothListFragment.connectedThread.getId());

                    break;

                case UserBluetoothListFragment.READING_MESSAGE:
                    String readLine = (String) msg.obj;
//                    Log.i(TAG, "Read Line in Chart " + readLine);

                    double puDouble = 0.0;
                    double pdDouble = 0.0;
                    double tDouble  = 0.0;
                    double vDouble  = 0.0;

                    String[] newStrings = readLine.split(",");
                    String puString = newStrings[0];
                    String pdString = newStrings[1];
                    String tString  = newStrings[2];
                    String vString  = newStrings[3];

                    // If there is a number then add it to the graph
                    try{
//                        Log.i(TAG, "PU String -->  " + puString);
//                        Log.i(TAG, "PD String -->  " + pdString);
//                        Log.i(TAG, "T  String -->  " + tString);
//                        Log.i(TAG, "V  String -->  " + vString);

                        // Replace the things we don't need in the V String.
                        vString = vString.replace("V = ", "")
                                .replace("#", "");

                        // Parse it to a double
                        vDouble = Double.parseDouble(vString);

                        // Create an entry
                        Entry a = new Entry((float) iteration, (float) vDouble);

                        // Try to sleep
                        UserBluetoothListFragment.connectedThread.sleep(10);

                        // Add entry to the chart
                        addEntryToChart((float) iteration, (float) vDouble);

                    }catch(Exception e){
                        Log.i(TAG, "Exception occured " + e.getMessage());
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };

    // Empty Constructor
    public UserBluetoothChartFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.activity_bluetooth_chart, container, false);
        background = (LinearLayout) view.findViewById(R.id.chart_bg);
        lineChart  = (LineChart)    view.findViewById(R.id.bluetooth_chart);
        setUpChart();
        return view;
    }

    private void setUpChart(){
        // Set Handler
        UserBluetoothListFragment.setHandler(mHandler);

        // Set the background color
        background.setBackgroundColor(Color.LTGRAY);

        // Create the line chart
        Description description = new Description();
        description.setText("Heart Rate Data For User");
        description.setTextColor(Color.RED);
        lineChart.setBackgroundColor(Color.LTGRAY);
        lineChart.setDescription(description);
        lineChart.setNoDataText("No Bluetooth Heart Rate Data to Display");
        lineChart.setNoDataTextColor(R.color.colorPrimaryDark);
        lineChart.setNoDataTextColor(Color.RED);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(true);
        lineChart.setGridBackgroundColor(Color.WHITE);
        lineChart.setDrawGridBackground(true);
        lineChart.setDrawMarkers(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawBorders(true);
        lineChart.setMaxVisibleValueCount(xMax);

        try{
            lineChart.setHardwareAccelerationEnabled(true);
        }catch(Exception e){
            Log.i(TAG, "Setting hardware acceleration failed");
            e.printStackTrace();
        }

        // Set up the charts legend
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

        // Set up the charts x and y axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setAxisLineWidth(3f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setGridColor(Color.RED);
        xAxis.setDrawGridLines(true);
        xAxis.setAvoidFirstLastClipping(true);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setTextColor(Color.WHITE);
        yAxis.setAxisLineColor(Color.WHITE);
        yAxis.setGridColor(Color.RED);
        yAxis.setDrawGridLines(true);
        yAxis.setAxisLineWidth(3f);

        YAxis yAxis2 = lineChart.getAxisRight();
        yAxis2.setEnabled(false);

        lineData = new LineData();
        lineData.setValueTextColor(Color.WHITE);
        lineChart.setData(lineData);

        lineChart.invalidate();

        // Logcat output bad for the processing but may be necessary
        lineChart.setLogEnabled(true);
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

            // Add a value to the data set
            int dataSetIndex = data.getEntryCount();
            Entry e = new Entry(dataSetIndex, y);
            data.addEntry(e, 0);

            // Notify the data set that is has changed
            data.notifyDataChanged();

            // Enable the chart to know when the value has changed
            lineChart.notifyDataSetChanged();

            // Limit the number of visible entries
            lineChart.setVisibleXRangeMaximum(xMax);

            lineChart.moveViewToX(data.getXMax() - (xMax + 1));

        }
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
        lineDataSet.setValueTextColor(Color.BLUE);

        // Set the data text value size to zero because it gets in the way
        lineDataSet.setValueTextSize(0f);
        return lineDataSet;
    }
}
