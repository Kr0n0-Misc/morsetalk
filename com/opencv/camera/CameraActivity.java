package com.opencv.camera;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import com.opencv.R;
import com.opencv.camera.NativeProcessor.PoolCallback;
import com.opencv.opengl.GL2CameraViewer;
import java.util.LinkedList;

public abstract class CameraActivity extends Activity implements CaptureListener {
    protected CameraButtonsHandler cameraButtonHandler;
    protected GL2CameraViewer glview;
    protected NativePreviewer mPreview;

    protected abstract LinkedList<PoolCallback> getCallBackStack();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullscreen();
        setOrientation();
        disableScreenTurnOff();
        setContentView(R.layout.camera);
        this.cameraButtonHandler = new CameraButtonsHandler(this, this);
        this.mPreview = (NativePreviewer) findViewById(R.id.nativepreviewer);
        LinearLayout glview_layout = (LinearLayout) findViewById(R.id.glview_layout);
        this.glview = new GL2CameraViewer(getApplication(), true, 0, 0);
        glview_layout.addView(this.glview);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 23:
            case 27:
            case 62:
                this.cameraButtonHandler.setIsCapture(true);
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    public boolean onTrackballEvent(MotionEvent event) {
        if (event.getAction() != 1) {
            return super.onTrackballEvent(event);
        }
        this.cameraButtonHandler.setIsCapture(true);
        return true;
    }

    public void disableScreenTurnOff() {
        getWindow().setFlags(128, 128);
    }

    public void setOrientation() {
        setRequestedOrientation(0);
    }

    public void setFullscreen() {
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
    }

    protected void onPause() {
        super.onPause();
        this.mPreview.onPause();
        this.glview.onPause();
    }

    protected void onResume() {
        super.onResume();
        this.mPreview.setParamsFromPrefs(getApplicationContext());
        this.glview.onResume();
        this.mPreview.onResume();
        setCallbackStack();
    }

    protected void setCallbackStack() {
        LinkedList<PoolCallback> callbackstack = getCallBackStack();
        if (callbackstack == null) {
            callbackstack = new LinkedList();
            callbackstack.add(this.glview.getDrawCallback());
        }
        this.mPreview.addCallbackStack(callbackstack);
    }

    public void onCapture() {
    }
}
