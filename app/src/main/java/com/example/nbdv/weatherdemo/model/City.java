package com.example.nbdv.weatherdemo.model;

/**
 * Created by nbdav on 2016/2/4.
 */
public class City {
    private String cityName;
    private String cityId;
    private double lon;
    private double lat;

    public City() {
    }

    public City(String cityName, String cityId) {
        this.cityId = cityId;
        this.cityName = cityName;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityId() {
        return cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
