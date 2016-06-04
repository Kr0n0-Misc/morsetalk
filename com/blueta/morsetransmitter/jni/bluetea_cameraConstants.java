package com.blueta.morsetransmitter.jni;

public interface bluetea_cameraConstants {
    public static final int DETECT_FAST;
    public static final int DETECT_STAR;
    public static final int DETECT_SURF;

    static {
        DETECT_FAST = bluetea_cameraJNI.DETECT_FAST_get();
        DETECT_STAR = bluetea_cameraJNI.DETECT_STAR_get();
        DETECT_SURF = bluetea_cameraJNI.DETECT_SURF_get();
    }
}
