package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private ListView alertsListView;
    private AlertAdapter alertAdapter;
    private List<Alert> recentAlerts;
    private Button moreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();
        setupDrawer();
        setupViews();
        setupDummyData();

        // Simulate receiving a new alert after 3 seconds
        new android.os.Handler().postDelayed(() -> showNewAlert(), 3000);
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

    private void setupDummyData() {
        recentAlerts = new ArrayList<>();
        recentAlerts.add(new Alert(1, "Sensor no. 1 activated!", "Countermeasures deployed.", "2 hours ago", Alert.STATE_GOOD));
        recentAlerts.add(new Alert(4, "Sensor no. 4 activated!", "Countermeasures deployed.", "4 hours ago", Alert.STATE_GOOD));
        recentAlerts.add(new Alert(2, "Sensor no. 2 activated!", "Reported as false positive.", "5 hours ago", Alert.STATE_BAD));
        recentAlerts.add(new Alert(4, "Sensor no. 4 activated!", "Countermeasures deployed.", "11 hours ago", Alert.STATE_GOOD));

        alertAdapter = new AlertAdapter(this, recentAlerts);
        alertsListView.setAdapter(alertAdapter);
    }

    private void showNewAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_new_alert, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        Button acknowledgeBtn = dialogView.findViewById(R.id.acknowledge_button);
        Button reportFalseBtn = dialogView.findViewById(R.id.report_false_button);

        acknowledgeBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Alert acknowledged", Toast.LENGTH_SHORT).show();
            // Add new alert to the list with GOOD state
            Alert newAlert = new Alert(2, "Sensor no. 2 activated!", "Countermeasures deployed.", "Just now", Alert.STATE_GOOD);
            recentAlerts.add(0, newAlert);
            alertAdapter.notifyDataSetChanged();
            dialog.dismiss();
        });

        reportFalseBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Reported as false positive", Toast.LENGTH_SHORT).show();
            // Add new alert to the list with BAD state
            Alert newAlert = new Alert(2, "Sensor no. 2 activated!", "Reported as false positive.", "Just now", Alert.STATE_BAD);
            recentAlerts.add(0, newAlert);
            alertAdapter.notifyDataSetChanged();
            dialog.dismiss();
        });

        dialog.show();
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