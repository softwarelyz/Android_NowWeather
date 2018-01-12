package com.nowweather.android.gson;

import com.google.gson.annotations.SerializedName;

/*
* 预报类
* */
public class Forecast {

    //未来预报日期
    public String date;

    //最大小度数
    @SerializedName("tmp")
    public Temperature temperature;

    //天气状况
    @SerializedName("cond")
    public More more;

    public class Temperature{

        //最大温度
        public String max;

        //最小温度
        public String min;
    }

    public class More{
        //天气状况
        @SerializedName("txt_d")
        public String info;
    }
}
