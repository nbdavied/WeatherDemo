package com.example.nbdv.weatherdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;


public class MainActivity extends AppCompatActivity {
    private ImageButton ibSetting;
    private TextView tvCity;
    private TextView tvResult;

    private String city;
    private Handler handler=new Handler();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==1)
        {
            city=data.getStringExtra("city");
            tvCity.setText(city);
            HttpThread httpThread=new HttpThread(city,tvResult,handler);
            httpThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ibSetting=(ImageButton)findViewById(R.id.ibSetting);
        tvCity= (TextView) findViewById(R.id.tvCity);
        tvResult= (TextView) findViewById(R.id.tvResult);


        //点击设置按钮跳转
        ibSetting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivityForResult(intent, 1);
            }
        });



        //读取城市名称
        SharedPreferences sp=MainActivity.this.getSharedPreferences("Preference",MODE_PRIVATE);
        city=sp.getString("city","");
        if(city=="")
            return;
        tvCity.setText(city);


        //开启线程获取天气数据
        HttpThread httpThread=new HttpThread(city,tvResult,handler);
        httpThread.start();

/*        Weather weather=new Weather();
        String jsonString="{\"HeWeather data service 3.0\":\"asdf\"}";
        Gson gson=new Gson();
        weather=gson.fromJson(jsonString,Weather.class);
        tvResult.setText(weather.getServiceVersion());*/


    }


}
