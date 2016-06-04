package com.opencv.camera;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import com.opencv.R;

public class CameraConfig extends Activity {
    public static final String CAMERA_MODE = "camera_mode";
    public static final int CAMERA_MODE_BW = 0;
    public static final int CAMERA_MODE_COLOR = 1;
    public static final String CAMERA_SETTINGS = "CAMERA_SETTINGS";
    public static final String IMAGE_HEIGHT = "IMAGE_HEIGHT";
    public static final String IMAGE_WIDTH = "IMAGE_WIDTH";
    private static final String WHITEBALANCE = "WHITEBALANCE";

    public static int readCameraMode(Context ctx) {
        return ctx.getSharedPreferences(CAMERA_SETTINGS, CAMERA_MODE_BW).getInt(CAMERA_MODE, CAMERA_MODE_BW);
    }

    public static String readWhitebalace(Context ctx) {
        return ctx.getSharedPreferences(CAMERA_SETTINGS, CAMERA_MODE_BW).getString(WHITEBALANCE, "auto");
    }

    public static void setCameraMode(Context context, String mode) {
        int m = CAMERA_MODE_BW;
        if (mode.equals("BW")) {
            m = CAMERA_MODE_BW;
        } else if (mode.equals("color")) {
            m = CAMERA_MODE_COLOR;
        }
        setCameraMode(context, m);
    }

    private static String sizeToString(int[] size) {
        return size[CAMERA_MODE_BW] + "x" + size[CAMERA_MODE_COLOR];
    }

    private static void parseStrToSize(String ssize, int[] size) {
        String[] sz = ssize.split("x");
        size[CAMERA_MODE_BW] = Integer.valueOf(sz[CAMERA_MODE_BW]).intValue();
        size[CAMERA_MODE_COLOR] = Integer.valueOf(sz[CAMERA_MODE_COLOR]).intValue();
    }

    public static void readImageSize(Context ctx, int[] size) {
        SharedPreferences settings = ctx.getSharedPreferences(CAMERA_SETTINGS, CAMERA_MODE_BW);
        size[CAMERA_MODE_BW] = settings.getInt(IMAGE_WIDTH, 640);
        size[CAMERA_MODE_COLOR] = settings.getInt(IMAGE_HEIGHT, 480);
    }

    public static void setCameraMode(Context ctx, int mode) {
        Editor editor = ctx.getSharedPreferences(CAMERA_SETTINGS, CAMERA_MODE_BW).edit();
        editor.putInt(CAMERA_MODE, mode);
        editor.commit();
    }

    public static void setImageSize(Context ctx, String strsize) {
        int[] size = new int[2];
        parseStrToSize(strsize, size);
        setImageSize(ctx, size[CAMERA_MODE_BW], size[CAMERA_MODE_COLOR]);
    }

    public static void setImageSize(Context ctx, int width, int height) {
        Editor editor = ctx.getSharedPreferences(CAMERA_SETTINGS, CAMERA_MODE_BW).edit();
        editor.putInt(IMAGE_WIDTH, width);
        editor.putInt(IMAGE_HEIGHT, height);
        editor.commit();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camerasettings);
        int mode = readCameraMode(this);
        int[] size = new int[2];
        readImageSize(this, size);
        final Spinner size_spinner = (Spinner) findViewById(R.id.image_size);
        final Spinner mode_spinner = (Spinner) findViewById(R.id.camera_mode);
        final Spinner whitebalance_spinner = (Spinner) findViewById(R.id.whitebalance);
        String strsize = sizeToString(size);
        String strmode = modeToString(mode);
        String wbmode = readWhitebalace(getApplicationContext());
        String[] sizes = getResources().getStringArray(R.array.image_sizes);
        int i = CAMERA_MODE_COLOR;
        int length = sizes.length;
        int i2 = CAMERA_MODE_BW;
        while (i2 < length && !sizes[i2].equals(strsize)) {
            i += CAMERA_MODE_COLOR;
            i2 += CAMERA_MODE_COLOR;
        }
        if (i <= sizes.length) {
            size_spinner.setSelection(i - 1);
        }
        i = CAMERA_MODE_COLOR;
        String[] modes = getResources().getStringArray(R.array.camera_mode);
        length = modes.length;
        i2 = CAMERA_MODE_BW;
        while (i2 < length && !modes[i2].equals(strmode)) {
            i += CAMERA_MODE_COLOR;
            i2 += CAMERA_MODE_COLOR;
        }
        if (i <= modes.length) {
            mode_spinner.setSelection(i - 1);
        }
        i = CAMERA_MODE_COLOR;
        String[] wbmodes = getResources().getStringArray(R.array.whitebalance);
        length = wbmodes.length;
        i2 = CAMERA_MODE_BW;
        while (i2 < length && !wbmodes[i2].equals(wbmode)) {
            i += CAMERA_MODE_COLOR;
            i2 += CAMERA_MODE_COLOR;
        }
        if (i <= wbmodes.length) {
            whitebalance_spinner.setSelection(i - 1);
        }
        size_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View spinner, int position, long arg3) {
                Object o = size_spinner.getItemAtPosition(position);
                if (o != null) {
                    CameraConfig.setImageSize(spinner.getContext(), (String) o);
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        mode_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View spinner, int position, long arg3) {
                Object o = mode_spinner.getItemAtPosition(position);
                if (o != null) {
                    CameraConfig.setCameraMode(spinner.getContext(), (String) o);
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        whitebalance_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View spinner, int position, long arg3) {
                Object o = whitebalance_spinner.getItemAtPosition(position);
                if (o != null) {
                    CameraConfig.setWhitebalance(spinner.getContext(), (String) o);
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public static void setWhitebalance(Context ctx, String o) {
        Editor editor = ctx.getSharedPreferences(CAMERA_SETTINGS, CAMERA_MODE_BW).edit();
        editor.putString(WHITEBALANCE, o);
        editor.commit();
    }

    private String modeToString(int mode) {
        switch (mode) {
            case CAMERA_MODE_BW /*0*/:
                return "BW";
            case CAMERA_MODE_COLOR /*1*/:
                return "color";
            default:
                return "";
        }
    }
}
