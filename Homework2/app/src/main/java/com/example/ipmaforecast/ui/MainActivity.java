package com.example.ipmaforecast.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

import com.example.ipmaforecast.R;
import com.example.ipmaforecast.datamodel.City;
import com.example.ipmaforecast.datamodel.Weather;
import com.example.ipmaforecast.datamodel.WeatherType;
import com.example.ipmaforecast.network.CityResultsObserver;
import com.example.ipmaforecast.network.ForecastForACityResultsObserver;
import com.example.ipmaforecast.network.IpmaWeatherClient;
import com.example.ipmaforecast.network.WeatherTypesResultsObserver;

public class MainActivity extends AppCompatActivity {

    private TextView feedback;

    IpmaWeatherClient client = new IpmaWeatherClient();
    private HashMap<String, City> cities;
    private HashMap<Integer, WeatherType> weatherDescriptions;

    private RecyclerView mRecyclerView;
    private CityListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int gridColumnCount =
                getResources().getInteger(R.integer.grid_column_count);

        // Initialize the RecyclerView.
        mRecyclerView = findViewById(R.id.customRecyclerView);

        // Set the Layout Manager.
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridColumnCount));

        // Initialize the ArrayList that will contain the data.
        cities = new HashMap<>();

        receiveCitiesList();

        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = new CityListAdapter(this, cities);

        mRecyclerView.setAdapter(mAdapter);
    }

    private void callWeatherForecastForACityStep1(String city) {

        feedback.append("\nGetting forecast for: " + city); feedback.append("\n");

        // call the remote api, passing an (anonymous) listener to get back the results
        client.retrieveWeatherConditionsDescriptions(new WeatherTypesResultsObserver() {
            @Override
            public void receiveWeatherTypesList(HashMap<Integer, WeatherType> descriptorsCollection) {
                com.example.ipmaforecast.ui.MainActivity.this.weatherDescriptions = descriptorsCollection;
                //receiveCitiesList( city);
            }

            @Override
            public void onFailure(Throwable cause) {
                feedback.append("Failed to get weather conditions!");
            }
        });

    }

    private void receiveCitiesList() {
        client.retrieveCitiesList(new CityResultsObserver() {

            @Override
            public void receiveCitiesList(HashMap<String, City> citiesCollection) {
                com.example.ipmaforecast.ui.MainActivity.this.cities = citiesCollection;
            }

            @Override
            public void onFailure(Throwable cause) {
                feedback.append("Failed to get cities list!");
            }
        });
    }

    private void callWeatherForecastForACityStep3(int localId) {
        client.retrieveForecastForCity(localId, new ForecastForACityResultsObserver() {
            @Override
            public void receiveForecastList(List<Weather> forecast) {
                for (Weather day : forecast) {
                    feedback.append(day.toString());
                    feedback.append("\t");
                }
            }
            @Override
            public void onFailure(Throwable cause) {
                feedback.append( "Failed to get forecast for 5 days");
            }
        });

    }

}
