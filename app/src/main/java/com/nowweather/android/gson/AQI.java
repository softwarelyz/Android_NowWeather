package com.nowweather.android.gson;

/**
 * 空气质量类
 */

public class AQI {

    public AQICity city;

    public class AQICity{
        //aqi指数
        public String aqi;
        //pm2.5指数
        public String pm25;
    }
}
