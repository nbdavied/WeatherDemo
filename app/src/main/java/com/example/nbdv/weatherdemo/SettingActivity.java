package com.example.nbdv.weatherdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SettingActivity extends AppCompatActivity {
    EditText etCity;
    Button btConfirm;
    Context context;
    String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();

        //提交按钮监听
        btConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                city=etCity.getText().toString();
                if(city=="")
                {
                    finish();
                    return;
                }


                //输入的城市名称保存到本地
                SharedPreferences sp=context.getSharedPreferences("Preference",MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("city",city);
                editor.commit();

                //将输入的城市名称回传给mainactivity
                Intent data=new Intent();
                data.putExtra("city",city);
                setResult(1,data);

                finish();
            }
        });
    }

    private void init(){
        etCity=(EditText)findViewById(R.id.etCity);
        btConfirm= (Button) findViewById(R.id.btConfirm);


        /*
        *获取本地保存数据
        * 1.保存的当前城市信息
        * 2.是否已经将城市数据下载到数据库
        **/
        context=SettingActivity.this;
        SharedPreferences sp=context.getSharedPreferences("Preference",MODE_PRIVATE);
        city=sp.getString("city","");
        etCity.setText(city);
        boolean isDownloaded=sp.getBoolean("download",false);
        //如果未下载城市数据，则下载
        if(!isDownloaded) {
            DownLoadCityThread downloadCity = new DownLoadCityThread(context);
            downloadCity.start();
            isDownloaded = true;

        }else{
            //如已经下载到数据库，则从数据库读取到列表


        }

    }
}
