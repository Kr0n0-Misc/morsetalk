package com.opencv.calibration.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.opencv.R;
import com.opencv.calibration.CalibrationViewer;
import com.opencv.calibration.Calibrator;
import com.opencv.calibration.Calibrator.CalibrationCallback;
import java.io.File;
import java.io.IOException;

public class CalibrationService extends Service implements CalibrationCallback {
    Class<?> activity;
    File calibration_file;
    int icon;
    private final IBinder mBinder;
    private NotificationManager mNM;

    public class CalibrationServiceBinder extends Binder {
        public CalibrationService getService() {
            return CalibrationService.this;
        }
    }

    public CalibrationService() {
        this.mBinder = new CalibrationServiceBinder();
    }

    public void startCalibrating(Class<?> activitycaller, int icon_id, Calibrator calibrator, File calibration_file) throws IOException {
        this.activity = activitycaller;
        this.icon = icon_id;
        showNotification();
        this.calibration_file = calibration_file;
        calibrator.setCallback(this);
        calibrator.calibrate(calibration_file);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        return 2;
    }

    public void onCreate() {
        this.mNM = (NotificationManager) getSystemService("notification");
    }

    public void onDestroy() {
        Toast.makeText(this, R.string.calibration_service_finished, 0).show();
    }

    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    private void showNotification() {
        CharSequence text = getText(R.string.calibration_service_started);
        Notification notification = new Notification(this.icon, text, System.currentTimeMillis());
        notification.setLatestEventInfo(this, getText(R.string.calibration_service_label), text, PendingIntent.getActivity(this, 0, new Intent(this, this.activity), 0));
        notification.defaults |= 1;
        this.mNM.notify(R.string.calibration_service_started, notification);
    }

    private void doneNotification() {
        CharSequence text = getText(R.string.calibration_service_finished);
        Notification notification = new Notification(this.icon, text, System.currentTimeMillis());
        Intent intent = new Intent(this, CalibrationViewer.class);
        intent.putExtra("calibfile", this.calibration_file.getAbsolutePath());
        notification.setLatestEventInfo(this, getText(R.string.calibration_service_label), text, PendingIntent.getActivity(this, 0, intent, 0));
        notification.defaults |= 1;
        this.mNM.notify(R.string.calibration_service_started, notification);
    }

    public void onFoundChessboard(Calibrator calibrator) {
    }

    public void onDoneCalibration(Calibrator calibration, File calibfile) {
        doneNotification();
        stopSelf();
    }

    public void onFailedChessboard(Calibrator calibrator) {
    }
}
