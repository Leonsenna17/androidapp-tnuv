package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    private TextView totalAlertsText;
    private TextView todayAlertsText;
    private TextView goodAlertsText;
    private TextView badAlertsText;
    private TextView pendingAlertsText;
    private LinearLayout sensorStatsContainer;
    private AlertManager alertManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        alertManager = AlertManager.getInstance(this);

        setupToolbar();
        setupViews();
        loadStatistics();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Statistics");
        }
    }

    private void setupViews() {
        totalAlertsText = findViewById(R.id.tv_total_alerts);
        todayAlertsText = findViewById(R.id.tv_today_alerts);
        goodAlertsText = findViewById(R.id.tv_good_alerts);
        badAlertsText = findViewById(R.id.tv_bad_alerts);
        pendingAlertsText = findViewById(R.id.tv_pending_alerts);
        sensorStatsContainer = findViewById(R.id.sensor_stats_container);
    }

    private void loadStatistics() {
        List<Alert> allAlerts = alertManager.getAllAlerts();

        // Calculate basic statistics
        int totalAlerts = allAlerts.size();
        int todayAlerts = 0;
        int goodAlerts = 0;
        int badAlerts = 0;
        int pendingAlerts = 0;

        // Get today's date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.M.yyyy", Locale.getDefault());
        String today = dateFormat.format(new Date());

        // Calculate sensor statistics
        Map<Integer, Integer> sensorCounts = new HashMap<>();

        for (Alert alert : allAlerts) {
            // Count alerts by state
            switch (alert.getState()) {
                case Alert.STATE_GOOD:
                    goodAlerts++;
                    break;
                case Alert.STATE_BAD:
                    badAlerts++;
                    break;
                case Alert.STATE_PENDING:
                    pendingAlerts++;
                    break;
            }

            // Count today's alerts
            if (alert.getTimestamp().contains(today)) {
                todayAlerts++;
            }

            // Count alerts by sensor
            int sensorNumber = alert.getSensorNumber();
            sensorCounts.put(sensorNumber, sensorCounts.getOrDefault(sensorNumber, 0) + 1);
        }

        // Update UI
        totalAlertsText.setText(String.valueOf(totalAlerts));
        todayAlertsText.setText(String.valueOf(todayAlerts));
        goodAlertsText.setText(String.valueOf(goodAlerts));
        badAlertsText.setText(String.valueOf(badAlerts));
        pendingAlertsText.setText(String.valueOf(pendingAlerts));

        // Update sensor statistics
        updateSensorStatistics(sensorCounts, totalAlerts);
    }

    private void updateSensorStatistics(Map<Integer, Integer> sensorCounts, int totalAlerts) {
        sensorStatsContainer.removeAllViews();

        for (int i = 1; i <= 5; i++) {
            int count = sensorCounts.getOrDefault(i, 0);
            float percentage = totalAlerts > 0 ? (float) count / totalAlerts * 100 : 0;

            // Create sensor stat view
            LinearLayout sensorView = new LinearLayout(this);
            sensorView.setOrientation(LinearLayout.HORIZONTAL);
            sensorView.setPadding(0, 8, 0, 8);

            // Sensor label
            TextView sensorLabel = new TextView(this);
            sensorLabel.setText("Sensor " + i);
            sensorLabel.setLayoutParams(new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT));

            // Progress bar
            ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            progressParams.setMargins(16, 0, 16, 0);
            progressBar.setLayoutParams(progressParams);
            progressBar.setMax(100);
            progressBar.setProgress((int) percentage);

            // Count label
            TextView countLabel = new TextView(this);
            countLabel.setText(count + " (" + String.format("%.1f", percentage) + "%)");
            countLabel.setLayoutParams(new LinearLayout.LayoutParams(120, LinearLayout.LayoutParams.WRAP_CONTENT));
            countLabel.setTextSize(12);

            sensorView.addView(sensorLabel);
            sensorView.addView(progressBar);
            sensorView.addView(countLabel);

            sensorStatsContainer.addView(sensorView);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStatistics(); // Refresh statistics when returning to this activity
    }
}
