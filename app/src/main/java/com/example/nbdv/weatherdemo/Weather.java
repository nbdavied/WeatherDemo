package com.example.nbdv.weatherdemo;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by nbdav on 2016/1/25.
 */
public class Weather {
    @SerializedName("HeWeather data service 3.0")
    public WeatherInfo[] serviceVersion;

    public class WeatherInfo{
        public AirQuality aqi;
        public Basic basic;
        public DailyForecast[] daily_forecast;
        public HourlyForecast[] hourly_forecast;
        public Now now;
        public String status;
        public Suggestion suggestion;
    }
    public class AirQuality
    {
        public City city;
        public class City{
            int aqi;
            int co;
            int no2;
            int o3;
            int pm10;
            int pm25;
            String qlty;
            int so2;
        }
    }
    public class Basic
    {
        public String city;
        public  String cnty;
        public String id;
        public String lat;
        public String lon;
        public  Update update;
        public class Update
        {
            public  String loc;
            public String utc;
        }
    }
    public class DailyForecast
    {
        public Astro astro;
        public Cond cond;
        public String date;
        public int hum;
        public String pcpn;
        public int pop;
        public int pres;
        public Tmp tmp;
        public int vis;
        public Wind wind;
        public class Astro
        {
            public String sr;
            public  String ss;
        }
        public class Cond
        {
            public int code_d;
            public int code_n;
            public String txt_d;
            public String txt_n;
        }
        public class Tmp
        {
            public int max;
            public int min;
        }

    }
    public class Wind
    {
        public String deg;
        public  String dir;
        public String sc;
        public int spd;
    }

    public class HourlyForecast
    {
        public String date;
        public int hum;
        public int pop;
        public int pres;
        public int tmp;
        public  Wind wind;

    }
    public class Now
    {
        public  Cond cond;
        public int fl;
        public int hum;
        public String pcpn;
        public int pres;
        public int tmp;
        public  int vis;
        public Wind wind;
        public class Cond
        {
            public int code;
            public String txt;
        }
    }

    public class Suggestion
    {
        public Description comf;
        public Description cw;
        public Description drsg;
        public Description flu;
        public Description sport;
        public  Description trav;
        public Description uv;
        class Description
        {
            public  String brf;
            public  String txt;
        }
    }

    public static int getConditionImage(int cond) {
        int result;
        switch (cond) {
            case 100:
                result = R.drawable.c100;
                break;
            case 101:
                result = R.drawable.c101;
                break;
            case 102:
                result = R.drawable.c102;
                break;
            case 103:
                result = R.drawable.c103;
                break;
            case 104:
                result = R.drawable.c104;
                break;
            default:
                result = R.drawable.c100;
        }
        return result;
    }

    ;
}







