package com.example.weatherapp.model;


public class Weather {

    private final String date;
    private String imageUrl;
    private String description;
    private String temperature;

    public Weather(String date, String icon, String description, String temperature) {
        this.date = date;
        this.imageUrl = icon;
        this.description = description;
        this.temperature = temperature;
    }


    public String getDate() {
        return date;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getTemperature() {
        return temperature;
    }

}
