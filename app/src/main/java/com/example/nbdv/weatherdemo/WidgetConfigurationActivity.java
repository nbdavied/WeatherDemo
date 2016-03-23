package com.example.nbdv.weatherdemo;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.nbdv.weatherdemo.Utils.DataStore;
import com.example.nbdv.weatherdemo.model.City;

import java.util.ArrayList;
import java.util.List;

public class WidgetConfigurationActivity extends AppCompatActivity {
    List<String> cityList;
    List<String> cityIdList;
    ListView lvStoredCities;
    Button button;
    int selectedPosition=-1;
    int appWidgetId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.activity_widget_configuration);
        final Context context=this;
        lvStoredCities= (ListView) findViewById(R.id.lv_widget_conf_list);
        button= (Button) findViewById(R.id.bt_widget_conf_submit);

        final Intent intent=getIntent();
        Bundle extras=intent.getExtras();
        if(extras!=null)
        {
            appWidgetId=extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        //读取存储的城市清单并显示到listview
        cityList = new ArrayList<String>();
        cityIdList = new ArrayList<String>();
        City[] cities = DataStore.getStoredCities(this);
        for (City city : cities) {
            cityList.add(city.getCityName());
            cityIdList.add(city.getCityId());
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,cityList);
        lvStoredCities.setAdapter(adapter);

        //选中某项时记录位置
        lvStoredCities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //widget设置为选定城市
                AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(context);
                String cityId=cityIdList.get(position);
                DataStore.saveWidgetConf(context, appWidgetId, cityId);

                WeatherWidgetProvider.updateAppWidget(context, appWidgetManager, appWidgetId, cityId);

                Intent result=new Intent();
                result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
                setResult(RESULT_OK,result);
                finish();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPosition=-1;
            }
        });

        lvStoredCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition=position;
            }
        });
        //点击确定时将选中的城市id存入
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedPosition==-1){
                    Toast.makeText(context,getResources().getString(R.string.select_city_please),Toast.LENGTH_LONG).show();
                }
                else
                {
                    //widget设置为选定城市
                    AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(context);
                    String cityId=cityIdList.get(selectedPosition);
                    DataStore.saveWidgetConf(context, appWidgetId, cityId);

                    WeatherWidgetProvider.updateAppWidget(context, appWidgetManager, appWidgetId, cityId);

                    Intent result=new Intent();
                    result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
                    setResult(RESULT_OK, result);
                    finish();
                }
            }
        });
    }

}
