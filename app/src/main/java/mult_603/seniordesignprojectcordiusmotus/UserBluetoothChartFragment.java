package mult_603.seniordesignprojectcordiusmotus;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
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
import java.util.ArrayList;

/**
 * Created by Wes on 3/13/17.
 * Fragment for viewing User's Heart rate in real time
 * The graph is a line chart and it moves after filling up with a certain amount of values
 * The graphing library we used was MPAndroid Chart
 */
public class UserBluetoothChartFragment extends Fragment {
    private static final String TAG = UserBluetoothChartFragment.class.getSimpleName();
    private LinearLayout background;
    private LineChart    lineChart;
    private LineData     lineData;
    private View         view;
    private static int xMax = 100;
    static double iteration = 1.0;
    private double bpm      = 0.0;
    private int xValue      = 0;
    ArrayList<Entry> entries = new ArrayList<>();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference userVitalsReference = firebaseDatabase.getReference(currentUser.getUid()).child("Vitals");
    private DatabaseReference userBpmReference    = firebaseDatabase.getReference(currentUser.getUid()).child("BPM");
    private DatabaseReference userImuReference    = firebaseDatabase.getReference(currentUser.getUid()).child("IMU");
    private ArrayList<Double> vitalsArray = new ArrayList<>(100);
    ApplicationController applicationController;
    ArrayList<String> collectContacts;
    static public boolean isOkay;
    private int testIMU = 0;
    private double testBPM = 180.0;


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);

            switch(msg.what){
                case UserBluetoothListFragment.SUCCESSFUL_CONNECTION:
                    UserBluetoothListFragment.connectedThread = new UserBluetoothListFragment.ConnectedThread((BluetoothSocket) msg.obj);
                    Log.i(TAG,  " Connected Successfully Starting thread... ");
                    UserBluetoothListFragment.connectedThread.start();
                    Log.i(TAG,  " Bluetooth Activity Connected Thread " + UserBluetoothListFragment.connectedThread.getName()
                            +   " Bluetooth Activity Connected Thread State " + UserBluetoothListFragment.connectedThread.getState()
                            +   " Bluetooth Activity Connected Thread Id    " + UserBluetoothListFragment.connectedThread.getId());

                    break;

                case UserBluetoothListFragment.READING_MESSAGE:
                    String readLine = (String) msg.obj;
                    // If there is a number then add it to the graph
                    try{
                        double vDouble   = 0.0;
                        double bpmDouble = 0.0;
                        int imuInt = 0;

                        String[] newStrings = readLine.split(",");
                        String vString   = newStrings[0];
                        String bpmString = newStrings[1];
                        String imuString = newStrings[2];

                        // Replace the things we don't need in the V String.
                        vString   = vString.replace("V = ", "").trim();
                        bpmString = bpmString.replace("B = ", "").trim();
                        imuString = imuString.replace("M = ", "").trim();

                        // Parse it to a double
                        // IMU is 1 then moving
                        // IMU is 0 then not moving
                        vDouble   = Double.parseDouble(vString);
                        bpmDouble = Double.parseDouble(bpmString);
//                        imuInt    = Integer.parseInt(imuString);

                        // Update user's BPM
                        bpm = bpmDouble;


                        // Update IMU and BPM
                        if (xValue % 25 == 0){
                            userImuReference.setValue(imuInt);
                            userBpmReference.setValue(bpm);
                            testBPM = bpm;
//                            testIMU = imuInt;
                        }

                        Log.i(TAG, "V: " + vDouble + " BPM: " + bpmDouble + " IMU: " + imuInt);

                        // If the user is not moving and the bpm is zero fire an alert
                        // IF the test IMU value and the test BPM value are zero then make the vDouble zero
                        if(testIMU == 0 && testBPM == 0.0){
                            Log.i(TAG, "An Alert Should Happen In APP");
                            // TODO Take this out later
                            vDouble = 100.0;

                            // TODO Figure out why this doesn't always work
                            final AlertDialog heartAlert;
                            AlertDialog.Builder heartAttackAlert = new AlertDialog.Builder(getContext());
                            heartAttackAlert.setMessage(R.string.dialog_message_emergency)
                                    .setPositiveButton(R.string.dialog_positive_response, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            isOkay = true;
                                            dialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton(R.string.dialog_negative_response, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            isOkay = false;
                                            dialog.dismiss();
                                            deadAction();
                                        }
                                    });
                            heartAlert = heartAttackAlert.create();
                            final Handler handler = new Handler();
                            final  Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    if (heartAlert.isShowing()){
                                        heartAlert.dismiss();
                                        deadAction();
                                    }
                                }
                            };
                            handler.postDelayed(runnable,10000);
                        }

                        // Add entry to the chart
                        addEntryToChart((float) iteration, (float) vDouble);

                        // Add to the values array list
                        vitalsArray.add(vDouble);

                        // If x value is 100 reset it
                        if (xValue == 250){
                            userVitalsReference.setValue(vitalsArray);
                            vitalsArray.clear();
                            xValue = 0;
                        }
                        xValue ++;


                    }catch(Exception e){
                        Log.i(TAG, "Exception occurred " + e.getMessage());
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };

    /**
     * Should rename this method
     * This gets called when the user's BPM is zero and the IMU value is zero
     * meaning the user is not moving
     */
    public void deadAction(){
            LocationHolder emergencyLocation = LocationService.getLocationHolder();
            String uri = "http://maps.google.com/maps?saddr=" + 37.5407 + "," + -77.4360;
            StringBuffer smsBody = new StringBuffer();
            smsBody.append("http://maps.google.com?q=");
            smsBody.append(emergencyLocation.getLatitude());
            smsBody.append(",");
            smsBody.append(emergencyLocation.getLongitude());
            Log.d(TAG, "Status code check");
            PendingIntent pi = PendingIntent.getActivity(getActivity(), 0, new Intent(getActivity(), UserAddContactFragment.class), 0);
            SmsManager sms = SmsManager.getDefault();
            DatabaseReference reference = firebaseDatabase.getReference(String.valueOf(applicationController.firebaseAuth.getCurrentUser())).child("Contacts");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<String> tempCollect = new ArrayList<String>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        collectContacts.add(data.child("number").getValue().toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            for (String contacts : collectContacts) {
                sms.sendTextMessage(contacts, null, smsBody.toString(), pi, null);
            }
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", "7577090180", null)));
    }
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

    /**
     * Set up the line chart to be displayed to the user
     */
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
    }

    /**
     * Add vital information to the line chart
     * @param x
     * @param y
     */
    private void addEntryToChart(float x, float y){
        LineData data = lineChart.getData();

        if(data != null){
            LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);

            if(set == null){
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

    /**
     * Create Line Chart Data Set set up axis and colors of the line
     * @return lineDataSet
     */
    private LineDataSet createSet(){
        Log.i(TAG, "Creating Set");
        LineDataSet lineDataSet = new LineDataSet(null , "Heart Rate Data BPM: " + bpm);
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
