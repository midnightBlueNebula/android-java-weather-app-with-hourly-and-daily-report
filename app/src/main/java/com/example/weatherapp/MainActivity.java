package com.example.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.databinding.ActivityMainBinding;
import com.example.weatherapp.weatherdata.WeatherData;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static androidx.constraintlayout.widget.ConstraintLayout constraintLayout;
    private SearchView search;
    private androidx.recyclerview.widget.RecyclerView hourlyList;
    private androidx.recyclerview.widget.RecyclerView dailyList;
    private static ActivityMainBinding binding;
    private String locationName;

    public WeatherData data;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        assignViews(); // constraintLayout, search, hourlyList, dailyList.
        assignLayoutsToRecyclerViews(); // Makes hourlyList and dailyList horizontal.

        SharedPreferences sharedPref = getSharedPreferences("citypref", 0);
        locationName = sharedPref.getString("location", "London");

        data = new WeatherData(locationName);
        handleBinding();


        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onQueryTextSubmit(String query) {
                data = new WeatherData(query);
                handleBinding();

                SharedPreferences.Editor editor= sharedPref.edit();
                editor.putString("location", query);
                editor.commit();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


    private void assignViews(){
        constraintLayout = findViewById(R.id.constraintLayout);
        search = findViewById(R.id.searchCity);
        hourlyList = findViewById(R.id.hourlyList);
        dailyList = findViewById(R.id.dailyList);
    }

    private void assignLayoutsToRecyclerViews(){
        RecyclerView.LayoutManager RecyclerViewLayoutManagerForHourlyList
                = new LinearLayoutManager(
                getApplicationContext());

        RecyclerView.LayoutManager RecyclerViewLayoutManagerForDailyList
                = new LinearLayoutManager(
                getApplicationContext());

        hourlyList.setLayoutManager(
                RecyclerViewLayoutManagerForHourlyList);

        dailyList.setLayoutManager(
                RecyclerViewLayoutManagerForDailyList);

        LinearLayoutManager HorizontalLayoutForHourlyList
                = new LinearLayoutManager(
                MainActivity.this,
                LinearLayoutManager.HORIZONTAL,
                false);

        LinearLayoutManager HorizontalLayoutForDailyList
                = new LinearLayoutManager(
                MainActivity.this,
                LinearLayoutManager.HORIZONTAL,
                false);


        hourlyList.setLayoutManager(HorizontalLayoutForHourlyList);
        dailyList.setLayoutManager(HorizontalLayoutForDailyList);
    }

    private void handleBinding(){
        binding.setData(data);

        hourlyList.setAdapter(data.getHourlyWeatherAdapter());
        dailyList.setAdapter(data.getDailyWeatherAdapter());

        WeatherData.setBackgroundImage(constraintLayout,
                                       data.getWeather(),
                                       data.isDay());
    }

    public static void bindData(WeatherData selectedData){
        binding.setData(selectedData);
        WeatherData.setBackgroundImage(constraintLayout,
                                       selectedData.getWeather(),
                                       selectedData.isDay());
    }
}