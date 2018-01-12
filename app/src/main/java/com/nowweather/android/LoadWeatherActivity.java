package com.nowweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class LoadWeatherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_weather);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //读取weatheractivity传过来的缓存数据 如果不为null就说明已经请求过天气数据了，就直接跳到weatheractivity了
        if(prefs.getString("weather",null)!=null){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
