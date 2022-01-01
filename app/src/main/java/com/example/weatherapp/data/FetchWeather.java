package com.example.weatherapp.data;
import android.util.Log;

import com.example.weatherapp.model.Weather;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class FetchWeather {
    private static final String TAG = "FetchWeather";
    private static final String Image_URL = "https://openweathermap.org/img/wn/%s@4x.png";
    private static final String Seven_Day_API = "https://api.openweathermap.org/data/2.5/onecall?lat=%s&lon=%s&units=metric&exclude={hourly,minutely}&appid=d71211e60444c33c17cbbd0d36a1f86d";

    public List<Weather> getWeatherForSevenDays(double lat, double lng) {
        System.out.println("in getweather");
        try {
            URL url = new URL(String.format(Seven_Day_API, lat, lng));
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();


            System.out.println("after connection");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            );

            StringBuilder sb = new StringBuilder(1024);
            String tmp;
            while ((tmp = reader.readLine()) != null) {
                sb.append(tmp).append("\n");
            }
            reader.close();
            System.out.println("response :" + sb.toString());
            JSONObject WeatherData = new JSONObject(sb.toString());


            System.out.println("json:" + WeatherData.toString());
            return parseWeeklyJSON(WeatherData);

        }
        catch (Exception e) {
            Log.e(TAG,"error in fetchweather:" + e.getMessage());
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
            return null;
        }

    }

    public List<Weather> parseWeeklyJSON(JSONObject weeklyJSON){
        try{
            JSONArray array = weeklyJSON.getJSONArray("daily");
            List<Weather> weatherData = new ArrayList<>();

            for(int i = 0; i <array.length(); i++ ){
                JSONObject day = array.getJSONObject(i);
                Date weeklyDate = new Date(day.getLong("dt") * 1000);
                SimpleDateFormat weeklyTime = new SimpleDateFormat("MMM dd,  HH:mm:ss");
                String weeklyTemp = String.format("%.2f", day.getJSONObject("temp").getDouble("day")) + " °C";

                JSONObject weatherDay = day.getJSONArray("weather").getJSONObject(0);
                String description = weatherDay.getString("description");
                String weeklyIcon = weatherDay.getString("icon");
                String imageUrl = String.format(Image_URL, weeklyIcon);
                Weather weather = new Weather(weeklyTime.format(weeklyDate), imageUrl, description, weeklyTemp);
                weatherData.add(weather);
            }

            return weatherData;
        }

        catch(Exception e){
            Log.e(TAG, "Error in parsing" + e.getMessage());
            return null;
        }
    }

}
