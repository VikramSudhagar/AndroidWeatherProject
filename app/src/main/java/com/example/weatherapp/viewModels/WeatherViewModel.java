package com.example.weatherapp.viewModels;
import android.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weatherapp.data.FetchWeather;
import com.example.weatherapp.data.Geocoder;
import com.example.weatherapp.model.Weather;

import java.util.List;

public class WeatherViewModel extends ViewModel {

    private Geocoder geocoder;
    private FetchWeather fetchWeather;

    public WeatherViewModel(FetchWeather fetchWeather, Geocoder geocoder){
        this.fetchWeather = fetchWeather;
        this.geocoder = geocoder;
    }

    public MutableLiveData<List<Weather>> futureWeatherLiveData = new MutableLiveData<>();
    public MutableLiveData<Weather> currentWeatherLiveData = new MutableLiveData<>();
    public MutableLiveData<String> currentLocation = new MutableLiveData<>();

    public void getWeather(final String city){
        final Pair<Double,Double> cityLatLon = geocoder.getLatLngFromCity(city);
                new Thread() {
            public void run() {

                List<Weather> weatherData = fetchWeather.getWeatherForSevenDays(cityLatLon.first, cityLatLon.second);
                currentWeatherLiveData.postValue(weatherData.get(0));
                futureWeatherLiveData.postValue(weatherData.subList(1, weatherData.size()));
            }
        }.start();


    }

    public void getCurrentLocation(double latitude, double longitude) {
        Pair<Double, Double> coordinates = new Pair<>(latitude, longitude);
        currentLocation.postValue(geocoder.getCityFromLatLng(coordinates));
    }


}
