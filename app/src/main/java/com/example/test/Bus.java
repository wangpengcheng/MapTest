package com.example.test;

import com.amap.api.maps.model.LatLng;

/**
 * Created by Administrator on 2018/5/11.
 */

public class Bus {
    /**
     * bus_name : ceshi
     * bus_num : 123456
     * bus_line_num : 1
     * bus_position : {"x":30.67091861397,"y":104.14373084986}
     * bus_speed : 10
     */

    private String bus_name;
    private int bus_num;
    private int bus_line_num;
    private BusPositionBean bus_position;
    private int bus_speed;
    public Bus(){
    }
    public Bus(String var1,int var2,int var3,BusPositionBean var4,int var5){
        this.bus_name=var1;
        this.bus_num=var2;
        this.bus_line_num=var3;
        this.bus_position=var4;
        this.bus_speed=var5;

    }
    public Bus(String var1,int var2,int var3,LatLng var4,int var5){
        BusPositionBean temp=new BusPositionBean(var4.latitude,var4.longitude);
        this.bus_name=var1;
        this.bus_num=var2;
        this.bus_line_num=var3;
        this.bus_position= temp;
        this.bus_speed=var5;

    }


    public String getBus_name() {
        return this.bus_name;
    }

    public void setBus_name(String bus_name) {
        this.bus_name = bus_name;
    }

    public int getBus_num() {
        return this.bus_num;
    }

    public void setBus_num(int bus_num) {
        this.bus_num = bus_num;
    }

    public int getBus_line_num() {
        return bus_line_num;
    }

    public void setBus_line_num(int bus_line_num) {
        this.bus_line_num = bus_line_num;
    }

    public BusPositionBean getBus_position() {
        return this.bus_position;
    }

    public void setBus_position(BusPositionBean bus_position) {
        this.bus_position = bus_position;
    }
    public void setBus_position(LatLng bus_position) {
        this.bus_position.setX( bus_position.latitude);
        this.bus_position.setY(bus_position.longitude);
    }

    public int getBus_speed() {
        return this.bus_speed;
    }

    public void setBus_speed(int bus_speed) {
        this.bus_speed = bus_speed;
    }

    public static class BusPositionBean {
        /**
         * x : 30.67091861397
         * y : 104.14373084986
         */

        private double x;
        private double y;
        public BusPositionBean(double var1,double var2){
            this.x=var1;
            this.y=var2;
        }

        public double getX() {
            return this.x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return this.y;
        }

        public void setY(double y) {
            this.y = y;
        }
        public LatLng ToLatLng(){
            LatLng temp=new LatLng(this.x,this.y);
            return temp;
        }
    }
}
