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
import android.widget.TextView;

import com.google.gson.Gson;


/**
 * Created by nbdav on 2016/1/24.
 */
public class HttpThread extends Thread {
    private static String APIURL="http://apis.baidu.com/heweather/weather/free";
    private static String APIKEY="b6dbd12c5e1dc6aa8edede7edecde246";

    private String city;
    private HttpURLConnection httpURLConnection;
    private URL url;
    private Handler handler;
    private String result;
    private TextView tvResult;
    public HttpThread(String city,TextView tvResult,Handler handler){
        this.city=city;
        this.tvResult=tvResult;
        this.handler=handler;
        result="";
    }

    @Override
    public void run() {
        super.run();

        String requestURL=APIURL+"?city="+city;

        try {
            url=new URL(requestURL);
            httpURLConnection= (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(8000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("apikey", APIKEY);


            //获得读取的内容
            InputStreamReader in;
            InputStream inputStream;
            inputStream = httpURLConnection.getInputStream();
            in = new InputStreamReader(inputStream);
            BufferedReader buffer = new BufferedReader(in);
            String inputLine = null;
            while ((inputLine = buffer.readLine()) != null) {
                result += inputLine + "\n";
            }
            httpURLConnection.disconnect();

            //刷新控件内容
            handler.post(new Runnable() {
                @Override
                public void run() {

                    Weather weather=new Weather();
                    String jsonString="{\"HeWeather data service 3.0\":\"asdf\"}";
                    Gson gson=new Gson();
                    weather=gson.fromJson(result,Weather.class);
                    String condition_d=weather.serviceVersion[0].daily_forecast[0].cond.txt_d;
                    String condition_n=weather.serviceVersion[0].daily_forecast[0].cond.txt_n;
                    int tmp_min=weather.serviceVersion[0].daily_forecast[0].tmp.min;
                    int tmp_max=weather.serviceVersion[0].daily_forecast[0].tmp.max;
                    String air_quality=weather.serviceVersion[0].aqi.city.qlty;
                    String weatherDesc="白天天气："+condition_d+"\n"
                            +"夜晚天气："+condition_n+"\n"
                            +"气温："+tmp_min+" ~ "+tmp_max+"\n"
                            +"空气质量："+air_quality;
                    tvResult.setText(weatherDesc);

                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
