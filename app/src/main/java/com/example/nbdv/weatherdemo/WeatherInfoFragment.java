package com.example.nbdv.weatherdemo;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.nbdv.weatherdemo.View.LineChart;
import com.example.nbdv.weatherdemo.json.JsonWeather;
import com.example.nbdv.weatherdemo.model.City;
import com.google.gson.Gson;


public class WeatherInfoFragment extends Fragment {
    //view类
    private ImageView ivNowInfoCondition;
    private TextView tvNowInfoCondition;
    private TextView tvMaxTemp;
    private TextView tvMinTemp;
    private TextView tvCurrentTemp;
    private TextView tvCityName;
    private ScrollView scrollView;
    private LinearLayout mainWeatherInfoLayout;
    private LineChart lineChart;
    private View view;             //根视图
    private String CityName;    //选定的城市名称
    private String CityID;      //城市id
    private JsonWeather weather;
    private boolean viewCreated = false;
    private boolean returnHandlerToParent = false;    //如果为true，则需要向parent activity传递handler，告知数据已经刷新
    private Handler parentHandler;
    private SwipeRefreshLayout swipeRefreshLayout;  //传入parent activity下拉刷新控件

    private boolean needDisableScrollView;      //true则需要将scroll view屏蔽


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weather_info, container, false);

        /*--------------------find view here-----------------*/
        ivNowInfoCondition= (ImageView) view.findViewById(R.id.iv_now_info_condition);
         tvNowInfoCondition= (TextView) view.findViewById(R.id.tv_now_info_condition);
         tvMaxTemp= (TextView) view.findViewById(R.id.tv_max_temp);
         tvMinTemp= (TextView) view.findViewById(R.id.tv_min_temp);
         tvCurrentTemp= (TextView) view.findViewById(R.id.tv_current_temp);
        tvCityName= (TextView) view.findViewById(R.id.tv_city_name);
        lineChart = (LineChart) view.findViewById(R.id.lineChart);
        scrollView = (ScrollView) view.findViewById(R.id.weather_fragment_scroll_view);
        mainWeatherInfoLayout= (LinearLayout) view.findViewById(R.id.main_weather_info);
        /*-------------------find view finished--------------*/

        viewCreated = true;
        Log.i("info", "fragment onCreateView");


        ViewTreeObserver vto=mainWeatherInfoLayout.getViewTreeObserver();
        //等待视图加载完成之后，读取linear layout高度并设置margin值
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                int h=mainWeatherInfoLayout.getHeight();
                int h2=tvCityName.getHeight();
                int padding=getResources().getDisplayMetrics().heightPixels-h-h2;
                //mainWeatherInfoLayout.setPadding(0, padding, 0, 0);

                LinearLayout.LayoutParams lp= (LinearLayout.LayoutParams) mainWeatherInfoLayout.getLayoutParams();
                lp.setMargins(0,padding,0,0);
                mainWeatherInfoLayout.setLayoutParams(lp);
                mainWeatherInfoLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        return view;
    }

    public WeatherInfoFragment() {
    }



    public void setFragmentCity(City city) {
        CityName = city.getCityName();
        CityID = city.getCityId();
        refreshData();
    }

    public void setFragmentCity(String id, String name) {
        CityName = name;
        CityID = id;
        refreshData();
    }


    public void refreshData() {
        GetWeatherThread getWeather = new GetWeatherThread(CityID, handler, GetWeatherThread.SEARCH_BY_ID);
        getWeather.start();
    }

    public void refreshData(Handler parentHandler) {
        this.parentHandler = parentHandler;
        returnHandlerToParent = true;
        GetWeatherThread getWeather = new GetWeatherThread(CityID, handler, GetWeatherThread.SEARCH_BY_ID);
        getWeather.start();
    }

    private void updateContent() {
        JsonWeather.Now now=weather.serviceVersion[0].now;
        //设置图标
        int curCond=now.cond.code;
        ivNowInfoCondition.setImageResource(JsonWeather.getConditionImage(curCond));
        //设置天气状态
        String cond=now.cond.txt;
        tvNowInfoCondition.setText(cond);
        //设置最高气温
        tvMaxTemp.setText(weather.serviceVersion[0].daily_forecast[0].tmp.max+"°");
        //设置最低气温
        tvMinTemp.setText(weather.serviceVersion[0].daily_forecast[0].tmp.min+"°");
        //设置当前温度
        tvCurrentTemp.setText(now.tmp+"°");
        //设置城市名称
        tvCityName.setText(CityName);

        //设置LineChart属性
//        int lenth = weather.serviceVersion[0].daily_forecast.length;
//        int lowTemp[] = new int[lenth];
//        int highTemp[] = new int[lenth];
//        for (int i = 0; i < lenth; i++) {
//            lowTemp[i] = weather.serviceVersion[0].daily_forecast[i].tmp.min;
//            highTemp[i] = weather.serviceVersion[0].daily_forecast[i].tmp.max;
//        }
        lineChart.setData(weather);

    }

    //处理api数据并刷新ui
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Gson gson = new Gson();
            weather = gson.fromJson(msg.getData().getString("weatherString"), JsonWeather.class);

            if (viewCreated)
                updateContent();
            if (returnHandlerToParent) {
                parentHandler.sendEmptyMessage(MainActivity.HANDLER_MESSAGE_FRAGMENT_REFRESH_FINISHED);
                returnHandlerToParent = false;
            }
        }
    };

    private void DisableSwipeRefreshLayoutWhileScrolling() {
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        if (scrollView.getScrollY() != 0) {
                            swipeRefreshLayout.setEnabled(false);
                        } else if (scrollView.getScrollY() == 0)
                            swipeRefreshLayout.setEnabled(true);
                        break;
                    default:
                        break;

                }
                return false;
            }
        });
    }

    public void DisableSwipe(SwipeRefreshLayout swipeRefreshLayout) {
        needDisableScrollView = true;
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("info", "fragment onresume ");
        if (needDisableScrollView)
            DisableSwipeRefreshLayoutWhileScrolling();
        if (weather != null)
            updateContent();

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("info", "fragment onPause");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("info", "fragment onDestroyView");
    }
}
