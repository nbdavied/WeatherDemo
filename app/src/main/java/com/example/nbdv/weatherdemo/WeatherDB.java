package com.example.nbdv.weatherdemo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
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

    public void PrepareDatabase()
    {
        DownLoadCityThread thread=new DownLoadCityThread(context,handler);
        thread.start();
    }

    /*
    * 从数据库读取省份名字和id，返回省份列表
    * */
    public List<Province> getProvinceList(){
        db=context.openOrCreateDatabase("weather.db",Context.MODE_PRIVATE,null);
        cursor=db.rawQuery("select distinct prov from city", null);
        provinceList=new ArrayList<Province>();
        while (cursor.moveToNext()){
            provinceList.add(new Province(cursor.getString(0)));
        }
        db.close();
        return provinceList;
    }

    public List<City> getCityList(String provinceName){
        db=context.openOrCreateDatabase("weather.db",Context.MODE_PRIVATE,null);
        cityList=new ArrayList<City>();
        cursor=db.rawQuery("select city,id from city where prov=?",new String[]{provinceName});
        while(cursor.moveToNext()){
            cityList.add(new City(cursor.getString(0),cursor.getString(1)));
        }
        db.close();
        return cityList;
    }
    public String getProvinceNameById(String id){
        String provinceName;
        db=context.openOrCreateDatabase("weather.db",Context.MODE_PRIVATE,null);
        cityList=new ArrayList<City>();
        cursor=db.rawQuery("select prov from city where id=?",new String[]{id});
        if(cursor.getCount()>1){
            Log.e("error","province count>1 while id is set");
        }
        cursor.moveToFirst();
        provinceName=cursor.getString(0);
        db.close();
        return provinceName;
    }
}
