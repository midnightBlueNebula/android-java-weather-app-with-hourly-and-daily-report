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
import com.example.weatherapp.MainActivity;
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
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Locale;

public class WeatherData {
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    private static String timezone;
    private int currentHour;
    private static int sunriseHour;
    private static int sunsetHour;
    private boolean day;
    private String date;
    private String weather;
    private String temperature;
    private String weatherDescription;
    private static String cityName;
    private String humidity;
    private String feelsLike;
    private String wind;
    private String uvIndex;
    private static String visibility;
    private String pressure;

    private static String langCode = Locale.getDefault().getLanguage();
    private double geoCoords[] = new double[2];

    private static HourlyWeatherAdapter hourlyWeatherAdapter;
    private static DailyWeatherAdapter dailyWeatherAdapter;

    private final String apiKey = APIKEY.getKey();
    private String geoReq = "https://api.openweathermap.org/geo/1.0/direct?q=";
    private String weatherReq = "https://api.openweathermap.org/data/2.5/onecall?lat=";

    private static JSONObject currentWeather;

    private String metricDate;
    private String imperialDate;
    private double metricTemp;
    private double imperialTemp;
    private double metricFeel;
    private double imperialFeel;
    private double metricVisibility;
    private double imperialVisibility;
    private double metricWind;
    private double imperialWind;

    private static String cDeg = "°C";
    private static String fDeg = "°F";


    @RequiresApi(api = Build.VERSION_CODES.O)
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

        weatherReq += (geoCoords[0])+"&lon="+(geoCoords[1])+"&units=metric&exclude=minutely&lang="+langCode+"&appid=" + apiKey;

        try {
            JSONObject jsonWeather = readJsonFromUrl(weatherReq, false);

            JSONArray hourly = new JSONArray();

            currentWeather = jsonWeather.getJSONObject("current");
            hourly.put(currentWeather);

            JSONArray daily = jsonWeather.getJSONArray("daily");
            JSONArray hourlyObjs = jsonWeather.getJSONArray("hourly");

            for(int i = 1; i < hourlyObjs.length(); ++i){
                JSONObject obj = (JSONObject) hourlyObjs.get(i);
                hourly.put(obj);
            }

            timezone = jsonWeather.getString("timezone");

            sunriseHour = getHourFromUnix(currentWeather.getLong("sunrise"));
            sunsetHour = getHourFromUnix(currentWeather.getLong("sunset"));

            hourlyWeatherAdapter = new HourlyWeatherAdapter(hourly);
            dailyWeatherAdapter = new DailyWeatherAdapter(daily);

            assignFromJSON(currentWeather);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public WeatherData(JSONObject weatherObj) throws JSONException {
        assignFromJSON(weatherObj);
        MainActivity.bindData(this);
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


    public static boolean dayOrNight(int hour){
        if(hour > sunriseHour && hour < sunsetHour){
            return true;
        } else {
            return false;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void assignFromJSON(JSONObject currentWeather) throws JSONException {
        long dt = currentWeather.getLong("dt");

        metricDate = getMetricDateStrFromUnix(dt);
        imperialDate = getImperialDateStrFromUnix(dt);
        date = getDateStrForUnit();

        currentHour = getHourFromUnix(dt);
        day = dayOrNight(currentHour);

        double temp = currentWeather.getDouble("temp");

        metricTemp = temp;
        imperialTemp = cToF(temp);
        temperature = getTempForUnit();

        humidity = currentWeather.getInt("humidity") + "%";

        double feel = currentWeather.getDouble("feels_like");
        metricFeel = feel;
        imperialFeel = cToF(feel);
        feelsLike = getFeelForUnit();

        double speed = Double.parseDouble(currentWeather.getString("wind_speed"));
        metricWind = speed;
        imperialWind = metreSecondToMilesHour(speed);

        wind = getWindForUnit() + " " + currentWeather.getString("wind_deg")+"°";

        uvIndex = currentWeather.getDouble("uvi") + "";

        double distance = (double) currentWeather.getInt("visibility")/1000;
        metricVisibility = distance;
        imperialVisibility = kilometreToMiles(distance);

        visibility = getVisibilityForUnit();

        pressure = currentWeather.getInt("pressure") + "hPa";

        JSONArray weatherArr = currentWeather.getJSONArray("weather");
        JSONObject weatherObj = (JSONObject) weatherArr.get(0);

        weather = weatherObj.getString("main");
        weatherDescription = weatherObj.getString("description");
    }


    public static double cToF(double c){
        return c * 1.8 + 32;
    }


    public static double metreSecondToMilesHour(double speed){
        return speed * 3.6 / 1.6;
    }


    public static double kilometreToMiles(double distance){
        return distance / 1.6;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getMetricDateStrFromUnix(long unix){
        LocalDateTime dateTime =  getDateFromUnix(unix);

        return doubleDigit(dateTime.getHour()) + ":"
                + doubleDigit(dateTime.getMinute()) + " - "
                + doubleDigit(dateTime.getDayOfMonth()) + "/"
                + doubleDigit(dateTime.getMonthValue()) + "/"
                + dateTime.getYear();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getImperialDateStrFromUnix(long unix){
        LocalDateTime dateTime =  getDateFromUnix(unix);

        return doubleDigit(dateTime.getHour()) + ":"
                + doubleDigit(dateTime.getMinute()) + " - "
                + doubleDigit(dateTime.getMonthValue()) + "/"
                + doubleDigit(dateTime.getDayOfMonth()) + "/"
                + dateTime.getYear();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getDateStrForUnit(){
        if(MainActivity.unit == "metric"){
            return metricDate;
        }

        return imperialDate;
    }


    private String getMetricTemp(){
        return Math.round(metricTemp) + cDeg;
    }


    private String getImperialTemp(){
        return Math.round(imperialTemp) + fDeg;
    }


    private String getTempForUnit(){
        if(MainActivity.unit == "metric"){
            return getMetricTemp();
        }

        return getImperialTemp();
    }


    private String getMetricFeel(){
        return Math.round(metricFeel) + cDeg;
    }


    private String getImperialFeel(){
        return Math.round(imperialFeel) + fDeg;
    }


    private String getFeelForUnit(){
        if(MainActivity.unit == "metric"){
            return getMetricFeel();
        }

        return getImperialFeel();
    }


    private String getMetricWind(){
        return metricWind + "m/s";
    }


    private String getImperialWind(){
        DecimalFormat df = new DecimalFormat("#.0");
        return df.format(imperialWind) + "mi/hr";
    }


    private String getWindForUnit(){
        if(MainActivity.unit == "metric"){
            return getMetricWind();
        }

        return getImperialWind();
    }


    private String getMetricVisibility(){
        return metricVisibility + "km";
    }


    private String getImperialVisibility(){
        return imperialVisibility + "mi";
    }


    private String getVisibilityForUnit(){
        if(MainActivity.unit == "metric"){
            return getMetricVisibility();
        }

        return getImperialVisibility();
    }


    public static void setBackgroundImage(View layout, String weather, boolean d){
        switch (weather){
            case "Clear" :
                if(d){
                    layout.setBackgroundResource(R.drawable.sunny);
                } else {
                    layout.setBackgroundResource(R.drawable.clear_night);
                } ; break;
            case "Rain" : layout.setBackgroundResource(R.drawable.rain); break;
            case "Clouds" :
                if(d) {
                    layout.setBackgroundResource(R.drawable.cloudy);
                } else {
                    layout.setBackgroundResource(R.drawable.cloudy_night);
                } ; break;
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
            default: layout.setBackgroundResource(R.drawable.cloudy); break;
        }
    }


    private static String doubleDigit(int dt){
        if (dt < 10){
            return "0" + dt;
        }

        return dt + "";
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LocalDateTime getDateFromUnix(long unix){
        long ms = (long) unix * 1000;

        LocalDateTime dateTime
                = LocalDateTime.ofInstant(Instant.ofEpochMilli(ms), ZoneId.of(timezone));

        return dateTime;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int getHourFromUnix(long unix){
        long ms = (long) unix * 1000;

        LocalDateTime dateTime
                = LocalDateTime.ofInstant(Instant.ofEpochMilli(ms), ZoneId.of(timezone));

        return dateTime.getHour();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getHourAndMinuteFromUnix(long unix){
        long ms = (long) unix * 1000;

        LocalDateTime dateTime
                = LocalDateTime.ofInstant(Instant.ofEpochMilli(ms), ZoneId.of(timezone));

        return doubleDigit(dateTime.getHour()) + ":" + doubleDigit(dateTime.getMinute());
    }

    public static String getTimezone() { return timezone; }

    public boolean isDay() { return day; }

    public String getDate() { return date; }

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

    public static HourlyWeatherAdapter getHourlyWeatherAdapter() {
        return hourlyWeatherAdapter;
    }

    public static DailyWeatherAdapter getDailyWeatherAdapter() { return dailyWeatherAdapter; }

    public String getHumidity() { return humidity; }

    public String getFeelsLike() { return feelsLike; }

    public String getWind() { return wind; }

    public String getUvIndex() { return uvIndex; }

    public String getVisibility() { return visibility; }

    public String getPressure() { return pressure; }

    public static JSONObject getCurrentWeatherJSON() { return currentWeather; }
}
