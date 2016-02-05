package com.example.nbdv.weatherdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
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
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.example.nbdv.weatherdemo.model.City;
import com.example.nbdv.weatherdemo.model.Province;

import java.util.ArrayList;
import java.util.List;

import static android.widget.AdapterView.*;

public class SettingActivity extends AppCompatActivity {
    private EditText etCity;
    private TextView progressHint;
    private Button btConfirm;
    private Spinner spProvince;
    private Spinner spCity;
    private Context context;
    private String cityName;
    private String id;
    private String savedCityName;
    private String savedCityId;
    private List<Province> provinceList;
    private List<City> cityList;
    private List<String> provinceNameList;
    private List<String> cityNameList;
    private ArrayAdapter provinceAdapter;
    private ArrayAdapter cityAdapter;
    private WeatherDB weatherDB;
    private City chosenCity;
    private boolean needSelectCity=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();



    }

    private void init(){
        context=SettingActivity.this;
        etCity=(EditText)findViewById(R.id.etCity);
        btConfirm= (Button) findViewById(R.id.btConfirm);
        spProvince= (Spinner) findViewById(R.id.spProvince);
        spCity= (Spinner) findViewById(R.id.spCity);
        progressHint= (TextView) findViewById(R.id.progressHint);
        id="";
        weatherDB=new WeatherDB(context,handler);
        provinceNameList=new ArrayList<String>();
        cityNameList=new ArrayList<String>();


        provinceAdapter=new ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item,provinceNameList);
        spProvince.setAdapter(provinceAdapter);
        cityAdapter=new ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item,cityNameList);
        spCity.setAdapter(cityAdapter);
        /*
        *获取本地保存数据
        * 1.保存的当前城市信息
        * 2.是否已经将城市数据下载到数据库
        **/

        SharedPreferences sp=context.getSharedPreferences("Preference", MODE_PRIVATE);
        savedCityName=sp.getString("city","");
        savedCityId=sp.getString("id","");
        boolean isDownloaded=sp.getBoolean("download",false);


        //如果未下载城市数据，则下载
        if(!isDownloaded) {
            weatherDB.PrepareDatabase();


        }else{
            //如已经下载到数据库，则从数据库读取到列表

            updateSpinner();

        }
        spProvince.setOnItemSelectedListener(provinceSelectedListener);
        spCity.setOnItemSelectedListener(citySelectedListener);

        btConfirm.setOnClickListener(confirmButtonListener);
    }

    //提交按钮监听
    private OnClickListener confirmButtonListener =new OnClickListener() {
        @Override
        public void onClick(View v) {
            cityName=etCity.getText().toString();
            if(etCity.getText().toString().equals(chosenCity.getCityName()))
            {
                //输入的城市名称、id保存到本地
                SharedPreferences sp=context.getSharedPreferences("Preference",MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("id",chosenCity.getCityId());
                editor.putString("city",chosenCity.getCityName());
                editor.commit();
                //将输入的城市名称回传给mainactivity
                Intent data=new Intent();
                data.putExtra("id", chosenCity.getCityId());
                data.putExtra("city",chosenCity.getCityName());
                setResult(1, data);//1---根据id查询


            }else
            {
                //输入的城市名称保存到本地
                SharedPreferences sp=context.getSharedPreferences("Preference",MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.remove("id");
                editor.putString("city",cityName);
                editor.commit();
                Intent data=new Intent();
                data.putExtra("city", cityName);
                setResult(2, data);//2---根据名称查询


            }

            finish();
        }
    };
    /*
    * 选择省份后，查找城市列表
    * */

    private OnItemSelectedListener provinceSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Spinner spProvince= (Spinner) parent;
            String prov=spProvince.getSelectedItem().toString();
            showCityList(prov);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private OnItemSelectedListener citySelectedListener=new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String city=parent.getSelectedItem().toString();
            chosenCity=cityList.get(parent.getSelectedItemPosition());
            etCity.setText(city);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }


    };

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1)
            {
                //开始加载
                progressHint.setVisibility(VISIBLE);
            }
            if(msg.what==2) {
                //加载完成，将数据库置为已下载状态，并更新界面
                SharedPreferences sp = context.getSharedPreferences("Preference", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("download", true);
                editor.commit();
                updateSpinner();
                progressHint.setVisibility(GONE);
            }
            if(msg.what==3){
                //数据加载失败
                progressHint.setText(" (＞﹏＜) \n你的网络是不是有问题啊亲");
            }
        }

    };
    /*
    * 刷新spinner控件
    * 从数据库读入省份列表，根据选择的省份读取城市列表
    * */
    private void updateSpinner(){
        showProvinceList();
        if(savedCityId!=""){
            //当已经保存了id，则直接在spinner选中保存的城市
            spProvince.setSelection(provinceNameList.indexOf(weatherDB.getProvinceNameById(savedCityId)));
            needSelectCity=true;
        }else if(savedCityName!=""){
            //当未保存id，只保存了城市名称，则尝试搜索城市，如果有则在spinner中选择
        }
    }
    private void showProvinceList()
    {
        provinceList=weatherDB.getProvinceList();
        provinceNameList.clear();
        for (Province province:provinceList) {
            provinceNameList.add(province.getProvinceName());
        }
        provinceAdapter.notifyDataSetChanged();

    }
    private void showCityList(String provinceName){
        cityList=weatherDB.getCityList(provinceName);
        cityNameList.clear();
        for(City city:cityList){
            cityNameList.add(city.getCityName());
        }

        cityAdapter.notifyDataSetChanged();
        if(needSelectCity)
        {
            spCity.setSelection(cityNameList.indexOf(savedCityName));
            needSelectCity=false;
        }
    }
    /**
     * 根据值, 设置spinner默认选中:
     * @param spinner
     * @param value
     */
    private int getSpinnerItemByValue(Spinner spinner,String value){
        SpinnerAdapter apsAdapter= spinner.getAdapter(); //得到SpinnerAdapter对象
        int k= apsAdapter.getCount();
        for(int i=0;i<k;i++){
            if(value.equals(apsAdapter.getItem(i).toString())){
                return i;
            }
        }
        return -1;
    }
}
