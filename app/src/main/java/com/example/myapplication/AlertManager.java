package com.example.myapplication;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AlertManager {
    private static final String ALERTS_FILE = "alerts.json";
    private static AlertManager instance;
    private Context context;
    private Gson gson;
    private List<Alert> alerts;

    // Callback interface for alert responses
    public interface AlertResponseCallback {
        void onAlertProcessed(Alert alert);
    }

    private AlertManager(Context context) {
        this.context = context.getApplicationContext();
        this.gson = new Gson();
        this.alerts = new ArrayList<>();
        loadAlerts();
    }

    public static synchronized AlertManager getInstance(Context context) {
        if (instance == null) {
            instance = new AlertManager(context);
        }
        return instance;
    }

    // Direct add method (for cases where no dialog is needed)
    public void addAlert(Alert alert) {
        alerts.add(0, alert);
        saveAlerts();
    }

    // Main function to add alert with user confirmation dialog
    // Now requires MainActivity instance to be passed
    public void addAlertWithUserConfirmation(MainActivity activity, int sensorNumber, String title, String description, String timestamp, AlertResponseCallback callback) {
        if (activity != null && !activity.isFinishing()) {
            activity.showAlertConfirmationDialog(sensorNumber, title, description, timestamp, new AlertResponseCallback() {
                @Override
                public void onAlertProcessed(Alert alert) {
                    addAlert(alert); // Save the alert with user's choice
                    if (callback != null) {
                        callback.onAlertProcessed(alert);
                    }
                }
            });
        }
    }

    public List<Alert> getAllAlerts() {
        return new ArrayList<>(alerts);
    }

    public List<Alert> getRecentAlerts(int limit) {
        List<Alert> recentAlerts = new ArrayList<>();
        int count = Math.min(limit, alerts.size());
        for (int i = 0; i < count; i++) {
            recentAlerts.add(alerts.get(i));
        }
        return recentAlerts;
    }

    private void saveAlerts() {
        try {
            FileOutputStream fos = context.openFileOutput(ALERTS_FILE, Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            gson.toJson(alerts, writer);
            writer.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAlerts() {
        try {
            FileInputStream fis = context.openFileInput(ALERTS_FILE);
            InputStreamReader reader = new InputStreamReader(fis);
            Type listType = new TypeToken<List<Alert>>(){}.getType();
            List<Alert> loadedAlerts = gson.fromJson(reader, listType);
            if (loadedAlerts != null) {
                alerts = loadedAlerts;
            }
            reader.close();
            fis.close();
        } catch (IOException e) {
            // File doesn't exist yet, start with empty list
            alerts = new ArrayList<>();
            // Add some default alerts for first run
            addDefaultAlerts();
        }
    }

    private void addDefaultAlerts() {
        alerts.add(new Alert(1, "Sensor no. 1 activated!", "Countermeasures deployed.", "2 hours ago", Alert.STATE_GOOD));
        alerts.add(new Alert(4, "Sensor no. 4 activated!", "Countermeasures deployed.", "4 hours ago", Alert.STATE_GOOD));
        alerts.add(new Alert(2, "Sensor no. 2 activated!", "Reported as false positive.", "5 hours ago", Alert.STATE_BAD));
        alerts.add(new Alert(4, "Sensor no. 4 activated!", "Countermeasures deployed.", "11 hours ago", Alert.STATE_GOOD));
        alerts.add(new Alert(4, "Sensor no. 4 activated!", "Countermeasures deployed.", "11:22 - 24.5.2025", Alert.STATE_GOOD));
        alerts.add(new Alert(2, "Sensor no. 2 activated!", "Reported as false positive.", "19:01 - 24.5.2025", Alert.STATE_BAD));
        alerts.add(new Alert(4, "Sensor no. 4 activated!", "Countermeasures deployed.", "22:48 - 23.5.2025", Alert.STATE_GOOD));
        alerts.add(new Alert(2, "Sensor no. 2 activated!", "Reported as false positive.", "08:17 - 22.5.2025", Alert.STATE_BAD));
        alerts.add(new Alert(3, "Sensor no. 3 activated!", "Countermeasures deployed.", "17:03 - 21.5.2025", Alert.STATE_GOOD));
        saveAlerts();
    }

    public void clearAllAlerts() {
        alerts.clear();
        saveAlerts();
    }
}
