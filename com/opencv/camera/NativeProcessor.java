package com.opencv.camera;

import android.util.Log;
import com.opencv.jni.image_pool;
import com.opencv.jni.opencv;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NativeProcessor {
    private boolean gray_scale_only;
    private final Lock lock;
    private ProcessorThread mthread;
    private LinkedList<PoolCallback> nextStack;
    private image_pool pool;
    private LinkedList<NPPostObject> postobjects;
    private LinkedList<PoolCallback> stack;
    private Lock stacklock;

    public interface PoolCallback {
        void process(int i, image_pool com_opencv_jni_image_pool, long j, NativeProcessor nativeProcessor);
    }

    protected interface NativeProcessorCallback {
        void onDoneNativeProcessing(byte[] bArr);
    }

    private static class NPPostObject {
        byte[] buffer;
        NativeProcessorCallback callback;
        int format;
        int height;
        long timestamp;
        int width;

        public NPPostObject(byte[] buffer, int width, int height, int format, long timestamp, NativeProcessorCallback callback) {
            this.buffer = buffer;
            this.width = width;
            this.height = height;
            this.format = format;
            this.timestamp = timestamp;
            this.callback = callback;
        }

        public void done() {
            this.callback.onDoneNativeProcessing(this.buffer);
        }
    }

    private class ProcessorThread extends Thread {
        private ProcessorThread() {
        }

        private void process(NPPostObject pobj) throws Exception {
            if (pobj.format == 17) {
                opencv.addYUVtoPool(NativeProcessor.this.pool, pobj.buffer, 0, pobj.width, pobj.height, NativeProcessor.this.gray_scale_only);
            } else if (pobj.format == 16) {
                opencv.addYUVtoPool(NativeProcessor.this.pool, pobj.buffer, 0, pobj.width, pobj.height, true);
            } else {
                throw new Exception("bad pixel format!");
            }
            Iterator it = NativeProcessor.this.stack.iterator();
            while (it.hasNext()) {
                PoolCallback x = (PoolCallback) it.next();
                if (interrupted()) {
                    throw new InterruptedException("Native Processor interupted while processing");
                }
                x.process(0, NativeProcessor.this.pool, pobj.timestamp, NativeProcessor.this);
            }
            pobj.done();
        }

        public void run() {
            while (true) {
                try {
                    yield();
                    do {
                    } while (!NativeProcessor.this.stacklock.tryLock(5, TimeUnit.MILLISECONDS));
                    if (NativeProcessor.this.nextStack != null) {
                        NativeProcessor.this.stack = NativeProcessor.this.nextStack;
                        NativeProcessor.this.nextStack = null;
                    }
                    NativeProcessor.this.stacklock.unlock();
                    do {
                    } while (!NativeProcessor.this.lock.tryLock(5, TimeUnit.MILLISECONDS));
                    if (NativeProcessor.this.postobjects.isEmpty()) {
                        NativeProcessor.this.lock.unlock();
                    } else {
                        NPPostObject pobj = (NPPostObject) NativeProcessor.this.postobjects.removeLast();
                        NativeProcessor.this.lock.unlock();
                        if (interrupted()) {
                            throw new InterruptedException();
                        } else if (!(NativeProcessor.this.stack == null || pobj == null)) {
                            process(pobj);
                        }
                    }
                } catch (InterruptedException e) {
                    Log.i("NativeProcessor", "native processor interupted, ending now");
                    return;
                } catch (Exception e2) {
                    e2.printStackTrace();
                    return;
                } catch (Throwable th) {
                    NativeProcessor.this.stacklock.unlock();
                }
            }
        }
    }

    public void addCallbackStack(LinkedList<PoolCallback> stack) {
        do {
            try {
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            } catch (Throwable th) {
                this.stacklock.unlock();
            }
        } while (!this.stacklock.tryLock(10, TimeUnit.MILLISECONDS));
        this.nextStack = stack;
        this.stacklock.unlock();
    }

    public NativeProcessor() {
        this.postobjects = new LinkedList();
        this.pool = new image_pool();
        this.lock = new ReentrantLock();
        this.stack = new LinkedList();
        this.stacklock = new ReentrantLock();
        this.gray_scale_only = false;
    }

    public void setGrayscale(boolean grayscale) {
        this.gray_scale_only = grayscale;
    }

    protected void stop() {
        this.mthread.interrupt();
        try {
            this.mthread.join();
        } catch (InterruptedException e) {
            Log.w("NativeProcessor", "interupted while stoping " + e.getMessage());
        }
        this.mthread = null;
    }

    protected void start() {
        this.mthread = new ProcessorThread();
        this.mthread.start();
    }

    protected boolean post(byte[] buffer, int width, int height, int format, long timestamp, NativeProcessorCallback callback) {
        this.lock.lock();
        try {
            this.postobjects.addFirst(new NPPostObject(buffer, width, height, format, timestamp, callback));
            return true;
        } finally {
            this.lock.unlock();
        }
    }
}
