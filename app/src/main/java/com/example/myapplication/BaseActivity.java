package com.example.myapplication;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public abstract class BaseActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
    protected ActionBarDrawerToggle toggle;
    protected Toolbar toolbar;
    protected ImageView backArrow;
    protected ImageView hamburgerMenu;

    protected void setupToolbar(boolean isHomeActivity) {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Remove default title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        
        backArrow = toolbar.findViewById(R.id.back_arrow);
        hamburgerMenu = toolbar.findViewById(R.id.hamburger_menu);

        hamburgerMenu.setVisibility(View.VISIBLE);
        hamburgerMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        if (isHomeActivity) {
            // Hide back arrow for home activity
            backArrow.setVisibility(View.GONE);
        } else {
            // Show back arrow for other activities
            backArrow.setVisibility(View.VISIBLE);
            backArrow.setOnClickListener(v -> navigateToHome());
        }
    }

    protected void setupDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);

        if (drawerLayout != null) {
            NavigationView navigationView = findViewById(R.id.nav_view);

            highlightCurrentActivity(navigationView);

            navigationView.setNavigationItemSelectedListener(item -> {
                handleNavigationItemSelected(item.getItemId());
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });
        }
    }

    protected void highlightCurrentActivity(NavigationView navigationView) {
        String currentActivity = this.getClass().getSimpleName();
        
        switch (currentActivity) {
            case "HomeActivity":
                navigationView.setCheckedItem(R.id.nav_home);
                break;
            case "MainActivity":
                navigationView.setCheckedItem(R.id.nav_real_time);
                break;
            case "AlertsHistoryActivity":
                navigationView.setCheckedItem(R.id.nav_alerts_history);
                break;
            case "StatisticsActivity":
                navigationView.setCheckedItem(R.id.nav_statistics);
                break;
            case "SensorsActivity":
                navigationView.setCheckedItem(R.id.nav_sensors);
                break;
            case "SettingsActivity":
                navigationView.setCheckedItem(R.id.nav_settings);
                break;
        }
    }

    protected void handleNavigationItemSelected(int itemId) {
        Intent intent = null;

        if (itemId == R.id.nav_home) {
            intent = new Intent(this, HomeActivity.class);
        } else if (itemId == R.id.nav_real_time) {
            intent = new Intent(this, MainActivity.class);
        } else if (itemId == R.id.nav_alerts_history) {
            intent = new Intent(this, AlertsHistoryActivity.class);
        } else if (itemId == R.id.nav_statistics) {
            intent = new Intent(this, StatisticsActivity.class);
        } else if (itemId == R.id.nav_settings) {
            intent = new Intent(this, SettingsActivity.class);
        } else if (itemId == R.id.nav_sensors) {
            intent = new Intent(this, SensorsActivity.class);
        }

        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }

    protected void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
