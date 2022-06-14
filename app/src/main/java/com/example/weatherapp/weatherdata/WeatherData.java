package com.example.weatherapp.weatherdata;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;

import com.example.weatherapp.BuildConfig;
import com.example.weatherapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;

public class WeatherData {
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    private String weather;
    private String temperature;
    private String weatherDescription;
    private String cityName;
    private String humidity;
    private String feelsLike;
    private String wind;
    private String uvIndex;
    private String visibility;
    private String pressure;
    private double geoCoords[] = new double[2];

    private HourlyWeatherAdapter hourlyWeatherAdapteradapter;
    private DailyWeatherAdapter dailyWeatherAdapter;

    private final String apiKey = APIKEY.getKey();
    private String geoReq = "https://api.openweathermap.org/geo/1.0/direct?q=";
    private String weatherReq = "https://api.openweathermap.org/data/2.5/onecall?lat=";


    public WeatherData(String city) {
        StrictMode.setThreadPolicy(policy);

        geoReq += (city)+"&limit=1&appid=" + apiKey;

        try{
            JSONObject jsonGeo = readJsonFromUrl(geoReq, true);

            cityName = jsonGeo.getString("name");
            geoCoords[0] = jsonGeo.getDouble("lat");
            geoCoords[1] = jsonGeo.getDouble("lon");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        weatherReq += (geoCoords[0])+"&lon="+(geoCoords[1])+"&units=metric&exclude=minutely,current&appid=" + apiKey;

        try {
            JSONObject jsonWeather = readJsonFromUrl(weatherReq, false);

            JSONArray hourly = jsonWeather.getJSONArray("hourly");
            JSONArray daily = jsonWeather.getJSONArray("daily");

            JSONObject currentWeather = (JSONObject) hourly.get(0);

            hourlyWeatherAdapteradapter = new HourlyWeatherAdapter(hourly);
            dailyWeatherAdapter = new DailyWeatherAdapter(daily);

            temperature = Math.round(currentWeather.getDouble("temp")) + "°C";
            humidity = currentWeather.getInt("humidity") + "%";
            feelsLike = Math.round(currentWeather.getDouble("feels_like")) + "°C";
            wind = currentWeather.getString("wind_speed")
                    + "m/s " + currentWeather.getString("wind_deg")+"°";

            uvIndex = currentWeather.getDouble("uvi") + "";
            visibility = (double) currentWeather.getInt("visibility")/1000 + "km";
            pressure = currentWeather.getInt("pressure") + "hPa";

            JSONArray weatherArr = currentWeather.getJSONArray("weather");
            JSONObject weatherObj = (JSONObject) weatherArr.get(0);

            weather = weatherObj.getString("main");
            weatherDescription = weatherObj.getString("description");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static JSONObject readJsonFromUrl(String url, boolean enclosedWithBrackets) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);

            if (enclosedWithBrackets){
                jsonText = jsonText.replace("[", "").replace("]", "");
            }

            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }


    public static void setBackgroundImage(View layout, String weather){
        switch (weather){
            case "Clear" : layout.setBackgroundResource(R.drawable.sunny); break;
            case "Rain" : layout.setBackgroundResource(R.drawable.rain); break;
            case "Clouds" : layout.setBackgroundResource(R.drawable.clouds); break;
            case "Snow" : layout.setBackgroundResource(R.drawable.snow); break;
            case "Extreme" : layout.setBackgroundResource(R.drawable.extreme_weather); break;
            case "Thunderstorm" : layout.setBackgroundResource(R.drawable.thunderstorm); break;
            case "Drizzle" : layout.setBackgroundResource(R.drawable.drizzle); break;
            case "Mist" : layout.setBackgroundResource(R.drawable.mist); break;
            case "Smoke" : layout.setBackgroundResource(R.drawable.smoke); break;
            case "Haze" : layout.setBackgroundResource(R.drawable.haze); break;
            case "Dust" : layout.setBackgroundResource(R.drawable.dust); break;
            case "Fog" : layout.setBackgroundResource(R.drawable.fog); break;
            case "Sand" : layout.setBackgroundResource(R.drawable.sand); break;
            case "Tornado" : layout.setBackgroundResource(R.drawable.tornado); break;
            case "Squall" : layout.setBackgroundResource(R.drawable.squall); break;
            case "Ash" : layout.setBackgroundResource(R.drawable.ash); break;
            default: layout.setBackgroundResource(R.drawable.clouds); break;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getHourAndMinuteFromUnix(long unix){
        long ms = (long) unix * 1000;

        LocalDateTime dateTime
                = LocalDateTime.ofInstant(Instant.ofEpochMilli(ms), ZoneId.systemDefault());

        return dateTime.getHour() + ":" + dateTime.getMinute();
    }


    public String getWeather() {
        return weather;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public String getCityName() {
        return cityName;
    }

    public HourlyWeatherAdapter getHourlyWeatherAdapter() {
        return hourlyWeatherAdapteradapter;
    }

    public DailyWeatherAdapter getDailyWeatherAdapter() { return dailyWeatherAdapter; }

    public String getHumidity() { return humidity; }

    public String getFeelsLike() { return feelsLike; }

    public String getWind() { return wind; }

    public String getUvIndex() { return uvIndex; }

    public String getVisibility() { return visibility; }

    public String getPressure() { return pressure; }
}
