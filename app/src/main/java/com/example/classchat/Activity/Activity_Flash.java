package com.example.classchat.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.classchat.R;
import com.example.classchat.Util.SharedPreferencesUtil;
import com.example.classchat.Util.Util_NetUtil;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Activity_Flash extends AppCompatActivity {

    private int SPLASH_DISPLAY_LENGHT = 3;//界面停留的时间
    private Handler loginhandler;
    private Handler handler;
    private Runnable runnable;
    private String editPerson;
    private String editCode;
    private boolean isLogin=false;

    // 登录时就返回必须的数据，这里先定义好
    private boolean isAuthentation=true;
    private String imageUrl;
    private String headUrl;
    private String nickName;
    private String proUni;
    private String realName;
    private String token;


    //设置登录成功或失败的常量
    private static final int LOGIN_FAILED = 0;
    private static final int LOGIN_SUCCESS = 1;

    Timer timer = new Timer();


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        getWindow().setFlags(flag, flag);

        // 判断是否是第一次开启应用
        boolean isFirstOpen = SharedPreferencesUtil.getBoolean(this, SharedPreferencesUtil.FIRST_OPEN, true);
        // 如果是第一次启动，则先进入功能引导页
        if (isFirstOpen) {
            Intent intent = new Intent(this, WelcomeGuideActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity__flash);
        getUserInfo();
//        timer.schedule(task, 1000, 1000);//等待时间一秒，停顿时间一秒
//        /**
//         * 正常情况下不点击跳过
//         */
//        handler = new Handler();
//        handler.postDelayed(runnable = new Runnable() {
//            @Override
//            public void run() {
//                //从闪屏界面跳转到首界面
//                Intent intent = new Intent(Activity_Flash.this, Activity_Enter.class);
//                startActivity(intent);
//                finish();
//            }
//        }, SPLASH_DISPLAY_LENGHT*1000);//延迟5S后发送handler信息

        /*
            设置handler接收网络线程的信号并处理
         */
        loginhandler = new Handler(){
            public void handleMessage(Message msg){
                switch (msg.what){
                    case LOGIN_FAILED:
                        Intent intent2 = new Intent(Activity_Flash.this, Activity_Enter.class);
                        startActivity(intent2);
                        finish();
                        break;
                    case LOGIN_SUCCESS:
                        Intent intent = new Intent(Activity_Flash.this,MainActivity.class);
                        intent.putExtra("userName", nickName);
                        intent.putExtra("userPassword", editCode);
                        intent.putExtra("userId", editPerson);
                        intent.putExtra("userImage", imageUrl);
                        intent.putExtra("userAuthentationStatus", isAuthentation);
                        intent.putExtra("proUni", proUni);
                        intent.putExtra("token", token);
                        intent.putExtra("realName", realName);
                        intent.putExtra("headUrl", headUrl);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                }
            }
        };


        //避免从桌面启动程序后，会重新实例化入口的Activity
        //第三方平台安装app启动后，home键回到桌面后点击app启动时会再次启动入口类
        if (!this.isTaskRoot()) {
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    finish();
                    return;
                }
            }
        }
    }
    private void getUserInfo(){
        SharedPreferences sp = getSharedPreferences("userinfo" , Context.MODE_PRIVATE );

        /* 多次判断 */
        if(sp!=null){
            editPerson = sp.getString("name","");
            editCode = sp.getString("psw","");
            isLogin = sp.getBoolean("auto", false);
        }
        /* 选择了自动登录 */
        if (isLogin){
            login();
        }

        else{
            //从闪屏界面跳转到首界面
            //ditor.putString("psw" , "").commit();
            Intent intent = new Intent(Activity_Flash.this, Activity_Enter.class);
            startActivity(intent);
            finish();
        }
    }
    /*
    登录方法
     */
    public void login() {
        /*
        开启网络线程，发送登录请求
         */
        final RequestBody requestBody = new FormBody.Builder()
                .add("username", editPerson)
                .add("password", editCode)
                .build();   //构建请求体

        Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/login/student", requestBody, new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 得到服务器返回的具体内容
                Message message = new Message();
                String responsedata = response.body().string();
                if (responsedata.equals("ERROR")) {
                    message.what = LOGIN_FAILED;
                    loginhandler.sendMessage(message);
                } else {
                    JSONObject jsonObject = JSON.parseObject(responsedata);
                    nickName = jsonObject.getString("nickname");
                    imageUrl = jsonObject.getString("ico");
                    isAuthentation = Boolean.parseBoolean(jsonObject.getString("authentationstatus"));
                    realName = jsonObject.getString("realname");
                    proUni = jsonObject.getString("university") + "_" + jsonObject.getString("school");
                    token = jsonObject.getString("token");
                    headUrl = jsonObject.getString("head");
                    message.what = LOGIN_SUCCESS;
                    loginhandler.sendMessage(message);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // 在这里对异常情况进行处理
                Intent intent = new Intent(Activity_Flash.this, Activity_Enter.class);
                startActivity(intent);
                finish();
            }
        });

    }
//
//    TimerTask task = new TimerTask() {
//        @Override
//        public void run() {
//            runOnUiThread(new Runnable() { // UI thread
//                @Override
//                public void run() {
//                    SPLASH_DISPLAY_LENGHT--;
//                    if (SPLASH_DISPLAY_LENGHT < 0) {
//                        timer.cancel();
//                    }
//                }
//            });
//        }
//    };
}



