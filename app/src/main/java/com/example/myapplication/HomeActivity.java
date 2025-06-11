package com.example.myapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupToolbar(true); // true = is home activity
        setupDrawer();
        setupClickListeners();

        Intent serviceIntent = new Intent(this, NotificationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reset navigation highlighting when returning to home
        if (drawerLayout != null) {
            NavigationView navigationView = findViewById(R.id.nav_view);
            if (navigationView != null) {
                navigationView.setCheckedItem(R.id.nav_home);
            }
        }
    }

    private void setupClickListeners() {
        // Quick action buttons
        findViewById(R.id.profile_section).setOnClickListener(v -> {
            Toast.makeText(this, "Profile - Coming soon!", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.settings_section).setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.sensors_section).setOnClickListener(v -> {
            Intent intent = new Intent(this, SensorsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.about_section).setOnClickListener(v -> {
            Toast.makeText(this, "About - Coming soon!", Toast.LENGTH_SHORT).show();
        });

        // Main feature cards
        CardView realTimeCard = findViewById(R.id.real_time_overview_card);
        realTimeCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        CardView historyCard = findViewById(R.id.alerts_history_card);
        historyCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, AlertsHistoryActivity.class);
            startActivity(intent);
        });

        CardView mapCard = findViewById(R.id.customise_map_card);
        mapCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        CardView sensorMapCard = findViewById(R.id.sensor_map_card);
        sensorMapCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        CardView statisticsCard = findViewById(R.id.statistics_card);
        statisticsCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, StatisticsActivity.class);
            startActivity(intent);
        });
    }
}
