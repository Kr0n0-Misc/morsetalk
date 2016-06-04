package com.opencv.jni;

public class PtrMat {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public PtrMat(long cPtr, boolean cMemoryOwn) {
        this.swigCMemOwn = cMemoryOwn;
        this.swigCPtr = cPtr;
    }

    public static long getCPtr(PtrMat obj) {
        return obj == null ? 0 : obj.swigCPtr;
    }

    protected void finalize() {
        delete();
    }

    public synchronized void delete() {
        if (this.swigCPtr != 0) {
            if (this.swigCMemOwn) {
                this.swigCMemOwn = false;
                opencvJNI.delete_PtrMat(this.swigCPtr);
            }
            this.swigCPtr = 0;
        }
    }

    public PtrMat() {
        this(opencvJNI.new_PtrMat__SWIG_0(), true);
    }

    public PtrMat(Mat _obj) {
        this(opencvJNI.new_PtrMat__SWIG_1(Mat.getCPtr(_obj), _obj), true);
    }

    public PtrMat(PtrMat ptr) {
        this(opencvJNI.new_PtrMat__SWIG_2(getCPtr(ptr), ptr), true);
    }

    public void addref() {
        opencvJNI.PtrMat_addref(this.swigCPtr, this);
    }

    public void release() {
        opencvJNI.PtrMat_release(this.swigCPtr, this);
    }

    public void delete_obj() {
        opencvJNI.PtrMat_delete_obj(this.swigCPtr, this);
    }

    public boolean empty() {
        return opencvJNI.PtrMat_empty(this.swigCPtr, this);
    }

    public Mat __deref__() {
        long cPtr = opencvJNI.PtrMat___deref__(this.swigCPtr, this);
        return cPtr == 0 ? null : new Mat(cPtr, false);
    }

    public void create(Size size, int type) {
        opencvJNI.PtrMat_create(this.swigCPtr, this, Size.getCPtr(size), size, type);
    }

    public int channels() {
        return opencvJNI.PtrMat_channels(this.swigCPtr, this);
    }

    public int getRows() {
        return opencvJNI.PtrMat_rows_get(this.swigCPtr, this);
    }

    public int getCols() {
        return opencvJNI.PtrMat_cols_get(this.swigCPtr, this);
    }
}
