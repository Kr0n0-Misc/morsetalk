package com.blueta.morsetransmitter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MorseTransToLcd extends Activity {
    public static final String ACTION_FINISHED = "com.bluetea.morsetransmitter.intent.action.ACTION_FINISHED";
    public static final String ACTION_TURN_OFF_LCD = "com.bluetea.morsetransmitter.intent.action.ACTION_TURN_OFF_LCD";
    public static final String ACTION_TURN_ON_LCD = "com.bluetea.morsetransmitter.intent.action.ACTION_TURN_ON_LCD";
    private TimerTask doStartTransmit;
    private boolean isloopMode;
    private LayoutParams layoutParam;
    private LcdBlinkView lcdLight;
    private String morseData;
    ArrayList<MorseCodeElement> morseDataList;
    private MorseTransLcdReceiver receiver;
    private Timer startTimer;
    private ComponentName transmitToLcdService;
    private Window window;

    public class MorseTransLcdReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MorseTransToLcd.ACTION_TURN_ON_LCD)) {
                MorseTransToLcd.this.lcdTurnOn();
            } else if (action.equals(MorseTransToLcd.ACTION_TURN_OFF_LCD)) {
                MorseTransToLcd.this.lcdTurnOff();
            } else if (action.equals(MorseTransToLcd.ACTION_FINISHED)) {
                MorseTransToLcd.this.stopLcdTransmitMessageService();
                MorseTransToLcd.this.finish();
            }
        }
    }

    public MorseTransToLcd() {
        this.isloopMode = false;
        this.transmitToLcdService = null;
        this.doStartTransmit = new TimerTask() {
            public void run() {
                MorseTransToLcd.this.startTimer.cancel();
                Intent startTransmitIntent = new Intent(MorseTransToLcd.this, MorseTranService.class);
                startTransmitIntent.putExtra(MorseTranService.MORSE_SHOOTER_TYPE, MorseTranService.LCD_SHOTTER);
                startTransmitIntent.putParcelableArrayListExtra(MorseTranService.MORSE_DATA, MorseTransToLcd.this.morseDataList);
                MorseTransToLcd.this.transmitToLcdService = MorseTransToLcd.this.startService(startTransmitIntent);
            }
        };
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.morse_trans_lcd_view);
        this.lcdLight = (LcdBlinkView) findViewById(R.id.lcdBlinkView);
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            this.morseDataList = intent.getParcelableArrayListExtra(MorseTranService.MORSE_DATA);
        }
        this.window = getWindow();
        this.layoutParam = getWindow().getAttributes();
        this.layoutParam.screenBrightness = 1.0f;
        this.window.setAttributes(this.layoutParam);
        updataFromFreference();
        startTransmission();
    }

    public void onResume() {
        IntentFilter filter1 = new IntentFilter(ACTION_TURN_ON_LCD);
        IntentFilter filter2 = new IntentFilter(ACTION_TURN_OFF_LCD);
        IntentFilter filter3 = new IntentFilter(ACTION_FINISHED);
        this.receiver = new MorseTransLcdReceiver();
        registerReceiver(this.receiver, filter1);
        registerReceiver(this.receiver, filter2);
        registerReceiver(this.receiver, filter3);
        super.onResume();
    }

    public void onPause() {
        unregisterReceiver(this.receiver);
        super.onPause();
    }

    private void startTransmission() {
        Toast.makeText(getApplicationContext(), "Lcd will blink soon", 1).show();
        this.startTimer = new Timer("MorseStartTimer");
        this.startTimer.schedule(this.doStartTransmit, 2000);
    }

    private void stopLcdTransmitMessageService() {
        try {
            if (this.transmitToLcdService != null) {
                stopService(new Intent(this, Class.forName(this.transmitToLcdService.getClassName())));
                this.transmitToLcdService = null;
            }
        } catch (ClassNotFoundException e) {
        }
    }

    public void updataFromFreference() {
        this.isloopMode = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(MorseTransPreference.PREF_LOOP_TRANSMSSION_MODE, true);
    }

    private void lcdTurnOn() {
        this.lcdLight.TurnOn();
        this.lcdLight.invalidate();
    }

    private void lcdTurnOff() {
        this.lcdLight.TurnOff();
        this.lcdLight.invalidate();
    }

    protected void notifyUpdating() {
        Toast.makeText(getApplicationContext(), "Will start transmission through LCD", 0).show();
    }

    public void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            this.layoutParam.screenBrightness = -1.0f;
            this.window.setAttributes(this.layoutParam);
            stopLcdTransmitMessageService();
        }
    }
}
