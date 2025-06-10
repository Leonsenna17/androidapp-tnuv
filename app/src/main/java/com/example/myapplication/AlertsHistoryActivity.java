package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.List;

public class AlertsHistoryActivity extends BaseActivity {

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

        setupToolbar(false); // false = not home activity
        setupDrawer();
        setupViews();
        loadAlertsHistory();
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
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
