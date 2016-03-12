package com.example.nbdv.weatherdemo;

/**
 * Created by nbdav on 2016/1/26.
 * 定义城市信息，用于反序列化json数据
 */
public class Cities {
    public CityInfo[] city_info;
    public String status;
}
class CityInfo{
    public String city; //城市名称
    public String cnty; //国家
    public String id;   //id
    public String lat;  //纬度
    public String lon;  //经度
    public String prov; //省份
}