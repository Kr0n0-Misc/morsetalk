package com.blueta.morsetransmitter;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import java.util.Timer;

public class FlashControlCaseDummy extends FlashControl {
    private static final String TAG = "FlashControlCaseDummy";
    public static final String flash_control_type = "Dummy";
    private Camera camera;
    private Timer flashOnTimer;
    private boolean isFlashTurnedOn;
    private Parameters parameters;

    public FlashControlCaseDummy() {
        this.camera = null;
        this.isFlashTurnedOn = false;
    }

    public void Init() {
        Log.d(TAG, "Init():Dummy flash Control was assigned");
    }

    public void Destroy() {
        Log.d(TAG, "Destroy(); Dummy flash Control was assigned");
    }

    public void FlashOnDuration(int updateFreq) {
        Log.d(TAG, "FlashOnDuration(); Dummy flash Control was assigned");
    }

    public void FlashOn() {
        Log.d(TAG, "FlashOn(); Dummy flash Control was assigned");
    }

    public void FlashOff() {
        Log.d(TAG, "FlashOff(); Dummy flash Control was assigned");
    }
}
