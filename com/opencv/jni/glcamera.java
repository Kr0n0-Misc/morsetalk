package com.opencv.jni;

public class glcamera {
    protected boolean swigCMemOwn;
    private long swigCPtr;

    public glcamera(long cPtr, boolean cMemoryOwn) {
        this.swigCMemOwn = cMemoryOwn;
        this.swigCPtr = cPtr;
    }

    public static long getCPtr(glcamera obj) {
        return obj == null ? 0 : obj.swigCPtr;
    }

    protected void finalize() {
        delete();
    }

    public synchronized void delete() {
        if (this.swigCPtr != 0) {
            if (this.swigCMemOwn) {
                this.swigCMemOwn = false;
                opencvJNI.delete_glcamera(this.swigCPtr);
            }
            this.swigCPtr = 0;
        }
    }

    public void init(int width, int height) {
        opencvJNI.glcamera_init(this.swigCPtr, this, width, height);
    }

    public void step() {
        opencvJNI.glcamera_step(this.swigCPtr, this);
    }

    public void drawMatToGL(int idx, image_pool pool) {
        opencvJNI.glcamera_drawMatToGL(this.swigCPtr, this, idx, image_pool.getCPtr(pool), pool);
    }

    public void clear() {
        opencvJNI.glcamera_clear(this.swigCPtr, this);
    }

    public glcamera() {
        this(opencvJNI.new_glcamera(), true);
    }
}
