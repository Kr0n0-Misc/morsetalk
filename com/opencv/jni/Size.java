package com.opencv.jni;

public class Size {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public Size(long cPtr, boolean cMemoryOwn) {
        this.swigCMemOwn = cMemoryOwn;
        this.swigCPtr = cPtr;
    }

    public static long getCPtr(Size obj) {
        return obj == null ? 0 : obj.swigCPtr;
    }

    protected void finalize() {
        delete();
    }

    public synchronized void delete() {
        if (this.swigCPtr != 0) {
            if (this.swigCMemOwn) {
                this.swigCMemOwn = false;
                opencvJNI.delete_Size(this.swigCPtr);
            }
            this.swigCPtr = 0;
        }
    }

    public Size() {
        this(opencvJNI.new_Size__SWIG_0(), true);
    }

    public Size(int width, int height) {
        this(opencvJNI.new_Size__SWIG_1(width, height), true);
    }

    public void setWidth(int value) {
        opencvJNI.Size_width_set(this.swigCPtr, this, value);
    }

    public int getWidth() {
        return opencvJNI.Size_width_get(this.swigCPtr, this);
    }

    public void setHeight(int value) {
        opencvJNI.Size_height_set(this.swigCPtr, this, value);
    }

    public int getHeight() {
        return opencvJNI.Size_height_get(this.swigCPtr, this);
    }
}
