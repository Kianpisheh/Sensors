package com.example.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.List;


public class SensorsManager {

    private SensorManager sensorManager;
    private Context context;
    private List<Sensor> sensorsList;

    public SensorsManager(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorsList = sensorManager.getSensorList(Sensor.TYPE_ALL);
    }

    public List<Sensor> getSensors() {
        return sensorsList;
    }
}
