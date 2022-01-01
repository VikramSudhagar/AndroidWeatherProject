package com.example.weatherapp.view;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weatherapp.R;
import com.example.weatherapp.data.FetchWeather;
import com.example.weatherapp.model.Weather;
import com.example.weatherapp.viewModels.WeatherViewModel;
import com.example.weatherapp.viewModelsFactory.WeatherViewModelFactory;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "WEATHER";
    private static final int REASON_CODE = 225;
    private static final String Image_URL = "https://openweathermap.org/img/wn/%s@4x.png";
    private RecyclerView recyclerView;

    private static final String Seven_Day_API = "https://api.openweathermap.org/data/2.5/onecall?lat=%s&lon=%s&units=metric&exclude={hourly,minutely}&appid=d71211e60444c33c17cbbd0d36a1f86d";
    private WeatherViewModel weatherViewModel;
    private FetchWeather fetchWeather;
    EditText cityText;
    TextView temperatureText;
    TextView responseText;
    TextView timeText;
    ImageView weatherImage;
    Handler handler;
    LocationManager locationManager;
    MyAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fetchWeather = new FetchWeather();
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        weatherViewModel = new ViewModelProvider(this, new WeatherViewModelFactory(fetchWeather, new com.example.weatherapp.data.Geocoder(this))).get(WeatherViewModel.class);
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

        cityText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                System.out.println(keyEvent.toString());
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    setWeatherResponse(cityText.getText().toString());
                }
                return false;
            }
        });

        weatherViewModel.currentWeatherLiveData.observe(this, new Observer<Weather>() {
            @Override
            public void onChanged(Weather weather) {
                timeText.setText(weather.getDate());
                temperatureText.setText(weather.getTemperature());
                responseText.setText(weather.getDescription());
                Picasso.get().load(weather.getImageUrl()).into(weatherImage);
            }
        });

        weatherViewModel.futureWeatherLiveData.observe(this, new Observer<List<Weather>>() {
            @Override
            public void onChanged(List<Weather> weathers) {
                mAdapter = new MyAdapter();
                recyclerView.setAdapter(mAdapter);
                Weather[] futureValues = weathers.toArray(new Weather[0]);
                mAdapter.setDataset(futureValues);
            }
        });

        weatherViewModel.currentLocation.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String location) {
                cityText.setText(location);
                setWeatherResponse(location);
            }
        });
        getLastLocation();

    }

    private void setWeatherResponse(final String city){
        weatherViewModel.getWeather(city);
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
            getLastLocation();
        }
    }

    private void getLastLocation() {
        if (checkLocationPermission()) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null ){
                weatherViewModel.getCurrentLocation(location.getLatitude(), location.getLongitude());
            } else {
                String city = "Toronto, Canada";
                weatherViewModel.getWeather(city);
                cityText.setText(city);
            }
        }
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
}