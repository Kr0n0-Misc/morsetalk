package com.blueta.morsetransmitter;

import android.hardware.Camera.Parameters;
import java.util.Timer;
import java.util.TimerTask;

public class FlashControlCaseTorchWithPreviewICS extends FlashControl {
    public static final String flash_control_type = "FLASH_CONTROL_TORCH_PREVIEW_ICS";
    private SurfacePreviewForFlashMode SurvacePreviewForFlash;
    private Timer flashOnTimer;
    private boolean isFlashTurnedOn;
    private Parameters parameters;

    public FlashControlCaseTorchWithPreviewICS() {
        this.isFlashTurnedOn = false;
    }

    public void Init() {
        this.SurvacePreviewForFlash = MorseTrans.GetInstance().surfaceForFlash;
    }

    public void InitInCameraOnMode() {
        this.isWithCameraOnMode = true;
    }

    public void Destroy() {
    }

    public void FlashOnDuration(int updateFreq) {
        this.flashOnTimer = new Timer("flashKeepOn");
        FlashOn();
        this.flashOnTimer.schedule(new TimerTask() {
            public void run() {
                FlashControlCaseTorchWithPreviewICS.this.FlashOff();
            }
        }, (long) updateFreq);
    }

    public void FlashOn() {
        if (!this.isFlashTurnedOn) {
            if (!this.isWithCameraOnMode) {
                this.parameters = this.SurvacePreviewForFlash.GetParameter();
                if (this.parameters != null) {
                    this.parameters.setFlashMode("torch");
                    this.SurvacePreviewForFlash.SetParameters(this.parameters);
                }
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
                this.parameters = this.SurvacePreviewForFlash.GetParameter();
                if (this.parameters != null) {
                    this.parameters.setFlashMode("off");
                    this.SurvacePreviewForFlash.SetParameters(this.parameters);
                }
            } else if (MorseDecodeMain.NativePrevieCamera != null) {
                Parameters parameters = MorseDecodeMain.NativePrevieCamera.getParameters();
                parameters.setFlashMode("off");
                MorseDecodeMain.NativePrevieCamera.setParameters(parameters);
            }
            this.isFlashTurnedOn = false;
        }
    }
}
