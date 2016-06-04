package com.blueta.morsetransmitter;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import com.opencv.camera.NativePreviewer;
import java.util.Timer;
import java.util.TimerTask;

public class FlashControlCaseTorchWithPreviewOpencvNative extends FlashControl {
    public static final String flash_control_type = "FLASH_CONTROL_TORCH_PREVIEW_OPENCV_NATIVE";
    private NativePreviewer NativePreview;
    private Camera camera;
    private Timer flashOnTimer;
    private boolean isFlashTurnedOn;
    private Parameters parameters;

    public FlashControlCaseTorchWithPreviewOpencvNative() {
        this.camera = null;
        this.isFlashTurnedOn = false;
    }

    public void Init() {
        this.NativePreview = new NativePreviewer(null, 80, 60);
        this.camera = this.NativePreview.GetCameraReference();
        this.parameters = this.camera.getParameters();
        this.camera.startPreview();
    }

    public void InitInCameraOnMode() {
        this.isWithCameraOnMode = true;
    }

    public void Destroy() {
        if (!this.isWithCameraOnMode) {
            this.camera.stopPreview();
            this.camera.release();
        }
    }

    public void FlashOnDuration(int updateFreq) {
        this.flashOnTimer = new Timer("flashKeepOn");
        FlashOn();
        this.flashOnTimer.schedule(new TimerTask() {
            public void run() {
                FlashControlCaseTorchWithPreviewOpencvNative.this.FlashOff();
            }
        }, (long) updateFreq);
    }

    public void FlashOn() {
        if (!this.isFlashTurnedOn) {
            if (!this.isWithCameraOnMode) {
                this.parameters.set("flash-mode", "torch");
                this.camera.setParameters(this.parameters);
            } else if (MorseDecodeMain.NativePrevieCamera != null) {
                Parameters parameters = MorseDecodeMain.NativePrevieCamera.getParameters();
                parameters.setFlashMode("torch");
                MorseDecodeMain.NativePrevieCamera.setParameters(parameters);
            }
            this.isFlashTurnedOn = true;
        }
    }

    public void FlashOff() {
        if (this.isFlashTurnedOn) {
            if (!this.isWithCameraOnMode) {
                this.parameters.setFlashMode("off");
                this.camera.setParameters(this.parameters);
            } else if (MorseDecodeMain.NativePrevieCamera != null) {
                Parameters parameters = MorseDecodeMain.NativePrevieCamera.getParameters();
                parameters.setFlashMode("off");
                MorseDecodeMain.NativePrevieCamera.setParameters(parameters);
            }
            this.isFlashTurnedOn = false;
        }
    }
}
