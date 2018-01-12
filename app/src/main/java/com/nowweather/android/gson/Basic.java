package com.nowweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 城市基本信息
 */

public class Basic {

    //使用@SerializedName注解的方式让JSON字段与Java字段建立起映射关系
    //城市名称
    @SerializedName("city")
    public String cityName;

    //天气id
    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        //更新时间
        @SerializedName("loc")
        public String updateTime;
    }
}
