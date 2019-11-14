package com.example.sensors;


import android.os.Environment;
import android.text.format.DateFormat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DataManager {

    public Map<String, FileOutputStream> outputStreams;
    private String folder_main;

    public DataManager(String folder_main) {
        this.folder_main = folder_main;
        File f = new File(Environment.getExternalStorageDirectory(), this.folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    public boolean createFile(List<String> selectedSensors) throws Exception{
        int numSensors = selectedSensors.size();
        outputStreams = new HashMap<>();


        boolean isSDPresent = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);

        if (isSDPresent) {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(System.currentTimeMillis());
            for (int i = 0; i < numSensors; i++) {
                String fileName =  selectedSensors.get(i) + ".csv";
                File file = new File(Environment.getExternalStorageDirectory() + "/" + this.folder_main, fileName);
                outputStreams.put(selectedSensors.get(i), new FileOutputStream(file));
            }
        } else {
            return false;
        }
        return true;
    }

    public File createAudioFile(int i) throws NullPointerException {
        // start audio sampling
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(System.currentTimeMillis());
        String fileName = System.currentTimeMillis() + "_audio_" + String.valueOf(i) + ".wav";
        return  new File(Environment.getExternalStorageDirectory() + "/" + this.folder_main, fileName);
    }

    public File createBatteryLog() throws NullPointerException {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(System.currentTimeMillis());
        String fileName = "battery_log.txt";
        return new File(Environment.getExternalStorageDirectory() + "/" + this.folder_main, fileName);
    }

    public File createLocationLog() throws NullPointerException {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(System.currentTimeMillis());
        String fileName = "location_log.txt";
        return new File(Environment.getExternalStorageDirectory() + "/" + this.folder_main, fileName);
    }

    public File createWifiLog() throws NullPointerException {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(System.currentTimeMillis());
        String fileName = "wifi_log.txt";
        return new File(Environment.getExternalStorageDirectory() + "/" + this.folder_main, fileName);
    }

    public static String getFolderName() {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(System.currentTimeMillis());
        return "SensorData/" + DateFormat.format("dd-MM-yyyy-HH-mm-ss", cal).toString();
    }

    public void saveNote(String note) throws IOException {
        String fileName = System.currentTimeMillis() + "_label.txt";
        File file = new File(Environment.getExternalStorageDirectory() + "/" + this.folder_main, fileName);
        FileOutputStream oStream = new FileOutputStream(file);
        oStream.write(note.getBytes());
        oStream.flush();
        oStream.close();
    }

}