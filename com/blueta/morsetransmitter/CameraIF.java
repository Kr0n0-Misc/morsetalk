package com.blueta.morsetransmitter;

import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Message;
import android.view.SurfaceHolder;

public interface CameraIF {
    void Destroy();

    void Init();

    Parameters getParameters();

    void handleMessage(Message message);

    void onDestroy();

    void onPause();

    void onResume();

    void onStart();

    void onStop();

    void resetPreviewSize(int i, int i2);

    void setParameters(Parameters parameters);

    void setPreviewCallback(PreviewCallback previewCallback);

    void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3);

    void surfaceCreated(SurfaceHolder surfaceHolder);

    void surfaceDestroyed(SurfaceHolder surfaceHolder);

    void takePicture();
}
