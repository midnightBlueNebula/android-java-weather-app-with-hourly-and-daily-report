package com.example.weatherapp.weatherdata;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherAdapter.ViewHolder> {
    private JSONArray localDataSet;
    private String temperature;
    private String weatherDescription;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView hourView;
        private final TextView temperatureView;
        private final TextView weatherDescriptionView;

        public ViewHolder(View view) {
            super(view);

            hourView = view.findViewById(R.id.hourlyHourView);
            temperatureView = view.findViewById(R.id.hourlyTemperatureView);
            weatherDescriptionView = view.findViewById(R.id.hourlyWeatherDescriptionView);
        }

        public TextView getHourView() {
            return hourView;
        }

        public TextView getTemperatureView() {
            return temperatureView;
        }

        public TextView getWeatherDescriptionView() {
            return weatherDescriptionView;
        }
    }


    public HourlyWeatherAdapter(JSONArray dataSet) {
        localDataSet = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.hourly_weather, viewGroup, false);

        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        try {
            JSONObject currentObj = (JSONObject)  localDataSet.get(position);
            JSONArray currentWeatherArray = currentObj.getJSONArray("weather");
            JSONObject currentWeatherObject = (JSONObject) currentWeatherArray.get(0);

            weatherDescription = currentWeatherObject.getString("description");
            temperature = Math.round(currentObj.getDouble("temp") - 273.15) + "°C";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(position == 0){
            viewHolder.getHourView().setText("NOW");
        } else {
            viewHolder.getHourView().setText(LocalDateTime.now().plusHours(position).getHour()+":00");
        }

        viewHolder.getTemperatureView().setText(temperature);
        viewHolder.getWeatherDescriptionView().setText(weatherDescription);
    }


    @Override
    public int getItemCount() {
        return localDataSet.length();
    }
}
