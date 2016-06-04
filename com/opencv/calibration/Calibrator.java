package com.opencv.calibration;

import android.os.AsyncTask;
import com.opencv.camera.NativeProcessor;
import com.opencv.camera.NativeProcessor.PoolCallback;
import com.opencv.jni.Calibration;
import com.opencv.jni.Size;
import com.opencv.jni.image_pool;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class Calibrator implements PoolCallback {
    private Calibration calibration;
    private CalibrationCallback callback;
    private boolean capture_chess;
    ReentrantLock lock;

    public interface CalibrationCallback {
        void onDoneCalibration(Calibrator calibrator, File file);

        void onFailedChessboard(Calibrator calibrator);

        void onFoundChessboard(Calibrator calibrator);
    }

    private class CalibrationTask extends AsyncTask<Object, Object, Object> {
        File calibfile;

        public CalibrationTask(File calib) throws IOException {
            this.calibfile = calib;
            this.calibfile.createNewFile();
        }

        protected Object doInBackground(Object... params) {
            Calibrator.this.lock.lock();
            try {
                Calibrator.this.calibration.calibrate(this.calibfile.getAbsolutePath());
                return null;
            } finally {
                Calibrator.this.lock.unlock();
            }
        }

        protected void onPostExecute(Object result) {
            Calibrator.this.callback.onDoneCalibration(Calibrator.this, this.calibfile);
        }
    }

    public Calibrator(CalibrationCallback callback) {
        this.lock = new ReentrantLock();
        this.calibration = new Calibration();
        this.callback = callback;
    }

    public void resetCalibration() {
        this.calibration.resetChess();
    }

    public void setPatternSize(Size size) {
        Size csize = this.calibration.getPatternsize();
        if (size.getWidth() != csize.getWidth() || size.getHeight() != csize.getHeight()) {
            this.calibration.setPatternsize(size);
            resetCalibration();
        }
    }

    public void setPatternSize(int width, int height) {
        setPatternSize(new Size(width, height));
    }

    public void calibrate(File calibration_file) throws IOException {
        if (getNumberPatternsDetected() >= 3) {
            new CalibrationTask(calibration_file).execute(null);
        }
    }

    public void queueChessCapture() {
        this.capture_chess = true;
    }

    public void process(int idx, image_pool pool, long timestamp, NativeProcessor nativeProcessor) {
        if (this.lock.tryLock()) {
            try {
                if (this.capture_chess) {
                    if (this.calibration.detectAndDrawChessboard(idx, pool)) {
                        this.callback.onFoundChessboard(this);
                    } else {
                        this.callback.onFailedChessboard(this);
                    }
                    this.capture_chess = false;
                }
                this.lock.unlock();
            } catch (Throwable th) {
                this.lock.unlock();
            }
        }
    }

    public int getNumberPatternsDetected() {
        return this.calibration.getNumberDetectedChessboards();
    }

    public void setCallback(CalibrationCallback callback) {
        this.callback = callback;
    }
}
