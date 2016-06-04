package com.blueta.morsetransmitter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LedTest extends Activity {
    boolean isLedOn;

    public LedTest() {
        this.isLedOn = false;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.led_test);
        final Button FlashLightControl = (Button) findViewById(R.id.ledOn);
        FlashLightControl.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (LedTest.this.isLedOn) {
                    FlashManager.GetInstance().FlashOnDuration(1000);
                    FlashLightControl.setText("Set FLASH_MODE_TORCH");
                    LedTest.this.isLedOn = false;
                    return;
                }
                FlashLightControl.setText("Set FLASH_MODE_OFF");
                FlashManager.GetInstance().FlashOnDuration(2000);
                LedTest.this.isLedOn = true;
            }
        });
    }
}
