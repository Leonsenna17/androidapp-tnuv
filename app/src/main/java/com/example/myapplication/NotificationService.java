package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationService extends Service {

    private static final String CHANNEL_ID = "json_check_channel";
    private static final String USER_CHANNEL_ID = "json_user_alerts_channel";
    private static final int NOTIFICATION_ID = 1;

    private Handler handler;
    private Runnable checkRunnable;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        createUserNotificationChannel();
        startForeground(NOTIFICATION_ID, getNotification());

        handler = new Handler();
        checkRunnable = new Runnable() {
            @Override
            public void run() {
                fetchData();
                handler.postDelayed(this, 10000); // vsakih 10 sekund
            }
        };

        handler.post(checkRunnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Če se servis po nesreči ustavi, naj se ponovno zažene
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null && checkRunnable != null) {
            handler.removeCallbacks(checkRunnable);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Ne uporabljamo bindanja
        return null;
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

            Intent intent = new Intent("com.example.JSON_UPDATED");

            if (!cas.equals(lastSavedTime)) {

                if (tip.equals("alarm")) {
                    showUpdateNotification(senzor);

                    intent.putExtra("marker", senzor);
                    sendBroadcast(intent);

                }
                else if (tip.equals("battery")) {
                    Log.d("NIVO_BATERIJE", senzor);
                    intent.putExtra("marker", "");
                    sendBroadcast(intent);
                }

                prefs.edit().putString("last_refresh_time", cas).apply();
            }
            else {
                intent.putExtra("marker", "");
                sendBroadcast(intent);
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "JSON Checker Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void createUserNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    USER_CHANNEL_ID,
                    "Obvestila o spremembah",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Obvestila o spremembah JSON podatkov");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }


    private Notification getNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Poslušanje na senzorjih.")
                .setContentText("Vsak hip se lahko kaj zgodi.")
                .setSmallIcon(R.drawable.ic_info)
                .setAutoCancel(true)
                .build();
    }

    private void showUpdateNotification(String senzor) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, USER_CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_warning_24)
                .setContentTitle("Sprožil se je senzor " + senzor + ".")
                .setContentText("Ukrepajte takoj!")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(2, builder.build()); // ID 2 = user notification
    }

}
