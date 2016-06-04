package com.opencv.jni;

import java.nio.ByteBuffer;

public class opencv implements opencvConstants {
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !opencv.class.desiredAssertionStatus();
    }

    public static void imwrite(String image_name, Mat image) {
        opencvJNI.imwrite(image_name, Mat.getCPtr(image), image);
    }

    public static Mat imread(String image_name) {
        return new Mat(opencvJNI.imread(image_name), true);
    }

    public static void copyMatToBuffer(ByteBuffer buffer, Mat mat) {
        if ($assertionsDisabled || buffer.isDirect()) {
            opencvJNI.copyMatToBuffer(buffer, Mat.getCPtr(mat), mat);
            return;
        }
        throw new AssertionError("Buffer must be allocated direct.");
    }

    public static void copyBufferToMat(Mat mat, ByteBuffer buffer) {
        if ($assertionsDisabled || buffer.isDirect()) {
            opencvJNI.copyBufferToMat(Mat.getCPtr(mat), mat, buffer);
            return;
        }
        throw new AssertionError("Buffer must be allocated direct.");
    }

    public static void addYUVtoPool(image_pool pool, byte[] data, int idx, int width, int height, boolean grey) {
        opencvJNI.addYUVtoPool(image_pool.getCPtr(pool), pool, data, idx, width, height, grey);
    }

    public static void RGB2BGR(Mat in, Mat out) {
        opencvJNI.RGB2BGR(Mat.getCPtr(in), in, Mat.getCPtr(out), out);
    }
}
