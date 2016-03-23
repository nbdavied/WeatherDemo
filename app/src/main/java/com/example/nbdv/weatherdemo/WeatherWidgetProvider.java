package com.example.nbdv.weatherdemo;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.nbdv.weatherdemo.Service.WidgetUpdateService;
import com.example.nbdv.weatherdemo.Utils.DataStore;

/**
 * Created by nbdav on 2016/2/29.
 */
public class WeatherWidgetProvider extends AppWidgetProvider {
    Context context;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {


        for (int appWidgetId:appWidgetIds) {
            String cityId= DataStore.getWidgetConf(context,appWidgetId);
            if(cityId!=null)
                updateAppWidget(context,appWidgetManager,appWidgetId,cityId);
        }
    }

    public static void updateAppWidget(Context context,AppWidgetManager appWidgetManager,int appWidgetId,String CityId){
        Intent intent=new Intent(context, WidgetUpdateService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
        intent.putExtra("CITY_ID",CityId);
        context.startService(intent);
    }

    @Override
    public void onEnabled(Context context) {
        this.context=context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(context);
        for (int appWidgetId:appWidgetManager.getAppWidgetIds(new ComponentName(context, WeatherWidgetProvider.class))) {
            DataStore.deleteWidgetConf(context,appWidgetId);
        }
    }
}
