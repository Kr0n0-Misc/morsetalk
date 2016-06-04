package com.blueta.morsetransmitter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LcdBlinkTest extends Activity {
    boolean isLedOn;

    public LcdBlinkTest() {
        this.isLedOn = false;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lcd_test);
        final LcdBlinkView lcdLight = (LcdBlinkView) findViewById(R.id.lcdBlinkView);
        ((Button) findViewById(R.id.lcdOn)).setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (LcdBlinkTest.this.isLedOn) {
                    lcdLight.TurnOff();
                    lcdLight.invalidate();
                    LcdBlinkTest.this.isLedOn = false;
                    return;
                }
                lcdLight.TurnOn();
                lcdLight.invalidate();
                LcdBlinkTest.this.isLedOn = true;
            }
        });
    }
}
