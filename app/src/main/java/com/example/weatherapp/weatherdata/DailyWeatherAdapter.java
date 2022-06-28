package com.example.weatherapp.weatherdata;

import android.annotation.SuppressLint;
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

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Iterator;

public class DailyWeatherAdapter extends RecyclerView.Adapter<DailyWeatherAdapter.ViewHolder> {
    private JSONArray localDataSet;
    private String weather;
    private String weatherDescription;
    private String maxTemperature;
    private String minTemperature;
    private String sunrise;
    private String sunset;

    private double metricMinTemp;
    private double imperialMinTemp;
    private double metricMaxTemp;
    private double imperialMaxTemp;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private JSONObject weatherObj;
        private final TextView dayView;
        private final TextView weatherDescriptionView;
        private final TextView maxTemperatureView;
        private final TextView minTemperatureView;
        private final TextView sunriseView;
        private final TextView sunsetView;
        private final androidx.constraintlayout.widget.ConstraintLayout dailyLayoutView;

        public ViewHolder(View view) {
            super(view);

            dayView = view.findViewById(R.id.dailyDayView);
            weatherDescriptionView = view.findViewById(R.id.dailyWeatherDescriptionView);
            maxTemperatureView = view.findViewById(R.id.dailyMaxTemperatureView);
            minTemperatureView = view.findViewById(R.id.dailyMinTemperatureView);
            sunriseView = view.findViewById(R.id.sunriseTime);
            sunsetView = view.findViewById(R.id.sunsetTime);
            dailyLayoutView = view.findViewById(R.id.dailyLayout);

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

        public TextView getWeatherDescriptionViewView() {
            return weatherDescriptionView;
        }

        public TextView getMaxTemperatureView() { return maxTemperatureView; }

        public TextView getMinTemperatureView() { return minTemperatureView; }

        public TextView getDayView() { return dayView; }

        public TextView getSunriseView() { return sunriseView; }

        public TextView getSunsetView() { return  sunsetView; }

        public androidx.constraintlayout.widget.ConstraintLayout getDailyLayoutView()
                                                                 { return dailyLayoutView; }
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
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        try {
            JSONObject currentObj = (JSONObject)  localDataSet.get(position);

            String temp = currentObj.toString();
            JSONObject curObj = new JSONObject(temp);

            JSONObject temperatureObj = currentObj.getJSONObject("temp");
            JSONObject feelsLikeObj = currentObj.getJSONObject("feels_like");
            JSONArray weatherArray = currentObj.getJSONArray("weather");
            JSONObject weatherObj = (JSONObject) weatherArray.get(0);

            double minTemp = temperatureObj.getDouble("min");
            double maxTemp = temperatureObj.getDouble("max");

            weather = weatherObj.getString("main");
            weatherDescription = weatherObj.getString("description");
            sunrise = WeatherData.getHourAndMinuteFromUnix(currentObj.getLong("sunrise"));
            sunset = WeatherData.getHourAndMinuteFromUnix(currentObj.getLong("sunset"));

            metricMinTemp = minTemp;
            metricMaxTemp = maxTemp;
            imperialMinTemp = WeatherData.cToF(minTemp);
            imperialMaxTemp = WeatherData.cToF(maxTemp);

            curObj.put("temp", temperatureObj.getDouble("day"));
            curObj.put("feels_like", feelsLikeObj.getDouble("day"));
            curObj.put("visibility", 10000);

            viewHolder.weatherObj = curObj;

            if(MainActivity.unit == "metric"){
                maxTemperature = Math.round(metricMaxTemp) + "°C";
                minTemperature = Math.round(metricMinTemp) + "°C";
            } else {
                maxTemperature = Math.round(imperialMaxTemp) + "°F";
                minTemperature = Math.round(imperialMinTemp) + "°F";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(position == 0){
            viewHolder.getDayView().setText(R.string.today);
        } else {
            int day = getLocalDayName(LocalDateTime.now().plusDays(position).getDayOfWeek()+"");
            viewHolder.getDayView().setText(day);
        }


        viewHolder.getWeatherDescriptionViewView().setText(weatherDescription);
        viewHolder.getMaxTemperatureView().setText(maxTemperature);
        viewHolder.getMinTemperatureView().setText(minTemperature);
        viewHolder.getSunriseView().setText(sunrise);
        viewHolder.getSunsetView().setText(sunset);

        WeatherData.setBackgroundImage(viewHolder.getDailyLayoutView(), weather, true);
    }


    @Override
    public int getItemCount() {
        return localDataSet.length();
    }

    private static int getLocalDayName(String day){
        switch (day.toLowerCase()){
            case "monday": return R.string.monday;
            case "tuesday": return R.string.tuesday;
            case "wednesday": return R.string.wednesday;
            case "thursday": return R.string.thursday;
            case "friday": return R.string.friday;
            case "frıday": return R.string.friday;
            case "saturday": return R.string.saturday;
            default: return R.string.sunday;
        }
    }
}
