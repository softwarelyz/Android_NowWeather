package com.nowweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * 县区类
 */

public class County extends DataSupport {
    //县区id
    private int id;
    //县区名称
    private String countyName;
    //天气id
    private String weatherId;
    //城市id
    private int cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
