package com.ichisadashioko.iot_graph;

import android.app.Activity;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.text.SimpleDateFormat;
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
import java.util.Date;
import java.util.Locale;
import java.util.Set;

public class LineChartActivity extends Activity {
    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.line_chart_activity);

        lineChart = findViewById(R.id.line_chart_1);
        Intent intent = getIntent();
        String data_str = intent.getStringExtra("STRING_DATA");
        ArrayList<Entry> entries = parseTemperatureData(data_str);
        setupChart(entries);
    }

    private ArrayList<Entry> parseTemperatureData(String data) {
        ArrayList<Entry> entries = new ArrayList<>();

        String[] lines = data.split("\n");
        for (String line : lines) {
            String[] parts = line.split("\\s+");
            if (parts.length == 2) {
                try {
                    long timestamp = Long.parseLong(parts[0]);  // Unix time
                    float temperature = Float.parseFloat(parts[1]); // Temperature

                    // Convert Unix timestamp to relative time (seconds from start)
                    if (entries.isEmpty()) {
                        entries.add(new Entry(0, temperature));
                    } else {
                        long firstTimestamp = Long.parseLong(lines[0].split("\\s+")[0]);
                        float timeInSeconds = (timestamp - firstTimestamp);
                        entries.add(new Entry(timeInSeconds, temperature));
                    }
                } catch (Exception e) {
                    Log.e("ParseError", "Invalid data: " + line);
                }
            }
        }
        return entries;
    }

    private void setupChart(ArrayList<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "Temperature (°C)");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.RED);
        dataSet.setValueTextSize(12f);
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(true);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // X-Axis Formatting
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setValueFormatter((value, axis) -> formatTime(value));
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return formatTime(value);
            }
        });

        // Y-Axis Settings
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        lineChart.getAxisRight().setEnabled(false);  // Hide right axis

        lineChart.getDescription().setEnabled(false);
        lineChart.invalidate(); // Refresh chart
    }

    private String formatTime(float seconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date((long) seconds * 1000));
    }
}
