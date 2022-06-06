package com.example.weatherapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.databinding.ActivityMainBinding;
import com.example.weatherapp.weatherdata.WeatherData;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        SearchView search = findViewById(R.id.searchCity);
        androidx.recyclerview.widget.RecyclerView hourlyList = findViewById(R.id.hourlyList);
        androidx.recyclerview.widget.RecyclerView dailyList = findViewById(R.id.dailyList);


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

        WeatherData data = new WeatherData("Ankara");

        binding.setData(data);
        hourlyList.setAdapter(data.getHourlyWeatherAdapter());
        dailyList.setAdapter(data.getDailyWeatherAdapter());


        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                WeatherData data = new WeatherData(query);

                binding.setData(data);
                hourlyList.setAdapter(data.getHourlyWeatherAdapter());
                dailyList.setAdapter(data.getDailyWeatherAdapter());

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}