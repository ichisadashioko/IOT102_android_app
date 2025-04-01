package com.ichisadashioko.iot_graph;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

public class Utils {
    public static byte BT_CMD_CODE_SET_THRESHOLD = 1;
    public static byte BT_CMD_CODE_ENABLE_FAN = 2;
    public static byte BT_CMD_CODE_DISABLE_FAN = 3;
    public static byte BT_CMD_CODE_DOWNLOAD_DATA = 4;
    public static byte BT_CMD_CODE_GET_TEMPERATURE = 5;

    public static ArrayList<LogDataPoint> LAST_PARSED_DATA = null;

    public static void toast(Activity context, String message) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static boolean is_digit(byte value) {
        return (value >= 48) && (value <= 57);
    }

    public static boolean is_digit(String s) {
        for (int i = 0; i < s.length(); i++) {
            char value = s.charAt(i);
            if ((value >= 48) && (value <= 57)) {
                continue;
            } else {
                return false;
            }
        }

        return true;
    }

    public static boolean is_valid_data_line(byte[] line_bs) {
        boolean has_tab = false;
        for (int i = 0; i < line_bs.length; i++) {
            byte _c = line_bs[i];
            if (is_digit(_c)) {
                continue;
            }

            if (_c == 9) {
                has_tab = true;
                continue;
            }

            if (_c == 46) {
                // dot character for float
                continue;
            }

            return false;
        }

        if (!has_tab) {
            return false;
        }

        try {
            String line = new String(line_bs, "ASCII");
            int tab_index = line.indexOf(9);
            String unix_ts_str = line.substring(0, tab_index);
            String temperature_value_str = line.substring(tab_index);

            if (unix_ts_str.length() == 0) {
                return false;
            }

            if (temperature_value_str.length() < 2) {
                return false;
            }

            if (!is_digit(unix_ts_str)) {
                return false;
            }

            try {
                Float.parseFloat(temperature_value_str.substring(1));
            } catch (Exception parse_float_ex) {
                return false;
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace(System.err);
            return false;
        }

        return true;
    }


    public static LogDataPoint parse_data_line_bs(byte[] line_bs) {
        boolean has_tab = false;
        for (int i = 0; i < line_bs.length; i++) {
            byte _c = line_bs[i];
            if (is_digit(_c)) {
                continue;
            }

            if (_c == 9) {
                has_tab = true;
                continue;
            }

            if (_c == 46) {
                // dot character for float
                continue;
            }

            if (_c == 13) {
                // dot character for float
                continue;
            }
            if (_c == 10) {
                // dot character for float
                continue;
            }

            return null;
        }

        if (!has_tab) {
            return null;
        }

        try {
            String line = new String(line_bs, "ASCII");
            line = line.replace("\r", "");
            line = line.replace("\n", "");
            int tab_index = line.indexOf(9);
            String unix_ts_str = line.substring(0, tab_index);
            String temperature_value_str = line.substring(tab_index);

            if (unix_ts_str.length() == 0) {
                return null;
            }

            if (temperature_value_str.length() < 2) {
                return null;
            }

            if (!is_digit(unix_ts_str)) {
                return null;
            }

            int unix_ts;
            try {
                unix_ts = Integer.parseInt(unix_ts_str);
            } catch (Exception parse_float_ex) {
                return null;
            }

            float temperature_value;

            try {
                temperature_value = Float.parseFloat(temperature_value_str.substring(1));
            } catch (Exception parse_float_ex) {
                return null;
            }

            LogDataPoint retval = new LogDataPoint();
            retval.temperature = temperature_value;
            retval.unix_ts = unix_ts;
            return retval;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace(System.err);
            return null;
        }
    }
}
