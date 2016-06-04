package com.opencv.jni;

public class Calibration {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public Calibration(long cPtr, boolean cMemoryOwn) {
        this.swigCMemOwn = cMemoryOwn;
        this.swigCPtr = cPtr;
    }

    public static long getCPtr(Calibration obj) {
        return obj == null ? 0 : obj.swigCPtr;
    }

    protected void finalize() {
        delete();
    }

    public synchronized void delete() {
        if (this.swigCPtr != 0) {
            if (this.swigCMemOwn) {
                this.swigCMemOwn = false;
                opencvJNI.delete_Calibration(this.swigCPtr);
            }
            this.swigCPtr = 0;
        }
    }

    public void setPatternsize(Size value) {
        opencvJNI.Calibration_patternsize_set(this.swigCPtr, this, Size.getCPtr(value), value);
    }

    public Size getPatternsize() {
        long cPtr = opencvJNI.Calibration_patternsize_get(this.swigCPtr, this);
        return cPtr == 0 ? null : new Size(cPtr, false);
    }

    public Calibration() {
        this(opencvJNI.new_Calibration(), true);
    }

    public boolean detectAndDrawChessboard(int idx, image_pool pool) {
        return opencvJNI.Calibration_detectAndDrawChessboard(this.swigCPtr, this, idx, image_pool.getCPtr(pool), pool);
    }

    public void resetChess() {
        opencvJNI.Calibration_resetChess(this.swigCPtr, this);
    }

    public int getNumberDetectedChessboards() {
        return opencvJNI.Calibration_getNumberDetectedChessboards(this.swigCPtr, this);
    }

    public void calibrate(String filename) {
        opencvJNI.Calibration_calibrate(this.swigCPtr, this, filename);
    }

    public void drawText(int idx, image_pool pool, String text) {
        opencvJNI.Calibration_drawText(this.swigCPtr, this, idx, image_pool.getCPtr(pool), pool, text);
    }
}
