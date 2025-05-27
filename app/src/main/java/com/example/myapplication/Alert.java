package com.example.myapplication;

public class Alert {
    public static final int STATE_GOOD = 1;
    public static final int STATE_BAD = 2;
    public static final int STATE_PENDING = 0;

    private int sensorNumber;
    private String title;
    private String description;
    private String timestamp;
    private int state;

    public Alert(int sensorNumber, String title, String description, String timestamp) {
        this.sensorNumber = sensorNumber;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.state = STATE_GOOD; // Default for existing alerts
    }

    public Alert(int sensorNumber, String title, String description, String timestamp, int state) {
        this.sensorNumber = sensorNumber;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.state = state;
    }

    public int getSensorNumber() {
        return sensorNumber;
    }

    public void setSensorNumber(int sensorNumber) {
        this.sensorNumber = sensorNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isGoodAlert() {
        return state == STATE_GOOD;
    }

    public boolean isBadAlert() {
        return state == STATE_BAD;
    }
}