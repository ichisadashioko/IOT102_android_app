package com.ichisadashioko.iot_graph;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.UUID;

public class DashboardActivity extends Activity {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    public Button button_turn_on_fan;
    public Button button_turn_off_fan;
    public Button button_set_threshold;
    public Button button_download_data;
    public String deviceAddress;
    private TextView textView;
    public EditText threshold_input_text;
    private static final UUID HC05_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public InputStream hc05_input_stream;
    public OutputStream hc05_output_stream;
    public BluetoothSocket hc05_bluetooth_socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.dashboard_activity);
        Intent intent = getIntent();
        deviceAddress = intent.getStringExtra("DEVICE_ADDRESS");
        textView = findViewById(R.id.tv_device_info);

        button_turn_off_fan = findViewById(R.id.button_turn_off_fan);
        button_download_data = findViewById(R.id.button_download_data);
        button_set_threshold = findViewById(R.id.button_set_threshold);
        threshold_input_text = findViewById(R.id.edit_text_threshold_input);

        button_turn_off_fan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_turn_off_fan_clicked();
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

//        .setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                button_turn_on_fan_clicked();
//            }
//        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (deviceAddress != null) {
            System.out.println("deviceAddress");
            System.out.println(deviceAddress);
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);
            textView.setText("Connected to: " + bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress());
            try {
                hc05_bluetooth_socket = bluetoothDevice.createRfcommSocketToServiceRecord(HC05_UUID);
                hc05_bluetooth_socket.connect();
                hc05_input_stream = hc05_bluetooth_socket.getInputStream();
                hc05_output_stream = hc05_bluetooth_socket.getOutputStream();

                System.out.println("Connected to HC-05");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        button_turn_on_fan = findViewById(R.id.button_turn_on_fan);
        button_turn_on_fan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_turn_on_fan_clicked();
            }
        });
    }

    public void button_turn_on_fan_clicked() {
        if (hc05_output_stream == null) {
            Toast.makeText(this, "hc05_output_stream is null", Toast.LENGTH_SHORT).show();
        } else {
            try {
                hc05_output_stream.write(new byte[]{Utils.BT_CMD_CODE_ENABLE_FAN});
//                hc05_output_stream.write(Utils.BT_CMD_CODE_ENABLE_FAN);
                hc05_output_stream.flush();
                if (hc05_input_stream != null) {
//                    int retval = hc05_input_stream.read();
//                    System.out.println("HC05 retval after turning fan on");
//                    System.out.println(retval);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void button_turn_off_fan_clicked() {
        if (hc05_output_stream == null) {
            Toast.makeText(this, "hc05_output_stream is null", Toast.LENGTH_SHORT).show();
        } else {
            try {
                hc05_output_stream.write(new byte[]{Utils.BT_CMD_CODE_DISABLE_FAN});
//                hc05_output_stream.write(Utils.BT_CMD_CODE_ENABLE_FAN);
                hc05_output_stream.flush();
                if (hc05_input_stream != null) {
//                    int retval = hc05_input_stream.read();
//                    System.out.println("HC05 retval after turning fan on");
//                    System.out.println(retval);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void button_download_data_clicked() {
        if (hc05_output_stream == null) {
            Toast.makeText(this, "hc05_output_stream is null", Toast.LENGTH_SHORT).show();
        } else {
            try {
                hc05_output_stream.write(new byte[]{Utils.BT_CMD_CODE_DOWNLOAD_DATA});
//                hc05_output_stream.write(Utils.BT_CMD_CODE_ENABLE_FAN);
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
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void button_set_threshold_clicked() {
        if (hc05_output_stream == null) {
            Toast.makeText(this, "hc05_output_stream is null", Toast.LENGTH_SHORT).show();
        } else {
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

//                hc05_output_stream.write(Utils.BT_CMD_CODE_ENABLE_FAN);
                    hc05_output_stream.flush();
                    if (hc05_input_stream != null) {
//                    int retval = hc05_input_stream.read();
//                    System.out.println("HC05 retval after turning fan on");
//                    System.out.println(retval);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}

