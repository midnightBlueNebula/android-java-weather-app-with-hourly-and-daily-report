package com.example.weatherapp;

import android.os.Bundle;
import android.view.View;
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
    private androidx.constraintlayout.widget.ConstraintLayout constraintLayout;
    private SearchView search;
    private androidx.recyclerview.widget.RecyclerView hourlyList;
    private androidx.recyclerview.widget.RecyclerView dailyList;
    private ActivityMainBinding binding;

    public WeatherData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        assignViews(); // constraintLayout, search, hourlyList, dailyList.
        assignLayoutsToRecyclerViews(); // Makes hourlyList and dailyList horizontal.

        data = new WeatherData("Ankara");
        handleBinding();


        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                data = new WeatherData(query);
                handleBinding();

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

        WeatherData.setBackgroundImage(constraintLayout, data.getWeather());
    }
}