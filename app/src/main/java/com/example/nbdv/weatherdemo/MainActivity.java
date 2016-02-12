package com.example.nbdv.weatherdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;


public class MainActivity extends AppCompatActivity {
    //定义控件
    private ImageButton ibSetting;
    private TextView tvCity;
    private TextView tvResult;

    private String city;    //选定的城市名称
    private String id;      //城市id
    private Weather weather;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Gson gson = new Gson();
            weather = gson.fromJson(msg.getData().getString("weatherString"), Weather.class);
            if(weather!=null)
                updateContent();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化界面
        init();

        //点击设置按钮跳转
        ibSetting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    /*
    * 处理设置页面返回信息
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            //根据id查询
            city = data.getStringExtra("city");
            tvCity.setText(city);
            id = data.getStringExtra("id");
            getWeather(id, GetWeatherThread.SEARCH_BY_ID);
        } else if (requestCode == 1 && resultCode == 2) {
            //根据名字查询
            city = data.getStringExtra("city");
            tvCity.setText(city);
            getWeather(city, GetWeatherThread.SEARCH_BY_CITY);
        }
    }

    /*
    * 初始化程序
    * */
    private void init() {
        //设置控件
        ibSetting = (ImageButton) findViewById(R.id.ibSetting);
        tvCity = (TextView) findViewById(R.id.tvCity);
        tvResult = (TextView) findViewById(R.id.tvResult);

        weather = new Weather();
        //查看本地是否存储城市名称或id，如有则直接载入
        SharedPreferences sp = MainActivity.this.getSharedPreferences("Preference", MODE_PRIVATE);
        city = sp.getString("city", "");
        id = sp.getString("id", "");
        if (id == "" && city == "") {
            //id和市名都未设置，则直接转向设置界面
            tvCity.setText("City not set.");
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivityForResult(intent, 1);
        } else if (id == "") {
            //id未设置，则根据市名查询天气
            tvCity.setText(city);
            //开启线程获取天气数据
            getWeather(city, GetWeatherThread.SEARCH_BY_CITY);

        } else {
            //已存储id和city
            tvCity.setText(city);
            getWeather(id, GetWeatherThread.SEARCH_BY_ID);
        }

    }

    private void getWeather(String searchString, int searchMode) {
        GetWeatherThread thread = new GetWeatherThread(searchString, handler, searchMode);
        thread.start();
    }

    private void updateContent() {
        if(weather.serviceVersion==null)
        {
            Log.e("error","no weather data.");
            return;
        }
        if (weather.serviceVersion[0].status.equals("ok")) {
            String condition_d = weather.serviceVersion[0].daily_forecast[0].cond.txt_d;
            String condition_n = weather.serviceVersion[0].daily_forecast[0].cond.txt_n;
            int tmp_min = weather.serviceVersion[0].daily_forecast[0].tmp.min;
            int tmp_max = weather.serviceVersion[0].daily_forecast[0].tmp.max;
            String air_quality;
            if (weather.serviceVersion[0].aqi != null) {
                air_quality = weather.serviceVersion[0].aqi.city.qlty;
            } else {
                air_quality = "no data";
            }

            String weatherDesc = "白天天气：" + condition_d + "\n"
                    + "夜晚天气：" + condition_n + "\n"
                    + "气温：" + tmp_min + " ~ " + tmp_max + "\n"
                    + "空气质量：" + air_quality;
            tvResult.setText(weatherDesc);
        } else
            tvResult.setText(weather.serviceVersion[0].status);
    }
}
