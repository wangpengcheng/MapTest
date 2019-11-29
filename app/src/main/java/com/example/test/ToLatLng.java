package com.example.test;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 将LatLonPoint转化为LatLng
 * 2018/5/10
 */

public class ToLatLng {
    public static int a = 2048;

    ToLatLng() {
    }

    public ToLatLng(List<LatLonPoint> pointList) {

    }


    public static LatLng toLatLng(LatLonPoint var0) {
        return new LatLng(var0.getLatitude(), var0.getLongitude());
    }

    public static ArrayList<LatLng> toLatLng(List<LatLonPoint> var0) {
        ArrayList var1 = new ArrayList();
        Iterator var2 = var0.iterator();

        while(var2.hasNext()) {
            LatLonPoint var3 = (LatLonPoint)var2.next();
            LatLng var4 = toLatLng(var3);
            var1.add(var4);
        }

        return var1;
    }
}
