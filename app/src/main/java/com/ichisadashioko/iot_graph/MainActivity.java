package com.ichisadashioko.iot_graph;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends Activity {

    public LineChart line_chart;

    public ListView bluetoothListView;
    public Button btnReload;
    public BluetoothAdapter bluetoothAdapter;
    public ArrayList<String> deviceList = new ArrayList<>();
    public ArrayList<BluetoothDevice> pairedDevicesList = new ArrayList<>();


    private void loadPairedDevices() {
        deviceList.clear();
        pairedDevicesList.clear();

        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Bluetooth is disabled", Toast.LENGTH_SHORT).show();
            return;
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                deviceList.add(device.getName() + "\n" + device.getAddress());
                pairedDevicesList.add(device);
            }
        } else {
            deviceList.add("No paired devices found.");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceList);
        bluetoothListView.setAdapter(adapter);
    }

    public void reload_bluetooth_devices(){
if(bluetoothAdapter == null){
    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
}

if(bluetoothAdapter == null){
    Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_SHORT).show();
    return;

}

loadPairedDevices();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        bluetoothListView = findViewById(R.id.bluetoothListView);
        btnReload = findViewById(R.id.btnReload);
        reload_bluetooth_devices();

        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload_bluetooth_devices();
            }
        });

        bluetoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice selectedDevice = pairedDevicesList.get(position);
                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                    intent.putExtra("DEVICE_ADDRESS", selectedDevice.getAddress());
                    startActivity(intent);
            }
        });



//        line_chart = findViewById(R.id.line_chart);
//        line_chart.setPinchZoom(true);
//
//        line_chart_populate_sample_data(100, 180);
    }

//    public void line_chart_populate_sample_data(int count, float range){
//        ArrayList<Entry> values = new ArrayList<Entry>();
//
//        for(int i = 0; i < count; i++){
//            float val = (float) (Math.random()*range)-30;
//            values.add(new Entry(i, val));
//        }
//
//        LineDataSet set1;
//
//        if(line_chart.getData() != null && line_chart.getData().getDataSetCount() > 0){
//            set1 = (LineDataSet) line_chart.getData().getDataSetByIndex(0);
//            set1.setValues(values);
//            set1.notifyDataSetChanged();
//            line_chart.getData().notifyDataChanged();
//            line_chart.notifyDataSetChanged();
//        }else{
//            set1 = new LineDataSet(values, "DataSet 1");
//            set1.setDrawIcons(false);
//
//            // draw dashed line
//            set1.enableDashedLine(10f, 5f, 0f);
//
//            // black lines and points
//            set1.setColor(Color.BLACK);
//            set1.setCircleColor(Color.BLACK);
//
//            // line thickness and point size
//            set1.setLineWidth(1f);;
//            set1.setCircleRadius(3f);
//
//            // draw points as solid circles
//            set1.setDrawCircleHole(false);
//
//            // customize legend entry
//            set1.setFormLineWidth(1f);
//            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
//            set1.setFormSize(15.f);
//
//            // text size of values
//            set1.setValueTextSize(9f);
//
//            // draw selection line as dashed
//            set1.enableDashedHighlightLine(10f, 5f, 0f);
//
//            // set the filled area
//            set1.setDrawFilled(true);
//            set1.setFillFormatter(new IFillFormatter() {
//                @Override
//                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
//                    return line_chart.getAxisLeft().getAxisMinimum();
//                }
//            });
//
//            // set color of filled area
//                set1.setFillColor(Color.BLACK);
//
//            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
//            dataSets.add(set1); // add the data sets
//
//            // create a data object with the data sets
//            LineData data = new LineData(dataSets);
//
//            // set data
//            line_chart.setData(data);
//        }
//    }
}