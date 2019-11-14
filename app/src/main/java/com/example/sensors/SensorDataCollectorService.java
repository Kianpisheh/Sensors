package com.example.sensors;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SensorDataCollectorService extends Service implements SensorEventListener {

    private DataManager dataManager;
    private long serviceEndingTime;
    private SensorManager sensorManager;
    private List<String> selectedSensors;
    private List<Sensor> sensorList;
    private List<Integer> sensorStatus;
    private int period, duration;
    private long posEdgeTime;
    private AudioRecorder audioRecorder = null;
    private long currentTime;
    private boolean recoedAudio = false;
    private boolean customTiming = false;
    private boolean customSampling = false;
    private boolean recording = false;


    private class SensorEventLoggerTask extends AsyncTask<SensorEvent, Void, Void> {

        @Override
        protected Void doInBackground(SensorEvent... events) {
            SensorEvent event = events[0];
            Sensor sensor = event.sensor;

            // write sensor values into the data
            try {
                for (String sensorName : selectedSensors) {
                    if (sensorName.equals(sensor.getName())) {
                        dataManager.outputStreams.get(sensorName).write((Long.toString(event.timestamp / 1000000) + ", ").getBytes());
                        dataManager.outputStreams.get(sensorName).write((Long.toString(System.currentTimeMillis()) + ", ").getBytes());
                        for (int i = 0; i < event.values.length; i++) {
                            dataManager.outputStreams.get(sensorName).write(Float.toString(event.values[i]).getBytes());
                            if (i != event.values.length-1) {
                                dataManager.outputStreams.get(sensorName).write(", ".getBytes());
                            } else {
                                dataManager.outputStreams.get(sensorName).write("\n".getBytes());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
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

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        recoedAudio = intent.getBooleanExtra("record_audio", false);

        System.out.println("Service started at: " + System.currentTimeMillis());
        // get timing values
        int endTime = intent.getIntExtra("endTime", 0);
        customSampling = intent.getBooleanExtra("customSampling", false);
        customTiming = intent.getBooleanExtra("customTiming", false);
        period = intent.getIntExtra("period", 0);
        duration = intent.getIntExtra("duration", 0);
        serviceEndingTime = System.currentTimeMillis() + endTime * 60000;

        // get sensor names and status
        sensorStatus = intent.getIntegerArrayListExtra("status");
        int i = 0;
        selectedSensors = new ArrayList<>();
        for (Sensor sensor : sensorList) {
            if (sensorStatus.get(i) == 1) {
                selectedSensors.add(sensor.getName());
            }
            i += 1;
        }

        dataManager = new DataManager(intent.getStringExtra("folder_name"));
        // create the file
        try {
            boolean ret = dataManager.createFile(selectedSensors);
            if (!ret) {
                Toast.makeText(getApplicationContext(), "No SD card found", Toast.LENGTH_LONG).show();
                return START_STICKY;
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Error in creating csv file\n" + e.toString(), Toast.LENGTH_LONG).show();
            return START_STICKY;
        }

//        try {
//            dataManager.saveNote(intent.getStringExtra("note"));
//        } catch (IOException e) {
//            Toast.makeText(this,
//                    "Error in creating the label file", Toast.LENGTH_LONG).show();
//        }

        // register selected sensors after the delay
        registerSensors();
        if (recoedAudio) {
            audioRecorder = new AudioRecorder(true);
            try {
                audioRecorder.startRecording_(dataManager.createAudioFile(0));
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        "Error in creating audio file\n" + e.toString(), Toast.LENGTH_LONG).show();
                return START_STICKY;
            }
            sendBroadcastMessage("recording", 0, 0);
        }

        return START_STICKY;
    }

    private void registerSensors() {
        int i = 0;
        posEdgeTime = System.currentTimeMillis();
        for (Sensor s: sensorList) {
            if (sensorStatus.get(i) == 1) {
                sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
                recording = true;
            }
            i += 1;
        }
    }

    private void unregisterSensors() {
        sensorManager.unregisterListener(this);
        recording = false;
        try {
            if (audioRecorder != null) {
                audioRecorder.stopRecording();
            }
        } catch (IllegalStateException e) {
            System.out.println("Error in unregisterSensors");
            System.out.println(e.toString());
        }
        System.out.println("stop");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                registerSensors();
                System.out.println("recording");
                try {
                    if (recoedAudio) {
                        audioRecorder.startRecording_(dataManager.createAudioFile(audioRecorder.count));
                    }
                } catch(Exception e) {
                    System.out.println(e.toString());
                }
            }
        }, period * 60000);
    }

    private void sendBroadcastMessage(String msg, int val1, int val2) {
        Intent intent = new Intent("ACTION_STATUS_DATA");
        intent.putExtra("msg", msg);
        intent.putExtra("val1", val1);
        intent.putExtra("val2", val2);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);

        if (audioRecorder != null) {
            audioRecorder.serviceIsDestroyed = true;
        }
        System.out.println("Service got destroyed");
        try {
            if (audioRecorder != null) {
                audioRecorder.stopRecording();
            }
        } catch (IllegalStateException e) {
            System.out.println(e.toString());
            sensorManager.unregisterListener(this);
            recording = false;
        }
        sendBroadcastMessage("done", 0, 0);
        recording = false;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        currentTime = System.currentTimeMillis();

        // check if the time is over
        if (currentTime > serviceEndingTime) {
            stopSelf();
            System.out.println("Service stopped at: " + System.currentTimeMillis());

        // custom sampling
        } else if (((currentTime - posEdgeTime) > duration * 1000) && (customSampling)) {
            unregisterSensors();
            try {
                if (audioRecorder != null) {
                    audioRecorder.stopRecording();
                }
            } catch (IllegalStateException e) {
                System.out.println("Error in onSensorChanged");
                System.out.println(e.toString());
            }
        } else {
            if (recording) {
                new SensorEventLoggerTask().execute(sensorEvent);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
