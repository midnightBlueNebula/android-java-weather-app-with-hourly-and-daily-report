package com.example.weatherapp.weatherdata;

import android.annotation.SuppressLint;
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

import java.time.LocalDateTime;

public class DailyWeatherAdapter extends RecyclerView.Adapter<DailyWeatherAdapter.ViewHolder> {
    private JSONArray localDataSet;
    private String weatherDescription;
    private String maxTemperature;
    private String minTemperature;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView dayView;
        private final TextView weatherDescriptionView;
        private final TextView maxTemperatureView;
        private final TextView minTemperatureView;

        public ViewHolder(View view) {
            super(view);

            dayView = view.findViewById(R.id.dailyDayView);
            weatherDescriptionView = view.findViewById(R.id.dailyWeatherDescriptionView);
            maxTemperatureView = view.findViewById(R.id.dailyMaxTemperatureView);
            minTemperatureView = view.findViewById(R.id.dailyMinTemperatureView);
        }

        public TextView getWeatherDescriptionViewView() {
            return weatherDescriptionView;
        }
        public TextView getMaxTemperatureView() { return maxTemperatureView; }
        public TextView getMinTemperatureView() { return minTemperatureView; }
        public TextView getDayView() { return dayView; }
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(DailyWeatherAdapter.ViewHolder viewHolder, final int position) {
        try {
            JSONObject currentObj = (JSONObject)  localDataSet.get(position);
            JSONObject temperatureObj = currentObj.getJSONObject("temp");
            JSONArray weatherArray = currentObj.getJSONArray("weather");
            JSONObject weatherObj = (JSONObject) weatherArray.get(0);

            maxTemperature = Math.round(temperatureObj.getDouble("max") - 273.15) + "°C";
            minTemperature = Math.round(temperatureObj.getDouble("min") - 273.15) + "°C";
            weatherDescription = weatherObj.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(position == 0){
            viewHolder.getDayView().setText("TODAY");
        } else {
            viewHolder.getDayView().setText(LocalDateTime.now().plusDays(position).getDayOfWeek()+"");
        }


        viewHolder.getWeatherDescriptionViewView().setText(weatherDescription);
        viewHolder.getMaxTemperatureView().setText(maxTemperature);
        viewHolder.getMinTemperatureView().setText(minTemperature);
    }


    @Override
    public int getItemCount() {
        return localDataSet.length();
    }
}
