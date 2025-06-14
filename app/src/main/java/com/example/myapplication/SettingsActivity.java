package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends BaseActivity {

    private SwitchMaterial notificationsSwitch;
    private SwitchMaterial soundSwitch;
    private Button clearAlertsButton;
    private Button exportDataButton;
    private AlertManager alertManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        alertManager = AlertManager.getInstance(this);

        setupToolbar(false); // false = not home activity
        setupDrawer();

        notificationsSwitch = findViewById(R.id.switch_notifications);
        soundSwitch = findViewById(R.id.switch_sound);
        clearAlertsButton = findViewById(R.id.btn_clear_alerts);
        exportDataButton = findViewById(R.id.btn_export_data);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean savedState = prefs.getBoolean("service_enabled", false);
        notificationsSwitch.setChecked(savedState);

        setupClickListeners();
    }

    private void setupClickListeners() {
        clearAlertsButton.setOnClickListener(v -> showClearAlertsDialog());

        exportDataButton.setOnClickListener(v -> {
            Toast.makeText(this, "Export feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, "Notifications " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();

            SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
            prefs.edit().putBoolean("service_enabled", isChecked).apply();
            Intent serviceIntent = new Intent(this, NotificationService.class);
            if (isChecked) {
                ContextCompat.startForegroundService(this, serviceIntent);
            } else {
                stopService(serviceIntent);
            }
        });

        soundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, "Sound alerts " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });
    }

    private void showClearAlertsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Clear Alerts")
                .setMessage("Are you sure you want to clear all alerts? This action cannot be undone.")
                .setPositiveButton("Clear All", (dialog, which) -> {
                    alertManager.clearAllAlerts();
                    Toast.makeText(this, "All alerts cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
