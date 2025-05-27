package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private ListView alertsListView;
    private AlertAdapter alertAdapter;
    private List<Alert> recentAlerts;
    private Button moreButton;
    private AlertManager alertManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize AlertManager
        alertManager = AlertManager.getInstance(this);

        setupToolbar();
        setupDrawer();
        setupViews();
        loadRecentAlerts();

        // Simulate receiving a new alert after 3 seconds
        Log.d("MainActivity", "Setting up delayed alert...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("MainActivity", "Showing simulated alert now...");
                simulateNewAlert();
            }
        }, 3000);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("SheepDistressApp");
        }
    }

    private void setupDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            // Handle navigation view item clicks here
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void setupViews() {
        alertsListView = findViewById(R.id.alerts_list_view);
        moreButton = findViewById(R.id.more_button);

        moreButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AlertsHistoryActivity.class);
            startActivity(intent);
        });
    }

    private void loadRecentAlerts() {
        recentAlerts = alertManager.getRecentAlerts(6);
        alertAdapter = new AlertAdapter(this, recentAlerts);
        alertsListView.setAdapter(alertAdapter);
    }

    // Public method that can be called by AlertManager
    public void showAlertConfirmationDialog(int sensorNumber, String title, String description, String timestamp, AlertManager.AlertResponseCallback callback) {
        Log.d("MainActivity", "Showing alert dialog for sensor: " + sensorNumber);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_new_alert, null);
        builder.setView(dialogView);

        // Update dialog content with provided data
        TextView sensorNumberView = dialogView.findViewById(R.id.sensor_number);
        TextView titleView = dialogView.findViewById(R.id.alert_title);
        TextView descriptionView = dialogView.findViewById(R.id.alert_description);

        if (sensorNumberView != null) sensorNumberView.setText(String.valueOf(sensorNumber));
        if (titleView != null) titleView.setText(title);
        if (descriptionView != null) descriptionView.setText(description);

        AlertDialog dialog = builder.create();

        Button acknowledgeBtn = dialogView.findViewById(R.id.acknowledge_button);
        Button reportFalseBtn = dialogView.findViewById(R.id.report_false_button);

        acknowledgeBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Alert acknowledged", Toast.LENGTH_SHORT).show();
            // Create alert with GOOD state
            Alert newAlert = new Alert(sensorNumber, title, "Countermeasures deployed.", timestamp, Alert.STATE_GOOD);
            callback.onAlertProcessed(newAlert);
            loadRecentAlerts(); // Refresh the display
            alertAdapter.notifyDataSetChanged();
            dialog.dismiss();
        });

        reportFalseBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Reported as false positive", Toast.LENGTH_SHORT).show();
            // Create alert with BAD state
            Alert newAlert = new Alert(sensorNumber, title, "Reported as false positive.", timestamp, Alert.STATE_BAD);
            callback.onAlertProcessed(newAlert);
            loadRecentAlerts(); // Refresh the display
            alertAdapter.notifyDataSetChanged();
            dialog.dismiss();
        });

        dialog.show();
    }

    // Method to simulate a new alert (for testing)
    private void simulateNewAlert() {
        Log.d("MainActivity", "simulateNewAlert() called");
        String currentTime = new SimpleDateFormat("HH:mm - dd.M.yyyy", Locale.getDefault()).format(new Date());

        // This is the main function you'll call to add alerts with user confirmation
        alertManager.addAlertWithUserConfirmation(
                this, // Pass MainActivity instance
                2,
                "Sensor no. 2 activated!",
                "Countermeasures are being deployed this very moment...",
                currentTime, null
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh alerts when returning to main activity
        loadRecentAlerts();
        if (alertAdapter != null) {
            alertAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
