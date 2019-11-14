package com.example.sensors;

public class SensorItem {

    private boolean isSelected;
    private String sensorName;

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public void setSensorName(String name) {
        this.sensorName = name;
    }

    public boolean getSelected() {
        return isSelected;
    }

    public String getSensorName() {
        return sensorName;
    }
}
