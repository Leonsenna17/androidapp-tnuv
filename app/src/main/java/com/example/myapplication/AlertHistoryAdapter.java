package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;

public class AlertHistoryAdapter extends BaseAdapter {
    private Context context;
    private List<Alert> alerts;
    private LayoutInflater inflater;

    public AlertHistoryAdapter(Context context, List<Alert> alerts) {
        this.context = context;
        this.alerts = alerts;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return alerts.size();
    }

    @Override
    public Object getItem(int position) {
        return alerts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_alert_history, parent, false);
            holder = new ViewHolder();
            holder.sensorNumber = convertView.findViewById(R.id.sensor_number);
            holder.title = convertView.findViewById(R.id.alert_title);
            holder.description = convertView.findViewById(R.id.alert_description);
            holder.timestamp = convertView.findViewById(R.id.alert_timestamp);
            holder.container = convertView.findViewById(R.id.alert_container);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Alert alert = alerts.get(position);
        holder.sensorNumber.setText(String.valueOf(alert.getSensorNumber()));
        holder.title.setText(alert.getTitle());
        holder.description.setText(alert.getDescription());
        holder.timestamp.setText(alert.getTimestamp());

        // Set background based on alert state
        if (alert.isBadAlert()) {
            holder.container.setBackgroundResource(R.drawable.alert_item_background_bad);
        } else {
            holder.container.setBackgroundResource(R.drawable.alert_item_background);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView sensorNumber;
        TextView title;
        TextView description;
        TextView timestamp;
        LinearLayout container;
    }
}