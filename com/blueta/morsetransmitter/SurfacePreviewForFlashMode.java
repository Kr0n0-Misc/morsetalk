package com.blueta.morsetransmitter;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import java.io.IOException;

class SurfacePreviewForFlashMode extends SurfaceView implements Callback {
    public Camera mCamera;
    SurfaceHolder mHolder;
    boolean mPrivewRunning;

    public SurfacePreviewForFlashMode(Context context) {
        super(context);
        this.mPrivewRunning = false;
        this.mHolder = getHolder();
        this.mHolder.addCallback(this);
        this.mHolder.setType(3);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("test", "surfaceCreated");
        this.mPrivewRunning = true;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i("test", "surfaceDestroyed");
        releaseCamera();
        this.mPrivewRunning = false;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.i("test", "surfaceChanged");
        try {
            initCamera(holder);
            if (this.mCamera != null) {
                this.mCamera.startPreview();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onPause() {
        releaseCamera();
    }

    public Parameters GetParameter() {
        if (this.mCamera != null) {
            return this.mCamera.getParameters();
        }
        return null;
    }

    public void SetParameters(Parameters parameter) {
        if (this.mCamera != null) {
            this.mCamera.setParameters(parameter);
        }
    }

    public void releaseCamera() {
        if (this.mCamera != null) {
            this.mCamera.stopPreview();
            this.mCamera.release();
            this.mCamera = null;
        }
    }

    private void initCamera(SurfaceHolder holder) throws InterruptedException {
        if (this.mCamera == null) {
            int i = 0;
            while (true) {
                int i2 = i + 1;
                if (i < 5) {
                    try {
                        this.mCamera = Camera.open();
                        break;
                    } catch (RuntimeException e) {
                        Thread.sleep(200);
                        i = i2;
                    }
                }
                try {
                    break;
                } catch (IOException e2) {
                    this.mCamera.release();
                    this.mCamera = null;
                    return;
                } catch (RuntimeException e3) {
                    Log.e("camera", "stacktrace", e3);
                    return;
                }
            }
            this.mCamera.setPreviewDisplay(holder);
        }
    }
}
