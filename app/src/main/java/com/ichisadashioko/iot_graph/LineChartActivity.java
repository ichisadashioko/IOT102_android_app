package com.ichisadashioko.iot_graph;

import android.app.Activity;


import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.view.View;
import android.widget.Button;

import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class LineChartActivity extends Activity {
    private LineChart line_chart;
    public Button button_plot_random_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.line_chart_activity);

        line_chart = findViewById(R.id.line_chart_1);
        button_plot_random_data = findViewById(R.id.button_plot_random_data);

//        setupChart();

        Activity that = this;
        button_plot_random_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.LAST_PARSED_DATA = new ArrayList<>();
                float min_range = 20;
                float max_range = 40;
                int unixTime = (int) (System.currentTimeMillis() / 1000L);
                Random rand = new Random();
                for (int i = 0; i < 100; i++) {
                    LogDataPoint item = new LogDataPoint();
                    item.unix_ts = unixTime + i;
                    item.temperature = min_range + rand.nextFloat() * (max_range - min_range);
                    Utils.LAST_PARSED_DATA.add(item);
                }

                that.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        setupChart();
                        line_chart.setPinchZoom(true);
//                        plotTemperatureData(line_chart, Utils.LAST_PARSED_DATA);
//                        render_line_chart();
                        stackoverflow_draw_line_chart();
                    }
                });
            }
        });
    }

    //    private void setupChart() {
//        if (Utils.LAST_PARSED_DATA == null) {
//            Utils.toast(this, "LAST_PARSED_DATA == null");
//        } else {
//            ArrayList<Entry> entries = new ArrayList<>();
//            for (int i = 0; i < Utils.LAST_PARSED_DATA.size(); i++) {
//                LogDataPoint item = Utils.LAST_PARSED_DATA.get(i);
//
//                long timestamp = item.unix_ts;  // Unix time
//                float temperature = item.temperature; // Temperature
//
//                // Convert Unix timestamp to relative time (seconds from start)
//                if (entries.isEmpty()) {
//                    entries.add(new Entry(0, temperature));
//                } else {
//                    long firstTimestamp = Utils.LAST_PARSED_DATA.get(0).unix_ts;
//                    float timeInSeconds = (timestamp - firstTimestamp);
//                    entries.add(new Entry(timeInSeconds, temperature));
//                }
//            }
//
//            LineDataSet dataSet = new LineDataSet(entries, "Temperature (°C)");
//            dataSet.setColor(Color.BLUE);
//            dataSet.setCircleColor(Color.RED);
//            dataSet.setValueTextSize(12f);
//            dataSet.setLineWidth(2f);
//            dataSet.setDrawCircles(true);
//            dataSet.setDrawValues(false);
//
//            LineData lineData = new LineData(dataSet);
//            line_chart.setData(lineData);
//
//            // X-Axis Formatting
//            XAxis xAxis = line_chart.getXAxis();
//            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
////        xAxis.setValueFormatter((value, axis) -> formatTime(value));
//            xAxis.setValueFormatter(new ValueFormatter() {
//                @Override
//                public String getFormattedValue(float value) {
//                    return formatTime(value);
//                }
//            });
//
//            // Y-Axis Settings
//            YAxis leftAxis = line_chart.getAxisLeft();
//            leftAxis.setDrawGridLines(true);
//            line_chart.getAxisRight().setEnabled(false);  // Hide right axis
//
//            line_chart.getDescription().setEnabled(false);
//            line_chart.invalidate(); // Refresh chart
//        }
//
//    }
//
//    private String formatTime(float seconds) {
//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
//        return sdf.format(new Date((long) seconds * 1000));
//    }
    private void stackoverflow_draw_line_chart() {
        ArrayList<Entry> lineEntries = new ArrayList<Entry>();
        int min_time = Utils.LAST_PARSED_DATA.get(0).unix_ts;
        int max_time = Utils.LAST_PARSED_DATA.get(Utils.LAST_PARSED_DATA.size() - 1).unix_ts;

        for (int i = 0; i < Utils.LAST_PARSED_DATA.size(); i++) {
            LogDataPoint d = Utils.LAST_PARSED_DATA.get(i);
//            lineEntries.add(new Entry(d.unix_ts, d.temperature));
            lineEntries.add(new Entry(d.unix_ts - min_time, d.temperature));
//            lineEntries.add(new Entry(i, d.temperature));
            System.out.println(d.unix_ts);
            System.out.println(d.temperature);
        }

        LineDataSet lineDataSet = new LineDataSet(lineEntries, "temperature");
//        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Work");
//        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
//        lineDataSet.setLineWidth(2);
//        lineDataSet.setDrawValues(false);
//        lineDataSet.setColor(Color.CYAN);
//        lineDataSet.setCircleRadius(6);
//        lineDataSet.setCircleHoleRadius(3);
//        lineDataSet.setDrawCircles(false);
//        lineDataSet.setDrawHighlightIndicators(true);
//        lineDataSet.setHighlightEnabled(true);
//        lineDataSet.setHighLightColor(Color.CYAN);
//        lineDataSet.setValueTextSize(12);
//        lineDataSet.setValueTextColor(Color.DKGRAY);
//        lineDataSet.setMode(LineDataSet.Mode.STEPPED);

        LineData lineData = new LineData(lineDataSet);
//        line_chart.getDescription().setTextSize(12);
//        line_chart.getDescription().setEnabled(false);
//        line_chart.animateY(1000);
        line_chart.setData(lineData);

        // Setup X Axis
        XAxis xAxis = line_chart.getXAxis();
//        xAxis.setAxisMinimum(min_time);
//        xAxis.setAxisMaximum(max_time);
        int x_axis_min = min_time - min_time;
        int x_axis_max = max_time - min_time;
        xAxis.setAxisMinimum(x_axis_min);
        xAxis.setAxisMaximum(max_time - min_time);
        System.out.println("min_time: " + min_time);
        System.out.println("max_time: " + max_time);
        System.out.println("x_axis_min: " + x_axis_min);
        System.out.println("x_axis_max: " + x_axis_max);

//        xAxis.setPosition(XAxis.XAxisPosition.TOP);
//        xAxis.setGranularityEnabled(true);
//        xAxis.setGranularity(1.0f);
//        xAxis.setXOffset(1f);
//        xAxis.setLabelCount(25);
//        xAxis.setAxisMinimum(0);
//        xAxis.setAxisMaximum(24);

        // Setup Y Axis
        YAxis yAxis = line_chart.getAxisLeft();
        yAxis.setAxisMinimum(10);
        yAxis.setAxisMaximum(50);
        yAxis.setGranularity(1f);

//        line_chart.getAxisRight().setEnabled(false);
        line_chart.invalidate();
    }

    public void render_line_chart() {
        ArrayList<Entry> values = new ArrayList<Entry>();

        for (int i = 0; i < Utils.LAST_PARSED_DATA.size(); i++) {
            LogDataPoint d = Utils.LAST_PARSED_DATA.get(i);
            values.add(new Entry(d.unix_ts, d.temperature));
            System.out.println(d.unix_ts);
            System.out.println(d.temperature);
        }

        LineDataSet set1;

        if (line_chart.getData() != null && line_chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) line_chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            line_chart.getData().notifyDataChanged();
            line_chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "DataSet 1");
            set1.setDrawIcons(false);

            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f);

            // black lines and points
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);

            // line thickness and point size
            set1.setLineWidth(1f);
            ;
            set1.setCircleRadius(3f);

            // draw points as solid circles
            set1.setDrawCircleHole(false);

            // customize legend entry
//            set1.setFormLineWidth(1f);
//            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
//            set1.setFormSize(15.f);

            // text size of values
            set1.setValueTextSize(9f);

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f);

            // set the filled area
            set1.setDrawFilled(true);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return line_chart.getAxisLeft().getAxisMinimum();
                }
            });

            // set color of filled area
            set1.setFillColor(Color.BLACK);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets

            // create a data object with the data sets
            LineData data = new LineData(dataSets);

            // set data
            line_chart.setData(data);
        }

    }

//    public void plotTemperatureData(LineChart lineChart, ArrayList<LogDataPoint> dataPoints) {
//        ArrayList<Entry> entries = new ArrayList<>();
//
//        for (LogDataPoint point : dataPoints) {
//            entries.add(new Entry(point.unix_ts, point.temperature));
//        }
//
//        // Create dataset
//        LineDataSet dataSet = new LineDataSet(entries, "Temperature over Time");
//        dataSet.setColor(0xFF6200EE);  // Line color
//        dataSet.setValueTextSize(10f); // Value text size
//        dataSet.setCircleRadius(3f);   // Circle size at data points
//
//        // Create LineData object
//        LineData lineData = new LineData(dataSet);
//        lineChart.setData(lineData);
//        lineChart.invalidate(); // Refresh the chart
//
//        // Format X-axis (time)
//        XAxis xAxis = lineChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
////        xAxis.setValueFormatter((value, axis) -> formatTime((long) value));
//
//        xAxis.setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                return formatTime((long) value);
//            }
//        });
//
//        // Y-axis settings
//        YAxis leftAxis = lineChart.getAxisLeft();
//        leftAxis.setGranularity(0.1f);
//        lineChart.getAxisRight().setEnabled(false); // Hide right Y-axis
//    }
//
//    // Function to format Unix timestamp to HH:mm:ss
//    private String formatTime(long unixTimestamp) {
//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
//        return sdf.format(new Date(unixTimestamp * 1000));
//    }
}
