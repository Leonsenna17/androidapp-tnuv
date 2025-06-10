package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;
import org.maplibre.android.MapLibre;
import org.maplibre.android.annotations.IconFactory;
import org.maplibre.android.annotations.Marker;
import org.maplibre.android.annotations.MarkerOptions;
import org.maplibre.android.camera.CameraPosition;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.OnMapReadyCallback;


public class MainActivity extends BaseActivity {

    private ListView alertsListView;
    private AlertAdapter alertAdapter;
    private List<Alert> recentAlerts;
    private Button moreButton;
    private AlertManager alertManager;
    private MapView mapView;
    private MapLibreMap mapLibreMap;
    private TextView textView;
    private TextView podatek;
    private Handler handler = new Handler();
    private String lastData = "";
    private final String CHANNEL_ID = "data_update_channel";
    List<Marker> markers = new ArrayList<>();

    private final LatLng[] markerPositions = new LatLng[] {
            /*
            new LatLng(46.056946, 14.505751), // Ljubljana
            new LatLng(46.5547, 15.6459),     // Maribor
            new LatLng(45.5439, 13.7306),     // Koper
            new LatLng(46.3588, 15.1106),     // Celje
            new LatLng(46.1700, 14.1961)      // Kranj

             */

            new LatLng(46.2500574884723, 14.25815490363353),
            new LatLng(46.2526102495906, 14.267221466802557),
            new LatLng(46.24956732745012, 14.269973090984017),
            new LatLng(46.24946349292436, 14.266346725324173),
            new LatLng(46.248565712115536, 14.263793266878723)
    };

    private final String[] markerTitles = new String[] {
            "naprava-za-ovce",
            "Maribor",
            "Koper",
            "Celje",
            "Kranj"
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapLibre.getInstance(this);
        setContentView(R.layout.activity_main);


        // Inicializacija pogleda z uporabo LayoutInflater
        LayoutInflater inflater = LayoutInflater.from(this);
        View rootView = inflater.inflate(R.layout.activity_main, null);
        setContentView(rootView);

        // Inicializacija MapView
        mapView = rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapLibreMap map) {
                mapLibreMap = map;
                //mapLibreMap.setStyle("https://demotiles.maplibre.org/style.json");
                mapLibreMap.setStyle("https://api.maptiler.com/maps/streets/style.json?key=aqhX5FMpQ7uvNvbZtp9r");

                // Začetna pozicija
                CameraPosition startPosition = new CameraPosition.Builder()
                        .target(new LatLng(46.24946349292436, 14.266346725324173)) // Ljubljana
                        .zoom(13.0)
                        .build();
                mapLibreMap.setCameraPosition(startPosition);

                // Dodaj oznako ob kliku
                mapLibreMap.addOnMapClickListener(point -> {
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(point)
                            .title("Senzor")
                            .snippet("Dodan ob kliku.");

                    mapLibreMap.addMarker(markerOptions);
                    return true;
                });

                // Ustvarjanje markerjev na zemljevidu
                for (int i = 0; i < markerPositions.length; i++) {
                    Marker marker = mapLibreMap.addMarker(new MarkerOptions()
                            .position(markerPositions[i])
                            .title(markerTitles[i]));
                    markers.add(marker);
                }
                highlightMarkerByName("");
            }
        });


        startRepeatingTask();       // začne zanko za preverjanje sprememb

        // Initialize AlertManager
        alertManager = AlertManager.getInstance(this);

        setupToolbar(false); // false = not home activity
        setupDrawer();
        setupViews();
        loadRecentAlerts();

        // Simulate receiving a new alert after 3 seconds
        /*
        Log.d("MainActivity", "Setting up delayed alert...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("MainActivity", "Showing simulated alert now...");
                simulateNewAlert();
            }
        }, 3000);

         */
    }

    private void startRepeatingTask() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchData();
                handler.postDelayed(this, 10000); // ponovi na 10 sekund
            }
        }, 0);
    }

    private void fetchData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://lukamali.com/ttn2value/data/70B3D57ED0070838.json";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        handleJson(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // textView.setText("That didn't work!");
                Log.d("NAPAKA: ", String.valueOf(error));
            }
        });

        queue.add(stringRequest);
    }

    private void handleJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String cas = jsonObject.getString("received_at");           // čas sprejema
            JSONObject end_deivce_ids = jsonObject.getJSONObject("end_device_ids");
            String senzor = end_deivce_ids.getString("device_id");      // ime senzorja
            JSONObject uplink_message = jsonObject.getJSONObject("uplink_message");
            JSONObject decoded_payload = uplink_message.getJSONObject("decoded_payload");
            String tip = decoded_payload.getString("type");             // tip obvestila

            SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
            String lastSavedTime = prefs.getString("last_refresh_time", "");

            if (!cas.equals(lastSavedTime)) {
                // lastData = cas;
                // textView.setText("Novi podatek sprejet ob: " + cajt);

                if (tip.equals("alarm")) {
                    highlightMarkerByName(senzor);
                    newAlert(Arrays.asList(markerTitles).indexOf(senzor) + 1);
                }
                else if (tip.equals("battery")) {
                    Log.d("NIVO_BATERIJE", senzor);
                    highlightMarkerByName("");
                }

                prefs.edit().putString("last_refresh_time", cas).apply();
            }
            else {
                //highlightMarkerByName("");
            }

            // textView.setText(podatek);
        } catch (JSONException e) {
            // textView.setText("Napaka pri parsiranju: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void highlightMarkerByName(String name) {
        for (int i = 0; i < 5; i++) {
            if (markerTitles[i].equals(name)) {
                markers.get(i).setIcon(IconFactory.getInstance(this).fromResource(R.drawable.rdeca_oznaka_32));
            }
            else {
                markers.get(i).setIcon(IconFactory.getInstance(this).fromResource(R.drawable.zelena_oznaka_32));
            }
        }
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

    private void newAlert(int st) {
        String trenutniCas = new SimpleDateFormat("HH:mm - dd.M.yyyy", Locale.getDefault()).format(new Date());
        alertManager.addAlertWithUserConfirmation(
                this,
                st,
                "Sensor no. " + st + " activated!",
                "Take action now!",
                trenutniCas, null
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
        mapView.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
