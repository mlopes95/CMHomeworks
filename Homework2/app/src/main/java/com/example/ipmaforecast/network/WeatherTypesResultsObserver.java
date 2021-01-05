package com.example.ipmaforecast.network;

import java.util.HashMap;

import com.example.ipmaforecast.datamodel.WeatherType;

public interface WeatherTypesResultsObserver {
    public void receiveWeatherTypesList(HashMap<Integer, WeatherType> descriptorsCollection);
    public void onFailure(Throwable cause);
}
