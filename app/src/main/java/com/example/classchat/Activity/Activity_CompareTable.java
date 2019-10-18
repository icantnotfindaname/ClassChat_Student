
package com.example.classchat.Activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.classchat.Adapter.Adapter_CompareTable;
import com.example.classchat.Object.Object_Comparison;
import com.example.classchat.R;
import com.example.classchat.Util.Util_NetUtil;
import com.example.library_cache.Cache;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Activity_CompareTable extends AppCompatActivity {

    private ImageView back;
    private android.support.v7.widget.RecyclerView rv;
    private LinearLayout empty;
    private Adapter_CompareTable adapter_compareTable;
    private boolean isFirst = true;
    private String userId;
    private List<Object_Comparison> activityList = new ArrayList<>();
    private static final int GET = 0;
    private static final int NULL = 1;
    private MyReceiver myReceiver = new MyReceiver();
    private Boolean isChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__compare_table);
        back = findViewById(R.id.iv_compare_back);
        empty = findViewById(R.id.ll_when_null);
        rv = findViewById(R.id.compare_rv);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        IntentFilter intentFilter = new IntentFilter("miniTimetable.send");
        registerReceiver(myReceiver, intentFilter);
        Intent intent1 = new Intent(Activity_CompareTable.this, MyReceiver.class);
        startService(intent1);

        initData();

        //沉浸式状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            //After LOLLIPOP not translucent status bar
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //Then call setStatusBarColor.
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.theme));
        }

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case GET:
                    empty.setVisibility(View.GONE);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(Activity_CompareTable.this);
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    rv.setLayoutManager(layoutManager);
                    Log.e("GET", activityList.toString());
                    adapter_compareTable = new Adapter_CompareTable(Activity_CompareTable.this, activityList, userId);
                    rv.setAdapter(adapter_compareTable);
                    break;
                case NULL:
                    empty.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    private void initData(){
        String info = Cache.with(Activity_CompareTable.this)
                .path(getCacheDir(Activity_CompareTable.this))
                .getCache("compareTable", String.class);
        final Message message = new Message();

        if(info == null || info.isEmpty() || info.equals("[]") || isChanged)
        {
            RequestBody requestBody = new FormBody.Builder()
                    .add("userID", userId)
                    .build();

            Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/getcomparison", requestBody,new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {}

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    // 得到服务器返回的具体内容
                    String responseData = response.body().string();
                    Log.e("response", responseData);
                    List<String> tempjsonlist = JSON.parseArray(responseData, String.class);
                    Log.e("tempjsonlist", tempjsonlist.toString());
                    Log.e("tempjsonlistSize", tempjsonlist.size()+"");

                    activityList.clear();
                    if(tempjsonlist.size() > 0){
                        for(String s : tempjsonlist) {
                            Object_Comparison comparison = JSON.parseObject(s, Object_Comparison.class);
                            activityList.add(comparison);
                        }
                        Log.e("activityList", activityList.toString());
                        message.what = GET;
                    }else {
                        message.what = NULL;
                    }

                    handler.sendMessage(message);
                }
            });

        }
        else{
            activityList.clear();
            List<String> tempjsonlist = JSON.parseArray(info, String.class);
            if(tempjsonlist.size() != 0){
                for(String s : tempjsonlist) {
                    Object_Comparison comparison = JSON.parseObject(s, Object_Comparison.class);
                    activityList.add(comparison);
                }

                Log.e("ListAfterRefresh",activityList.toString());

                message.what = GET;
            }else message.what = NULL;

            handler.sendMessage(message);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(isFirst){
            isFirst = false;
        }else {
            initData();
        }
    }

    public String getCacheDir(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }
    public void add(View view) {
        Intent intent = new Intent(Activity_CompareTable.this, Activity_AddNewComparison.class);
        intent.putExtra("userId", userId);
        intent.putExtra("activityList", (Serializable)activityList);
        startActivity(intent);

    }

    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Cache.with(Activity_CompareTable.this)
                    .path(getCacheDir(Activity_CompareTable.this))
                    .remove("compareTable");
            isChanged = true;
            initData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }
}
