package com.blueta.morsetransmitter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

public class CofigFlashLightType extends Activity {
    public static final String FLASH_CONFIG_SETTING_DONE = "flash config setting done";
    static boolean semaphore;
    private ConfigFlashLightTask configFlashLishtTask;
    private Runnable doUpdateGUI;
    private Handler handler;
    private Runnable informNoMatching;
    boolean isCanceled;
    boolean isFlashWorking;
    ConfigFlashLightReceiver receiver;
    int step;

    public class ConfigFlashLightReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            CofigFlashLightType.this.finish();
        }
    }

    private class ConfigFlashLightTask extends AsyncTask<Void, Void, Void> {
        private ConfigFlashLightTask() {
        }

        protected Void doInBackground(Void... params) {
            int controlTypeSize = FlashManager.flashControlTypeList.size();
            CofigFlashLightType.semaphore = true;
            CofigFlashLightType.this.isFlashWorking = false;
            loop0:
            for (int count = 0; count < controlTypeSize; count++) {
                if (isCancelled()) {
                    break;
                }
                String flashControlType = ((String) FlashManager.flashControlTypeList.get(count)).toString();
                FlashManager.GetInstance().CheckFlashByCtntrolType(flashControlType);
                CofigFlashLightType.this.step = count + 1;
                CofigFlashLightType.this.handler.post(CofigFlashLightType.this.doUpdateGUI);
                do {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    if (isCancelled()) {
                        break loop0;
                    }
                } while (CofigFlashLightType.semaphore);
                FlashManager.GetInstance().EndCurrentTypeChecking();
                CofigFlashLightType.semaphore = true;
                if (CofigFlashLightType.this.isFlashWorking) {
                    CofigFlashLightType.this.setFlashControlType(flashControlType);
                    break;
                }
            }
            if (CofigFlashLightType.this.isFlashWorking) {
                CofigFlashLightType.this.announceProcessDone();
            } else {
                CofigFlashLightType.this.setFlashControlType(FlashManager.FLASH_CONTROL_NO_MATCHING_USE_LCDS);
                CofigFlashLightType.this.handler.post(CofigFlashLightType.this.informNoMatching);
            }
            return null;
        }
    }

    private class FlashCheckOnCancel implements OnCancelListener {
        private FlashCheckOnCancel() {
        }

        public void onCancel(DialogInterface dialog) {
            CofigFlashLightType.this.finish();
        }
    }

    private class InformFlashLightTypeCheckingNo implements OnClickListener {
        private InformFlashLightTypeCheckingNo() {
        }

        public void onClick(DialogInterface dialog, int id) {
            CofigFlashLightType.this.finish();
        }
    }

    private class InformFlashLightTypeCheckingYes implements OnClickListener {
        private InformFlashLightTypeCheckingYes() {
        }

        public void onClick(DialogInterface dialog, int id) {
            CofigFlashLightType.this.startConfigflashLightTask();
        }
    }

    private class InformNoMatchingOkButtonListener implements OnClickListener {
        private InformNoMatchingOkButtonListener() {
        }

        public void onClick(DialogInterface dialog, int id) {
            Editor editor = PreferenceManager.getDefaultSharedPreferences(CofigFlashLightType.this.getApplicationContext()).edit();
            editor.putBoolean(MorseTransPreference.PREF_LED_FLASH_MODE, false);
            editor.commit();
            CofigFlashLightType.this.sendBroadcast(new Intent(CofigFlashLightType.FLASH_CONFIG_SETTING_DONE));
        }
    }

    private class NegativeButtonListener implements OnClickListener {
        private NegativeButtonListener() {
        }

        public void onClick(DialogInterface dialog, int id) {
            CofigFlashLightType.semaphore = false;
        }
    }

    private class PositiveButtonListener implements OnClickListener {
        private PositiveButtonListener() {
        }

        public void onClick(DialogInterface dialog, int id) {
            CofigFlashLightType.this.isFlashWorking = true;
            CofigFlashLightType.semaphore = false;
        }
    }

    public CofigFlashLightType() {
        this.isFlashWorking = false;
        this.isCanceled = false;
        this.step = 0;
        this.handler = new Handler();
        this.receiver = null;
        this.configFlashLishtTask = null;
        this.doUpdateGUI = new Runnable() {
            public void run() {
                CofigFlashLightType.this.CheckProperFlashControl("Step: " + CofigFlashLightType.this.step + "\nIs the flash light turned on?");
            }
        };
        this.informNoMatching = new Runnable() {
            public void run() {
                CofigFlashLightType.this.InformNoMatching("Sorry!\nCurrently, We are not supporting your device's flash light. We will use lcd mode instead");
            }
        };
    }

    static {
        semaphore = false;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_flash_light);
        FlashManager.InitialiseToCheckType();
        InformFlashLightTypeChecking();
    }

    public void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            FlashManager.GetInstance().EndCurrentTypeChecking();
            if (this.configFlashLishtTask != null && !this.configFlashLishtTask.getStatus().equals(Status.FINISHED)) {
                this.configFlashLishtTask.cancel(true);
                this.handler.removeCallbacks(this.doUpdateGUI);
            }
        }
    }

    public void onResume() {
        IntentFilter filter = new IntentFilter(FLASH_CONFIG_SETTING_DONE);
        this.receiver = new ConfigFlashLightReceiver();
        registerReceiver(this.receiver, filter);
        super.onResume();
    }

    public void onPause() {
        unregisterReceiver(this.receiver);
        super.onPause();
    }

    private void InformFlashLightTypeChecking() {
        Builder alertDialogBuilder = new Builder(this);
        OnClickListener positiveButtonListener = new InformFlashLightTypeCheckingYes();
        OnClickListener negativeButtonListener = new InformFlashLightTypeCheckingNo();
        alertDialogBuilder.setMessage("Flash light setting mode.\n\nWe have " + FlashManager.flashControlTypeList.size() + " steps to test your device\n\n" + "Every single step, we will ask you your device's flash light is turned on or not.");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Start", positiveButtonListener);
        alertDialogBuilder.setNegativeButton("Cancel", negativeButtonListener);
        alertDialogBuilder.create().show();
    }

    private void restetCurrentFlashTypeSetting() {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putString(MorseTransPreference.PREF_LED_FLASH_TYPE, FlashManager.FLASH_CONTROL_NOT_DEFINED);
        editor.commit();
        FlashManager.SetCurrentFlashControlType(FlashManager.FLASH_CONTROL_NOT_DEFINED);
    }

    private void CheckProperFlashControl(String messageToShow) {
        Builder alertDialogBuilder = new Builder(this);
        OnClickListener positiveButtonListener = new PositiveButtonListener();
        OnClickListener negativeButtonListener = new NegativeButtonListener();
        OnCancelListener onCancelListener = new FlashCheckOnCancel();
        alertDialogBuilder.setMessage(messageToShow);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setOnCancelListener(onCancelListener);
        alertDialogBuilder.setPositiveButton("Yes", positiveButtonListener);
        alertDialogBuilder.setNegativeButton("No", negativeButtonListener);
        AlertDialog alertDialog = alertDialogBuilder.create();
        Log.d("TEST", "CheckProperFlashControl window");
        alertDialog.show();
    }

    private void InformNoMatching(String messageToShow) {
        Builder alertDialogBuilder = new Builder(this);
        OnClickListener okButtonListener = new InformNoMatchingOkButtonListener();
        alertDialogBuilder.setMessage(messageToShow);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", okButtonListener);
        alertDialogBuilder.create().show();
    }

    private void setFlashControlType(String flashControlType) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putString(MorseTransPreference.PREF_LED_FLASH_TYPE, flashControlType);
        editor.commit();
        FlashManager.SetCurrentFlashControlType(flashControlType);
    }

    private void startConfigflashLightTask() {
        if (this.configFlashLishtTask == null || this.configFlashLishtTask.getStatus().equals(Status.FINISHED)) {
            this.configFlashLishtTask = new ConfigFlashLightTask();
            this.configFlashLishtTask.execute(null);
        }
    }

    private void announceProcessDone() {
        sendBroadcast(new Intent(FLASH_CONFIG_SETTING_DONE));
    }
}
