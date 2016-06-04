package com.blueta.morsetransmitter;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class FlashManager {
    public static final String CAMERA_ALREADY_ON = "camera already on";
    public static final String CAMERA_CURRENTLY_OFF = "camera currently off";
    public static final String FLASH_CONTROL_NOT_DEFINED = "FLASH_CONTROL_NOT_DEFINED";
    public static final String FLASH_CONTROL_NO_MATCHING_USE_LCDS = "FLASH_CONTROL_NO_MATCHING_USE_LCDS";
    public static final String FLASH_CONTROL_ON_PREVIEW = "FLASH_CONTROL_ON_PREVIEW";
    public static final String FLASH_CONTROL_TORCH_PREVIEW = "FLASH_CONTROL_TORCH_PREVIEW";
    public static final String FLASH_CONTROL_TORCH_PREVIEW_HACK = "FLASH_CONTROL_TORCH_PREVIEW_HACK";
    public static final String FLASH_CONTROL_TORCH_PREVIEW_ICS = "FLASH_CONTROL_TORCH_PREVIEW_ICS";
    public static final String FLASH_CONTROL_TORCH_PREVIEW_OPENCV_NATIVE = "FLASH_CONTROL_TORCH_PREVIEW_OPENCV_NATIVE";
    public static final String PREF_LED_FLASH_CONTROL_TYPE = "PREF_LED_FLASH_CONTROL_TYPE";
    private static String curentflashControlType;
    static final ArrayList<String> flashControlTypeList;
    private static FlashManager flashManager;
    private List<String> cameraSupportOption;
    private FlashControl flashControl;

    static {
        curentflashControlType = null;
        flashControlTypeList = new ArrayList();
        flashControlTypeList.add(FLASH_CONTROL_TORCH_PREVIEW_ICS);
        flashControlTypeList.add(FLASH_CONTROL_TORCH_PREVIEW);
        flashControlTypeList.add(FLASH_CONTROL_ON_PREVIEW);
        flashControlTypeList.add(FLASH_CONTROL_TORCH_PREVIEW_HACK);
        flashManager = null;
    }

    private FlashManager() {
    }

    public static void SetCurrentFlashControlType(String flashControlType) {
        curentflashControlType = flashControlType;
    }

    public void CheckFlashByCtntrolType(String controlType) {
        if (controlType.matches(FLASH_CONTROL_ON_PREVIEW)) {
            this.flashControl = new FlashControlCaseOnWithPreview();
            this.flashControl.Init();
            this.flashControl.FlashOn();
        }
        if (controlType.matches(FLASH_CONTROL_TORCH_PREVIEW_ICS)) {
            this.flashControl = new FlashControlCaseTorchWithPreviewICS();
            this.flashControl.Init();
            this.flashControl.FlashOn();
        } else if (controlType.matches(FLASH_CONTROL_TORCH_PREVIEW)) {
            this.flashControl = new FlashControlCaseTorchWithPreview();
            this.flashControl.Init();
            this.flashControl.FlashOn();
        } else if (controlType.matches(FLASH_CONTROL_TORCH_PREVIEW_HACK)) {
            this.flashControl = new FlashControlCaseTorchWithPreviewHack();
            this.flashControl.Init();
            this.flashControl.FlashOn();
        } else {
            this.flashControl = new FlashControlCaseDummy();
            this.flashControl.Init();
        }
    }

    public synchronized void EndCurrentTypeChecking() {
        if (this.flashControl != null) {
            Log.d("Test", "EndCurrentTypeChecking is invoded");
            this.flashControl.FlashOff();
            this.flashControl.Destroy();
            this.flashControl = null;
        }
    }

    private void init() {
        if (curentflashControlType == null) {
            this.flashControl = new FlashControlCaseDummy();
            this.flashControl.Init();
        }
        if (curentflashControlType.matches(FLASH_CONTROL_ON_PREVIEW)) {
            this.flashControl = new FlashControlCaseOnWithPreview();
            this.flashControl.Init();
        } else if (curentflashControlType.matches(FLASH_CONTROL_TORCH_PREVIEW)) {
            this.flashControl = new FlashControlCaseTorchWithPreview();
            this.flashControl.Init();
        } else if (curentflashControlType.matches(FLASH_CONTROL_TORCH_PREVIEW_HACK)) {
            this.flashControl = new FlashControlCaseTorchWithPreviewHack();
            this.flashControl.Init();
        } else if (curentflashControlType.matches(FLASH_CONTROL_TORCH_PREVIEW_ICS)) {
            this.flashControl = new FlashControlCaseTorchWithPreviewICS();
            this.flashControl.Init();
        } else {
            this.flashControl = new FlashControlCaseDummy();
            this.flashControl.Init();
        }
    }

    private void initCameraAlreadyOn() {
        if (curentflashControlType == null) {
            this.flashControl = new FlashControlCaseDummy();
            this.flashControl.InitInCameraOnMode();
        }
        if (curentflashControlType.matches(FLASH_CONTROL_ON_PREVIEW)) {
            this.flashControl = new FlashControlCaseOnWithPreview();
            this.flashControl.InitInCameraOnMode();
        }
        if (curentflashControlType.matches(FLASH_CONTROL_TORCH_PREVIEW)) {
            this.flashControl = new FlashControlCaseTorchWithPreview();
            this.flashControl.InitInCameraOnMode();
        } else if (curentflashControlType.matches(FLASH_CONTROL_TORCH_PREVIEW_HACK)) {
            this.flashControl = new FlashControlCaseTorchWithPreviewHack();
            this.flashControl.InitInCameraOnMode();
        } else if (curentflashControlType.matches(FLASH_CONTROL_TORCH_PREVIEW_ICS)) {
            this.flashControl = new FlashControlCaseTorchWithPreviewICS();
            this.flashControl.InitInCameraOnMode();
        } else {
            this.flashControl = new FlashControlCaseDummy();
            this.flashControl.InitInCameraOnMode();
        }
    }

    public void FlashOn() {
        if (this.flashControl != null) {
            this.flashControl.FlashOn();
        }
    }

    public void FlashOff() {
        if (this.flashControl != null) {
            this.flashControl.FlashOff();
        }
    }

    public void FlashOnDuration(int updateFreq) {
        if (this.flashControl != null) {
            this.flashControl.FlashOnDuration(updateFreq);
        }
    }

    public static FlashManager GetInstance() {
        return flashManager;
    }

    public static void Initialise() {
        flashManager = new FlashManager();
        flashManager.init();
    }

    public static void InitialiseInCameraAreadyOn() {
        flashManager = new FlashManager();
        flashManager.initCameraAlreadyOn();
    }

    public static void InitialiseToCheckType() {
        if (flashManager == null) {
            flashManager = new FlashManager();
        }
    }

    public static void DestroyForCheckType() {
        if (flashManager != null) {
            flashManager = null;
        }
    }

    public static void Destroy() {
        if (flashManager != null) {
            flashManager.destroy();
            flashManager = null;
        }
    }

    public void destroy() {
        if (this.flashControl != null) {
            this.flashControl.Destroy();
        }
    }
}
