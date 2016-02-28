package com.example.nbdv.weatherdemo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Handler;
import android.test.LoaderTestCase;
import android.util.Log;

import com.example.nbdv.weatherdemo.model.City;
import com.example.nbdv.weatherdemo.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nbdav on 2016/2/4.
 * 处理数据库操作事务
 */
public class WeatherDB {
    private final static double RANGE = 1;
    private Context context;
    private Handler handler;
    private List<Province> provinceList;
    private List<City> cityList;
    private SQLiteDatabase db;
    private Cursor cursor;

    public WeatherDB(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    public void PrepareDatabase() {
        DownLoadCityThread thread = new DownLoadCityThread(context, handler);
        thread.start();
    }

    /*
    * 从数据库读取省份名字和id，返回省份列表
    * */
    public List<Province> getProvinceList() {
        db = context.openOrCreateDatabase("weather.db", Context.MODE_PRIVATE, null);
        cursor = db.rawQuery("select distinct prov from city", null);
        provinceList = new ArrayList<Province>();
        while (cursor.moveToNext()) {
            provinceList.add(new Province(cursor.getString(0)));
        }
        db.close();
        return provinceList;
    }

    public List<City> getCityList(String provinceName) {
        db = context.openOrCreateDatabase("weather.db", Context.MODE_PRIVATE, null);
        cityList = new ArrayList<City>();
        cursor = db.rawQuery("select city,id from city where prov=?", new String[]{provinceName});
        while (cursor.moveToNext()) {
            cityList.add(new City(cursor.getString(0), cursor.getString(1)));
        }
        db.close();
        return cityList;
    }

    public String getProvinceNameById(String id) {
        String provinceName;
        db = context.openOrCreateDatabase("weather.db", Context.MODE_PRIVATE, null);
        cityList = new ArrayList<City>();
        cursor = db.rawQuery("select prov from city where id=?", new String[]{id});
        if (cursor.getCount() > 1) {
            Log.e("error", "province count>1 while id is set");
        }
        cursor.moveToFirst();
        provinceName = cursor.getString(0);
        db.close();
        return provinceName;
    }

    //根据location查询最近的城市，返回城市id，如果未查到返回null
    public City getCityByLocation(Location location) {
        db = context.openOrCreateDatabase("weather.db", Context.MODE_PRIVATE, null);
        String id = "";
        String name="";
        City city=new City(name,id);
        double shortestDistance = 0;
        double distance = 0;
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        String upperLat = String.valueOf(lat + RANGE);
        String lowerLat = String.valueOf(lat - RANGE);
        String upperLon = String.valueOf(lon + RANGE);
        String lowerLon = String.valueOf(lon - RANGE);
        cursor = db.rawQuery("select city,id,lon,lat from city where lon<? and lon>? and lat <? and lat>?", new String[]{upperLon, lowerLon, upperLat, lowerLat,});
        if (cursor.moveToFirst()) {
            //
            distance = Math.sqrt(Math.pow(cursor.getDouble(2) - lon, 2) + Math.pow(cursor.getDouble(3) - lat, 2));
            shortestDistance = distance;
            id = cursor.getString(1);
            name=cursor.getString(0);
            Log.i("sqrt", String.valueOf(Math.sqrt(Math.pow(3, 2) + Math.pow(4, 2))));
        }
        while (cursor.moveToNext()) {
            Log.i("city around", cursor.getString(0) + "," + cursor.getString(1));
            distance = Math.sqrt(Math.pow(cursor.getDouble(2) - lon, 2) + Math.pow(cursor.getDouble(3) - lat, 2));
            if (distance < shortestDistance) {
                shortestDistance = distance;
                id = cursor.getString(1);
                name=cursor.getString(0);
                Log.i("city", cursor.getString(0));
            }

        }
        db.close();
        city.setCityId(id);
        city.setCityName(name);
        return city;

    }
}
