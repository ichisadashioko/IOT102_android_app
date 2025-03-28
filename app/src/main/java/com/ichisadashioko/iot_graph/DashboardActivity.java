package com.ichisadashioko.iot_graph;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public class DashboardActivity extends Activity {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    public ToggleButton toggle_button_force_fan_on;
    public Button button_set_threshold;
    public Button button_download_data;
    public String deviceAddress;
    private TextView textView;
    public EditText threshold_input_text;
    private static final UUID HC05_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public InputStream hc05_input_stream;
    public OutputStream hc05_output_stream;
    public BluetoothSocket hc05_bluetooth_socket;

    public void reconnect_bluetooth_device() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (deviceAddress != null) {
            System.out.println("deviceAddress");
            System.out.println(deviceAddress);
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText("Connected to: " + bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress());
                }
            });

            try {
                hc05_bluetooth_socket = bluetoothDevice.createRfcommSocketToServiceRecord(HC05_UUID);
                hc05_bluetooth_socket.connect();
                hc05_input_stream = hc05_bluetooth_socket.getInputStream();
                hc05_output_stream = hc05_bluetooth_socket.getOutputStream();

                System.out.println("Connected to HC-05");
            } catch (IOException e) {
                e.printStackTrace();
                Utils.toast(this, e.getMessage());
            }
        }
    }

    public ReentrantLock HC05_LOCK = new ReentrantLock();
    public ReentrantLock TOGGLE_FORCE_FAN_BUTTON_LOCK = new ReentrantLock();

    public void toggle_force_fan_on(boolean is_on) {
        if (background_task_running) {
            // TODO
            return;
        }

        if (!UI_LOCK.tryLock()) {
            return;
        }

        background_task_running = true;
        UI_LOCK.unlock();

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("working");
        progressDialog.setCancelable(false); // Prevent user from dismissing
        progressDialog.show();

        Activity context = this;

        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {

                    if (!TOGGLE_FORCE_FAN_BUTTON_LOCK.tryLock()) {
                        Utils.toast(context, "last toggle force fan on command is not finished!");
                    } else {
                        try {
                            HC05_LOCK.lock();
                            try {
                                if (hc05_output_stream == null) {
                                    reconnect_bluetooth_device();
                                }
                            } finally {
                                HC05_LOCK.unlock();
                            }

                            if (hc05_output_stream == null) {
                                Utils.toast(context, "hc05_output_stream is null");
                            } else {
                                try {
                                    byte b_value;
                                    if (is_on) {
                                        b_value = Utils.BT_CMD_CODE_ENABLE_FAN;
                                    } else {
                                        b_value = Utils.BT_CMD_CODE_DISABLE_FAN;
                                    }
                                    hc05_output_stream.write(new byte[]{b_value});
                                    hc05_output_stream.flush();
                                    if (hc05_input_stream != null) {
                                        int retval = hc05_input_stream.read();
                                        String log_message = "HC05 retval after toggle force fan on: " + retval;
                                        System.out.println(log_message);
                                        if (retval == 0) {
                                            if (is_on) {
                                                toggle_button_force_fan_on.setBackgroundColor(Color.parseColor("#00ff00"));
                                            } else {
                                                toggle_button_force_fan_on.setBackgroundColor(Color.parseColor("#ff0000"));
                                            }

                                            Utils.toast(context, "toggle force fan on OK (" + b_value + ")");
                                        } else {
                                            Utils.toast(context, log_message);
                                        }
                                    } else {
                                        Utils.toast(context, "hc05_input_stream == null");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Utils.toast(context, e.getMessage());
                                }
                            }
                        } finally {
                            TOGGLE_FORCE_FAN_BUTTON_LOCK.unlock();
                        }
                    }
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                    System.err.println(ex.toString());
                } finally {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });
                    background_task_running = false;
                }

            }
        };
        Thread t = new Thread(task);
        t.start();
    }

    public ReentrantLock UI_LOCK = new ReentrantLock();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.dashboard_activity);
        Intent intent = getIntent();
        deviceAddress = intent.getStringExtra("DEVICE_ADDRESS");
        textView = findViewById(R.id.tv_device_info);

        toggle_button_force_fan_on = findViewById(R.id.toggle_button_force_fan_on);
        button_download_data = findViewById(R.id.button_download_data);
        button_set_threshold = findViewById(R.id.button_set_threshold);
        threshold_input_text = findViewById(R.id.edit_text_threshold_input);

        toggle_button_force_fan_on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                toggle_force_fan_on(b);
            }
        });

        button_download_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_download_data_clicked();
            }
        });

        button_set_threshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_set_threshold_clicked();
            }
        });

        HC05_LOCK.lock();
        try {
            reconnect_bluetooth_device();
        } finally {
            HC05_LOCK.unlock();
        }
    }

    public boolean background_task_running = false;

    public void button_download_data_clicked() {
        if (background_task_running) {
            // TODO
            return;
        }

        if (!UI_LOCK.tryLock()) {
            return;
        }

        background_task_running = true;
        UI_LOCK.unlock();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("working");
        progressDialog.setCancelable(false); // Prevent user from dismissing
        progressDialog.show();

        Activity context = this;

        Runnable task = new Runnable() {
            @Override
            public void run() {

                try {

                    HC05_LOCK.lock();
                    try {
                        if (hc05_output_stream == null) {
                            reconnect_bluetooth_device();
                        }
                    } finally {
                        HC05_LOCK.unlock();
                    }

                    if (hc05_output_stream == null) {
                        Utils.toast(context, "hc05_output_stream is null");
                    } else {

                        HC05_LOCK.lock();
                        try {
                            try {
                                hc05_output_stream.write(new byte[]{Utils.BT_CMD_CODE_DOWNLOAD_DATA});
                                hc05_output_stream.flush();
                                if (hc05_input_stream != null) {
//                    ArrayList<Integer> data_list = new ArrayList<>();
//                    byte[] buffer = new byte[64];
//                    while (true) {
//
//                        int data_count = hc05_input_stream.read(buffer);
//                        if (data_count < 1) {
//                            break;
//                        }
//
//                        for (int i = 0; i < data_count; i++) {
//                            data_list.add((int) buffer[i]);
//                        }
//                    }
//                    StringBuilder receivedData = new StringBuilder();
//                    for (int value : data_list) {
//                        receivedData.append((char) (value & 0xFF));  // Convert each byte to a character
//                    }
//
//// Print the result
//                    String result = receivedData.toString();
//                    System.out.println("Received Data: " + result);
//
//                    Intent intent = new Intent(DashboardActivity.this, LineChartActivity.class);
//                    intent.putExtra("STRING_DATA", result);
//                    startActivity(intent);
//
////                    int retval = hc05_input_stream.read();
////                    System.out.println("HC05 retval after turning fan on");
////                    System.out.println(retval);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.toast(context, e.getMessage());
                            }
                        } finally {
                            HC05_LOCK.unlock();
                        }
                    }
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                    System.err.println(ex.toString());
                } finally {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });
                    background_task_running = false;
                }

            }
        };
        Thread t = new Thread(task);
        t.start();

    }

    public void button_set_threshold_clicked() {
        if (background_task_running) {
            // TODO
            return;
        }

        if (!UI_LOCK.tryLock()) {
            return;
        }

        background_task_running = true;
        UI_LOCK.unlock();

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("working");
        progressDialog.setCancelable(false); // Prevent user from dismissing
        progressDialog.show();

        Activity context = this;

        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    HC05_LOCK.lock();
                    try {
                        if (hc05_output_stream == null) {
                            reconnect_bluetooth_device();
                        }
                    } finally {
                        HC05_LOCK.unlock();
                    }
                    if (hc05_output_stream == null) {
                        Utils.toast(context, "hc05_output_stream is null");
                    } else {
                        HC05_LOCK.lock();
                        try {
                            String input_str = threshold_input_text.getText().toString();
                            try {
                                float input_value = Float.parseFloat(input_str);
                                // Allocate a ByteBuffer with 4 bytes
                                ByteBuffer buffer = ByteBuffer.allocate(4);

                                // Set byte order to LITTLE_ENDIAN
                                buffer.order(ByteOrder.LITTLE_ENDIAN);

                                // Put float value into the buffer
                                buffer.putFloat(input_value);

                                // Get the byte array
                                byte[] littleEndianBytes = buffer.array();
                                for (int i = 0; i < littleEndianBytes.length; i++) {
                                    System.out.println(littleEndianBytes[i]);
                                }

                                hc05_output_stream.write(new byte[]{Utils.BT_CMD_CODE_SET_THRESHOLD});
                                hc05_output_stream.write(littleEndianBytes);

                                hc05_output_stream.flush();
                                if (hc05_input_stream != null) {
                                    int retval = hc05_input_stream.read();
                                    String log_message = "HC05 retval after BT_CMD_CODE_SET_THRESHOLD: " + retval;
                                    System.out.println(log_message);
                                    if (retval == 0) {
                                        Utils.toast(context, "BT_CMD_CODE_SET_THRESHOLD OK");
                                    } else {
                                        Utils.toast(context, log_message);
                                    }
                                } else {
                                    Utils.toast(context, "hc05_input_stream == null");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.toast(context, e.getMessage());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.toast(context, e.getMessage());
                        } finally {
                            HC05_LOCK.unlock();
                        }
                    }
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                    System.err.println(ex.toString());
                } finally {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });
                    background_task_running = false;
                }

            }
        };
        Thread t = new Thread(task);
        t.start();
        //

    }
}

