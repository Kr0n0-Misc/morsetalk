package com.opencv.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.EGLConfigChooser;
import android.opengl.GLSurfaceView.EGLContextFactory;
import android.util.AttributeSet;
import android.util.Log;
import com.opencv.camera.NativeProcessor;
import com.opencv.camera.NativeProcessor.PoolCallback;
import com.opencv.jni.glcamera;
import com.opencv.jni.image_pool;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

public class GL2CameraViewer extends GLSurfaceView {
    private static final boolean DEBUG = false;
    private static String TAG;
    glcamera mglcamera;
    private PoolCallback poolcallback;

    private static class ConfigChooser implements EGLConfigChooser {
        private static int EGL_OPENGL_ES2_BIT;
        private static int[] s_configAttribs2;
        protected int mAlphaSize;
        protected int mBlueSize;
        protected int mDepthSize;
        protected int mGreenSize;
        protected int mRedSize;
        protected int mStencilSize;
        private int[] mValue;

        public ConfigChooser(int r, int g, int b, int a, int depth, int stencil) {
            this.mValue = new int[1];
            this.mRedSize = r;
            this.mGreenSize = g;
            this.mBlueSize = b;
            this.mAlphaSize = a;
            this.mDepthSize = depth;
            this.mStencilSize = stencil;
        }

        static {
            EGL_OPENGL_ES2_BIT = 4;
            s_configAttribs2 = new int[]{12324, 4, 12323, 4, 12322, 4, 12352, EGL_OPENGL_ES2_BIT, 12344};
        }

        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
            int[] num_config = new int[1];
            egl.eglChooseConfig(display, s_configAttribs2, null, 0, num_config);
            int numConfigs = num_config[0];
            if (numConfigs <= 0) {
                throw new IllegalArgumentException("No configs match configSpec");
            }
            EGLConfig[] configs = new EGLConfig[numConfigs];
            egl.eglChooseConfig(display, s_configAttribs2, configs, numConfigs, num_config);
            return chooseConfig(egl, display, configs);
        }

        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display, EGLConfig[] configs) {
            for (EGLConfig config : configs) {
                int d = findConfigAttrib(egl, display, config, 12325, 0);
                int s = findConfigAttrib(egl, display, config, 12326, 0);
                if (d >= this.mDepthSize && s >= this.mStencilSize) {
                    int r = findConfigAttrib(egl, display, config, 12324, 0);
                    int g = findConfigAttrib(egl, display, config, 12323, 0);
                    int b = findConfigAttrib(egl, display, config, 12322, 0);
                    int a = findConfigAttrib(egl, display, config, 12321, 0);
                    if (r == this.mRedSize && g == this.mGreenSize && b == this.mBlueSize && a == this.mAlphaSize) {
                        return config;
                    }
                }
            }
            return null;
        }

        private int findConfigAttrib(EGL10 egl, EGLDisplay display, EGLConfig config, int attribute, int defaultValue) {
            if (egl.eglGetConfigAttrib(display, config, attribute, this.mValue)) {
                return this.mValue[0];
            }
            return defaultValue;
        }

        private void printConfigs(EGL10 egl, EGLDisplay display, EGLConfig[] configs) {
            Log.w(GL2CameraViewer.TAG, String.format("%d configurations", new Object[]{Integer.valueOf(configs.length)}));
            for (EGLConfig printConfig : configs) {
                Log.w(GL2CameraViewer.TAG, String.format("Configuration %d:\n", new Object[]{Integer.valueOf(i)}));
                printConfig(egl, display, printConfig);
            }
        }

        private void printConfig(EGL10 egl, EGLDisplay display, EGLConfig config) {
            int[] attributes = new int[]{12320, 12321, 12322, 12323, 12324, 12325, 12326, 12327, 12328, 12329, 12330, 12331, 12332, 12333, 12334, 12335, 12336, 12337, 12338, 12339, 12340, 12343, 12342, 12341, 12345, 12346, 12347, 12348, 12349, 12350, 12351, 12352, 12354};
            String[] names = new String[]{"EGL_BUFFER_SIZE", "EGL_ALPHA_SIZE", "EGL_BLUE_SIZE", "EGL_GREEN_SIZE", "EGL_RED_SIZE", "EGL_DEPTH_SIZE", "EGL_STENCIL_SIZE", "EGL_CONFIG_CAVEAT", "EGL_CONFIG_ID", "EGL_LEVEL", "EGL_MAX_PBUFFER_HEIGHT", "EGL_MAX_PBUFFER_PIXELS", "EGL_MAX_PBUFFER_WIDTH", "EGL_NATIVE_RENDERABLE", "EGL_NATIVE_VISUAL_ID", "EGL_NATIVE_VISUAL_TYPE", "EGL_PRESERVED_RESOURCES", "EGL_SAMPLES", "EGL_SAMPLE_BUFFERS", "EGL_SURFACE_TYPE", "EGL_TRANSPARENT_TYPE", "EGL_TRANSPARENT_RED_VALUE", "EGL_TRANSPARENT_GREEN_VALUE", "EGL_TRANSPARENT_BLUE_VALUE", "EGL_BIND_TO_TEXTURE_RGB", "EGL_BIND_TO_TEXTURE_RGBA", "EGL_MIN_SWAP_INTERVAL", "EGL_MAX_SWAP_INTERVAL", "EGL_LUMINANCE_SIZE", "EGL_ALPHA_MASK_SIZE", "EGL_COLOR_BUFFER_TYPE", "EGL_RENDERABLE_TYPE", "EGL_CONFORMANT"};
            int[] value = new int[1];
            for (int i = 0; i < attributes.length; i++) {
                int attribute = attributes[i];
                String name = names[i];
                if (egl.eglGetConfigAttrib(display, config, attribute, value)) {
                    Log.w(GL2CameraViewer.TAG, String.format("  %s: %d\n", new Object[]{name, Integer.valueOf(value[0])}));
                } else {
                    do {
                    } while (egl.eglGetError() != 12288);
                }
            }
        }
    }

    private static class ContextFactory implements EGLContextFactory {
        private static int EGL_CONTEXT_CLIENT_VERSION;

        private ContextFactory() {
        }

        static {
            EGL_CONTEXT_CLIENT_VERSION = 12440;
        }

        public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
            Log.w(GL2CameraViewer.TAG, "creating OpenGL ES 2.0 context");
            GL2CameraViewer.checkEglError("Before eglCreateContext", egl);
            EGLContext context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, new int[]{EGL_CONTEXT_CLIENT_VERSION, 2, 12344});
            GL2CameraViewer.checkEglError("After eglCreateContext", egl);
            return context;
        }

        public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
            egl.eglDestroyContext(display, context);
        }
    }

    private class Renderer implements android.opengl.GLSurfaceView.Renderer {
        private Renderer() {
        }

        public void onDrawFrame(GL10 gl) {
            GL2CameraViewer.this.mglcamera.step();
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GL2CameraViewer.this.mglcamera.init(width, height);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        }
    }

    static {
        TAG = "GL2JNIView";
    }

    public GL2CameraViewer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.poolcallback = new PoolCallback() {
            public void process(int idx, image_pool pool, long timestamp, NativeProcessor nativeProcessor) {
                GL2CameraViewer.this.drawMatToGL(idx, pool);
                GL2CameraViewer.this.requestRender();
            }
        };
        init(false, 0, 0);
        setZOrderMediaOverlay(true);
    }

    public GL2CameraViewer(Context context) {
        super(context);
        this.poolcallback = new PoolCallback() {
            public void process(int idx, image_pool pool, long timestamp, NativeProcessor nativeProcessor) {
                GL2CameraViewer.this.drawMatToGL(idx, pool);
                GL2CameraViewer.this.requestRender();
            }
        };
        init(false, 0, 0);
        setZOrderMediaOverlay(true);
    }

    public GL2CameraViewer(Context context, boolean translucent, int depth, int stencil) {
        super(context);
        this.poolcallback = new PoolCallback() {
            public void process(int idx, image_pool pool, long timestamp, NativeProcessor nativeProcessor) {
                GL2CameraViewer.this.drawMatToGL(idx, pool);
                GL2CameraViewer.this.requestRender();
            }
        };
        init(translucent, depth, stencil);
        setZOrderMediaOverlay(true);
    }

    private void init(boolean translucent, int depth, int stencil) {
        EGLConfigChooser configChooser;
        if (translucent) {
            getHolder().setFormat(-3);
        }
        setEGLContextFactory(new ContextFactory());
        if (translucent) {
            configChooser = new ConfigChooser(8, 8, 8, 8, depth, stencil);
        } else {
            configChooser = new ConfigChooser(5, 6, 5, 0, depth, stencil);
        }
        setEGLConfigChooser(configChooser);
        setRenderer(new Renderer());
        setRenderMode(0);
    }

    private static void checkEglError(String prompt, EGL10 egl) {
        while (egl.eglGetError() != 12288) {
            Log.e(TAG, String.format("%s: EGL error: 0x%x", new Object[]{prompt, Integer.valueOf(error)}));
        }
    }

    public void drawMatToGL(int idx, image_pool pool) {
        if (this.mglcamera != null) {
            this.mglcamera.drawMatToGL(idx, pool);
        } else {
            Log.e("android-opencv", "null glcamera!!!!");
        }
    }

    public void clear() {
        if (this.mglcamera != null) {
            this.mglcamera.clear();
        } else {
            Log.e("android-opencv", "null glcamera!!!!");
        }
    }

    public void onPause() {
        this.mglcamera = null;
        super.onPause();
    }

    public void onResume() {
        this.mglcamera = new glcamera();
        super.onResume();
    }

    public PoolCallback getDrawCallback() {
        return this.poolcallback;
    }
}
