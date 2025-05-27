package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;
import java.util.List;

public class AlertsHistoryActivity extends AppCompatActivity {

    private ListView historyListView;
    private AlertHistoryAdapter historyAdapter;
    private List<Alert> alertsHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts_history);

        setupToolbar();
        setupViews();
        setupDummyData();
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

    private void setupDummyData() {
        alertsHistory = new ArrayList<>();
        alertsHistory.add(new Alert(1, "Sensor no. 1 activated!", "Countermeasures deployed.", "12:42 - 24.5.2025", Alert.STATE_GOOD));
        alertsHistory.add(new Alert(4, "Sensor no. 4 activated!", "Countermeasures deployed.", "11:22 - 24.5.2025", Alert.STATE_GOOD));
        alertsHistory.add(new Alert(2, "Sensor no. 2 activated!", "Reported as false positive.", "19:01 - 24.5.2025", Alert.STATE_BAD));
        alertsHistory.add(new Alert(4, "Sensor no. 4 activated!", "Countermeasures deployed.", "22:48 - 23.5.2025", Alert.STATE_GOOD));
        alertsHistory.add(new Alert(2, "Sensor no. 2 activated!", "Reported as false positive.", "08:17 - 22.5.2025", Alert.STATE_BAD));
        alertsHistory.add(new Alert(3, "Sensor no. 3 activated!", "Countermeasures deployed.", "17:03 - 21.5.2025", Alert.STATE_GOOD));

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