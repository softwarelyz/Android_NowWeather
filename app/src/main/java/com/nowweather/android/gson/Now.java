package com.nowweather.android.gson;

import com.google.gson.annotations.SerializedName;

/*当前天气*/
public class Now {

    //天气度数
    @SerializedName("tmp")
    public String temperature;


    @SerializedName("cond")
    public More more;

    public class More{
        //天气状况
        @SerializedName("txt")
        public String info;
    }
}
