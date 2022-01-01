package com.example.weatherapp.data;


import android.content.Context;
import android.location.Address;
import android.util.Log;
import android.util.Pair;
import java.util.List;
import java.util.Locale;

public class Geocoder {

    private static final String TAG = "geocoder";
    private android.location.Geocoder geocoder;


    public Geocoder(Context context){
        geocoder = new android.location.Geocoder(context, Locale.getDefault());
    }

    public String getCityFromLatLng(Pair<Double, Double> location){
        System.out.println("Location is " + location.first + ", " + location.second);

        try {
            List<Address> address = geocoder.getFromLocation(location.first, location.second,  1);
            String city  = address.get(0).getLocality();
            String country = address.get(0).getCountryName();
            System.out.println(city + ", " + country);
            return city.concat(", ").concat(country);
        }
        catch(Exception e){
            Log.e(TAG,"Error in geocoding: " + e.getMessage());
            return null;

        }
    }

    public Pair<Double, Double> getLatLngFromCity(String city){

        try {
            List<Address> address = geocoder.getFromLocationName(city,1);
            Double lat  = address.get(0).getLatitude();
            Double lng = address.get(0).getLongitude();
            return new Pair<>(lat, lng);
        }
        catch(Exception e){
            Log.e(TAG,"Error in geocoding: " + e.getMessage());
            return null;

        }
    }
}
