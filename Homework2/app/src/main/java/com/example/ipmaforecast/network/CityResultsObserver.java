package com.example.ipmaforecast.network;

import java.util.HashMap;

import com.example.ipmaforecast.datamodel.City;

public interface  CityResultsObserver {
    public void receiveCitiesList(HashMap<String, City> citiesCollection);
    public void onFailure(Throwable cause);
}
