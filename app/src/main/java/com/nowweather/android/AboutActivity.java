package com.nowweather.android;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

//关于界面
public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //判断当前的设备版本是否大于21/Android5.0  若设备版本号在Android5.0以上  就执行代码   使得软件界面的头布局和设备系统栏融为一体
        if(Build.VERSION.SDK_INT>21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

    }

    public void ic_back(View view){
        finish();
    }

    public void ic_share(View view){
        Toast.makeText(this,"asdf",Toast.LENGTH_SHORT).show();
    }

}
