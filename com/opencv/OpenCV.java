package com.opencv;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.opencv.camera.NativePreviewer;
import com.opencv.camera.NativeProcessor.PoolCallback;
import com.opencv.opengl.GL2CameraViewer;
import java.util.LinkedList;

public class OpenCV extends Activity {
    private GL2CameraViewer glview;
    private NativePreviewer mPreview;

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return super.onKeyLongPress(keyCode, event);
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

    public void setNoTitle() {
        requestWindowFeature(1);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullscreen();
        disableScreenTurnOff();
        FrameLayout frame = new FrameLayout(getApplication());
        this.mPreview = new NativePreviewer(getApplication(), 640, 480);
        LayoutParams params = new LayoutParams(-2, -2);
        params.height = getWindowManager().getDefaultDisplay().getHeight();
        params.width = (int) ((((double) params.height) * 4.0d) / 2.88d);
        LinearLayout vidlay = new LinearLayout(getApplication());
        vidlay.setGravity(17);
        vidlay.addView(this.mPreview, params);
        frame.addView(vidlay);
        this.mPreview.setZOrderMediaOverlay(false);
        this.glview = new GL2CameraViewer(getApplication(), false, 0, 0);
        this.glview.setZOrderMediaOverlay(true);
        this.glview.setLayoutParams(new LayoutParams(-1, -1));
        frame.addView(this.glview);
        setContentView(frame);
    }

    protected void onPause() {
        super.onPause();
        this.mPreview.onPause();
        this.glview.onPause();
    }

    protected void onResume() {
        super.onResume();
        this.glview.onResume();
        LinkedList<PoolCallback> callbackstack = new LinkedList();
        callbackstack.add(this.glview.getDrawCallback());
        this.mPreview.addCallbackStack(callbackstack);
        this.mPreview.onResume();
    }
}
