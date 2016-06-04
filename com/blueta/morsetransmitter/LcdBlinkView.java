package com.blueta.morsetransmitter;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;

public class LcdBlinkView extends View {
    int color;

    public LcdBlinkView(Context context) {
        super(context);
        this.color = -16777216;
        initLcdBlinkView();
    }

    public LcdBlinkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.color = -16777216;
        initLcdBlinkView();
    }

    public LcdBlinkView(Context context, AttributeSet ats, int defaultStyle) {
        super(context, ats, defaultStyle);
        this.color = -16777216;
        initLcdBlinkView();
    }

    protected void onMeasure(int wMeasureSpec, int hMeasufeSpec) {
        setMeasuredDimension(measureWidth(wMeasureSpec), measureHeight(hMeasufeSpec));
    }

    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result;
        if (specMode == Integer.MIN_VALUE) {
            result = specSize;
        } else if (specMode == 1073741824) {
            result = specSize;
        }
        return specSize;
    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result;
        if (specMode == Integer.MIN_VALUE) {
            result = specSize;
        } else if (specMode == 1073741824) {
            result = specSize;
        }
        return specSize;
    }

    protected void onDraw(Canvas canvas) {
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        canvas.drawColor(this.color);
    }

    public void TurnOn() {
        this.color = -1;
    }

    public void TurnOff() {
        this.color = -16777216;
    }

    protected void initLcdBlinkView() {
        setFocusable(true);
    }
}
