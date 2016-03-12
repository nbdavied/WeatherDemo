package com.example.nbdv.weatherdemo.Service;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.example.nbdv.weatherdemo.GetWeatherThread;
import com.example.nbdv.weatherdemo.R;

import com.example.nbdv.weatherdemo.WeatherWidgetProvider;
import com.example.nbdv.weatherdemo.json.JsonWeather;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class WidgetUpdateService extends Service {
    private String CityName;
    private String CityId;
    private JsonWeather weather;
    private AppWidgetManager manager;
    private int[] tvWidgetDay = new int[]{
            R.id.tvWidgetDay1, R.id.tvWidgetDay2, R.id.tvWidgetDay3, R.id.tvWidgetDay4, R.id.tvWidgetDay5
    };
    private int[] ivWidgetCondition = new int[]{
            R.id.ivWidgetCondition1, R.id.ivWidgetCondition2, R.id.ivWidgetCondition3, R.id.ivWidgetCondition4, R.id.ivWidgetCondition5
    };
    private int[] tvWidgetTemp = new int[]{
            R.id.tvWidgetTemp1, R.id.tvWidgetTemp2, R.id.tvWidgetTemp3, R.id.tvWidgetTemp4, R.id.tvWidgetTemp5
    };
    private final static int[] dayOfWeek = new int[]{
            R.string.sunday, R.string.monday, R.string.tuesday, R.string.wednsday, R.string.thursday, R.string.friday, R.string.saturday
    };

    public WidgetUpdateService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        manager = AppWidgetManager.getInstance(this);
        //查看本地是否存储城市名称或id，如有则直接载入
        SharedPreferences sharedPreferences = this.getSharedPreferences("Preference", MODE_PRIVATE);
        CityName = sharedPreferences.getString("city", "");
        CityId = sharedPreferences.getString("id", "");
        if (CityId == "" && CityName == "") {
            //id和市名都未设置，则显示未设置状态
            setNull();
        } else if (CityId == "") {
            //id未设置，则根据市名查询天气
            //开启线程获取天气数据
            getWeather(CityName, GetWeatherThread.SEARCH_BY_CITY);

        } else {
            //已存储id和city

            getWeather(CityId, GetWeatherThread.SEARCH_BY_ID);
        }
        return START_NOT_STICKY;
    }

    private void setNull() {

    }

    private void getWeather(String searchString, int searchMode) {
        GetWeatherThread thread = new GetWeatherThread(searchString, handler, searchMode);
        thread.start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Gson gson = new Gson();
            weather = gson.fromJson(msg.getData().getString("weatherString"), JsonWeather.class);
            if (weather != null)
                updateContent();
        }
    };

    private void updateContent() {
        int imageId;
        int cond;
        int temp_max;
        int temp_min;
        String temp;
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        //计算今天星期几
        int todayWeek = c.get(Calendar.DAY_OF_WEEK);
        RemoteViews rViews = new RemoteViews(this.getPackageName(), R.layout.widget_layout);
        for (int i = 0; i < 5; i++) {
            //设置天气状态图片
            cond = weather.serviceVersion[0].daily_forecast[i].cond.code_d;
            rViews.setImageViewResource(ivWidgetCondition[i], JsonWeather.getConditionImage(cond));
            //设置高低气温
            temp_max = weather.serviceVersion[0].daily_forecast[i].tmp.max;
            temp_min = weather.serviceVersion[0].daily_forecast[i].tmp.min;
            temp = temp_min + "°/" + temp_max + "°";
            rViews.setTextViewText(tvWidgetTemp[i], temp);
            //设置星期
            if (i == 0) {
                rViews.setTextViewText(tvWidgetDay[i], this.getString(R.string.today));
            } else {
                //计算i天以后星期几
                //今天1（星期天），i=2，计算得第3天（星期二）
                //今天6（星期五），i=2,计算得第1天（8%7，星期天）
                int dayAfterI = (todayWeek + i) % 7;
                dayAfterI=dayAfterI==0?7:dayAfterI;
                rViews.setTextViewText(tvWidgetDay[i], this.getString(dayOfWeek[dayAfterI-1]));
            }

        }

        manager.updateAppWidget(manager.getAppWidgetIds(new ComponentName(this, WeatherWidgetProvider.class)), rViews);
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
