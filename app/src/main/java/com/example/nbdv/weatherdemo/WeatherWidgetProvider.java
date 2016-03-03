package com.example.nbdv.weatherdemo;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.nbdv.weatherdemo.Service.WidgetUpdateService;

/**
 * Created by nbdav on 2016/2/29.
 */
public class WeatherWidgetProvider extends AppWidgetProvider {
    SharedPreferences sp;
    int[] widgetIds;
    Context context;
    AppWidgetManager appWidgetManager;
/*    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            RemoteViews rviews=new RemoteViews(context.getPackageName(),R.layout.widget_layout);
            //rviews.setTextViewText(R.id.tvCity,cityName);
            for(int i=0;i<widgetIds.length;i++)
            appWidgetManager.updateAppWidget(widgetIds[i], rviews);
        }
    };*/
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Intent intent=new Intent(context, WidgetUpdateService.class);
        context.startService(intent);
/*        sp=context.getSharedPreferences("Preference",Context.MODE_PRIVATE);
        this.appWidgetManager=appWidgetManager;
        String cityName=sp.getString("city","");
        String cityId=sp.getString("id","");
        widgetIds=appWidgetIds;
        for(int i=0;i<appWidgetIds.length;i++)
        {
            RemoteViews remoteViews=new RemoteViews(context.getPackageName(),R.layout.widget_layout);
            remoteViews.setTextViewText(R.id.tvCity,cityName);
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
            Log.i("widget",cityName+" ");
        }

        Thread newthread=new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        });
        newthread.start();*/
    }

    @Override
    public void onEnabled(Context context) {
        this.context=context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
}
