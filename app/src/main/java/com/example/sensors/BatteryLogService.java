package com.example.sensors;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class BatteryLogService extends Service {

    private BroadcastReceiver batteryLevelReceiver;
    private long serviceEndingTime;
    private FileOutputStream outputStream = null;

    public BatteryLogService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        serviceEndingTime = System.currentTimeMillis() +
                intent.getIntExtra("endTime", 0) * 60000;

        if (intent.getBooleanExtra("log_battery", false)) {
            DataManager dataManager = new DataManager(intent.getStringExtra("folder_name"));
            try {
                File batteryLogFile = dataManager.createBatteryLog();
                outputStream = new FileOutputStream(batteryLogFile);
            } catch (Exception e) {
                Toast.makeText(this, "Error in creating battery log file",
                        Toast.LENGTH_LONG).show();
                return START_STICKY;
            }
            batteryLevelReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent batteryIntent) {
                    if (System.currentTimeMillis() > serviceEndingTime) {
                        stopSelf();
                        System.out.println(" Battery log service stopped at: " + System.currentTimeMillis());
                    }
                    int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    if (outputStream != null) {
                        try {
                            outputStream.write((System.currentTimeMillis() + ", ").getBytes());
                            outputStream.write(( (level /  ((float) scale)) + "\n").getBytes());
                        } catch (Exception e) { }
                    }
                }
            };
            this.registerReceiver(batteryLevelReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (batteryLevelReceiver != null) {
            this.unregisterReceiver(batteryLevelReceiver);
            System.out.println("Battery log service got destroyed");
        }
    }
}
