package com.opencv.jni;

import java.nio.ByteBuffer;

public class opencvJNI {
    public static final native int CV_16SC1_get();

    public static final native int CV_16SC2_get();

    public static final native int CV_16SC3_get();

    public static final native int CV_16SC4_get();

    public static final native int CV_16S_get();

    public static final native int CV_16UC1_get();

    public static final native int CV_16UC2_get();

    public static final native int CV_16UC3_get();

    public static final native int CV_16UC4_get();

    public static final native int CV_16U_get();

    public static final native int CV_32FC1_get();

    public static final native int CV_32FC2_get();

    public static final native int CV_32FC3_get();

    public static final native int CV_32FC4_get();

    public static final native int CV_32F_get();

    public static final native int CV_32SC1_get();

    public static final native int CV_32SC2_get();

    public static final native int CV_32SC3_get();

    public static final native int CV_32SC4_get();

    public static final native int CV_32S_get();

    public static final native int CV_64FC1_get();

    public static final native int CV_64FC2_get();

    public static final native int CV_64FC3_get();

    public static final native int CV_64FC4_get();

    public static final native int CV_64F_get();

    public static final native int CV_8SC1_get();

    public static final native int CV_8SC2_get();

    public static final native int CV_8SC3_get();

    public static final native int CV_8SC4_get();

    public static final native int CV_8S_get();

    public static final native int CV_8UC1_get();

    public static final native int CV_8UC2_get();

    public static final native int CV_8UC3_get();

    public static final native int CV_8UC4_get();

    public static final native int CV_8U_get();

    public static final native int CV_AUTO_STEP_get();

    public static final native int CV_CN_MAX_get();

    public static final native int CV_CN_SHIFT_get();

    public static final native int CV_DEPTH_MAX_get();

    public static final native int CV_MAGIC_MASK_get();

    public static final native int CV_MAT_CN_MASK_get();

    public static final native int CV_MAT_CONT_FLAG_SHIFT_get();

    public static final native int CV_MAT_CONT_FLAG_get();

    public static final native int CV_MAT_DEPTH_MASK_get();

    public static final native int CV_MAT_MAGIC_VAL_get();

    public static final native int CV_MAT_TYPE_MASK_get();

    public static final native int CV_SUBMAT_FLAG_SHIFT_get();

    public static final native int CV_SUBMAT_FLAG_get();

    public static final native String CV_TYPE_NAME_MAT_get();

    public static final native int CV_USRTYPE1_get();

    public static final native void Calibration_calibrate(long j, Calibration calibration, String str);

    public static final native boolean Calibration_detectAndDrawChessboard(long j, Calibration calibration, int i, long j2, image_pool com_opencv_jni_image_pool);

    public static final native void Calibration_drawText(long j, Calibration calibration, int i, long j2, image_pool com_opencv_jni_image_pool, String str);

    public static final native int Calibration_getNumberDetectedChessboards(long j, Calibration calibration);

    public static final native long Calibration_patternsize_get(long j, Calibration calibration);

    public static final native void Calibration_patternsize_set(long j, Calibration calibration, long j2, Size size);

    public static final native void Calibration_resetChess(long j, Calibration calibration);

    public static final native int Mat_channels(long j, Mat mat);

    public static final native int Mat_cols_get(long j, Mat mat);

    public static final native void Mat_create(long j, Mat mat, long j2, Size size, int i);

    public static final native int Mat_rows_get(long j, Mat mat);

    public static final native long PtrMat___deref__(long j, PtrMat ptrMat);

    public static final native void PtrMat_addref(long j, PtrMat ptrMat);

    public static final native int PtrMat_channels(long j, PtrMat ptrMat);

    public static final native int PtrMat_cols_get(long j, PtrMat ptrMat);

    public static final native void PtrMat_create(long j, PtrMat ptrMat, long j2, Size size, int i);

    public static final native void PtrMat_delete_obj(long j, PtrMat ptrMat);

    public static final native boolean PtrMat_empty(long j, PtrMat ptrMat);

    public static final native void PtrMat_release(long j, PtrMat ptrMat);

    public static final native int PtrMat_rows_get(long j, PtrMat ptrMat);

    public static final native void RGB2BGR(long j, Mat mat, long j2, Mat mat2);

    public static final native int Size_height_get(long j, Size size);

    public static final native void Size_height_set(long j, Size size, int i);

    public static final native int Size_width_get(long j, Size size);

    public static final native void Size_width_set(long j, Size size, int i);

    public static final native void addYUVtoPool(long j, image_pool com_opencv_jni_image_pool, byte[] bArr, int i, int i2, int i3, boolean z);

    public static final native void copyBufferToMat(long j, Mat mat, ByteBuffer byteBuffer);

    public static final native void copyMatToBuffer(ByteBuffer byteBuffer, long j, Mat mat);

    public static final native void delete_Calibration(long j);

    public static final native void delete_Mat(long j);

    public static final native void delete_PtrMat(long j);

    public static final native void delete_Size(long j);

    public static final native void delete_glcamera(long j);

    public static final native void delete_image_pool(long j);

    public static final native void glcamera_clear(long j, glcamera com_opencv_jni_glcamera);

    public static final native void glcamera_drawMatToGL(long j, glcamera com_opencv_jni_glcamera, int i, long j2, image_pool com_opencv_jni_image_pool);

    public static final native void glcamera_init(long j, glcamera com_opencv_jni_glcamera, int i, int i2);

    public static final native void glcamera_step(long j, glcamera com_opencv_jni_glcamera);

    public static final native void image_pool_addImage(long j, image_pool com_opencv_jni_image_pool, int i, long j2, Mat mat);

    public static final native void image_pool_convertYUVtoColor(long j, image_pool com_opencv_jni_image_pool, int i, long j2, Mat mat);

    public static final native long image_pool_getGrey(long j, image_pool com_opencv_jni_image_pool, int i);

    public static final native long image_pool_getImage(long j, image_pool com_opencv_jni_image_pool, int i);

    public static final native long imread(String str);

    public static final native void imwrite(String str, long j, Mat mat);

    public static final native long new_Calibration();

    public static final native long new_Mat();

    public static final native long new_PtrMat__SWIG_0();

    public static final native long new_PtrMat__SWIG_1(long j, Mat mat);

    public static final native long new_PtrMat__SWIG_2(long j, PtrMat ptrMat);

    public static final native long new_Size__SWIG_0();

    public static final native long new_Size__SWIG_1(int i, int i2);

    public static final native long new_glcamera();

    public static final native long new_image_pool();

    static {
        try {
            System.loadLibrary("bluetea-opencv");
        } catch (UnsatisfiedLinkError e) {
            throw e;
        }
    }
}
