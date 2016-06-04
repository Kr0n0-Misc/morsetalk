package com.blueta.morsetransmitter;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DevCameraNoPreview implements CameraIF {
    Camera camera;
    Parameters parameters;

    public DevCameraNoPreview(MorseDecodeMain mMainActivity, SurfaceView mSurfaceView) {
        mSurfaceView.getHolder().setType(0);
    }

    public void Init() {
        this.camera = Camera.open();
        this.camera.startPreview();
    }

    public void Destroy() {
        this.camera.stopPreview();
        this.camera.release();
    }

    public void setPreviewCallback(PreviewCallback cb) {
        this.camera.setPreviewCallback(cb);
    }

    public void setParameters(Parameters params) {
        this.camera.setParameters(this.parameters);
    }

    public Parameters getParameters() {
        this.parameters = this.camera.getParameters();
        return this.parameters;
    }

    public void resetPreviewSize(int width, int height) {
    }

    public void onStart() {
    }

    public void onResume() {
    }

    public void onStop() {
    }

    public void onPause() {
    }

    public void onDestroy() {
    }

    public void takePicture() {
    }

    public void handleMessage(Message msg) {
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    }

    public void surfaceCreated(SurfaceHolder holder) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }
}
