package com.blueta.morsetransmitter;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import java.util.Timer;
import java.util.TimerTask;

public class FlashControlCaseTorchWithPreview extends FlashControl {
    public static final String flash_control_type = "FLASH_CONTROL_TORCH_PREVIEW";
    private Camera camera;
    private Timer flashOnTimer;
    private boolean isFlashTurnedOn;
    private Parameters parameters;

    public FlashControlCaseTorchWithPreview() {
        this.camera = null;
        this.isFlashTurnedOn = false;
    }

    public void Init() {
        this.camera = Camera.open();
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
                FlashControlCaseTorchWithPreview.this.FlashOff();
            }
        }, (long) updateFreq);
    }

    public void FlashOn() {
        if (!this.isFlashTurnedOn) {
            if (!this.isWithCameraOnMode) {
                this.parameters.setFlashMode("torch");
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
