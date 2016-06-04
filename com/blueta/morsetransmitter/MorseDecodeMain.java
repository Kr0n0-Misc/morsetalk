package com.blueta.morsetransmitter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.blueta.morsetransmitter.jni.Processor;
import com.blueta.morsetransmitter.jni.bluetea_camera;
import com.opencv.camera.NativePreviewer;
import com.opencv.camera.NativeProcessor;
import com.opencv.camera.NativeProcessor.PoolCallback;
import com.opencv.jni.image_pool;
import com.opencv.opengl.GL2CameraViewer;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;

public class MorseDecodeMain extends Activity {
    static final int DIALOG_CALIBRATING = 0;
    static final int DIALOG_CALIBRATION_FILE = 1;
    private static final int DIALOG_OPENING_TUTORIAL = 2;
    private static final int DIALOG_TUTORIAL_CHESS = 6;
    private static final int DIALOG_TUTORIAL_FAST = 3;
    private static final int DIALOG_TUTORIAL_MORSE = 7;
    private static final int DIALOG_TUTORIAL_STAR = 5;
    private static final int DIALOG_TUTORIAL_SURF = 4;
    public static Camera NativePrevieCamera = null;
    public static final String START_DEBUG_INFO = "debug info mode";
    public static final String START_DEBUG_INFO_GLVIEW = "debug info mode with glview";
    public static final String START_MODE = "start mode";
    public static final String START_NORMAL = "normal mode";
    final String ALPHABET_GAB;
    final String LONG;
    final String LOWER;
    private final int MAX_SAMPLE_DATA;
    private final int MAX_SIMBOL_DATA;
    final String NOT_DEFINED;
    final String NO_RESULT;
    final String SHORT;
    final String SYMBOL_GAB;
    final String UPPER;
    final String WORD_GAB;
    String calib_file_loc;
    String calib_text;
    private boolean captureChess;
    MosreDecisionFactor decisionFactor;
    boolean doResetData;
    ArrayList<MorseCodeElement> encodedMessageInTalkmode;
    EnglishMorseTrans englishMorseTranslator;
    int fcount;
    double fps;
    private FrameLayout frame;
    private GL2CameraViewer glview;
    private Handler handler;
    private NativePreviewer mPreview;
    EditText messageEnter;
    MorseDecodeView morseDecodeView;
    LinkedList<MosreSampleData> morseSampledList;
    LinkedList<MosreSymbol> morseSymbolList;
    private int preViewDispHight;
    private int preViewDispWidth;
    final Processor processor;
    MorseTransDecodeModeReceiver receiver;
    StringBuffer resultToPrint;
    Button sendMessage;
    Date start;
    private String startMode;
    private int targetSizeInPixcel;
    private final double targetSizeRatio;
    ComponentName transmitServiceInTalkMode;
    int transmittedIndex;
    private Runnable updateMorseDecoded;
    double zoomScale;
    double zoomStep;

    class CalibrationProcessor implements PoolCallback {
        boolean calibrated;

        CalibrationProcessor() {
            this.calibrated = false;
        }

        public void process(int idx, image_pool pool, long timestamp, NativeProcessor nativeProcessor) {
            if (this.calibrated) {
                MorseDecodeMain.this.processor.drawText(idx, pool, "Calibrated successfully");
                return;
            }
            if (MorseDecodeMain.this.processor.getNumberDetectedChessboards() == 10) {
                File opencvdir = new File(Environment.getExternalStorageDirectory(), "opencv");
                if (!opencvdir.exists()) {
                    opencvdir.mkdir();
                }
                File calibfile = new File(opencvdir, "camera.yml");
                MorseDecodeMain.this.calib_file_loc = calibfile.getAbsolutePath();
                MorseDecodeMain.this.processor.calibrate(calibfile.getAbsolutePath());
                Log.i("chessboard", "calibrated");
                this.calibrated = true;
                MorseDecodeMain.this.processor.resetChess();
                MorseDecodeMain.this.runOnUiThread(new Runnable() {
                    public void run() {
                        MorseDecodeMain.this.removeDialog(MorseDecodeMain.DIALOG_CALIBRATING);
                    }
                });
                Scanner scanner;
                try {
                    StringBuilder text = new StringBuilder();
                    String NL = System.getProperty("line.separator");
                    scanner = new Scanner(calibfile);
                    while (scanner.hasNextLine()) {
                        text.append(scanner.nextLine() + NL);
                    }
                    scanner.close();
                    MorseDecodeMain.this.calib_text = text.toString();
                    MorseDecodeMain.this.runOnUiThread(new Runnable() {
                        public void run() {
                            MorseDecodeMain.this.showDialog(MorseDecodeMain.DIALOG_CALIBRATION_FILE);
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (Throwable th) {
                    scanner.close();
                }
            } else if (MorseDecodeMain.this.captureChess && MorseDecodeMain.this.processor.detectAndDrawChessboard(idx, pool)) {
                MorseDecodeMain.this.runOnUiThread(new Runnable() {
                    String numchess;

                    {
                        this.numchess = String.valueOf(MorseDecodeMain.this.processor.getNumberDetectedChessboards());
                    }

                    public void run() {
                        Toast.makeText(MorseDecodeMain.this, "Detected " + this.numchess + " of 10 chessboards", MorseDecodeMain.DIALOG_CALIBRATING).show();
                    }
                });
                Log.i("cvcamera", "detected a chessboard, n chess boards found: " + String.valueOf(MorseDecodeMain.this.processor.getNumberDetectedChessboards()));
            }
            MorseDecodeMain.this.captureChess = false;
            if (MorseDecodeMain.this.processor.getNumberDetectedChessboards() == 10) {
                MorseDecodeMain.this.runOnUiThread(new Runnable() {
                    public void run() {
                        MorseDecodeMain.this.showDialog(MorseDecodeMain.DIALOG_CALIBRATING);
                    }
                });
                MorseDecodeMain.this.processor.drawText(idx, pool, "Calibrating, please wait.");
            }
            if (MorseDecodeMain.this.processor.getNumberDetectedChessboards() < 10) {
                MorseDecodeMain.this.processor.drawText(idx, pool, "found " + MorseDecodeMain.this.processor.getNumberDetectedChessboards() + "/10 chessboards");
            }
        }
    }

    class FastProcessor implements PoolCallback {
        FastProcessor() {
        }

        public void process(int idx, image_pool pool, long timestamp, NativeProcessor nativeProcessor) {
            MorseDecodeMain.this.processor.detectAndDrawFeatures(idx, pool, bluetea_camera.DETECT_FAST);
        }
    }

    class MorseProcessor implements PoolCallback {
        MorseProcessor() {
        }

        public void process(int idx, image_pool pool, long timestamp, NativeProcessor nativeProcessor) {
            if (MorseDecodeMain.this.doResetData) {
                MorseDecodeMain.this.decisionFactor.ResetFactors();
                MorseDecodeMain.this.morseSampledList.clear();
                MorseDecodeMain.this.morseSymbolList.clear();
                MorseDecodeMain.this.doResetData = false;
            }
            MorseDecodeMain.this.targetSizeInPixcel = (int) (((double) MorseDecodeMain.this.preViewDispWidth) * 0.08d);
            MorseDecodeMain.this.morseDecodeView.SetTargetSize(MorseDecodeMain.this.targetSizeInPixcel);
            MorseDecodeMain.this.ProcessSampledData(idx, pool, timestamp);
            MorseDecodeMain.this.generateUpperSymbol();
            MorseDecodeMain.this.generateLowerSymbol();
            String result = MorseDecodeMain.this.DecodeMorseToAlphabet();
            if (!result.equals("no result")) {
                MorseDecodeMain.this.resultToPrint.append(result);
            }
            MorseDecodeMain.this.handler.post(MorseDecodeMain.this.updateMorseDecoded);
            if (MorseDecodeMain.this.morseSymbolList.size() > 0) {
                StringBuffer textToPrint = new StringBuffer("Simbol:");
                for (int count = MorseDecodeMain.DIALOG_CALIBRATING; count < MorseDecodeMain.this.morseSymbolList.size(); count += MorseDecodeMain.DIALOG_CALIBRATION_FILE) {
                    if (((MosreSymbol) MorseDecodeMain.this.morseSymbolList.get(count)).mean.equals("-")) {
                        textToPrint.append("-");
                    } else if (((MosreSymbol) MorseDecodeMain.this.morseSymbolList.get(count)).mean.equals(".")) {
                        textToPrint.append(".");
                    } else if (((MosreSymbol) MorseDecodeMain.this.morseSymbolList.get(count)).mean.equals("alphabet gab")) {
                        textToPrint.append("/");
                    } else if (((MosreSymbol) MorseDecodeMain.this.morseSymbolList.get(count)).mean.equals("word gab")) {
                        textToPrint.append(" ");
                    }
                }
            }
            MorseDecodeMain.this.measureFps();
        }
    }

    public class MorseTransDecodeModeReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle bundle = intent.getExtras();
            if (action.equals(MorseTranService.MORSE_TRANSMIT_DONE) && MorseDecodeMain.this.transmitServiceInTalkMode != null) {
                MorseDecodeMain.this.stopTransmitMessageServiceInTalkMode();
                MorseDecodeMain.NativePrevieCamera = null;
            }
            if (action.equals(MorseTranService.MORSE_TRANSMIT_PROGRESS)) {
                MorseDecodeMain.this.transmittedIndex = bundle.getInt(MorseTranService.MORSE_TRANSMITED_INDEX);
            }
        }
    }

    public class MosreSampleData {
        public int magnitude;
        public long sampleDataTimeStampInMilliSec;
        public int value;
    }

    public class MosreSymbol {
        public long duration;
        public long end_of_symbol;
        public String mean;
        public boolean processed;
        public long start_of_symbol;
        public String type;

        public MosreSymbol() {
            this.type = "not defined";
            this.duration = 0;
            this.start_of_symbol = 0;
            this.end_of_symbol = 0;
            this.mean = "not defined";
        }
    }

    class STARProcessor implements PoolCallback {
        STARProcessor() {
        }

        public void process(int idx, image_pool pool, long timestamp, NativeProcessor nativeProcessor) {
            MorseDecodeMain.this.processor.detectAndDrawFeatures(idx, pool, bluetea_camera.DETECT_STAR);
        }
    }

    class SURFProcessor implements PoolCallback {
        SURFProcessor() {
        }

        public void process(int idx, image_pool pool, long timestamp, NativeProcessor nativeProcessor) {
            MorseDecodeMain.this.processor.detectAndDrawFeatures(idx, pool, bluetea_camera.DETECT_SURF);
        }
    }

    public MorseDecodeMain() {
        this.MAX_SAMPLE_DATA = 300;
        this.MAX_SIMBOL_DATA = 30;
        this.LONG = "-";
        this.SHORT = ".";
        this.SYMBOL_GAB = "symbol gab";
        this.ALPHABET_GAB = "alphabet gab";
        this.WORD_GAB = "word gab";
        this.NOT_DEFINED = "not defined";
        this.UPPER = "upper";
        this.LOWER = "lower";
        this.NO_RESULT = "no result";
        this.morseSampledList = new LinkedList();
        this.morseSymbolList = new LinkedList();
        this.decisionFactor = new MosreDecisionFactor();
        this.englishMorseTranslator = new EnglishMorseTrans();
        this.handler = new Handler();
        this.targetSizeRatio = 0.08d;
        this.targetSizeInPixcel = DIALOG_CALIBRATING;
        this.zoomScale = 1.0d;
        this.transmitServiceInTalkMode = null;
        this.receiver = null;
        this.transmittedIndex = DIALOG_CALIBRATING;
        this.glview = null;
        this.zoomStep = 0.2d;
        this.processor = new Processor();
        this.resultToPrint = new StringBuffer();
        this.fcount = DIALOG_CALIBRATING;
        this.fps = 0.0d;
        this.calib_text = null;
        this.calib_file_loc = null;
        this.doResetData = false;
        this.updateMorseDecoded = new Runnable() {
            public void run() {
                if (MorseDecodeMain.this.morseSampledList.size() != 0) {
                    if (((MosreSampleData) MorseDecodeMain.this.morseSampledList.get(MorseDecodeMain.DIALOG_CALIBRATING)).value == MorseDecodeMain.DIALOG_CALIBRATION_FILE) {
                        MorseDecodeMain.this.morseDecodeView.drawTargetOn();
                    } else {
                        MorseDecodeMain.this.morseDecodeView.drawTargetOff();
                    }
                    MorseDecodeMain.this.morseDecodeView.DecodedMessageDraw(MorseDecodeMain.this.resultToPrint.toString());
                    MorseDecodeMain.this.drawTransmittedMessage();
                    MorseDecodeMain.this.morseDecodeView.DebugDataTextDraw(" th_u:" + MorseDecodeMain.this.decisionFactor.thresholdUpperDration + " th_l:" + MorseDecodeMain.this.decisionFactor.thresholdLowerDration + " fps: " + ((long) MorseDecodeMain.this.fps));
                    MorseDecodeMain.this.morseDecodeView.invalidate();
                }
            }
        };
    }

    ProgressDialog makeCalibDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Callibrating. Please wait...");
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    void toasts(int id) {
        switch (id) {
            case DIALOG_OPENING_TUTORIAL /*2*/:
                Toast.makeText(this, "Try clicking the menu for CV options.", DIALOG_CALIBRATION_FILE).show();
            case DIALOG_TUTORIAL_FAST /*3*/:
                Toast.makeText(this, "Detecting and Displaying FAST features", DIALOG_CALIBRATION_FILE).show();
            case DIALOG_TUTORIAL_SURF /*4*/:
                Toast.makeText(this, "Detecting and Displaying SURF features", DIALOG_CALIBRATION_FILE).show();
            case DIALOG_TUTORIAL_STAR /*5*/:
                Toast.makeText(this, "Detecting and Displaying STAR features", DIALOG_CALIBRATION_FILE).show();
            case DIALOG_TUTORIAL_CHESS /*6*/:
                Toast.makeText(this, "Calibration Mode, Point at a chessboard pattern and press the camera button, space,or the DPAD to capture.", DIALOG_CALIBRATION_FILE).show();
            case DIALOG_TUTORIAL_MORSE /*7*/:
                Toast.makeText(this, "Let the morse singal's position in a blue circle and press start button", DIALOG_CALIBRATION_FILE).show();
            default:
        }
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_CALIBRATING /*0*/:
                return makeCalibDialog();
            case DIALOG_CALIBRATION_FILE /*1*/:
                return makeCalibFileAlert();
            default:
                return null;
        }
    }

    private Dialog makeCalibFileAlert() {
        Builder builder = new Builder(this);
        builder.setMessage(this.calib_text).setTitle("camera.yml at " + this.calib_file_loc).setCancelable(false).setPositiveButton("Ok", new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        return builder.create();
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 23:
            case 27:
            case 62:
                this.captureChess = true;
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return super.onKeyLongPress(keyCode, event);
    }

    public void disableScreenTurnOff() {
        getWindow().setFlags(128, 128);
    }

    public void setOrientation() {
        setRequestedOrientation(DIALOG_CALIBRATING);
    }

    public void setFullscreen() {
        requestWindowFeature(DIALOG_CALIBRATION_FILE);
        getWindow().setFlags(1024, 1024);
    }

    public void setNoTitle() {
        requestWindowFeature(DIALOG_CALIBRATION_FILE);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Help");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        LinkedList<PoolCallback> defaultcallbackstack = new LinkedList();
        if (this.glview != null) {
            defaultcallbackstack.addFirst(this.glview.getDrawCallback());
        }
        if (item.getTitle().equals("FAST")) {
            defaultcallbackstack.addFirst(new FastProcessor());
            toasts(DIALOG_TUTORIAL_FAST);
        } else if (item.getTitle().equals("Chess")) {
            defaultcallbackstack.addFirst(new CalibrationProcessor());
            toasts(DIALOG_TUTORIAL_CHESS);
        } else if (item.getTitle().equals("STAR")) {
            defaultcallbackstack.addFirst(new STARProcessor());
            toasts(DIALOG_TUTORIAL_STAR);
        } else if (item.getTitle().equals("SURF")) {
            defaultcallbackstack.addFirst(new SURFProcessor());
            toasts(DIALOG_TUTORIAL_SURF);
        } else if (item.getTitle().equals("Morse")) {
            defaultcallbackstack.addFirst(new MorseProcessor());
            toasts(DIALOG_TUTORIAL_SURF);
        } else if (item.getTitle().equals("Help")) {
            boolean supRetVal = super.onOptionsItemSelected(item);
            openHelpDialog();
            return supRetVal;
        }
        this.mPreview.addCallbackStack(defaultcallbackstack);
        return true;
    }

    private void openHelpDialog() {
        new Builder(this).setTitle("Help").setView(LayoutInflater.from(this).inflate(R.layout.receive_mode_help_view, null)).setNegativeButton("Close", new OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).show();
    }

    private void iformOnlyFlashLightModeSupportDialog() {
        new Builder(this).setMessage("\nSending Message is supported \nonly in flash light mode.").setNegativeButton("Close", new OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).show();
    }

    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.startMode = (String) bundle.get(START_MODE);
        }
        setFullscreen();
        disableScreenTurnOff();
        setOrientation();
        this.frame = new FrameLayout(this);
        this.mPreview = new NativePreviewer(getApplication(), 80, 60);
        LayoutParams layoutParams = new LayoutParams(-2, -2);
        layoutParams.height = getWindowManager().getDefaultDisplay().getHeight();
        layoutParams.width = getWindowManager().getDefaultDisplay().getWidth();
        this.preViewDispHight = layoutParams.height;
        this.preViewDispWidth = layoutParams.width;
        this.frame.setForegroundGravity(DIALOG_TUTORIAL_STAR);
        View linearLayout = new LinearLayout(getApplication());
        linearLayout.setGravity(17);
        linearLayout.addView(this.mPreview, layoutParams);
        this.frame.addView(linearLayout);
        this.mPreview.setZOrderMediaOverlay(false);
        ImageButton capture_button = new ImageButton(getApplicationContext());
        capture_button.setImageDrawable(getResources().getDrawable(17301559));
        capture_button.setLayoutParams(new LayoutParams(-2, -2));
        capture_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MorseDecodeMain.this.captureChess = true;
            }
        });
        LinearLayout buttons = new LinearLayout(getApplicationContext());
        buttons.setLayoutParams(new LayoutParams(-2, -2));
        buttons.setOrientation(DIALOG_CALIBRATION_FILE);
        Button focus_button = new Button(getApplicationContext());
        focus_button.setLayoutParams(new LayoutParams(-2, -2));
        focus_button.setText("Focus");
        focus_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MorseDecodeMain.this.mPreview.postautofocus(100);
            }
        });
        buttons.addView(focus_button);
        Button clearDecisionFactors = new Button(getApplicationContext());
        clearDecisionFactors.setLayoutParams(new LayoutParams(-2, -2));
        clearDecisionFactors.setText("Start");
        clearDecisionFactors.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MorseDecodeMain.this.resetDatas();
            }
        });
        buttons.addView(clearDecisionFactors);
        this.morseDecodeView = new MorseDecodeView(getApplicationContext());
        this.morseDecodeView.RegisterDatas(this.morseSampledList, this.morseSymbolList, this.decisionFactor, this.preViewDispHight, this.preViewDispWidth);
        this.glview = new GL2CameraViewer(getApplication(), true, DIALOG_CALIBRATING, DIALOG_CALIBRATING);
        this.glview.setZOrderMediaOverlay(true);
        this.glview.setLayoutParams(new LayoutParams(-1, -1));
        this.frame.addView(this.glview);
        this.sendMessage = new Button(getApplicationContext());
        this.sendMessage.setLayoutParams(new LayoutParams(-2, -2));
        this.sendMessage.setText("Send");
        this.sendMessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (MorseTrans.prefLedFlashMode) {
                    MorseDecodeMain.this.popUpMessageInputDialog();
                } else {
                    MorseDecodeMain.this.iformOnlyFlashLightModeSupportDialog();
                }
            }
        });
        buttons.addView(this.sendMessage);
        Button cameraZoomIn = new Button(getApplicationContext());
        cameraZoomIn.setLayoutParams(new LayoutParams(-2, -2));
        cameraZoomIn.setText("+");
        cameraZoomIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MorseDecodeMain.this.ZoomIn();
            }
        });
        buttons.addView(cameraZoomIn);
        Button CameraZoomOut = new Button(getApplicationContext());
        CameraZoomOut.setLayoutParams(new LayoutParams(-2, -2));
        CameraZoomOut.setText("-");
        CameraZoomOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MorseDecodeMain.this.ZoomOut();
            }
        });
        buttons.addView(CameraZoomOut);
        this.frame.addView(buttons);
        setContentView(this.frame);
        addContentView(this.morseDecodeView, new LayoutParams(-2, -2));
        this.decisionFactor.ResetFactors();
        IntentFilter filter = new IntentFilter(MorseTranService.MORSE_TRANSMIT_DONE);
        filter.addAction(MorseTranService.MORSE_TRANSMIT_PROGRESS);
        this.receiver = new MorseTransDecodeModeReceiver();
        registerReceiver(this.receiver, filter);
        initMorseReceive();
    }

    private void popUpMessageInputDialog() {
        if (this.transmitServiceInTalkMode == null) {
            View layout = ((LayoutInflater) getSystemService("layout_inflater")).inflate(R.layout.message_input_dialog, (ViewGroup) findViewById(R.id.message_enter_dialog));
            this.messageEnter = (EditText) layout.findViewById(R.id.editMessageInRceiveMode);
            Builder builder = new Builder(this);
            builder.setView(layout);
            AlertDialog dialog = builder.create();
            dialog.setButton(MorseTrans.start_transmit, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    MorseDecodeMain.this.processTransmitButton();
                    dialog.dismiss();
                }
            });
            dialog.show();
            return;
        }
        stopTransmitMessageServiceInTalkMode();
    }

    public void processTransmitButton() {
        if (MorseTrans.prefLedFlashMode) {
            String inputMessage = this.messageEnter.getText().toString();
            if (inputMessage.length() == 0) {
                Toast.makeText(getApplicationContext(), "No message is entered", DIALOG_CALIBRATION_FILE).show();
                return;
            }
            this.encodedMessageInTalkmode = MorseTrans.englishMorseTrans.EncodeToMorseCodeData(inputMessage);
            transmitMessageServiceInTalkMode();
        }
    }

    private void transmitMessageServiceInTalkMode() {
        NativePrevieCamera = this.mPreview.GetCameraReference();
        Intent startTransmitIntent = new Intent(this, MorseTranService.class);
        startTransmitIntent.putExtra(MorseTranService.MORSE_SHOOTER_TYPE, MorseTranService.IN_TALK_FLASH_SHOOTER);
        startTransmitIntent.putParcelableArrayListExtra(MorseTranService.MORSE_DATA, this.encodedMessageInTalkmode);
        this.transmitServiceInTalkMode = startService(startTransmitIntent);
        this.sendMessage.setText(MorseTrans.stop);
        this.transmittedIndex = DIALOG_CALIBRATING;
    }

    private void stopTransmitMessageServiceInTalkMode() {
        try {
            stopService(new Intent(this, Class.forName(this.transmitServiceInTalkMode.getClassName())));
            this.transmitServiceInTalkMode = null;
            this.sendMessage.setText("Send");
        } catch (ClassNotFoundException e) {
        }
    }

    private void initMorseReceive() {
        toasts(DIALOG_TUTORIAL_MORSE);
    }

    public boolean onTrackballEvent(MotionEvent event) {
        if (event.getAction() != DIALOG_CALIBRATION_FILE) {
            return super.onTrackballEvent(event);
        }
        this.captureChess = true;
        return true;
    }

    public void onDestroy() {
        super.onDestroy();
        if (isFinishing() && this.transmitServiceInTalkMode != null) {
            stopTransmitMessageServiceInTalkMode();
            NativePrevieCamera = null;
        }
        unregisterReceiver(this.receiver);
    }

    public void onBackPressed() {
        if (this.transmitServiceInTalkMode != null) {
            stopTransmitMessageServiceInTalkMode();
        }
        super.onBackPressed();
    }

    protected void onPause() {
        super.onPause();
        if (this.transmitServiceInTalkMode != null) {
            stopTransmitMessageServiceInTalkMode();
            NativePrevieCamera = null;
        }
        this.mPreview.onPause();
        if (this.glview != null) {
            this.glview.onPause();
        }
    }

    protected void onResume() {
        super.onResume();
        if (this.glview != null) {
            this.glview.onResume();
        }
        LinkedList<PoolCallback> cbstack = new LinkedList();
        if (this.glview != null) {
            cbstack.add(this.glview.getDrawCallback());
            this.mPreview.addCallbackStack(cbstack);
        }
        cbstack.addFirst(new MorseProcessor());
        this.mPreview.addCallbackStack(cbstack);
        this.mPreview.onResume();
        if (this.transmitServiceInTalkMode == null) {
            this.sendMessage.setText("Send");
        } else {
            this.sendMessage.setText(MorseTrans.stop);
        }
    }

    void ZoomIn() {
        if (this.zoomScale >= 0.4d) {
            this.zoomScale -= this.zoomStep;
        }
    }

    void ZoomOut() {
        if (this.zoomScale < 1.0d) {
            this.zoomScale += this.zoomStep;
        }
    }

    public void measureFps() {
        if (this.start == null) {
            this.start = new Date();
        }
        this.fcount += DIALOG_CALIBRATION_FILE;
        if (this.fcount % 100 == 0) {
            this.fps = ((double) this.fcount) / (((double) (new Date().getTime() - this.start.getTime())) / 1000.0d);
            this.start = new Date();
            this.fcount = DIALOG_CALIBRATING;
        }
    }

    public void ProcessSampledData(int idx, image_pool pool, long timestamp) {
        MosreSampleData sampleData = new MosreSampleData();
        sampleData.magnitude = this.processor.DetectMorseSignalConvert(idx, pool, false, this.zoomScale);
        sampleData.sampleDataTimeStampInMilliSec = timestamp / 1000000;
        if (this.morseSampledList.size() > 300) {
            this.morseSampledList.removeLast();
        }
        this.morseSampledList.addFirst(sampleData);
        int sumOfSample = DIALOG_CALIBRATING;
        for (int count = DIALOG_CALIBRATING; count < this.morseSampledList.size(); count += DIALOG_CALIBRATION_FILE) {
            sumOfSample += ((MosreSampleData) this.morseSampledList.get(count)).magnitude;
        }
        if (this.morseSampledList.size() > 0) {
            this.decisionFactor.threshold_magnitude = sumOfSample / this.morseSampledList.size();
        }
        if (((MosreSampleData) this.morseSampledList.get(DIALOG_CALIBRATING)).magnitude > this.decisionFactor.threshold_magnitude) {
            ((MosreSampleData) this.morseSampledList.get(DIALOG_CALIBRATING)).value = DIALOG_CALIBRATION_FILE;
        } else {
            ((MosreSampleData) this.morseSampledList.get(DIALOG_CALIBRATING)).value = DIALOG_CALIBRATING;
        }
    }

    public void generateUpperSymbol() {
        if (this.morseSampledList.size() > DIALOG_CALIBRATION_FILE && ((MosreSampleData) this.morseSampledList.get(DIALOG_CALIBRATING)).value == 0 && ((MosreSampleData) this.morseSampledList.get(DIALOG_CALIBRATION_FILE)).value == DIALOG_CALIBRATION_FILE) {
            MosreSymbol symbol = new MosreSymbol();
            symbol.type = "upper";
            symbol.end_of_symbol = ((MosreSampleData) this.morseSampledList.get(DIALOG_CALIBRATION_FILE)).sampleDataTimeStampInMilliSec;
            int searchStartPointCount = DIALOG_CALIBRATING;
            do {
                searchStartPointCount += DIALOG_CALIBRATION_FILE;
                if (searchStartPointCount >= this.morseSampledList.size()) {
                    return;
                }
            } while (((MosreSampleData) this.morseSampledList.get(searchStartPointCount)).value == DIALOG_CALIBRATION_FILE);
            symbol.start_of_symbol = ((MosreSampleData) this.morseSampledList.get(searchStartPointCount - 1)).sampleDataTimeStampInMilliSec;
            symbol.duration = symbol.end_of_symbol - symbol.start_of_symbol;
            this.decisionFactor.AddUpperSymbolDurationDataInMillicsec((double) symbol.duration);
            this.morseSymbolList.addFirst(symbol);
            if (this.morseSymbolList.size() > 30) {
                this.morseSymbolList.removeLast();
            }
            if (this.morseSymbolList.size() <= DIALOG_CALIBRATION_FILE) {
                return;
            }
            if (((MosreSymbol) this.morseSymbolList.get(DIALOG_CALIBRATING)).duration > this.decisionFactor.thresholdUpperDration) {
                ((MosreSymbol) this.morseSymbolList.get(DIALOG_CALIBRATING)).mean = "-";
                return;
            }
            ((MosreSymbol) this.morseSymbolList.get(DIALOG_CALIBRATING)).mean = ".";
        }
    }

    public void generateLowerSymbol() {
        if (this.morseSampledList.size() > DIALOG_CALIBRATION_FILE && ((MosreSampleData) this.morseSampledList.get(DIALOG_CALIBRATING)).value == DIALOG_CALIBRATION_FILE && ((MosreSampleData) this.morseSampledList.get(DIALOG_CALIBRATION_FILE)).value == 0) {
            MosreSymbol symbol = new MosreSymbol();
            symbol.type = "lower";
            symbol.end_of_symbol = ((MosreSampleData) this.morseSampledList.get(DIALOG_CALIBRATION_FILE)).sampleDataTimeStampInMilliSec;
            int searchStartPointCount = DIALOG_CALIBRATING;
            do {
                searchStartPointCount += DIALOG_CALIBRATION_FILE;
                if (searchStartPointCount >= this.morseSampledList.size()) {
                    return;
                }
            } while (((MosreSampleData) this.morseSampledList.get(searchStartPointCount)).value == 0);
            symbol.start_of_symbol = ((MosreSampleData) this.morseSampledList.get(searchStartPointCount - 1)).sampleDataTimeStampInMilliSec;
            symbol.duration = symbol.end_of_symbol - symbol.start_of_symbol;
            this.decisionFactor.AddLowerSymbolDurationDataInMillicsec((double) symbol.duration);
            this.morseSymbolList.addFirst(symbol);
            if (this.morseSymbolList.size() > 30) {
                this.morseSymbolList.removeLast();
            }
            if (this.morseSymbolList.size() <= DIALOG_CALIBRATION_FILE) {
                return;
            }
            if (((MosreSymbol) this.morseSymbolList.get(DIALOG_CALIBRATING)).duration <= this.decisionFactor.thresholdLowerDration) {
                ((MosreSymbol) this.morseSymbolList.get(DIALOG_CALIBRATING)).mean = "symbol gab";
            } else if (((MosreSymbol) this.morseSymbolList.get(DIALOG_CALIBRATING)).duration <= this.decisionFactor.thresholdLowerDration || ((MosreSymbol) this.morseSymbolList.get(DIALOG_CALIBRATING)).duration > this.decisionFactor.thresholdSpaceDration) {
                ((MosreSymbol) this.morseSymbolList.get(DIALOG_CALIBRATING)).mean = "word gab";
            } else {
                ((MosreSymbol) this.morseSymbolList.get(DIALOG_CALIBRATING)).mean = "alphabet gab";
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String DecodeMorseToAlphabet() {
        /*
        r8 = this;
        r7 = 1;
        r6 = 0;
        r4 = r8.morseSymbolList;
        r4 = r4.size();
        if (r4 <= r7) goto L_0x00ee;
    L_0x000a:
        r4 = r8.morseSymbolList;
        r4 = r4.get(r6);
        r4 = (com.blueta.morsetransmitter.MorseDecodeMain.MosreSymbol) r4;
        r4 = r4.mean;
        r5 = "alphabet gab";
        r4 = r4.equals(r5);
        if (r4 != 0) goto L_0x002e;
    L_0x001c:
        r4 = r8.morseSymbolList;
        r4 = r4.get(r6);
        r4 = (com.blueta.morsetransmitter.MorseDecodeMain.MosreSymbol) r4;
        r4 = r4.mean;
        r5 = "word gab";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x00ee;
    L_0x002e:
        r2 = new java.lang.StringBuffer;
        r2.<init>();
        r1 = new java.lang.StringBuffer;
        r1.<init>();
        r0 = 1;
    L_0x0039:
        r4 = r8.morseSymbolList;
        r4 = r4.size();
        if (r0 < r4) goto L_0x0053;
    L_0x0041:
        r2.reverse();
        r4 = r8.englishMorseTranslator;
        r5 = r2.toString();
        r3 = r4.DecodToAlphabet(r5);
        if (r3 != 0) goto L_0x00ce;
    L_0x0050:
        r4 = "no result";
    L_0x0052:
        return r4;
    L_0x0053:
        r4 = r8.morseSymbolList;
        r4 = r4.get(r0);
        r4 = (com.blueta.morsetransmitter.MorseDecodeMain.MosreSymbol) r4;
        r4 = r4.processed;
        if (r4 != 0) goto L_0x0041;
    L_0x005f:
        r4 = r8.morseSymbolList;
        r4 = r4.get(r0);
        r4 = (com.blueta.morsetransmitter.MorseDecodeMain.MosreSymbol) r4;
        r4.processed = r7;
        r4 = r8.morseSymbolList;
        r4 = r4.get(r0);
        r4 = (com.blueta.morsetransmitter.MorseDecodeMain.MosreSymbol) r4;
        r4 = r4.mean;
        r5 = "-";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x0080;
    L_0x007b:
        r4 = "-";
        r2.append(r4);
    L_0x0080:
        r4 = r8.morseSymbolList;
        r4 = r4.get(r0);
        r4 = (com.blueta.morsetransmitter.MorseDecodeMain.MosreSymbol) r4;
        r4 = r4.mean;
        r5 = ".";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x0097;
    L_0x0092:
        r4 = ".";
        r2.append(r4);
    L_0x0097:
        r4 = r8.morseSymbolList;
        r4 = r4.get(r0);
        r4 = (com.blueta.morsetransmitter.MorseDecodeMain.MosreSymbol) r4;
        r4 = r4.mean;
        r5 = "symbol gab";
        r4.equals(r5);
        r4 = r8.morseSymbolList;
        r4 = r4.get(r0);
        r4 = (com.blueta.morsetransmitter.MorseDecodeMain.MosreSymbol) r4;
        r4 = r4.mean;
        r5 = "alphabet gab";
        r4 = r4.equals(r5);
        if (r4 != 0) goto L_0x0041;
    L_0x00b8:
        r4 = r8.morseSymbolList;
        r4 = r4.get(r0);
        r4 = (com.blueta.morsetransmitter.MorseDecodeMain.MosreSymbol) r4;
        r4 = r4.mean;
        r5 = "word gab";
        r4 = r4.equals(r5);
        if (r4 != 0) goto L_0x0041;
    L_0x00ca:
        r0 = r0 + 1;
        goto L_0x0039;
    L_0x00ce:
        r1.append(r3);
        r4 = r8.morseSymbolList;
        r4 = r4.get(r6);
        r4 = (com.blueta.morsetransmitter.MorseDecodeMain.MosreSymbol) r4;
        r4 = r4.mean;
        r5 = "word gab";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x00e8;
    L_0x00e3:
        r4 = " ";
        r1.append(r4);
    L_0x00e8:
        r4 = r1.toString();
        goto L_0x0052;
    L_0x00ee:
        r4 = "no result";
        goto L_0x0052;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.blueta.morsetransmitter.MorseDecodeMain.DecodeMorseToAlphabet():java.lang.String");
    }

    private void resetDatas() {
        this.doResetData = true;
    }

    private void drawTransmittedMessage() {
        if (this.encodedMessageInTalkmode != null) {
            StringBuffer textToPrint = new StringBuffer();
            for (int count = DIALOG_CALIBRATING; count < this.transmittedIndex + DIALOG_CALIBRATION_FILE; count += DIALOG_CALIBRATION_FILE) {
                if (count < this.encodedMessageInTalkmode.size()) {
                    textToPrint.append(((MorseCodeElement) this.encodedMessageInTalkmode.get(count)).Character);
                }
            }
            this.morseDecodeView.TransmitdMessageDraw(textToPrint.toString());
        }
    }
}
