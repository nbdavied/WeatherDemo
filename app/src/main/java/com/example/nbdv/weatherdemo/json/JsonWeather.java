package com.example.nbdv.weatherdemo.json;

import com.example.nbdv.weatherdemo.R;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by nbdav on 2016/1/25.
 */
public class JsonWeather {
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
            public int aqi;
            public int co;
            public int no2;
            public int o3;
            public int pm10;
            public int pm25;
            public String qlty;
            public int so2;
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
            case 200:
                result = R.drawable.c200;
                break;
            case 300:
                result = R.drawable.c300;
                break;
            case 301:
                result = R.drawable.c301;
                break;
            case 302:
                result = R.drawable.c302;
                break;
            case 303:
                result = R.drawable.c303;
                break;
            case 304:
                result = R.drawable.c304;
                break;
            case 305:
                result = R.drawable.c305;
                break;
            case 306:
                result = R.drawable.c306;
                break;
            case 307:
                result = R.drawable.c307;
                break;
            case 308:
                result = R.drawable.c308;
                break;
            case 309:
                result = R.drawable.c309;
                break;

            default:
                result = R.drawable.c100;
        }
        return result;
    }

    ;
}







