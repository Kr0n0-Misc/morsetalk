package com.blueta.morsetransmitter;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.widget.Toast;
import java.util.ArrayList;

public class MorseTranService extends Service {
    public static final String FLASH_SHOOTER = "flash shooter";
    public static final String IN_TALK_FLASH_SHOOTER = "flash shooter with camera alrady on";
    public static final String LCD_SHOTTER = "lcd shooter";
    public static final String MORSE_DATA = "morsedata";
    public static final String MORSE_SHOOTER_TYPE = "morse shooter type";
    public static final String MORSE_TRANSMITED_INDEX = "morse transmited index";
    public static final String MORSE_TRANSMIT_DONE = "Morse transmit done";
    public static final String MORSE_TRANSMIT_PROGRESS = "Morse transmit progress";
    static final int alphabetInterval = 600;
    static final int longTerm = 600;
    static final int shortTerm = 200;
    static final int symbolInterval = 200;
    static final int wordInterval = 1400;
    private Handler handler;
    boolean isloopMode;
    ArrayList<MorseCodeElement> morseDataList;
    private MorseShooter morseShooter;
    public String morse_shooter;
    PowerManager powerManager;
    MorseTransmitTask transmitTask;
    WakeLock wakeLock;

    public class MorseShooter {
        public void Initialize() {
        }

        public void InitializeWithCameraAlreayOn() {
        }

        public void SendMorseDot() {
        }

        public void SendMorseDash() {
        }

        public void Distroy() {
        }
    }

    public class FlashMorseShooter extends MorseShooter {
        private Runnable doNotifyTransmitting;

        public FlashMorseShooter() {
            super();
            this.doNotifyTransmitting = new Runnable() {
                public void run() {
                    FlashMorseShooter.this.notifyUpdating();
                }
            };
        }

        public void Initialize() {
            super.Initialize();
            FlashManager.Initialise();
            MorseTranService.this.handler.post(this.doNotifyTransmitting);
        }

        public void InitializeWithCameraAlreayOn() {
            super.Initialize();
            FlashManager.InitialiseInCameraAreadyOn();
            MorseTranService.this.handler.post(this.doNotifyTransmitting);
        }

        public void SendMorseDot() {
            FlashManager.GetInstance().FlashOn();
            synchronized (this) {
                try {
                    wait(200);
                } catch (InterruptedException e) {
                }
            }
            FlashManager.GetInstance().FlashOff();
        }

        public void SendMorseDash() {
            FlashManager.GetInstance().FlashOn();
            synchronized (this) {
                try {
                    wait(600);
                } catch (InterruptedException e) {
                }
            }
            FlashManager.GetInstance().FlashOff();
        }

        public void Distroy() {
            FlashManager.GetInstance().FlashOff();
            FlashManager.Destroy();
        }

        protected void notifyUpdating() {
            if (MorseTranService.this.isloopMode) {
                Toast.makeText(MorseTranService.this.getApplicationContext(), "Start transmission in loop mode", 1).show();
            }
        }
    }

    public class LcdMorseShooter extends MorseShooter {
        public LcdMorseShooter() {
            super();
        }

        public void Initialize() {
            super.Initialize();
        }

        public void InitializeWithCameraAlreayOn() {
            super.Initialize();
        }

        public void SendMorseDot() {
            turnOnLcd();
            synchronized (this) {
                try {
                    wait(200);
                } catch (InterruptedException e) {
                }
            }
            turnOffLcd();
        }

        public void SendMorseDash() {
            turnOnLcd();
            synchronized (this) {
                try {
                    wait(600);
                } catch (InterruptedException e) {
                }
            }
            turnOffLcd();
        }

        private void turnOnLcd() {
            MorseTranService.this.sendBroadcast(new Intent(MorseTransToLcd.ACTION_TURN_ON_LCD));
        }

        private void turnOffLcd() {
            MorseTranService.this.sendBroadcast(new Intent(MorseTransToLcd.ACTION_TURN_OFF_LCD));
        }
    }

    private class MorseTransmitTask extends AsyncTask<Void, Void, Void> {
        private MorseTransmitTask() {
        }

        protected Void doInBackground(Void... params) {
            while (!isCancelled()) {
                for (int count_1 = 0; count_1 < MorseTranService.this.morseDataList.size() && !isCancelled(); count_1++) {
                    String morseData = ((MorseCodeElement) MorseTranService.this.morseDataList.get(count_1)).MorseCode.toString();
                    for (int count_2 = 0; count_2 < morseData.length() && !isCancelled(); count_2++) {
                        if (morseData.charAt(count_2) == '.') {
                            MorseTranService.this.morseShooter.SendMorseDot();
                            if (count_2 + 1 != morseData.length()) {
                                synchronized (this) {
                                    try {
                                        wait(200);
                                    } catch (InterruptedException e) {
                                    }
                                }
                            } else {
                                continue;
                            }
                        } else if (morseData.charAt(count_2) == '-') {
                            MorseTranService.this.morseShooter.SendMorseDash();
                            if (count_2 + 1 != morseData.length()) {
                                synchronized (this) {
                                    try {
                                        wait(200);
                                    } catch (InterruptedException e2) {
                                    }
                                }
                            } else {
                                continue;
                            }
                        } else if (morseData.charAt(count_2) == ' ') {
                            synchronized (this) {
                                try {
                                    wait(200);
                                } catch (InterruptedException e3) {
                                }
                            }
                        } else {
                            continue;
                        }
                    }
                    synchronized (this) {
                        try {
                            wait(600);
                        } catch (InterruptedException e4) {
                        }
                    }
                    announceTransmitProgress(count_1);
                }
                if (!MorseTranService.this.isloopMode) {
                    break;
                }
            }
            announceTransmitDone();
            return null;
        }

        private void announceTransmitDone() {
            if (MorseTranService.this.morse_shooter.equals(MorseTranService.FLASH_SHOOTER) || MorseTranService.this.morse_shooter.equals(MorseTranService.IN_TALK_FLASH_SHOOTER)) {
                MorseTranService.this.sendBroadcast(new Intent(MorseTranService.MORSE_TRANSMIT_DONE));
            } else if (MorseTranService.this.morse_shooter.equals(MorseTranService.LCD_SHOTTER)) {
                MorseTranService.this.sendBroadcast(new Intent(MorseTransToLcd.ACTION_FINISHED));
            }
        }

        private void announceTransmitProgress(int index) {
            Intent intent = new Intent(MorseTranService.MORSE_TRANSMIT_PROGRESS);
            intent.putExtra(MorseTranService.MORSE_TRANSMITED_INDEX, index);
            MorseTranService.this.sendBroadcast(intent);
        }
    }

    public MorseTranService() {
        this.handler = new Handler();
        this.transmitTask = null;
    }

    public void onCreate() {
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            this.morseDataList = intent.getParcelableArrayListExtra(MORSE_DATA);
            this.morse_shooter = (String) bundle.get(MORSE_SHOOTER_TYPE);
        }
        selectShooter();
        if (this.morse_shooter.equals(IN_TALK_FLASH_SHOOTER)) {
            this.morseShooter.InitializeWithCameraAlreayOn();
        } else {
            this.morseShooter.Initialize();
        }
        updataFromFreference();
        this.powerManager = (PowerManager) getSystemService("power");
        this.wakeLock = this.powerManager.newWakeLock(1, "MorseTransWakeLock");
        this.wakeLock.acquire();
        startMorseTransmit();
        return 1;
    }

    public void selectShooter() {
        if (this.morse_shooter.equals(FLASH_SHOOTER)) {
            this.morseShooter = new FlashMorseShooter();
        }
        if (this.morse_shooter.equals(IN_TALK_FLASH_SHOOTER)) {
            this.morseShooter = new FlashMorseShooter();
        } else if (this.morse_shooter.equals(LCD_SHOTTER)) {
            this.morseShooter = new LcdMorseShooter();
        }
    }

    public void updataFromFreference() {
        this.isloopMode = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(MorseTransPreference.PREF_LOOP_TRANSMSSION_MODE, true);
    }

    public void onDestroy() {
        super.onDestroy();
        this.wakeLock.release();
        if (!this.transmitTask.getStatus().equals(Status.FINISHED)) {
            this.transmitTask.cancel(true);
        }
        this.morseShooter.Distroy();
        this.morseShooter = null;
    }

    private void startMorseTransmit() {
        if (this.transmitTask == null || this.transmitTask.getStatus().equals(Status.FINISHED)) {
            this.transmitTask = new MorseTransmitTask();
            this.transmitTask.execute(null);
        }
    }
}
