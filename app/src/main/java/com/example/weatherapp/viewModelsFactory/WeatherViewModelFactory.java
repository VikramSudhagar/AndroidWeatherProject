package com.example.weatherapp.viewModelsFactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.weatherapp.data.FetchWeather;
import com.example.weatherapp.data.Geocoder;
import com.example.weatherapp.viewModels.WeatherViewModel;

public class WeatherViewModelFactory implements ViewModelProvider.Factory {

    private Geocoder geocoder;
    private FetchWeather fetchWeather;

    public WeatherViewModelFactory(FetchWeather fetchWeather, Geocoder geocoder){
        this.fetchWeather = fetchWeather;
        this.geocoder = geocoder;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(WeatherViewModel.class)) {
            return (T) new WeatherViewModel(fetchWeather, geocoder);
        } else {
            throw new IllegalArgumentException("It's not a WeatherViewModel");
        }
    }
}
