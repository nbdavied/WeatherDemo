package com.example.nbdv.weatherdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by nbdav on 2016/1/24.
 */
public class HttpThread extends Thread {
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
    private TextView tvResult;
    /*
    * 当mode=1，根据id查询
    * 当mode=2，根据city name
    * */
    public HttpThread(String searchString,TextView tvResult,Handler handler,int mode){
        if(mode==SEARCH_BY_ID)
            this.id=searchString;
        else if(mode==SEARCH_BY_CITY)
            this.city=searchString;
        this.tvResult=tvResult;
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

            //刷新控件内容
            handler.post(new Runnable() {
                @Override
                public void run() {

                    Weather weather=new Weather();

                    Gson gson=new Gson();
                    weather=gson.fromJson(result,Weather.class);
                    if(weather.serviceVersion[0].status.equals("ok")) {
                        String condition_d = weather.serviceVersion[0].daily_forecast[0].cond.txt_d;
                        String condition_n = weather.serviceVersion[0].daily_forecast[0].cond.txt_n;
                        int tmp_min = weather.serviceVersion[0].daily_forecast[0].tmp.min;
                        int tmp_max = weather.serviceVersion[0].daily_forecast[0].tmp.max;
                        String air_quality;
                        if(weather.serviceVersion[0].aqi!=null)
                        {
                            air_quality=weather.serviceVersion[0].aqi.city.qlty;
                        }else
                        {
                            air_quality="no data";
                        }

                        String weatherDesc = "白天天气：" + condition_d + "\n"
                                + "夜晚天气：" + condition_n + "\n"
                                + "气温：" + tmp_min + " ~ " + tmp_max + "\n"
                                + "空气质量：" + air_quality;
                        tvResult.setText(weatherDesc);
                    } else
                        tvResult.setText(weather.serviceVersion[0].status);

                }
            });
        } catch (MalformedURLException e) {
            Log.e("error","url");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("error","下载天气信息");
            e.printStackTrace();
        }
    }
}