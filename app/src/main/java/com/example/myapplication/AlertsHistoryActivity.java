package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.List;

public class AlertsHistoryActivity extends AppCompatActivity {

    private ListView historyListView;
    private AlertHistoryAdapter historyAdapter;
    private List<Alert> alertsHistory;
    private AlertManager alertManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts_history);

        // Initialize AlertManager
        alertManager = AlertManager.getInstance(this);

        setupToolbar();
        setupViews();
        loadAlertsHistory();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Alerts History");
        }
    }

    private void setupViews() {
        historyListView = findViewById(R.id.history_list_view);
    }

    private void loadAlertsHistory() {
        alertsHistory = alertManager.getAllAlerts(); // Load all alerts from JSON
        historyAdapter = new AlertHistoryAdapter(this, alertsHistory);
        historyListView.setAdapter(historyAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
