package com.example.nbdv.weatherdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by nbdav on 2016/1/24.
 */
public class GetWeatherThread extends Thread {
    private static final String APIURL="https://api.heweather.com/x3/weather";    //天气api url
    private static final String KEY="259b556b9f504e1db746e19fe813ff22";        //apikey

    public static final int SEARCH_BY_ID=1;
    public static final int SEARCH_BY_CITY=2;

    private int mode;                                   //查找模式
    private String city;                                //设置的城市
    private String id;
    private HttpsURLConnection httpsURLConnection;
    private URL url;
    private Handler handler;
    private String result;

    /*
    * 当mode=1，根据id查询
    * 当mode=2，根据city name
    * */
    public GetWeatherThread(String searchString,Handler handler,int mode){
        if(mode==SEARCH_BY_ID)
            this.id=searchString;
        else if(mode==SEARCH_BY_CITY)
            this.city=searchString;
        this.handler=handler;
        this.mode=mode;
        result="";
    }

    @Override
    public void run() {
        super.run();
        String requestURL;
        if(mode==SEARCH_BY_ID){
            //根据id设置url
            requestURL=APIURL+"?cityid="+id+"&key="+KEY;
        }
        else if(mode==SEARCH_BY_CITY){
            //根据城市名称设置url
            requestURL=APIURL+"?city="+city+"&key="+KEY;
        }
        else{
            requestURL="";
            Log.e("error","url错误");
        }

        try {
            url=new URL(requestURL);
            httpsURLConnection= (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setConnectTimeout(8000);
            httpsURLConnection.setRequestMethod("GET");
            //httpsURLConnection.setRequestProperty("key", APIKEY);


            //获得读取的内容
            InputStreamReader in;
            InputStream inputStream;
            inputStream = httpsURLConnection.getInputStream();
            in = new InputStreamReader(inputStream);
            BufferedReader buffer = new BufferedReader(in);
            String inputLine = null;
            while ((inputLine = buffer.readLine()) != null) {
                result += inputLine + "\n";
            }
            httpsURLConnection.disconnect();

//            Gson gson=new Gson();
//            weather=gson.fromJson(result,Weather.class);
            //刷新控件内容
            //handler.sendEmptyMessage(0);
            Message msg=new Message();
            Bundle bundle=new Bundle();
            bundle.putString("weatherString",result);
            msg.setData(bundle);
            handler.sendMessage(msg);

        } catch (MalformedURLException e) {
            Log.e("error","url");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("error","下载天气信息");
            e.printStackTrace();
        }
    }
}
