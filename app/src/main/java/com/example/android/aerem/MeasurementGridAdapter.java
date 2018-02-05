package com.example.android.aerem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.aerem.utils.ApplicationUtils;
import com.example.android.aerem.utils.GaugeUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by peterpomlett on 19/01/2018.
 * To show gauges to represent measurements in a grid view
 */

public class MeasurementGridAdapter extends BaseAdapter {

    private final Context mContext;
    private ArrayList<HashMap<String, String>> mMeasurements;

    void setMeasurements(ArrayList<HashMap<String, String>> measurements) {
        mMeasurements = measurements;
        notifyDataSetChanged();
    }

    MeasurementGridAdapter(Context context, ArrayList<HashMap<String, String>> measurements) {
        this.mContext = context;
        this.mMeasurements = measurements;
    }

    @Override
    public int getCount() {
        return mMeasurements.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getView(int position, View itemView, ViewGroup parent) {
        if (itemView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            itemView = layoutInflater.inflate(R.layout.measurement_item, parent, false);
        }
        final TextView nameTextView = itemView.findViewById(R.id.mi_name_textView);
        final TextView measurementTextView = itemView.findViewById(R.id.mi_measurement_textView);
        final TextView dateTextView = itemView.findViewById(R.id.mi_date_textView);
        final ImageView pointer = itemView.findViewById(R.id.imageViewArrow);
        HashMap<String, String> details = mMeasurements.get(position);
        String name = details.get("parameter");
        String value = details.get("value");
        String unit = details.get("unit");
        String lastUpdated = details.get("lastUpdated");
        dateTextView.setText(ApplicationUtils.formattedDateWithLocal(lastUpdated));
        String measurement = String.format("%s %s", value, unit);
        nameTextView.setText(name);
        measurementTextView.setText(measurement);
        GaugeUtils.gaugePointerForMeasurement(pointer, name, unit, value);
        return itemView;
    }

}
