package com.opencv.calibration;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import com.opencv.R;
import com.opencv.jni.Size;

public class ChessBoardChooser extends Activity {
    public static final String CHESS_SIZE = "chess_size";
    public static final int DEFAULT_HEIGHT = 8;
    public static final int DEFAULT_WIDTH = 6;
    public static final int LOWEST = 2;

    class DimChooser implements OnItemSelectedListener {
        private String dim;

        public DimChooser(String dim) {
            this.dim = dim;
        }

        public void onItemSelected(AdapterView<?> adapterView, View arg1, int pos, long arg3) {
            Editor editor = ChessBoardChooser.this.getSharedPreferences(ChessBoardChooser.CHESS_SIZE, 0).edit();
            editor.putInt(this.dim, pos + ChessBoardChooser.LOWEST);
            editor.commit();
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chesssizer);
        SharedPreferences settings = getSharedPreferences(CHESS_SIZE, 0);
        int width = settings.getInt("width", DEFAULT_WIDTH);
        int height = settings.getInt("height", DEFAULT_HEIGHT);
        Spinner wspin = (Spinner) findViewById(R.id.rows);
        Spinner hspin = (Spinner) findViewById(R.id.cols);
        wspin.setSelection(width - 2);
        hspin.setSelection(height - 2);
        wspin.setOnItemSelectedListener(new DimChooser("width"));
        hspin.setOnItemSelectedListener(new DimChooser("height"));
    }

    public static Size getPatternSize(Context ctx) {
        SharedPreferences settings = ctx.getSharedPreferences(CHESS_SIZE, 0);
        return new Size(settings.getInt("width", DEFAULT_WIDTH), settings.getInt("height", DEFAULT_HEIGHT));
    }
}
