package com.example.ipmaforecast.network;

import java.util.List;

import com.example.ipmaforecast.datamodel.Weather;

public interface ForecastForACityResultsObserver {
    public void receiveForecastList(List<Weather> forecast);
    public void onFailure(Throwable cause);
}
