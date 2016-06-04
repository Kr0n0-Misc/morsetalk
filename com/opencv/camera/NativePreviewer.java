package com.opencv.camera;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import com.opencv.camera.NativeProcessor.PoolCallback;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class NativePreviewer extends SurfaceView implements Callback, PreviewCallback, NativeProcessorCallback {
    private AutoFocusCallback autocallback;
    private Runnable autofocusrunner;
    private int fcount;
    private Handler handler;
    private boolean hasAutoFocus;
    private Method mAcb;
    private Camera mCamera;
    private SurfaceHolder mHolder;
    private Method mPCWB;
    private int pixelformat;
    private PixelFormat pixelinfo;
    private int preview_height;
    private int preview_width;
    private NativeProcessor processor;
    private Date start;
    private String whitebalance_mode;

    public NativePreviewer(Context context, AttributeSet attributes) {
        super(context, attributes);
        this.whitebalance_mode = "auto";
        this.autofocusrunner = new Runnable() {
            public void run() {
                NativePreviewer.this.mCamera.autoFocus(NativePreviewer.this.autocallback);
            }
        };
        this.autocallback = new AutoFocusCallback() {
            public void onAutoFocus(boolean success, Camera camera) {
                if (!success) {
                    NativePreviewer.this.postautofocus(1000);
                }
            }
        };
        this.handler = new Handler();
        this.fcount = 0;
        this.hasAutoFocus = false;
        listAllCameraMethods();
        this.mHolder = getHolder();
        this.mHolder.addCallback(this);
        this.mHolder.setType(3);
        this.preview_width = attributes.getAttributeIntValue("opencv", "preview_width", 600);
        this.preview_height = attributes.getAttributeIntValue("opencv", "preview_height", 600);
        Log.d("NativePreviewer", "Trying to use preview size of " + this.preview_width + " " + this.preview_height);
        this.processor = new NativeProcessor();
        setZOrderMediaOverlay(false);
    }

    public Camera GetCameraReference() {
        if (this.mCamera != null) {
            return this.mCamera;
        }
        return null;
    }

    public NativePreviewer(Context context, int preview_width, int preview_height) {
        super(context);
        this.whitebalance_mode = "auto";
        this.autofocusrunner = new Runnable() {
            public void run() {
                NativePreviewer.this.mCamera.autoFocus(NativePreviewer.this.autocallback);
            }
        };
        this.autocallback = new AutoFocusCallback() {
            public void onAutoFocus(boolean success, Camera camera) {
                if (!success) {
                    NativePreviewer.this.postautofocus(1000);
                }
            }
        };
        this.handler = new Handler();
        this.fcount = 0;
        this.hasAutoFocus = false;
        listAllCameraMethods();
        this.mHolder = getHolder();
        this.mHolder.addCallback(this);
        this.mHolder.setType(3);
        this.preview_width = preview_width;
        this.preview_height = preview_height;
        this.processor = new NativeProcessor();
        setZOrderMediaOverlay(false);
    }

    public void setPreviewSize(int width, int height) {
        this.preview_width = width;
        this.preview_height = height;
        Log.d("NativePreviewer", "Trying to use preview size of " + this.preview_width + " " + this.preview_height);
    }

    public void setParamsFromPrefs(Context ctx) {
        boolean z = true;
        int[] size = new int[2];
        CameraConfig.readImageSize(ctx, size);
        int mode = CameraConfig.readCameraMode(ctx);
        setPreviewSize(size[0], size[1]);
        if (mode != 0) {
            z = false;
        }
        setGrayscale(z);
        this.whitebalance_mode = CameraConfig.readWhitebalace(ctx);
    }

    public void surfaceCreated(SurfaceHolder holder) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        try {
            initCamera(this.mHolder);
            Parameters parameters = this.mCamera.getParameters();
            int best_width = 1000000;
            int best_height = 1000000;
            int bdist = 100000;
            for (Size x : this.mCamera.getParameters().getSupportedPreviewSizes()) {
                if (Math.abs(x.width - this.preview_width) < bdist) {
                    bdist = Math.abs(x.width - this.preview_width);
                    best_width = x.width;
                    best_height = x.height;
                }
            }
            this.preview_width = best_width;
            this.preview_height = best_height;
            int i = this.preview_width;
            Log.d("NativePreviewer", "Determined compatible preview size is: (" + r0 + "," + this.preview_height + ")");
            Log.d("NativePreviewer", "Supported params: " + this.mCamera.getParameters().flatten());
            List<String> whiteBalanceModes = parameters.getSupportedWhiteBalance();
            if (whiteBalanceModes != null && whiteBalanceModes.contains(this.whitebalance_mode)) {
                parameters.setWhiteBalance(this.whitebalance_mode);
            }
            if (parameters.get("meter-mode") != null) {
                parameters.set("meter-mode", "meter-average");
            }
            List<String> fmodes = this.mCamera.getParameters().getSupportedFocusModes();
            if (fmodes != null) {
                if (fmodes.indexOf("infinity") != -1) {
                    parameters.setFocusMode("infinity");
                } else if (fmodes.indexOf("fixed") != -1) {
                    parameters.setFocusMode("fixed");
                }
                if (fmodes.indexOf("auto") != -1) {
                    this.hasAutoFocus = true;
                }
            }
            List<String> scenemodes = this.mCamera.getParameters().getSupportedSceneModes();
            if (!(scenemodes == null || scenemodes.indexOf("action") == -1)) {
                parameters.setSceneMode("action");
                Log.d("NativePreviewer", "set scenemode to action");
            }
            parameters.setPreviewSize(this.preview_width, this.preview_height);
            this.mCamera.setParameters(parameters);
            this.pixelinfo = new PixelFormat();
            this.pixelformat = this.mCamera.getParameters().getPreviewFormat();
            PixelFormat.getPixelFormatInfo(this.pixelformat, this.pixelinfo);
            Size preview_size = this.mCamera.getParameters().getPreviewSize();
            this.preview_width = preview_size.width;
            this.preview_height = preview_size.height;
            int i2 = this.preview_height;
            int bufSize = ((this.preview_width * r0) * this.pixelinfo.bitsPerPixel) / 8;
            initForACB();
            initForPCWB();
            addCallbackBuffer(new byte[bufSize]);
            setPreviewCallbackWithBuffer();
            this.mCamera.startPreview();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void postautofocus(int delay) {
        if (this.hasAutoFocus) {
            this.handler.postDelayed(this.autofocusrunner, (long) delay);
        }
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        if (this.start == null) {
            this.start = new Date();
        }
        this.processor.post(data, this.preview_width, this.preview_height, this.pixelformat, System.nanoTime(), this);
        this.fcount++;
        if (this.fcount % 100 == 0) {
            Log.i("NativePreviewer", "fps:" + (((double) this.fcount) / (((double) (new Date().getTime() - this.start.getTime())) / 1000.0d)));
            this.start = new Date();
            this.fcount = 0;
        }
    }

    public void onDoneNativeProcessing(byte[] buffer) {
        addCallbackBuffer(buffer);
    }

    public void addCallbackStack(LinkedList<PoolCallback> callbackstack) {
        this.processor.addCallbackStack(callbackstack);
    }

    public void onPause() {
        releaseCamera();
        addCallbackStack(null);
        this.processor.stop();
    }

    public void onResume() {
        this.processor.start();
    }

    private void initForPCWB() {
        try {
            this.mPCWB = Class.forName("android.hardware.Camera").getMethod("setPreviewCallbackWithBuffer", new Class[]{PreviewCallback.class});
        } catch (Exception e) {
            Log.e("NativePreviewer", "Problem setting up for setPreviewCallbackWithBuffer: " + e.toString());
        }
    }

    private void addCallbackBuffer(byte[] b) {
        try {
            this.mAcb.invoke(this.mCamera, new Object[]{b});
        } catch (Exception e) {
            Log.e("NativePreviewer", "invoking addCallbackBuffer failed: " + e.toString());
        }
    }

    private void setPreviewCallbackWithBuffer() {
        try {
            this.mPCWB.invoke(this.mCamera, new Object[]{this});
        } catch (Exception e) {
            Log.e("NativePreviewer", e.toString());
        }
    }

    private void clearPreviewCallbackWithBuffer() {
        try {
            this.mPCWB.invoke(this.mCamera, new Object[]{null});
        } catch (Exception e) {
            Log.e("NativePreviewer", e.toString());
        }
    }

    private void initForACB() {
        try {
            this.mAcb = Class.forName("android.hardware.Camera").getMethod("addCallbackBuffer", new Class[]{byte[].class});
        } catch (Exception e) {
            Log.e("NativePreviewer", "Problem setting up for addCallbackBuffer: " + e.toString());
        }
    }

    private void listAllCameraMethods() {
        try {
            Method[] m = Class.forName("android.hardware.Camera").getMethods();
            for (Method method : m) {
                Log.d("NativePreviewer", "  method:" + method.toString());
            }
        } catch (Exception e) {
            Log.e("NativePreviewer", e.toString());
        }
    }

    private void initCamera(SurfaceHolder holder) throws InterruptedException {
        if (this.mCamera == null) {
            int i = 0;
            while (true) {
                int i2 = i + 1;
                if (i < 5) {
                    try {
                        this.mCamera = Camera.open();
                        break;
                    } catch (RuntimeException e) {
                        Thread.sleep(200);
                        i = i2;
                    }
                }
                try {
                    break;
                } catch (IOException e2) {
                    this.mCamera.release();
                    this.mCamera = null;
                    return;
                } catch (RuntimeException e3) {
                    Log.e("camera", "stacktrace", e3);
                    return;
                }
            }
            this.mCamera.setPreviewDisplay(holder);
        }
    }

    private void releaseCamera() {
        if (this.mCamera != null) {
            this.mCamera.stopPreview();
            this.mCamera.release();
        }
        this.mCamera = null;
        this.mAcb = null;
        this.mPCWB = null;
    }

    public void setGrayscale(boolean b) {
        this.processor.setGrayscale(b);
    }
}
