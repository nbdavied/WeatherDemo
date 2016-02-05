package com.example.nbdv.weatherdemo.model;

/**
 * Created by nbdav on 2016/2/4.
 */
public class Province {
    private String name;
    //private String id;

    public Province(String name) {
        //this.id = id;
        this.name = name;
    }

    public Province() {
        name="";
        //id="";
    }

    public void setProvinceName(String name){
        this.name=name;
    }
    public String getProvinceName(){
        return name;
    }

/*    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }*/
}
