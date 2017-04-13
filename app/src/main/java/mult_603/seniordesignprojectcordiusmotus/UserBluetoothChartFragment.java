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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialize.color.Material;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Wes on 3/13/17.
 */
public class UserBluetoothChartFragment extends Fragment {
    private static final String TAG = UserBluetoothChartFragment.class.getSimpleName();
    private LinearLayout background;
    private LineChart lineChart;
    private LineData  lineData;
    private static int xMax = 100;
    private View view;
    static double iteration = 1.0;
    private int xValue = 0;
    ArrayList<Entry> entries = new ArrayList<>();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference userVitalsReference = firebaseDatabase.getReference(currentUser.getUid()).child("Vitals");
    private ArrayList<Double> vitalsArray = new ArrayList<>(100);


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
                    // If there is a number then add it to the graph
                    try{
                        double vDouble  = 0.0;

                        String[] newStrings = readLine.split(",");
                        String vString  = newStrings[0];

                        // Replace the things we don't need in the V String.
                        vString = vString.replace("V = ", "").trim();

                        // Parse it to a double
                        vDouble = Double.parseDouble(vString);

                        // Create an entry
                        Entry a = new Entry((float) iteration, (float) vDouble);

                        // Add entry to the chart
                        addEntryToChart((float) iteration, (float) vDouble);

                        // Add to the values array list
                        vitalsArray.add(vDouble);

                        // If x value is 100 reset it
                        if (xValue == 100){
                            userVitalsReference.setValue(vitalsArray);
                            vitalsArray.clear();
                            xValue = 0;
                        }
                        xValue ++;


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
        description.setText("Heart Rate Data");
        description.setTextColor(R.color.colorPrimaryDark);
        lineChart.setDescription(description);

        lineChart.setBackgroundColor(Color.LTGRAY);
        lineChart.setNoDataText("No Bluetooth Heart Rate Data to Display");
        lineChart.setNoDataTextColor(R.color.colorPrimaryDark);
        lineChart.setTouchEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(true);
        lineChart.setGridBackgroundColor(Color.BLACK);
        lineChart.setDrawGridBackground(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawBorders(true);
        lineChart.setMaxVisibleValueCount(xMax);
        lineChart.setAutoScaleMinMaxEnabled(true);

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
        legend.setTextColor(Color.BLACK);
        legend.setTextSize(15f);
        legend.setMaxSizePercent(25);
        legend.setDrawInside(true);
        legend.setFormSize(25);
        legend.setDirection(Legend.LegendDirection.RIGHT_TO_LEFT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
//        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);

        // Set up the charts x and y axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setAxisLineWidth(3f);
        xAxis.setDrawLabels(false);
        xAxis.setTextColor(Color.LTGRAY);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setGridColor(Color.RED);
        xAxis.setDrawGridLines(true);
        xAxis.setAvoidFirstLastClipping(true);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setDrawLabels(false);
        yAxis.setTextColor(Color.LTGRAY);
        yAxis.setAxisLineColor(Color.BLACK);
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
//        lineChart.setLogEnabled(true);
    }

    // Add an entry to the chart
    private void addEntryToChart(float x, float y){
        LineData data = lineChart.getData();
        Log.i(TAG, "Adding Entry");

        if(data != null){
//            Log.i(TAG, "Data is not null");
            LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);

            if(set == null){
//                Log.i(TAG, "Set is null");
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
        lineDataSet.setCubicIntensity(0.2f);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setColor(Color.RED);
        lineDataSet.setCircleColor(Color.RED);
        lineDataSet.setCircleColorHole(Color.RED);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setLineWidth(4f);

        // Set the data text value size to zero because it gets in the way
        lineDataSet.setValueTextSize(0f);
        return lineDataSet;
    }
}
