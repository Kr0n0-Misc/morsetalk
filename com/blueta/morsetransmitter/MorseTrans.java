package com.blueta.morsetransmitter;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.opencv.camera.CameraConfig;
import java.util.ArrayList;
import java.util.HashMap;

public class MorseTrans extends Activity implements OnSharedPreferenceChangeListener {
    private static final int ABOUT_MENU = 2;
    private static final int FLASH_CHECK = 4;
    static MorseTrans Instance = null;
    private static final int LCD_TEST = 3;
    private static final int RECEIVE_MODE = 5;
    private static final int RECEIVE_MODE_DEBUG = 6;
    private static final int RECEIVE_MODE_DEBUG_GL = 7;
    private static final int SETTING_MENU = 1;
    private static final int SHOW_PREFERENCES = 1;
    private static String encodedMessage;
    private static ArrayList<MorseCodeElement> encodedMorseData;
    static EnglishMorseTrans englishMorseTrans;
    static final boolean isDebugMode = false;
    static boolean prefLedFlashMode;
    static int sdkVersion;
    static String start_transmit;
    static String stop;
    private static int transmittedIndex;
    final String ENCODED_MORSE_DATA;
    final int MORSE_CODE_VIEW_COPY_ID;
    final String TRANSMIT_SERVICE;
    Button TransmitButton;
    EditText editMessageBox;
    private String flashLishtType;
    int index;
    TextView morseCodeView;
    private boolean prefSOSDefault;
    MorseTransReceiver receiver;
    TextView resultView;
    public SurfacePreviewForFlashMode surfaceForFlash;
    ComponentName transmitService;

    private class InformItDoesNotSupportFlashModesNo implements OnClickListener {
        private InformItDoesNotSupportFlashModesNo() {
        }

        public void onClick(DialogInterface dialog, int id) {
            Editor editor = PreferenceManager.getDefaultSharedPreferences(MorseTrans.this.getApplicationContext()).edit();
            editor.putBoolean(MorseTransPreference.PREF_LED_FLASH_MODE, false);
            editor.commit();
        }
    }

    private class InformItDoesNotSupportFlashModesYes implements OnClickListener {
        private InformItDoesNotSupportFlashModesYes() {
        }

        public void onClick(DialogInterface dialog, int id) {
            MorseTrans.this.startActivity(new Intent(MorseTrans.this, CofigFlashLightType.class));
        }
    }

    private class InformResettingFlashTypeCheckNo implements OnClickListener {
        private InformResettingFlashTypeCheckNo() {
        }

        public void onClick(DialogInterface dialog, int id) {
            Editor editor = PreferenceManager.getDefaultSharedPreferences(MorseTrans.this.getApplicationContext()).edit();
            editor.putBoolean(MorseTransPreference.PREF_LED_FLASH_MODE, false);
            editor.commit();
        }
    }

    private class InformResettingFlashTypeCheckYes implements OnClickListener {
        private InformResettingFlashTypeCheckYes() {
        }

        public void onClick(DialogInterface dialog, int id) {
            MorseTrans.this.startActivity(new Intent(MorseTrans.this, CofigFlashLightType.class));
        }
    }

    public class MorseTransReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle bundle = intent.getExtras();
            if (action.equals(MorseTranService.MORSE_TRANSMIT_DONE) && MorseTrans.this.transmitService != null) {
                MorseTrans.this.stopTransmitMessageService();
            }
            if (action.equals(MorseTranService.MORSE_TRANSMIT_PROGRESS)) {
                MorseTrans.transmittedIndex = bundle.getInt(MorseTranService.MORSE_TRANSMITED_INDEX);
                MorseTrans.this.updateProgress(MorseTrans.transmittedIndex);
            }
        }
    }

    public class TransmitButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            String inputMessage;
            if (!MorseTrans.prefLedFlashMode) {
                inputMessage = MorseTrans.this.editMessageBox.getText().toString();
                if (inputMessage.length() == 0) {
                    Toast.makeText(MorseTrans.this.getApplicationContext(), "No message is entered", MorseTrans.SHOW_PREFERENCES).show();
                    return;
                }
                MorseTrans.encodedMorseData = MorseTrans.englishMorseTrans.EncodeToMorseCodeData(inputMessage);
                Intent startTransmitIntent = new Intent(MorseTrans.this, MorseTransToLcd.class);
                startTransmitIntent.putParcelableArrayListExtra(MorseTranService.MORSE_DATA, MorseTrans.encodedMorseData);
                MorseTrans.this.morseCodeRsultView(MorseTrans.encodedMorseData);
                MorseTrans.this.startActivity(startTransmitIntent);
            } else if (MorseTrans.this.transmitService == null) {
                inputMessage = MorseTrans.this.editMessageBox.getText().toString();
                if (inputMessage.length() == 0) {
                    Toast.makeText(MorseTrans.this.getApplicationContext(), "No message is entered", MorseTrans.SHOW_PREFERENCES).show();
                    return;
                }
                MorseTrans.encodedMorseData = MorseTrans.englishMorseTrans.EncodeToMorseCodeData(inputMessage);
                MorseTrans.this.morseCodeRsultView(MorseTrans.encodedMorseData);
                MorseTrans.this.transmitMessageService();
            } else {
                MorseTrans.this.stopTransmitMessageService();
            }
        }
    }

    public MorseTrans() {
        this.transmitService = null;
        this.index = 0;
        this.receiver = null;
        this.TRANSMIT_SERVICE = "Transmit Service";
        this.ENCODED_MORSE_DATA = "Encoded Morse Data";
        this.prefSOSDefault = false;
        this.surfaceForFlash = null;
        this.MORSE_CODE_VIEW_COPY_ID = 0;
    }

    static {
        Instance = null;
        englishMorseTrans = new EnglishMorseTrans();
        sdkVersion = VERSION.SDK_INT;
        stop = "Stop";
        start_transmit = "Start Transmission";
        prefLedFlashMode = false;
        transmittedIndex = 0;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Instance = this;
        processBuildVersion();
        FrameLayout preview = (FrameLayout) findViewById(R.id.preview);
        this.editMessageBox = (EditText) findViewById(R.id.editMessage);
        this.TransmitButton = (Button) findViewById(R.id.startTransmit);
        this.resultView = (TextView) findViewById(R.id.ViewResult);
        this.morseCodeView = (TextView) findViewById(R.id.MorseCodeView);
        this.TransmitButton.setOnClickListener(new TransmitButtonListener());
        updateFromPreferences();
        HashMap<String, Object> lastConfiguration = getLastNonConfigurationInstance();
        registerForContextMenu(this.morseCodeView);
        if (this.prefSOSDefault) {
            this.editMessageBox.setText("SOS");
        }
        Context context = getApplicationContext();
        this.surfaceForFlash = new SurfacePreviewForFlashMode(context);
        LayoutParams params = new LayoutParams(-2, -2);
        params.height = SHOW_PREFERENCES;
        params.width = SHOW_PREFERENCES;
        preview.addView(this.surfaceForFlash, params);
        if (lastConfiguration != null) {
            HashMap<String, Object> savedData = lastConfiguration;
            this.transmitService = (ComponentName) savedData.get("Transmit Service");
            encodedMessage = (String) savedData.get("Encoded Morse Data");
        }
        PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(this);
        IntentFilter filter = new IntentFilter(MorseTranService.MORSE_TRANSMIT_DONE);
        filter.addAction(MorseTranService.MORSE_TRANSMIT_PROGRESS);
        this.receiver = new MorseTransReceiver();
        registerReceiver(this.receiver, filter);
        checkReconfigFlashMode();
    }

    private void checkReconfigFlashMode() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!prefLedFlashMode) {
            return;
        }
        if (this.flashLishtType == FlashManager.FLASH_CONTROL_NOT_DEFINED || this.flashLishtType == FlashManager.FLASH_CONTROL_NO_MATCHING_USE_LCDS) {
            Editor editor = prefs.edit();
            editor.putBoolean(MorseTransPreference.PREF_LED_FLASH_MODE, false);
            editor.commit();
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        updateFromPreferences();
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == this.morseCodeView.getId()) {
            menu.add(0, 0, 0, "Copy");
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case CameraConfig.CAMERA_MODE_BW /*0*/:
                ((ClipboardManager) getSystemService("clipboard")).setText(this.morseCodeView.getText());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void InformResettingFlashTypeCheck() {
        Builder alertDialogBuilder = new Builder(this);
        OnClickListener positiveButtonListener = new InformResettingFlashTypeCheckYes();
        OnClickListener negativeButtonListener = new InformResettingFlashTypeCheckNo();
        alertDialogBuilder.setMessage("Led flash light mode is on!\n\nBefore use led flash light mode we need to set flash type.\n\nDo you want to start setting process now?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Yes", positiveButtonListener);
        alertDialogBuilder.setNegativeButton("No", negativeButtonListener);
        alertDialogBuilder.create().show();
    }

    private void InformItDoesNotSupportFlashModes() {
        Builder alertDialogBuilder = new Builder(this);
        OnClickListener positiveButtonListener = new InformItDoesNotSupportFlashModesYes();
        OnClickListener negativeButtonListener = new InformItDoesNotSupportFlashModesNo();
        alertDialogBuilder.setMessage("Currently we do not support flash light mode for your device\nDo you want to re-test your device?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Yes", positiveButtonListener);
        alertDialogBuilder.setNegativeButton("No", negativeButtonListener);
        alertDialogBuilder.create().show();
    }

    private void processBuildVersion() {
    }

    private void morseCodeRsultView(ArrayList<MorseCodeElement> morseCodeData) {
        StringBuffer morseCodeToPrint = new StringBuffer();
        for (int count = 0; count < morseCodeData.size(); count += SHOW_PREFERENCES) {
            morseCodeToPrint.append(((MorseCodeElement) morseCodeData.get(count)).MorseCode);
        }
        if (this.morseCodeView != null) {
            this.morseCodeView.setText("\nMorse Code:\n" + morseCodeToPrint);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, RECEIVE_MODE, 0, "Chat Mode");
        menu.add(0, SHOW_PREFERENCES, 0, "Setting");
        menu.add(0, ABOUT_MENU, 0, "About");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        super.onOptionsItemSelected(menuItem);
        boolean supRetVal;
        Intent i;
        switch (menuItem.getItemId()) {
            case SHOW_PREFERENCES /*1*/:
                if (this.transmitService != null) {
                    stopTransmitMessageService();
                }
                startActivityForResult(new Intent(this, MorseTransPreference.class), SHOW_PREFERENCES);
                return true;
            case ABOUT_MENU /*2*/:
                if (this.transmitService != null) {
                    stopTransmitMessageService();
                }
                supRetVal = super.onOptionsItemSelected(menuItem);
                openAboutDialog();
                return supRetVal;
            case FLASH_CHECK /*4*/:
                if (this.transmitService != null) {
                    stopTransmitMessageService();
                }
                supRetVal = super.onOptionsItemSelected(menuItem);
                startActivity(new Intent(this, CofigFlashLightType.class));
                return supRetVal;
            case RECEIVE_MODE /*5*/:
                if (this.transmitService != null) {
                    Toast.makeText(getApplicationContext(), "Please stop Transmitting before switch", SHOW_PREFERENCES).show();
                    return true;
                }
                if (this.surfaceForFlash != null) {
                    this.surfaceForFlash.releaseCamera();
                }
                supRetVal = super.onOptionsItemSelected(menuItem);
                i = new Intent(this, MorseDecodeMain.class);
                i.putExtra(MorseDecodeMain.START_MODE, MorseDecodeMain.START_NORMAL);
                startActivity(i);
                return supRetVal;
            case RECEIVE_MODE_DEBUG_GL /*7*/:
                if (this.transmitService != null) {
                    stopTransmitMessageService();
                }
                supRetVal = super.onOptionsItemSelected(menuItem);
                i = new Intent(this, MorseDecodeMain.class);
                i.putExtra(MorseDecodeMain.START_MODE, MorseDecodeMain.START_DEBUG_INFO_GLVIEW);
                startActivity(i);
                return supRetVal;
            default:
                return false;
        }
    }

    private void openAboutDialog() {
        new Builder(this).setTitle("About").setView(LayoutInflater.from(this).inflate(R.layout.about_view, null)).setNegativeButton("Close", new OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).show();
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.receiver);
        boolean isFinishng = isFinishing();
        if (isFinishng && this.transmitService != null) {
            stopTransmitMessageService();
        }
        if (isFinishng) {
            encodedMorseData = null;
            encodedMessage = null;
        }
    }

    public void onStop() {
        super.onStop();
    }

    public void onStart() {
        super.onStart();
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        if (this.transmitService == null) {
            this.TransmitButton.setText(start_transmit);
        } else {
            this.TransmitButton.setText(stop);
        }
        updateProgress(transmittedIndex);
        if (encodedMorseData != null) {
            morseCodeRsultView(encodedMorseData);
        }
        super.onResume();
    }

    public void onBackPressed() {
        if (this.transmitService != null) {
            stopTransmitMessageService();
        }
        super.onBackPressed();
    }

    private void transmitMessageService() {
        Intent startTransmitIntent = new Intent(this, MorseTranService.class);
        startTransmitIntent.putExtra(MorseTranService.MORSE_SHOOTER_TYPE, MorseTranService.FLASH_SHOOTER);
        startTransmitIntent.putParcelableArrayListExtra(MorseTranService.MORSE_DATA, encodedMorseData);
        this.transmitService = startService(startTransmitIntent);
        this.TransmitButton.setText(stop);
    }

    private void stopTransmitMessageService() {
        try {
            stopService(new Intent(this, Class.forName(this.transmitService.getClassName())));
            this.transmitService = null;
            this.TransmitButton.setText(start_transmit);
        } catch (ClassNotFoundException e) {
        }
    }

    private void updateProgress(int index) {
        if (encodedMorseData != null) {
            StringBuffer textToPrint = new StringBuffer();
            for (int count = 0; count < index + SHOW_PREFERENCES; count += SHOW_PREFERENCES) {
                if (count < encodedMorseData.size()) {
                    textToPrint.append(((MorseCodeElement) encodedMorseData.get(count)).Character);
                }
            }
            if (this.resultView != null) {
                this.resultView.setText("\nMessage Transmitting:\n" + textToPrint);
            }
        }
    }

    public Object onRetainNonConfigurationInstance() {
        HashMap<String, Object> localData = new HashMap();
        localData.put("Transmit Service", this.transmitService);
        localData.put("Encoded Morse Data", encodedMessage);
        return localData;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHOW_PREFERENCES) {
            updateFromPreferences();
        }
    }

    private void checkNeedResetFlashType() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (prefs.getBoolean(MorseTransPreference.PREF_LED_FLASH_MODE, false)) {
            String flashLightType = prefs.getString(MorseTransPreference.PREF_LED_FLASH_TYPE, FlashManager.FLASH_CONTROL_NOT_DEFINED);
            if (flashLightType.equals(FlashManager.FLASH_CONTROL_NOT_DEFINED)) {
                InformResettingFlashTypeCheck();
            } else if (flashLightType.equals(FlashManager.FLASH_CONTROL_NO_MATCHING_USE_LCDS)) {
                InformItDoesNotSupportFlashModes();
            }
        }
    }

    public void updateFromPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefLedFlashMode = prefs.getBoolean(MorseTransPreference.PREF_LED_FLASH_MODE, false);
        this.prefSOSDefault = prefs.getBoolean(MorseTransPreference.PREF_SOS_DEFAULT_SET, true);
        this.flashLishtType = FlashManager.FLASH_CONTROL_TORCH_PREVIEW_ICS;
        FlashManager.SetCurrentFlashControlType(this.flashLishtType);
    }

    static MorseTrans GetInstance() {
        return Instance;
    }
}
