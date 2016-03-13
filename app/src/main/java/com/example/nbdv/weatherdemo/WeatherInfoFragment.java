package com.example.nbdv.weatherdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nbdv.weatherdemo.View.LineChart;
import com.example.nbdv.weatherdemo.json.JsonWeather;
import com.example.nbdv.weatherdemo.model.City;
import com.google.gson.Gson;


public class WeatherInfoFragment extends Fragment {
    private TextView tvCity;
    private TextView tvTemp;
    private TextView tvTempRange;
    private TextView tvAirQua;
    private TextView tvPM25;
    private ImageView ivCond;
    private LineChart lineChart;
    private String CityName;    //选定的城市名称
    private String CityID;      //城市id
    private JsonWeather weather;
    private boolean viewCreated=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_info, container, false);
        tvCity = (TextView) view.findViewById(R.id.tvCity);
        tvTemp= (TextView) view.findViewById(R.id.tvTemp);
        viewCreated=true;
        Log.i("info","fragment onCreateView");
        return view;
    }

    public WeatherInfoFragment() {
    }

    public void setFragmentCity(City city) {
        CityName=city.getCityName();
        CityID=city.getCityId();
        refreshData();
    }

    public void setFragmentCity(String id,String name){
        CityName=name;
        CityID=id;
        refreshData();
    }

    public void refreshData(){
        GetWeatherThread getWeather=new GetWeatherThread(CityID,handler,GetWeatherThread.SEARCH_BY_ID);
        getWeather.start();
    }

    private void updateContent(){
        tvCity.setText(CityName);
        int curTmp=weather.serviceVersion[0].now.tmp;
        tvTemp.setText(curTmp + "℃");
    }

    //处理api数据并刷新ui
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Gson gson=new Gson();
            weather = gson.fromJson(msg.getData().getString("weatherString"), JsonWeather.class);
            if(viewCreated)
                updateContent();

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Log.i("info","fragment onresume ");
        if(weather!=null)
            updateContent();

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("info","fragment onPause");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("info", "fragment onDestroyView");
    }
}
