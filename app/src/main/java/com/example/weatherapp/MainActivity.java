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

        RecyclerView.LayoutManager RecyclerViewLayoutManager
                = new LinearLayoutManager(
                getApplicationContext());

        hourlyList.setLayoutManager(
                RecyclerViewLayoutManager);

        LinearLayoutManager HorizontalLayout
                = new LinearLayoutManager(
                MainActivity.this,
                LinearLayoutManager.HORIZONTAL,
                false);
        hourlyList.setLayoutManager(HorizontalLayout);

        WeatherData data = new WeatherData("Ankara");

        binding.setData(data);
        hourlyList.setAdapter(data.getHourlyWeatherAdapter());


        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                WeatherData data = new WeatherData(query);

                binding.setData(data);
                hourlyList.setAdapter(data.getHourlyWeatherAdapter());

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}