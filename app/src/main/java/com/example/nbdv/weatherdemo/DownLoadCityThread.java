package com.example.nbdv.weatherdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by nbdav on 2016/1/26.
 */
public class DownLoadCityThread extends Thread {
    private static final String APIURL="https://api.heweather.com/x3/citylist";
    private static final String KEY="259b556b9f504e1db746e19fe813ff22";
    private  Context context;
    public DownLoadCityThread(Context context) {
        this.context=context;
    }

    private  String result;
    @Override
    public void run() {
        super.run();
        String requestURL=APIURL+"?search=allchina&key="+KEY;

        result="";
        URL url= null;
        HttpsURLConnection httpsURLConnection;
        try {
            url = new URL(requestURL);
            httpsURLConnection= (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setConnectTimeout(30000);
            httpsURLConnection.setRequestMethod("GET");

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

            //解析json
            //result="{\"city_info\":[{\"city\":\"ABCD\",\"cnty\":\"中国\",\"id\":\"CN101310230\",\"lat\":\"11.26\",\"lon\":\"114.20\",\"prov\":\"海南\"},{\"city\":\"北京\",\"cnty\":\"中国\",\"id\":\"CN101010100\",\"lat\":\"39.904000\",\"lon\":\"116.391000\",\"prov\":\"直辖市\"},{\"city\":\"海淀\",\"cnty\":\"中国\",\"id\":\"CN101010200\",\"lat\":\"39.590000\",\"lon\":\"116.170000\",\"prov\":\"直辖市\"}],\"status\":\"ok\"}";
            Cities cities;
            Gson gson=new Gson();
            cities=gson.fromJson(result,Cities.class);
            if(cities.status.equals("ok")){
                //当数据正常下载，则导入数据库
                Log.i("status", "download success");
                SQLiteDatabase db=context.openOrCreateDatabase("weather.db",Context.MODE_PRIVATE,null);

                //创建新表
                db.execSQL("DROP TABLE IF EXISTS city");
                db.execSQL("CREATE TABLE city(city VARCHAR,cnty VARCHAR,id VARCHAR PRIMARY KEY,lat VARCHAR,lon VARCHAR,prov VARCHAR)");
                for (CityInfo cityInfo:cities.city_info
                     ) {
                    //执行插入语句
                    db.execSQL("insert into city values(?,?,?,?,?,?)",new Object[]{cityInfo.city,cityInfo.cnty,cityInfo.id,cityInfo.lat,cityInfo.lon,cityInfo.prov});
                }
                db.close();
                //将数据库置为已下载状态
                SharedPreferences sp=context.getSharedPreferences("Preference",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("download", true);
                editor.commit();
            }else
            {
                //未正常下载数据
                Log.i("status",cities.status);

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("error","internet wrong");
        }

    }
}
