package com.example.weatherapp.weatherdata;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DailyWeatherAdapter extends RecyclerView.Adapter<DailyWeatherAdapter.ViewHolder> {
    private JSONArray localDataSet;
    private String tempeture;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);

            textView = (TextView) view.findViewById(R.id.dailyTextView);
        }

        public TextView getTextView() {
            return textView;
        }
    }


    public DailyWeatherAdapter(JSONArray dataSet) {
        localDataSet = dataSet;
    }

    @Override
    public DailyWeatherAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.daily_weather, viewGroup, false);

        return new DailyWeatherAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DailyWeatherAdapter.ViewHolder viewHolder, final int position) {
        try {
            JSONObject currentObj = (JSONObject)  localDataSet.get(position);
            JSONObject tempetureObj = currentObj.getJSONObject("temp");

            tempeture = Math.round(tempetureObj.getDouble("day") - 273.15) + "°C";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        viewHolder.getTextView().setText(tempeture);
    }


    @Override
    public int getItemCount() {
        return localDataSet.length();
    }
}
