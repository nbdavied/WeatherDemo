package com.example.nbdv.weatherdemo.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nbdv.weatherdemo.json.JsonCities;
import com.example.nbdv.weatherdemo.model.City;

/**
 * Created by nbdav on 2016/3/12.
 */
public class DataStore {
    private final static String DEFAULT_PREFERENCE="Preference";
    //添加选择的城市
    public static void addCity(Context context,City city){
        SQLiteDatabase db=context.openOrCreateDatabase("weather.db",Context.MODE_PRIVATE,null);
        db.execSQL("insert into storedcity values(?,?)",new String[]{city.getCityName(),city.getCityId()});
        db.close();
    }
    public static void addCity(Context context,String cityName,String cityID){
        SQLiteDatabase db=context.openOrCreateDatabase("weather.db",Context.MODE_PRIVATE,null);
        db.execSQL("insert into storedcity values(?,?)",new String[]{cityName,cityID});
        db.close();
    }

    //删除城市
    public static void deleteCity(Context context,City city){
        //删除数据库信息
        SQLiteDatabase db=context.openOrCreateDatabase("weather.db",Context.MODE_PRIVATE,null);
        db.execSQL("delete from storedcity where id=?", new String[]{city.getCityId()});
        db.close();
        //删除文件中存储的天气信息
        SharedPreferences sp=context.getSharedPreferences(DEFAULT_PREFERENCE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.remove(city.getCityId());
        editor.commit();
    }
    public static void deleteCity(Context context,String cityID){
        //删除数据库信息
        SQLiteDatabase db=context.openOrCreateDatabase("weather.db",Context.MODE_PRIVATE,null);
        db.execSQL("delete from storedcity where id=?",new String[]{cityID});
        db.close();
        SharedPreferences sp=context.getSharedPreferences(DEFAULT_PREFERENCE,Context.MODE_PRIVATE);
        //删除文件中存储的天气信息
        SharedPreferences.Editor editor=sp.edit();
        editor.remove(cityID);
        editor.commit();
    }

    //存储已下载的天气
    public static void storeWeather(Context context,String cityID,String jsonWeather){
        SharedPreferences sp=context.getSharedPreferences(DEFAULT_PREFERENCE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(cityID, jsonWeather);
        editor.commit();

    }

    //初始化数据库表
    public static void initDatabase(Context context){
        SQLiteDatabase db=context.openOrCreateDatabase("weather.db", Context.MODE_PRIVATE, null);
        //创建新表
        db.execSQL("DROP TABLE IF EXISTS city");
        db.execSQL("CREATE TABLE city(city VARCHAR,cnty VARCHAR,id VARCHAR PRIMARY KEY,lat float,lon float,prov VARCHAR)");
        db.execSQL("DROP TABLE IF EXISTS storedcity");
        db.execSQL("CREATE TABLE storedcity(city VARCHAR,id VARCHAR)");
        db.close();
        //存储数据库已设置标志
        SharedPreferences sp=context.getSharedPreferences(DEFAULT_PREFERENCE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean("isDatabaseSetted",true);
        editor.commit();
    }

    //将全部城市数据存入数据库
    public static void loadCityList(Context context,JsonCities cities){
        SQLiteDatabase db=context.openOrCreateDatabase("weather.db",Context.MODE_PRIVATE,null);

        //创建新表
        db.execSQL("DROP TABLE IF EXISTS city");
        db.execSQL("CREATE TABLE city(city VARCHAR,cnty VARCHAR,id VARCHAR PRIMARY KEY,lat float,lon float,prov VARCHAR)");
        for (JsonCities.CityInfo cityInfo:cities.city_info
                ) {
            //执行插入语句,将城市数据插入数据库
            db.execSQL("insert into city values(?,?,?,?,?,?)",new Object[]{cityInfo.city,cityInfo.cnty,cityInfo.id,cityInfo.lat,cityInfo.lon,cityInfo.prov});
        }
        db.close();
    }

    //获得数据库中已添加的城市数组
    public static City[] getStoredCities(Context context){
        SQLiteDatabase db=context.openOrCreateDatabase("weather.db", Context.MODE_PRIVATE, null);
        Cursor cursor=db.rawQuery("select * from storedcity", null);
        int count=cursor.getCount();
        City[] cities=new City[count];
        City city;
        int i=0;
        while(cursor.moveToNext()){
            city=new City(cursor.getString(0),cursor.getString(1));
            cities[i]=city;
            i++;
        }
        db.close();
        return cities;
    }

    //判断数据库是否已经初始化
    public static boolean isDatabaseSetted(Context context)
    {
        SharedPreferences sp=context.getSharedPreferences(DEFAULT_PREFERENCE,Context.MODE_PRIVATE);
        boolean DatabaseSetted=sp.getBoolean("isDatabaseSetted",false);
        if(DatabaseSetted)
            return true;
        else
            return false;
    }

}
