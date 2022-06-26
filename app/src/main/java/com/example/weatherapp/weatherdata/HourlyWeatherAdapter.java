package com.example.weatherapp.weatherdata;

import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.MainActivity;
import com.example.weatherapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherAdapter.ViewHolder> {
    private JSONArray localDataSet;
    private boolean day;
    private int currentHour;
    private String temperature;
    private String weather;
    private String weatherDescription;
    private String humidity;
    private String feelsLike;
    private String wind;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private JSONObject weatherObj;
        private final TextView hourView;
        private final TextView temperatureView;
        private final TextView weatherDescriptionView;
        private final TextView humidityView;
        private final TextView windView;
        private final androidx.constraintlayout.widget.ConstraintLayout hourlyLayoutView;

        public ViewHolder(View view) {
            super(view);
            hourView = view.findViewById(R.id.hourlyHourView);
            temperatureView = view.findViewById(R.id.hourlyTemperatureView);
            weatherDescriptionView = view.findViewById(R.id.hourlyWeatherDescriptionView);
            hourlyLayoutView = view.findViewById(R.id.hourlyLayout);
            humidityView = view.findViewById(R.id.hourlyHumidityView);
            windView = view.findViewById(R.id.hourlyWindView);

            view.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                public void onClick(View v) {
                    try {
                        new WeatherData(weatherObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
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

        public TextView getHumidityView() { return humidityView; }

        public TextView getWindView() { return windView; }

        public androidx.constraintlayout.widget.ConstraintLayout getHourlyLayoutView()
                                                                 { return hourlyLayoutView; }
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
            viewHolder.weatherObj = currentObj;
            JSONArray currentWeatherArray = currentObj.getJSONArray("weather");
            JSONObject currentWeatherObject = (JSONObject) currentWeatherArray.get(0);

            currentHour = WeatherData.getHourFromUnix(currentObj.getLong("dt"));

            day = WeatherData.dayOrNight(currentHour);

            weather = currentWeatherObject.getString("main");
            weatherDescription = currentWeatherObject.getString("description");
            temperature = Math.round(currentObj.getDouble("temp")) + "°C";
            humidity = currentObj.getInt("humidity") + "%";
            feelsLike = Math.round(currentObj.getDouble("feels_like")) + "°C";
            wind = currentObj.getString("wind_speed") + "m/s ";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(position == 0){
            viewHolder.getHourView().setText(R.string.now);
        } else {
            viewHolder.getHourView().setText(ZonedDateTime.now(ZoneId.of(WeatherData.getTimezone())).plusHours(position).getHour()+":00");
        }

        viewHolder.getTemperatureView().setText(temperature);
        viewHolder.getWeatherDescriptionView().setText(weatherDescription);
        viewHolder.getHumidityView().setText(humidity);
        viewHolder.getWindView().setText(wind);

        WeatherData.setBackgroundImage(viewHolder.getHourlyLayoutView(), weather, day);
    }


    @Override
    public int getItemCount() {
        return localDataSet.length();
    }
}
