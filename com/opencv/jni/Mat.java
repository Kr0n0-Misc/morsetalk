package com.opencv.jni;

public class Mat {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public Mat(long cPtr, boolean cMemoryOwn) {
        this.swigCMemOwn = cMemoryOwn;
        this.swigCPtr = cPtr;
    }

    public static long getCPtr(Mat obj) {
        return obj == null ? 0 : obj.swigCPtr;
    }

    protected void finalize() {
        delete();
    }

    public synchronized void delete() {
        if (this.swigCPtr != 0) {
            if (this.swigCMemOwn) {
                this.swigCMemOwn = false;
                opencvJNI.delete_Mat(this.swigCPtr);
            }
            this.swigCPtr = 0;
        }
    }

    public Mat() {
        this(opencvJNI.new_Mat(), true);
    }

    public void create(Size size, int type) {
        opencvJNI.Mat_create(this.swigCPtr, this, Size.getCPtr(size), size, type);
    }

    public int channels() {
        return opencvJNI.Mat_channels(this.swigCPtr, this);
    }

    public int getRows() {
        return opencvJNI.Mat_rows_get(this.swigCPtr, this);
    }

    public int getCols() {
        return opencvJNI.Mat_cols_get(this.swigCPtr, this);
    }
}
