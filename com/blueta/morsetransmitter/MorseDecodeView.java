package com.blueta.morsetransmitter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import com.blueta.morsetransmitter.MorseDecodeMain.MosreSampleData;
import com.blueta.morsetransmitter.MorseDecodeMain.MosreSymbol;
import java.util.LinkedList;

public class MorseDecodeView extends View {
    final String ALPHABET_GAB;
    String DebugDataText;
    final String LONG;
    Paint LowerDurationMeanFirstStatitic;
    Paint LowerDurationMeanSecondStatitic;
    Paint LowerDurationMeanStatitic;
    Paint LowerDurationMeanThirdStatitic;
    Paint LowerDurationStatitic;
    final String NOT_DEFINED;
    final String SHORT;
    final String SYMBOL_GAB;
    RectF TextBackgroundRectf;
    RectF TextTransmittedBackgroundRectf;
    Paint UpperDurationStatitic;
    final String WORD_GAB;
    Paint debugDataTextPaint;
    MosreDecisionFactor decisionFactorToDraw;
    String decodedText;
    Paint decodedTextPanit;
    int graphBottom;
    Paint graphFrame;
    int graphHeight;
    final int graphMagScale;
    int graphTimeScale;
    final int graphTimeScaleAdjustStep;
    int graphWidth;
    final int graphXStart;
    final int graphYStart;
    boolean isDrawGraphOn;
    float magintudePerPixelScale;
    int measureWidth;
    int measuredHeidht;
    int milisecPerPixelScale;
    int previewDispHight;
    int previewDispWidth;
    Paint sampledData;
    LinkedList<MosreSampleData> sampledDataListToDraw;
    float statMagintudePerPixelScale;
    Paint symbolAlphabetGab;
    LinkedList<MosreSymbol> symbolListToDraw;
    Paint symbolLong;
    Paint symbolShort;
    Paint symbolSymbolGab;
    Paint symbolWordGab;
    Paint targetCircle;
    final int targetCircleThickness;
    Paint targetDetectCircle;
    final int targetDetectCircleThickness;
    boolean targetOn;
    int targetSize;
    Paint textBagroundRountRect;
    final int textMarginInPixel;
    Paint textTransmittedBagroundRountRect;
    Paint thresholdLowerDurationag;
    Paint thresholdMag;
    Paint thresholdSpaceDurationag;
    Paint thresholdUpperDurationag;
    String transmittedText;

    public MorseDecodeView(Context context) {
        super(context);
        this.LONG = "-";
        this.SHORT = ".";
        this.SYMBOL_GAB = "symbol gab";
        this.ALPHABET_GAB = "alphabet gab";
        this.WORD_GAB = "word gab";
        this.NOT_DEFINED = "not defined";
        this.graphYStart = 10;
        this.graphXStart = 100;
        this.graphTimeScale = 10000;
        this.graphTimeScaleAdjustStep = 2000;
        this.graphMagScale = 256;
        this.targetCircleThickness = 3;
        this.targetDetectCircleThickness = 10;
        this.targetOn = false;
        this.isDrawGraphOn = false;
        this.textMarginInPixel = 10;
    }

    public MorseDecodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.LONG = "-";
        this.SHORT = ".";
        this.SYMBOL_GAB = "symbol gab";
        this.ALPHABET_GAB = "alphabet gab";
        this.WORD_GAB = "word gab";
        this.NOT_DEFINED = "not defined";
        this.graphYStart = 10;
        this.graphXStart = 100;
        this.graphTimeScale = 10000;
        this.graphTimeScaleAdjustStep = 2000;
        this.graphMagScale = 256;
        this.targetCircleThickness = 3;
        this.targetDetectCircleThickness = 10;
        this.targetOn = false;
        this.isDrawGraphOn = false;
        this.textMarginInPixel = 10;
    }

    public MorseDecodeView(Context context, AttributeSet ats, int defaultStyle) {
        super(context, ats, defaultStyle);
        this.LONG = "-";
        this.SHORT = ".";
        this.SYMBOL_GAB = "symbol gab";
        this.ALPHABET_GAB = "alphabet gab";
        this.WORD_GAB = "word gab";
        this.NOT_DEFINED = "not defined";
        this.graphYStart = 10;
        this.graphXStart = 100;
        this.graphTimeScale = 10000;
        this.graphTimeScaleAdjustStep = 2000;
        this.graphMagScale = 256;
        this.targetCircleThickness = 3;
        this.targetDetectCircleThickness = 10;
        this.targetOn = false;
        this.isDrawGraphOn = false;
        this.textMarginInPixel = 10;
    }

    public void RegisterDatas(LinkedList<MosreSampleData> sampledataList, LinkedList<MosreSymbol> symbolList, MosreDecisionFactor decisionFactor, int previewDisHight, int previewDisWidth) {
        this.sampledDataListToDraw = sampledataList;
        this.symbolListToDraw = symbolList;
        this.decisionFactorToDraw = decisionFactor;
        this.previewDispHight = previewDisHight;
        this.previewDispWidth = previewDisWidth;
        initDatas();
    }

    protected void onMeasure(int wMeasureSpec, int hMeasufeSpec) {
        this.measuredHeidht = measureHeight(hMeasufeSpec);
        this.measureWidth = measureWidth(wMeasureSpec);
        setMeasuredDimension(this.measureWidth, this.measuredHeidht);
        setGraphRegionAndScale(this.measureWidth, this.measuredHeidht);
        initTextBackGroundData();
        initTransmittedTextBackGroundData();
    }

    private void setGraphRegionAndScale(int height, int width) {
        this.graphWidth = (height * 2) / 3;
        this.graphHeight = (width * 1) / 3;
        this.graphBottom = this.graphHeight + 10;
        this.milisecPerPixelScale = this.graphTimeScale / this.graphWidth;
        this.magintudePerPixelScale = 256.0f / ((float) this.graphHeight);
        this.decisionFactorToDraw.getClass();
        this.statMagintudePerPixelScale = (256.0f / ((float) this.graphHeight)) / 10.0f;
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
        decodedMessageDraw(canvas);
        transmittededMessageDraw(canvas);
        drawTarget(canvas);
        drawDetectedCircle(canvas);
        super.onDraw(canvas);
    }

    private void drawMeasuredData(Canvas canvas) {
        if (this.sampledDataListToDraw.size() > 0) {
            int count;
            canvas.drawLine(100.0f, (float) this.graphBottom, (float) (this.graphWidth + 100), (float) this.graphBottom, this.graphFrame);
            canvas.drawLine(100.0f, 10.0f, 100.0f, (float) this.graphBottom, this.graphFrame);
            int thresholdMagPos = this.graphBottom - ((int) (((float) this.decisionFactorToDraw.threshold_magnitude) / this.magintudePerPixelScale));
            canvas.drawLine(100.0f, (float) thresholdMagPos, (float) (this.graphWidth + 100), (float) thresholdMagPos, this.thresholdMag);
            int thresholdDurationPos = (((int) this.decisionFactorToDraw.thresholdUpperDration) / this.milisecPerPixelScale) + 100;
            canvas.drawLine((float) thresholdDurationPos, 10.0f, (float) thresholdDurationPos, (float) this.graphBottom, this.thresholdUpperDurationag);
            int thresholdLowerDurationPos = (((int) this.decisionFactorToDraw.thresholdLowerDration) / this.milisecPerPixelScale) + 100;
            canvas.drawLine((float) thresholdLowerDurationPos, 10.0f, (float) thresholdLowerDurationPos, (float) this.graphBottom, this.thresholdLowerDurationag);
            int thresholdSpaceDurationPos = (((int) this.decisionFactorToDraw.thresholdSpaceDration) / this.milisecPerPixelScale) + 100;
            canvas.drawLine((float) thresholdSpaceDurationPos, 10.0f, (float) thresholdSpaceDurationPos, (float) this.graphBottom, this.thresholdSpaceDurationag);
            double lastSamplesTime = (double) ((MosreSampleData) this.sampledDataListToDraw.get(0)).sampleDataTimeStampInMilliSec;
            for (count = 0; count < this.sampledDataListToDraw.size(); count++) {
                int yPos = this.graphBottom - ((int) (((float) ((MosreSampleData) this.sampledDataListToDraw.get(count)).magnitude) / this.magintudePerPixelScale));
                int timeGab = (int) (lastSamplesTime - ((double) ((MosreSampleData) this.sampledDataListToDraw.get(count)).sampleDataTimeStampInMilliSec));
                if (timeGab > this.graphTimeScale) {
                    break;
                }
                canvas.drawCircle((float) ((timeGab / this.milisecPerPixelScale) + 100), (float) yPos, 2.0f, this.sampledData);
            }
            if (this.symbolListToDraw.size() > 0) {
                for (count = 0; count < this.symbolListToDraw.size(); count++) {
                    int topPos = this.graphBottom - (this.graphHeight / 5);
                    long endOfSymbol = (long) ((int) (lastSamplesTime - ((double) ((MosreSymbol) this.symbolListToDraw.get(count)).end_of_symbol)));
                    long startOfsymbol = ((long) ((int) lastSamplesTime)) - ((MosreSymbol) this.symbolListToDraw.get(count)).start_of_symbol;
                    if (((int) startOfsymbol) > this.graphTimeScale) {
                        break;
                    }
                    int leftPos = (((int) endOfSymbol) / this.milisecPerPixelScale) + 100;
                    int rightPos = (((int) startOfsymbol) / this.milisecPerPixelScale) + 100;
                    if (((MosreSymbol) this.symbolListToDraw.get(count)).mean.equals("-")) {
                        canvas.drawRect((float) leftPos, (float) topPos, (float) rightPos, (float) this.graphBottom, this.symbolLong);
                    } else if (((MosreSymbol) this.symbolListToDraw.get(count)).mean.equals(".")) {
                        canvas.drawRect((float) leftPos, (float) topPos, (float) rightPos, (float) this.graphBottom, this.symbolShort);
                    } else if (((MosreSymbol) this.symbolListToDraw.get(count)).mean.equals("symbol gab")) {
                        canvas.drawRect((float) leftPos, (float) topPos, (float) rightPos, (float) this.graphBottom, this.symbolSymbolGab);
                    } else if (((MosreSymbol) this.symbolListToDraw.get(count)).mean.equals("alphabet gab")) {
                        canvas.drawRect((float) leftPos, (float) topPos, (float) rightPos, (float) this.graphBottom, this.symbolAlphabetGab);
                    } else if (((MosreSymbol) this.symbolListToDraw.get(count)).mean.equals("word gab")) {
                        canvas.drawRect((float) leftPos, (float) topPos, (float) rightPos, (float) this.graphBottom, this.symbolWordGab);
                    } else {
                        ((MosreSymbol) this.symbolListToDraw.get(count)).mean.equals("not defined");
                    }
                }
                if (this.DebugDataText != null) {
                    canvas.drawText(this.DebugDataText, 100.0f, (float) (this.graphBottom + 20), this.debugDataTextPaint);
                }
            }
            drawLowerSymbolDurationData(canvas);
        }
    }

    private void drawUpperSymbolDurationData(Canvas canvas) {
        int count = 0;
        while (true) {
            this.decisionFactorToDraw.getClass();
            if (count >= 1000) {
                break;
            }
            int timeGab;
            if (this.decisionFactorToDraw.UpperSymbolDurationData[count] != 0) {
                int yPos = this.graphBottom - ((int) (((float) this.decisionFactorToDraw.UpperSymbolDurationData[count]) / this.statMagintudePerPixelScale));
                timeGab = count * 10;
                if (timeGab > this.graphTimeScale) {
                    break;
                }
                int i = (timeGab / this.milisecPerPixelScale) + 100;
            }
            count++;
        }
        count = 0;
        while (true) {
            this.decisionFactorToDraw.getClass();
            if (count >= 1000) {
                break;
            }
            if (this.decisionFactorToDraw.UpperSymbolDurationStatisticData[count] != 0) {
                yPos = this.graphBottom - ((int) (((float) this.decisionFactorToDraw.UpperSymbolDurationStatisticData[count]) / this.statMagintudePerPixelScale));
                timeGab = count * 10;
                if (timeGab > this.graphTimeScale) {
                    break;
                }
                canvas.drawCircle((float) ((timeGab / this.milisecPerPixelScale) + 100), (float) yPos, 2.0f, this.sampledData);
            }
            count++;
        }
        if (this.decisionFactorToDraw.SymbolPosCenter[0] != null) {
            int UpperDurationPos1 = ((this.decisionFactorToDraw.SymbolPosCenter[0].pos * 10) / this.milisecPerPixelScale) + 100;
            canvas.drawLine((float) UpperDurationPos1, 10.0f, (float) UpperDurationPos1, (float) this.graphBottom, this.LowerDurationMeanFirstStatitic);
        }
        if (this.decisionFactorToDraw.SymbolPosCenter[1] != null) {
            int UpperDurationPos2 = ((this.decisionFactorToDraw.SymbolPosCenter[1].pos * 10) / this.milisecPerPixelScale) + 100;
            canvas.drawLine((float) UpperDurationPos2, 10.0f, (float) UpperDurationPos2, (float) this.graphBottom, this.LowerDurationMeanSecondStatitic);
        }
    }

    private void drawLowerSymbolDurationData(Canvas canvas) {
        int timeGab;
        int count = 0;
        while (true) {
            this.decisionFactorToDraw.getClass();
            if (count >= 1000) {
                break;
            }
            if (this.decisionFactorToDraw.LowerSymbolDurationData[count] != 0) {
                int yPos = this.graphBottom - ((int) (((float) this.decisionFactorToDraw.LowerSymbolDurationData[count]) / this.statMagintudePerPixelScale));
                timeGab = count * 10;
                if (timeGab > this.graphTimeScale) {
                    break;
                }
                int i = (timeGab / this.milisecPerPixelScale) + 100;
            }
            count++;
        }
        count = 0;
        while (true) {
            this.decisionFactorToDraw.getClass();
            if (count >= 1000) {
                break;
            }
            if (this.decisionFactorToDraw.LowerSymbolDurationStatisticData[count] != 0) {
                yPos = this.graphBottom - ((int) (((float) this.decisionFactorToDraw.LowerSymbolDurationStatisticData[count]) / this.statMagintudePerPixelScale));
                timeGab = count * 10;
                if (timeGab > this.graphTimeScale) {
                    break;
                }
                canvas.drawCircle((float) ((timeGab / this.milisecPerPixelScale) + 100), (float) yPos, 2.0f, this.LowerDurationStatitic);
            }
            count++;
        }
        if (this.decisionFactorToDraw.gabPosCenter[0] != null) {
            int LowerDurationPos1 = ((this.decisionFactorToDraw.gabPosCenter[0].pos * 10) / this.milisecPerPixelScale) + 100;
            canvas.drawLine((float) LowerDurationPos1, 10.0f, (float) LowerDurationPos1, (float) this.graphBottom, this.LowerDurationMeanFirstStatitic);
        }
        if (this.decisionFactorToDraw.gabPosCenter[1] != null) {
            int LowerDurationPos2 = ((this.decisionFactorToDraw.gabPosCenter[1].pos * 10) / this.milisecPerPixelScale) + 100;
            canvas.drawLine((float) LowerDurationPos2, 10.0f, (float) LowerDurationPos2, (float) this.graphBottom, this.LowerDurationMeanSecondStatitic);
        }
        if (this.decisionFactorToDraw.gabPosCenter[2] != null) {
            int LowerDurationPos3 = ((this.decisionFactorToDraw.gabPosCenter[2].pos * 10) / this.milisecPerPixelScale) + 100;
            canvas.drawLine((float) LowerDurationPos3, 10.0f, (float) LowerDurationPos3, (float) this.graphBottom, this.LowerDurationMeanThirdStatitic);
        }
    }

    private void initDatas() {
        this.decodedTextPanit = new Paint();
        this.decodedTextPanit.setStyle(Style.FILL);
        this.decodedTextPanit.setColor(-1);
        this.decodedTextPanit.setTextSize(30.0f);
        this.sampledData = new Paint();
        this.sampledData.setStyle(Style.FILL);
        this.sampledData.setColor(-65536);
        this.LowerDurationStatitic = new Paint();
        this.LowerDurationStatitic.setStyle(Style.FILL);
        this.LowerDurationStatitic.setColor(-12303292);
        this.LowerDurationMeanStatitic = new Paint();
        this.LowerDurationMeanStatitic.setStyle(Style.FILL);
        this.LowerDurationMeanStatitic.setColor(-16711681);
        this.UpperDurationStatitic = new Paint();
        this.UpperDurationStatitic.setStyle(Style.FILL);
        this.UpperDurationStatitic.setColor(-16776961);
        this.thresholdMag = new Paint();
        this.thresholdMag.setStyle(Style.FILL);
        this.thresholdMag.setStrokeWidth(2.0f);
        this.thresholdMag.setColor(-16711936);
        this.graphFrame = new Paint();
        this.graphFrame.setStyle(Style.FILL);
        this.thresholdMag.setStrokeWidth(3.0f);
        this.graphFrame.setColor(-16776961);
        this.thresholdUpperDurationag = new Paint();
        this.thresholdUpperDurationag.setStyle(Style.FILL);
        this.thresholdUpperDurationag.setStrokeWidth(2.0f);
        this.thresholdUpperDurationag.setColor(-16711936);
        this.thresholdLowerDurationag = new Paint();
        this.thresholdLowerDurationag.setStyle(Style.FILL);
        this.thresholdLowerDurationag.setStrokeWidth(2.0f);
        this.thresholdLowerDurationag.setColor(-12303292);
        this.thresholdSpaceDurationag = new Paint();
        this.thresholdSpaceDurationag.setStyle(Style.FILL);
        this.thresholdSpaceDurationag.setStrokeWidth(2.0f);
        this.thresholdSpaceDurationag.setColor(-3355444);
        this.LowerDurationMeanFirstStatitic = new Paint();
        this.LowerDurationMeanFirstStatitic.setStyle(Style.FILL);
        this.LowerDurationMeanFirstStatitic.setStrokeWidth(2.0f);
        this.LowerDurationMeanFirstStatitic.setColor(-65536);
        this.LowerDurationMeanSecondStatitic = new Paint();
        this.LowerDurationMeanSecondStatitic.setStyle(Style.FILL);
        this.LowerDurationMeanSecondStatitic.setStrokeWidth(2.0f);
        this.LowerDurationMeanSecondStatitic.setColor(-16776961);
        this.LowerDurationMeanThirdStatitic = new Paint();
        this.LowerDurationMeanThirdStatitic.setStyle(Style.FILL);
        this.LowerDurationMeanThirdStatitic.setStrokeWidth(2.0f);
        this.LowerDurationMeanThirdStatitic.setColor(-16711681);
        this.symbolLong = new Paint();
        this.symbolLong.setStyle(Style.FILL);
        this.symbolLong.setStrokeWidth(2.0f);
        this.symbolLong.setColor(-16711936);
        this.symbolLong.setAlpha(127);
        this.symbolShort = new Paint();
        this.symbolShort.setStyle(Style.FILL);
        this.symbolShort.setStrokeWidth(2.0f);
        this.symbolShort.setColor(-16776961);
        this.symbolShort.setAlpha(127);
        this.symbolSymbolGab = new Paint();
        this.symbolSymbolGab.setStyle(Style.FILL);
        this.symbolSymbolGab.setStrokeWidth(2.0f);
        this.symbolSymbolGab.setColor(-7829368);
        this.symbolSymbolGab.setAlpha(127);
        this.symbolAlphabetGab = new Paint();
        this.symbolAlphabetGab.setStyle(Style.FILL);
        this.symbolAlphabetGab.setStrokeWidth(2.0f);
        this.symbolAlphabetGab.setColor(-16711681);
        this.symbolAlphabetGab.setAlpha(127);
        this.symbolWordGab = new Paint();
        this.symbolWordGab.setStyle(Style.FILL);
        this.symbolWordGab.setStrokeWidth(2.0f);
        this.symbolWordGab.setColor(-1);
        this.symbolWordGab.setAlpha(127);
        this.debugDataTextPaint = new Paint();
        this.debugDataTextPaint.setStyle(Style.FILL);
        this.debugDataTextPaint.setColor(-16776961);
        this.debugDataTextPaint.setTextSize(20.0f);
        this.targetCircle = new Paint();
        this.targetCircle.setStyle(Style.STROKE);
        this.targetCircle.setColor(-16776961);
        this.targetCircle.setStrokeWidth(3.0f);
        this.targetCircle.setAntiAlias(true);
        this.targetDetectCircle = new Paint();
        this.targetDetectCircle.setStyle(Style.STROKE);
        this.targetDetectCircle.setColor(-65536);
        this.targetDetectCircle.setStrokeWidth(10.0f);
        this.targetDetectCircle.setAntiAlias(true);
        this.targetDetectCircle.setAlpha(160);
        this.textBagroundRountRect = new Paint();
        this.textBagroundRountRect.setStyle(Style.FILL);
        this.textBagroundRountRect.setColor(-65536);
        this.textBagroundRountRect.setAntiAlias(true);
        this.textBagroundRountRect.setAlpha(160);
        this.textTransmittedBagroundRountRect = new Paint();
        this.textTransmittedBagroundRountRect.setStyle(Style.FILL);
        this.textTransmittedBagroundRountRect.setColor(-16776961);
        this.textTransmittedBagroundRountRect.setAntiAlias(true);
        this.textTransmittedBagroundRountRect.setAlpha(160);
    }

    private void initTextBackGroundData() {
        int width = (this.previewDispWidth * 6) / 7;
        this.TextBackgroundRectf = new RectF();
        this.TextBackgroundRectf.bottom = (float) ((this.previewDispHight * 9) / 10);
        this.TextBackgroundRectf.left = (float) ((this.measureWidth - width) / 2);
        this.TextBackgroundRectf.right = this.TextBackgroundRectf.left + ((float) width);
        this.TextBackgroundRectf.top = this.TextBackgroundRectf.bottom - ((float) 40);
    }

    private void initTransmittedTextBackGroundData() {
        int width = (this.previewDispWidth * 6) / 7;
        this.TextTransmittedBackgroundRectf = new RectF();
        this.TextTransmittedBackgroundRectf.bottom = this.TextBackgroundRectf.top - 20.0f;
        this.TextTransmittedBackgroundRectf.left = (float) ((this.measureWidth - width) / 2);
        this.TextTransmittedBackgroundRectf.right = this.TextTransmittedBackgroundRectf.left + ((float) width);
        this.TextTransmittedBackgroundRectf.top = this.TextTransmittedBackgroundRectf.bottom - ((float) 40);
    }

    public void DecodedMessageDraw(String text) {
        this.decodedText = new String(text);
    }

    public void TransmitdMessageDraw(String text) {
        this.transmittedText = new String(text);
    }

    public void DebugDataTextDraw(String text) {
        this.DebugDataText = new String(text);
    }

    private void decodedMessageDraw(Canvas canvas) {
        if (this.decodedText != null) {
            StringBuffer stringToPrint = new StringBuffer();
            stringToPrint.append("\u2199: ");
            stringToPrint.append(this.decodedText);
            canvas.drawRoundRect(this.TextBackgroundRectf, 10.0f, 10.0f, this.textBagroundRountRect);
            while (((float) ((int) Math.ceil((double) this.decodedTextPanit.measureText(stringToPrint.toString())))) >= this.TextBackgroundRectf.width() - 20.0f) {
                stringToPrint.deleteCharAt(3);
            }
            canvas.drawText(stringToPrint.toString(), this.TextBackgroundRectf.left + 10.0f, this.TextBackgroundRectf.bottom - 10.0f, this.decodedTextPanit);
        }
    }

    private void transmittededMessageDraw(Canvas canvas) {
        if (this.transmittedText != null && this.transmittedText.length() != 0) {
            StringBuffer stringToPrint = new StringBuffer();
            stringToPrint.append("\u2197: ");
            stringToPrint.append(this.transmittedText);
            canvas.drawRoundRect(this.TextTransmittedBackgroundRectf, 10.0f, 10.0f, this.textTransmittedBagroundRountRect);
            while (((float) ((int) Math.ceil((double) this.decodedTextPanit.measureText(stringToPrint.toString())))) >= this.TextTransmittedBackgroundRectf.width() - 20.0f) {
                stringToPrint.deleteCharAt(3);
            }
            canvas.drawText(stringToPrint.toString(), this.TextTransmittedBackgroundRectf.left + 10.0f, this.TextTransmittedBackgroundRectf.bottom - 10.0f, this.decodedTextPanit);
        }
    }

    private void drawTarget(Canvas canvas) {
        canvas.drawCircle((float) (this.measureWidth / 2), (float) (this.measuredHeidht / 2), (float) (this.targetSize / 2), this.targetCircle);
    }

    private void drawDetectedCircle(Canvas canvas) {
        int xPoint = this.measureWidth / 2;
        int yPoint = this.measuredHeidht / 2;
        if (this.targetOn) {
            canvas.drawCircle((float) xPoint, (float) yPoint, (float) (((this.targetSize / 2) + 3) + 5), this.targetDetectCircle);
        }
    }

    public void drawTargetOn() {
        this.targetOn = true;
    }

    public void drawTargetOff() {
        this.targetOn = false;
    }

    public void ZoomOut() {
        this.graphTimeScale += 2000;
        this.milisecPerPixelScale = this.graphTimeScale / this.graphWidth;
    }

    public void SetTargetSize(int size) {
        this.targetSize = size;
    }

    public void ZoomIn() {
        if (this.graphTimeScale > 2000) {
            this.graphTimeScale -= 2000;
            this.milisecPerPixelScale = this.graphTimeScale / this.graphWidth;
        }
    }

    public void ToggleGraphMode() {
        this.isDrawGraphOn = !this.isDrawGraphOn;
    }
}
