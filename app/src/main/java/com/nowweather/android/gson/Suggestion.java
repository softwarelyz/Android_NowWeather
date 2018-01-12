package com.nowweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 一些与天气相关的建议
 */

public class Suggestion {

    //舒适度
    @SerializedName("comf")
    public Comfort comfort;

    //洗车指数
    @SerializedName("cw")
    public CarWash carWash;

    //运动指数
    public Sport sport;

    public class Comfort{

        @SerializedName("txt")
        public String info;
    }

    public class CarWash{

        @SerializedName("txt")
        public String info;
    }

    public class Sport{

        @SerializedName("txt")
        public String info;
    }
}
