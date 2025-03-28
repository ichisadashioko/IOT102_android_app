package com.ichisadashioko.iot_graph;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class Utils {
    public static byte BT_CMD_CODE_SET_THRESHOLD = 1;
    public static byte BT_CMD_CODE_ENABLE_FAN = 2;
    public static byte BT_CMD_CODE_DISABLE_FAN = 3;
    public static byte BT_CMD_CODE_DOWNLOAD_DATA = 4;

    public static void toast(Activity context, String message) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
