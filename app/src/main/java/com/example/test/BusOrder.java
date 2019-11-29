package com.example.test;

/**
 * Created by Administrator on 2018/5/15.
 */

public class BusOrder {

    /**
     * bus_order_num : 20180515014352271811784273798580
     * bus_occupants_num : 27
     * remaining_seats_num : 3
     * queuing_num : 25
     */

    private String bus_order_num;
    private int bus_occupants_num;
    private int remaining_seats_num;
    private int queuing_num;

    public String getBus_order_num() {
        return bus_order_num;
    }

    public void setBus_order_num(String bus_order_num) {
        this.bus_order_num = bus_order_num;
    }

    public int getBus_occupants_num() {
        return bus_occupants_num;
    }

    public void setBus_occupants_num(int bus_occupants_num) {
        this.bus_occupants_num = bus_occupants_num;
    }

    public int getRemaining_seats_num() {
        return remaining_seats_num;
    }

    public void setRemaining_seats_num(int remaining_seats_num) {
        this.remaining_seats_num = remaining_seats_num;
    }

    public int getQueuing_num() {
        return queuing_num;
    }

    public void setQueuing_num(int queuing_num) {
        this.queuing_num = queuing_num;
    }
}
