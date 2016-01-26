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

        etCity=(EditText)findViewById(R.id.etCity);
        btConfirm= (Button) findViewById(R.id.btConfirm);


        //从本地获取保存的城市名称
        context=SettingActivity.this;
        SharedPreferences sp=context.getSharedPreferences("Preference",MODE_PRIVATE);
        city=sp.getString("city","");
        etCity.setText(city);

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
}
