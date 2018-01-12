package com.nowweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nowweather.android.gson.Forecast;
import com.nowweather.android.gson.Weather;
import com.nowweather.android.service.AutoUpdateService;
import com.nowweather.android.util.HttpUtil;
import com.nowweather.android.util.Utility;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 在活动中请求天气数据，并将数据展示到界面上
 */

public class WeatherActivity extends AppCompatActivity {

    //左滑出现城市列表
    public DrawerLayout drawerlayout;
    //点击出现城市列表
    private Button navButton;
    //下拉刷新
    public SwipeRefreshLayout swipeRefresh;
    //用来记录城市天气的id
    private String mWeatherId;
    //滚动条
    private ScrollView weatherLayout;
    //城市
    private TextView titleCity;
    //更新时间
    private TextView titleUpdateTime;
    //天气度数
    private TextView degreeText;
    //天气状况
    private TextView weatherInfoText;
    //未来天气预报
    private LinearLayout forecastLayout;
    //aqi指数
    private TextView aqiText;
    //pm2.5
    private TextView pm25Text;
    //舒适度
    private TextView comfortText;
    //洗车指数
    private TextView carWashText;
    //运动指数
    private TextView sportText;
    //背景图片
    private ImageView bingPicImg;
    //右边菜单
    private TextView doMenu;
    //退出时间
    private long exitTime = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //LocationResult.setMovementMethod(ScrollingMovementMethod.getInstance());
        //判断当前的设备版本是否大于21/Android5.0  若设备版本号在Android5.0以上  就执行代码   使得软件界面的头布局和设备系统栏融为一体
        if (Build.VERSION.SDK_INT > 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        //初始化各个控件
        initView();
        //刷新的颜色
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            //请求获取天气
            requestWeather(mWeatherId);
            //显示天气
            showWeatherInfo(weather);
        } else {
            //无缓存时区服务器查询天气
            mWeatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }

        //滑动刷新背景图片
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开滑动菜单
                drawerlayout.openDrawer(GravityCompat.START);
            }
        });
        doMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(WeatherActivity.this,"asdfsd",Toast.LENGTH_SHORT).show();
                doMenu.showContextMenu();
            }
        });
        doMenu.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 1, 0, "设置");
                menu.add(0, 2, 0, "关于");
            }
        });
    }
    /**
     * 加载必应图片
     */
    private void loadBingPic() {
        String resquestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(resquestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //图片加载失败
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //图片加载成功
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //图片加载成功
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

    // 选择上下文菜单
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getGroupId()) {
            case 0: {
                switch (item.getItemId()) {
                    case 1: {
                        Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    case 2: {
                        //Toast.makeText(this,"关于",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, AboutActivity.class);
                        startActivity(intent);
                    }
                    break;
                }
            }
            default:
                break;
        }
        return true;
    }


    /**
     * 根据天气的id去请求天气信息
     *
     * @param weatherId
     */
    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=b0a1a5083f9a49aaa4d71d4ebdb1b0ba";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        //刷新完毕后关闭下拉提示
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            mWeatherId = weather.basic.weatherId;
                            showWeatherInfo(weather);
                            Date date = new Date();
                            SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getTimeInstance(DateFormat.SHORT);
                            String time = sdf.format(date);
                            Toast.makeText(WeatherActivity.this, time + "刷新成功", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
                            startService(intent);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }

    /*处理并展示Weather实体类中的数据*/
    private void showWeatherInfo(Weather weather) {

        //城市名字
        String cityName = weather.basic.cityName;
        titleCity.setText(cityName);

        //更新天气发布时间
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        titleUpdateTime.setText(updateTime + "发布");

        //天气度数
        String degree = weather.now.temperature + "℃";
        degreeText.setText(degree);

        //天气状况
        String weatherInfo = weather.now.more.info;
        weatherInfoText.setText(weatherInfo);

        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);

            //未来三天的日期
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            dateText.setText(forecast.date);
            //天气情况
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            infoText.setText(forecast.more.info);
            //最小温度
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            minText.setText(forecast.temperature.min + "℃");
            //最大温度
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            maxText.setText(forecast.temperature.max + "℃");

            forecastLayout.addView(view);
        }

        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        setAqiAndPm25(weather);

        String comfort = "舒适度:" + weather.suggestion.comfort.info;
        String carWash = "洗车指数:" + weather.suggestion.carWash.info;
        String sport = "运动指数: " + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }

    //初始化各个控件
    private void initView() {
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        bingPicImg = (ImageView) findViewById(R.id.bing_pic);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        drawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);
        doMenu = (TextView) findViewById(R.id.domenu);
    }


    //双击退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    //判断API 与 pm2.5指数高低来显示颜色
    void setAqiAndPm25(Weather weather) {
        if (weather.aqi != null) {
            int aqi = 0, pm25 = 0;
            try {
                aqi = Integer.parseInt(weather.aqi.city.aqi);
                pm25 = Integer.parseInt(weather.aqi.city.pm25);
            } catch (Exception e) {
                e.printStackTrace();
            }
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
            aqiText.setTextSize(40);
            pm25Text.setTextSize(40);

            if (aqi == 0) aqiText.setTextColor(Color.WHITE);
            else if (aqi < 50) aqiText.setTextColor(getResources().getColor(R.color.a50));
            else if (aqi < 100) aqiText.setTextColor(getResources().getColor(R.color.a100));
            else if (aqi < 150) aqiText.setTextColor(getResources().getColor(R.color.a150));
            else if (aqi < 200) aqiText.setTextColor(getResources().getColor(R.color.a200));
            else if (aqi < 300) aqiText.setTextColor(getResources().getColor(R.color.a300));
            else if (aqi > 300) aqiText.setTextColor(getResources().getColor(R.color.a300up));

            if (pm25 == 0) pm25Text.setTextColor(Color.WHITE);
            else if (pm25 < 35) pm25Text.setTextColor(getResources().getColor(R.color.a50));
            else if (pm25 < 75) pm25Text.setTextColor(getResources().getColor(R.color.a100));
            else if (pm25 < 115) pm25Text.setTextColor(getResources().getColor(R.color.a150));
            else if (pm25 < 150) pm25Text.setTextColor(getResources().getColor(R.color.a200));
            else if (pm25 < 250) pm25Text.setTextColor(getResources().getColor(R.color.a300));
            else if (pm25 > 250) pm25Text.setTextColor(getResources().getColor(R.color.a300up));
            } else {
                aqiText.setTextColor(Color.WHITE);
                pm25Text.setTextColor(Color.WHITE);
                aqiText.setText("暂无数据");
                pm25Text.setText("暂无数据");
                aqiText.setTextSize(25);
                pm25Text.setTextSize(25);
                aqiText.setSingleLine();
                pm25Text.setSingleLine();
            }
    }

    public void addCity(View view){
        Toast.makeText(this,"添加城市",Toast.LENGTH_SHORT).show();
    }

    public void search(View view){
        Toast.makeText(this,"搜索城市",Toast.LENGTH_SHORT).show();
    }

}