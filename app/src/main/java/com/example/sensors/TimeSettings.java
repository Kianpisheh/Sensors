package com.example.sensors;

import android.content.Context;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class TimeSettings {

    private int duration, period, delay, endTime;
    private boolean customSampling, customTiming;
    private static Map<String, Integer> timeValues = new HashMap<>();
    private Context context;


    public TimeSettings(Context context) {
        timeValues.put("delay", delay);
        timeValues.put("period", period);
        timeValues.put("duration", duration);
        timeValues.put("endTime", endTime);
        this.context = context;
    }

    public void updateValues(Map<String, Integer> timeSettingValues) {
        for (String timeParam: timeSettingValues.keySet()) {
            int paramValue;
            try {
                if (timeSettingValues.containsKey(timeParam)) {
                    paramValue = timeSettingValues.get(timeParam);
                    timeValues.put(timeParam, paramValue);
                } else {
                    Toast.makeText(context, "Error: time settings not available", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Not integer value for time parameter " + timeParam, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setTimeParameter(String param, int value) {
        timeValues.put(param, value);
    }

    public int getTimeParameter(String param) {
        return timeValues.get(param);
    }

    public boolean isCustomSampling() {
        return customSampling;
    }

    public boolean isCustomTiming() {
        return customTiming;
    }

    public void setCustomSampling(boolean customSampling) {
        this.customSampling = customSampling;
    }

    public void setCustomTiming(boolean customTiming) {
        this.customTiming = customTiming;
    }
}
