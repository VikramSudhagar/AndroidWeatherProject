package com.example.weatherapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "WEATHER";
    private static final int REASON_CODE = 225;
    private static final String Image_URL = "https://openweathermap.org/img/wn/%s@4x.png";
    private RecyclerView recyclerView;

    private static final String Seven_Day_API = "https://api.openweathermap.org/data/2.5/onecall?lat=%s&lon=%s&units=metric&exclude={hourly,minutely}&appid=d71211e60444c33c17cbbd0d36a1f86d";
//    private static final String Seven_Day_API = "https://api.openweathermap.org/data/2.5/forecast?q=%s&units=metric&APPID=d71211e60444c33c17cbbd0d36a1f86d&cnt=7";
    EditText cityText;
    TextView temperatureText;
    TextView responseText;
    TextView timeText;
    ImageView weatherImage;
    Handler handler;
    LocationManager locationManager;
    Geocoder geocoder;
    Weather[] myDataset;
    MyAdapter mAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyAdapter();


        cityText = findViewById(R.id.city);
        responseText = findViewById(R.id.response);
        temperatureText = findViewById(R.id.temperature);
        timeText = findViewById(R.id.time);
        weatherImage = findViewById(R.id.imageView);
        handler = new Handler();
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        geocoder = new Geocoder(this, Locale.getDefault());

        cityText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                System.out.println(keyEvent.toString());
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    Pair<Double,Double> cityLatLon = getLatLngFromCity(cityText.getText().toString());

                    setWeatherResponse(cityLatLon);

                }
                return false;
            }
        });


        Pair<Double, Double> locationCoordinates  = getLastLocation();

        cityText.setText(getCityFromLatLng(locationCoordinates));
        setWeatherResponse(locationCoordinates);


    }

    private void setWeatherResponse(final Pair<Double, Double> location ){
        new Thread() {
            public void run() {
                final JSONObject jsonResult = getWeather(location.first, location.second);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        parseWeeklyJSON(jsonResult);
                    }
                });
            }
        }.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("Start of main activity");
    }

    protected boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;

        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REASON_CODE);
            return false;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REASON_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setWeatherResponse(getLastLocation());
        }
    }
    private String getCityFromLatLng(Pair<Double, Double> location){
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

    private Pair<Double, Double> getLatLngFromCity(String city){

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

    private Pair<Double, Double> getLastLocation() {

        String result = "Toronto, Canada";

        if (checkLocationPermission()) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                return new Pair<>(lat, lng);
            }


        }

        return getLatLngFromCity(result);


    }


    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("Pause of main activity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Resume of main activity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("Destroy of main activity");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("Stop of main activity");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public JSONObject getWeather(double lat, double lng) {
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
            return WeatherData;

        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
            return null;
        }

    }

    public void parseWeeklyJSON(JSONObject weeklyJSON){
        try{
            JSONArray array = weeklyJSON.getJSONArray("daily");
            JSONObject current = array.getJSONObject(0);
            JSONObject temp = current.getJSONObject("temp");
            JSONObject response = current.getJSONArray("weather").getJSONObject(0);
            Date date = new Date(current.getLong("dt") * 1000);
            SimpleDateFormat time = new SimpleDateFormat("MMM dd,  HH:mm:ss");
            timeText.setText(time.format(date));
            temperatureText.setText(temp.getDouble("day")+ " °C");
            responseText.setText( response.getString("description"));
            String icon = response.getString("icon");
            String ImageURL = String.format(Image_URL, icon);
            Picasso.get().load(ImageURL).into(weatherImage);
            myDataset = new Weather [array.length() - 1];

            for(int i = 1; i <array.length(); i++ ){
                JSONObject day = array.getJSONObject(i);
                Date weeklyDate = new Date(day.getLong("dt") * 1000);
                SimpleDateFormat weeklyTime = new SimpleDateFormat("MMM dd,  HH:mm:ss");
                String weeklyTemp = String.format("%.2f", day.getJSONObject("temp").getDouble("day")) + " °C";

                JSONObject weatherDay = day.getJSONArray("weather").getJSONObject(0);
                String description = weatherDay.getString("description");
                String weeklyIcon = weatherDay.getString("icon");
                String imageUrl = String.format(Image_URL, weeklyIcon);
                Weather weather = new Weather(weeklyTime.format(weeklyDate), imageUrl, description, weeklyTemp);

                myDataset[i- 1] = weather;
            }
            mAdapter = new MyAdapter();
            recyclerView.setAdapter(mAdapter);
            mAdapter.setDataset(myDataset);

        }

        catch(Exception e){
            Log.e(TAG, "Error in parsing" + e.getMessage());

        }
    }

}