package com.example.nbdv.weatherdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import static android.widget.AdapterView.*;

public class SettingActivity extends AppCompatActivity {
    EditText etCity;
    Button btConfirm;
    Spinner spProvince;
    Spinner spCity;
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
        spProvince= (Spinner) findViewById(R.id.spProvince);
        spCity= (Spinner) findViewById(R.id.spCity);

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
            SQLiteDatabase db=context.openOrCreateDatabase("weather.db", MODE_PRIVATE, null);
            Cursor cursor=db.rawQuery("select distinct prov from city", null);
            List<String> provList=new ArrayList<String>();
            while(cursor.moveToNext()){
                provList.add(cursor.getString(0));
            }
            db.close();
            ArrayAdapter adapter=new ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item,provList);
            spProvince.setAdapter(adapter);

            spProvince.setOnItemSelectedListener(provinceSelectedListener);
        }

    }
    /*
    * 选择省份后，查找城市列表
    * */
    private OnItemSelectedListener provinceSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Spinner spProvince= (Spinner) parent;
            String prov=spProvince.getSelectedItem().toString();
            //打开并查找数据库（根据省份名称）
            SQLiteDatabase db=context.openOrCreateDatabase("weather.db", MODE_PRIVATE, null);
            Cursor cursor=db.rawQuery("select city from city where prov=?",new String[]{prov});
            List<String> cityList=new ArrayList<String>();
            while(cursor.moveToNext()){
                cityList.add(cursor.getString(0));
            }
            db.close();
            ArrayAdapter adapter=new ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item,cityList);
            spCity.setAdapter(adapter);
            spCity.setOnItemSelectedListener(citySelectedListener);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private OnItemSelectedListener citySelectedListener=new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String city=parent.getSelectedItem().toString();
            etCity.setText(city);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}
