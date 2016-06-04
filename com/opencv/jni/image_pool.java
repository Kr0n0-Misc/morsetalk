package com.opencv.jni;

public class image_pool {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public image_pool(long cPtr, boolean cMemoryOwn) {
        this.swigCMemOwn = cMemoryOwn;
        this.swigCPtr = cPtr;
    }

    public static long getCPtr(image_pool obj) {
        return obj == null ? 0 : obj.swigCPtr;
    }

    protected void finalize() {
        delete();
    }

    public synchronized void delete() {
        if (this.swigCPtr != 0) {
            if (this.swigCMemOwn) {
                this.swigCMemOwn = false;
                opencvJNI.delete_image_pool(this.swigCPtr);
            }
            this.swigCPtr = 0;
        }
    }

    public Mat getGrey(int i) {
        return new Mat(opencvJNI.image_pool_getGrey(this.swigCPtr, this, i), true);
    }

    public Mat getImage(int i) {
        return new Mat(opencvJNI.image_pool_getImage(this.swigCPtr, this, i), true);
    }

    public void addImage(int i, Mat mat) {
        opencvJNI.image_pool_addImage(this.swigCPtr, this, i, Mat.getCPtr(mat), mat);
    }

    public void convertYUVtoColor(int i, Mat out) {
        opencvJNI.image_pool_convertYUVtoColor(this.swigCPtr, this, i, Mat.getCPtr(out), out);
    }

    public image_pool() {
        this(opencvJNI.new_image_pool(), true);
    }
}
