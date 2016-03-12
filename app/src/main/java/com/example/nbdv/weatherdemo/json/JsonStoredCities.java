package com.example.nbdv.weatherdemo.json;

/**
 * Created by nbdav on 2016/3/12.
 */
public class JsonStoredCities {
    public City storedCity;
    public class City{
        public String cityName;
        public String cityID;
        public JsonWeather weatherInfo;
    }
}
