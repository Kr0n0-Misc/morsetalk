package com.blueta.morsetransmitter.jni;

import com.opencv.jni.image_pool;

public class Processor {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public Processor(long cPtr, boolean cMemoryOwn) {
        this.swigCMemOwn = cMemoryOwn;
        this.swigCPtr = cPtr;
    }

    public static long getCPtr(Processor obj) {
        return obj == null ? 0 : obj.swigCPtr;
    }

    protected void finalize() {
        delete();
    }

    public synchronized void delete() {
        if (this.swigCPtr != 0) {
            if (this.swigCMemOwn) {
                this.swigCMemOwn = false;
                bluetea_cameraJNI.delete_Processor(this.swigCPtr);
            }
            this.swigCPtr = 0;
        }
    }

    public Processor() {
        this(bluetea_cameraJNI.new_Processor(), true);
    }

    public void detectAndDrawFeatures(int idx, image_pool pool, int feature_type) {
        bluetea_cameraJNI.Processor_detectAndDrawFeatures(this.swigCPtr, this, idx, image_pool.getCPtr(pool), pool, feature_type);
    }

    public boolean detectAndDrawChessboard(int idx, image_pool pool) {
        return bluetea_cameraJNI.Processor_detectAndDrawChessboard(this.swigCPtr, this, idx, image_pool.getCPtr(pool), pool);
    }

    public void resetChess() {
        bluetea_cameraJNI.Processor_resetChess(this.swigCPtr, this);
    }

    public int getNumberDetectedChessboards() {
        return bluetea_cameraJNI.Processor_getNumberDetectedChessboards(this.swigCPtr, this);
    }

    public void calibrate(String filename) {
        bluetea_cameraJNI.Processor_calibrate(this.swigCPtr, this, filename);
    }

    public void drawText(int idx, image_pool pool, String text) {
        bluetea_cameraJNI.Processor_drawText(this.swigCPtr, this, idx, image_pool.getCPtr(pool), pool, text);
    }

    public int DetectMorseSignal(int input_idx, image_pool pool, boolean glmode, double scale) {
        return bluetea_cameraJNI.Processor_DetectMorseSignal(this.swigCPtr, this, input_idx, image_pool.getCPtr(pool), pool, glmode, scale);
    }

    public int DetectMorseSignalConvert(int input_idx, image_pool pool, boolean glmode, double scale) {
        return bluetea_cameraJNI.Processor_DetectMorseSignalConvert(this.swigCPtr, this, input_idx, image_pool.getCPtr(pool), pool, glmode, scale);
    }

    public int DetectMorseSignalGray(int input_idx, image_pool pool, boolean glmode, double scale) {
        return bluetea_cameraJNI.Processor_DetectMorseSignalGray(this.swigCPtr, this, input_idx, image_pool.getCPtr(pool), pool, glmode, scale);
    }
}
