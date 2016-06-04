package com.blueta.morsetransmitter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class MorseTransPreference extends PreferenceActivity {
    public static final String MORSE_PREF_KILL = "morese prefernce kill";
    public static final String PREF_LED_FLASH_MODE = "PREF_LED_FLASH_MODE";
    public static final String PREF_LED_FLASH_TYPE = "PREF_LED_FLASH_TYPE";
    public static final String PREF_LOOP_TRANSMSSION_MODE = "PREF_LOOP_TRANSMSSION_MODE";
    public static final String PREF_SOS_DEFAULT_SET = "PREF_SOS_DEFAULT_SET";
    MorseTransPreferenceReceiver receiver;

    public class MorseTransPreferenceReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MorseTransPreference.MORSE_PREF_KILL)) {
                MorseTransPreference.this.finish();
            }
        }
    }

    public MorseTransPreference() {
        this.receiver = null;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.morsetranspreference);
        IntentFilter filter = new IntentFilter(MORSE_PREF_KILL);
        this.receiver = new MorseTransPreferenceReceiver();
        registerReceiver(this.receiver, filter);
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    public void onDestroy() {
        unregisterReceiver(this.receiver);
        super.onDestroy();
    }
}
