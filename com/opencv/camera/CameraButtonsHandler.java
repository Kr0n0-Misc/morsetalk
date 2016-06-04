package com.opencv.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import com.opencv.R;

public class CameraButtonsHandler {
    private CaptureListener captureListener;
    private boolean capture_flag;
    private OnClickListener capture_listener;
    private Context ctx;
    private OnClickListener settings_listener;

    interface CaptureListener {
        void onCapture();
    }

    public CameraButtonsHandler(Activity a, CaptureListener l) {
        this.capture_listener = new OnClickListener() {
            public void onClick(View v) {
                CameraButtonsHandler.this.setIsCapture(true);
            }
        };
        this.settings_listener = new OnClickListener() {
            public void onClick(View v) {
                CameraButtonsHandler.this.ctx.startActivity(new Intent(CameraButtonsHandler.this.ctx, CameraConfig.class));
            }
        };
        this.capture_flag = false;
        ImageButton settings = (ImageButton) a.findViewById(R.id.button_camera_settings);
        ((ImageButton) a.findViewById(R.id.button_capture)).setOnClickListener(this.capture_listener);
        settings.setOnClickListener(this.settings_listener);
        this.captureListener = l;
        this.ctx = a;
    }

    public CameraButtonsHandler(Activity a) {
        this.capture_listener = new OnClickListener() {
            public void onClick(View v) {
                CameraButtonsHandler.this.setIsCapture(true);
            }
        };
        this.settings_listener = new OnClickListener() {
            public void onClick(View v) {
                CameraButtonsHandler.this.ctx.startActivity(new Intent(CameraButtonsHandler.this.ctx, CameraConfig.class));
            }
        };
        this.capture_flag = false;
        ImageButton settings = (ImageButton) a.findViewById(R.id.button_camera_settings);
        ((ImageButton) a.findViewById(R.id.button_capture)).setOnClickListener(this.capture_listener);
        settings.setOnClickListener(this.settings_listener);
        this.ctx = a;
    }

    public synchronized boolean isCapture() {
        return this.capture_flag;
    }

    public synchronized void resetIsCapture() {
        this.capture_flag = false;
    }

    public synchronized void setIsCapture(boolean isCapture) {
        this.capture_flag = isCapture;
        if (this.capture_flag && this.captureListener != null) {
            this.captureListener.onCapture();
        }
    }
}
