package com.blueta.morsetransmitter.jni;

import com.opencv.jni.image_pool;

public class bluetea_cameraJNI {
    public static final native int DETECT_FAST_get();

    public static final native int DETECT_STAR_get();

    public static final native int DETECT_SURF_get();

    public static final native int Processor_DetectMorseSignal(long j, Processor processor, int i, long j2, image_pool com_opencv_jni_image_pool, boolean z, double d);

    public static final native int Processor_DetectMorseSignalConvert(long j, Processor processor, int i, long j2, image_pool com_opencv_jni_image_pool, boolean z, double d);

    public static final native int Processor_DetectMorseSignalGray(long j, Processor processor, int i, long j2, image_pool com_opencv_jni_image_pool, boolean z, double d);

    public static final native void Processor_calibrate(long j, Processor processor, String str);

    public static final native boolean Processor_detectAndDrawChessboard(long j, Processor processor, int i, long j2, image_pool com_opencv_jni_image_pool);

    public static final native void Processor_detectAndDrawFeatures(long j, Processor processor, int i, long j2, image_pool com_opencv_jni_image_pool, int i2);

    public static final native void Processor_drawText(long j, Processor processor, int i, long j2, image_pool com_opencv_jni_image_pool, String str);

    public static final native int Processor_getNumberDetectedChessboards(long j, Processor processor);

    public static final native void Processor_resetChess(long j, Processor processor);

    public static final native void delete_Processor(long j);

    public static final native long new_Processor();

    static {
        try {
            System.loadLibrary("bluetea-opencv");
            System.loadLibrary("bluetea_camera");
        } catch (UnsatisfiedLinkError e) {
            throw e;
        }
    }
}
