package com.example.sensors;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class WifiLogService extends Service {

    private long serviceEndingTime;
    private FileOutputStream outputStream = null;
    private BroadcastReceiver wifiApReceiver;

    public WifiLogService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, "ForegroundServiceChannel")
                .setContentTitle("Foreground Service2")
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        serviceEndingTime = System.currentTimeMillis() +
                intent.getIntExtra("endTime", 0) * 60000;

        final IntentFilter filters = new IntentFilter();
        filters.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filters.addAction("android.net.wifi.STATE_CHANGE");

        if (intent.getBooleanExtra("log_wifi", false)) {
            DataManager dataManager = new DataManager(intent.getStringExtra("folder_name"));
            try {
                File wifiLogFile = dataManager.createWifiLog();
                outputStream = new FileOutputStream(wifiLogFile);
            } catch (Exception e) {
                Toast.makeText(this, "Error in creating wifi log file",
                        Toast.LENGTH_LONG).show();
                return START_STICKY;
            }
            wifiApReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                        if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                            WifiManager myWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                            WifiInfo wifiInfo = myWifiManager.getConnectionInfo();
                            if (outputStream != null) {
                                try {
                                    outputStream.write((System.currentTimeMillis() + ", ").getBytes());
                                    outputStream.write((wifiInfo.getBSSID() + ", ").getBytes());
                                    outputStream.write((wifiInfo.getSSID() + "\n").getBytes());
                                } catch (Exception e) { }
                            }
                        }
                    }
                }
            };
            this.registerReceiver(wifiApReceiver, filters);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wifiApReceiver != null) {
            this.unregisterReceiver(wifiApReceiver);
            System.out.println("Wifi log service got destroyed");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
