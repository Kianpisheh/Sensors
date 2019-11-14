package com.example.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.AsyncTask;

import java.io.FileOutputStream;
import java.util.Map;

public class SensorEventLoggerTask extends AsyncTask<Map<String, Object>, Void, Void> {

    @Override
    protected Void doInBackground(Map<String, Object>... args) {

        SensorEvent event = (SensorEvent) args[0].get("event");
        FileOutputStream oStream = (FileOutputStream) args[0].get("outputStream");

        // get the timestamp
        long timeStamp = event.timestamp;
        try {
            String FileContents = Long.toString(timeStamp);
            oStream.write(FileContents.getBytes());
            FileContents = ", ";
            oStream.write(FileContents.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
