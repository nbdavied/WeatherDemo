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
    //定义控件
    private ImageButton ibSetting;
    private TextView tvCity;
    private TextView tvResult;

    private String city;    //选定的城市名称
    private String id;      //城市id
    private Handler handler=new Handler();




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
        if(requestCode==1&&resultCode==1)
        {
            city=data.getStringExtra("city");
            tvCity.setText(city);
            HttpThread httpThread=new HttpThread(city,tvResult,handler,HttpThread.SEARCH_BY_CITY);
            httpThread.start();
        }
    }
    /*
    * 初始化程序
    * */
    private void init(){
        //设置控件
        ibSetting=(ImageButton)findViewById(R.id.ibSetting);
        tvCity= (TextView) findViewById(R.id.tvCity);
        tvResult= (TextView) findViewById(R.id.tvResult);

        //查看本地是否存储城市名称或id，如有则直接载入
        SharedPreferences sp=MainActivity.this.getSharedPreferences("Preference",MODE_PRIVATE);
        city=sp.getString("city","");
        id=sp.getString("id","");
        if(id==""&&city=="")
        {
            //id和市名都未设置，则直接转向设置界面
            tvCity.setText("City not set.");
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivityForResult(intent, 1);
        }
        else if(id=="")
        {
            //id未设置，则根据市名查询天气
            tvCity.setText(city);
            //开启线程获取天气数据
            HttpThread httpThread=new HttpThread(city,tvResult,handler,HttpThread.SEARCH_BY_CITY);
            httpThread.start();

        }
        else
        {
            //已存储id和city
            tvCity.setText(city);
            HttpThread httpThread=new HttpThread(city,tvResult,handler,HttpThread.SEARCH_BY_ID);
            httpThread.start();
        }

    }
}
