package com.example.classchat.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.example.classchat.Adapter.Adapter_See_All_Memo;
import com.example.classchat.Object.Object_TodoList;
import com.example.classchat.R;
import com.example.classchat.Util.Util_NetUtil;
import com.example.classchat.Util.Util_ToastUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Activity_AllTodo extends AppCompatActivity {

    private String userID;
    private ImageView back;
    private RecyclerView rv;
    private LinearLayout empty;
    private List<Object_TodoList> todoLists;
    private List<String> jsonlist;
    private Adapter_See_All_Memo adapter_all_todo;
    private static final int GETSUCCESS = 1;
    private static final int GETNULL = 0;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__all_todo);
        todoLists = new ArrayList<>();

        //沉浸式状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            //After LOLLIPOP not translucent status bar
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //Then call setStatusBarColor.
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.theme));
        }

        back = findViewById(R.id.iv_all_todo_back);
        rv = findViewById(R.id.rv_todo);
        empty = findViewById(R.id.ll_when_null);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        userID = intent.getStringExtra("userId");
        initData();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case GETSUCCESS:
                    LinearLayoutManager layoutManager = new LinearLayoutManager(Activity_AllTodo.this);
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    rv.setLayoutManager(layoutManager);
                    adapter_all_todo = new Adapter_See_All_Memo(Activity_AllTodo.this, todoLists);
                    rv.setAdapter(adapter_all_todo);
                    adapter_all_todo.notifyDataSetChanged();
                    break;
                case GETNULL:
                    Util_ToastUtils.showToast(Activity_AllTodo.this, "暂时没有添加任务");
                    empty.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume","onResume");
        if(isFirst) isFirst = false;
        else {
            initData();
            adapter_all_todo.notifyDataSetChanged();
        }
    }

    private void initData() {
        todoLists.clear();
        final Message message = new Message();
        //构建requestbody
        RequestBody requestBody = new FormBody.Builder()
                .add("userid", userID)
                .build();
        // 发送网络请求，联络信息
        Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/getalltodoitem", requestBody, new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 得到服务器返回的具体内容
                String responseData = response.body().string();
                if(responseData.equals("None")){
                    message.what = GETNULL;
                    handler.sendMessage(message);
                }
                else{
                    // 转化为具体的对象列表
                    jsonlist = JSON.parseArray(responseData, String.class);
                    if(jsonlist.size() != 0){
                        for (int i = 0; i < jsonlist.size(); i++) {
                            Object_TodoList o = JSON.parseObject(jsonlist.get(i), Object_TodoList.class);
                            todoLists.add(o);
                        }
                        // 发送收到成功的信息
                        //由getQuery()按需处理后载入布局
                        message.what = GETSUCCESS;
                        handler.sendMessage(message);
                    }
                    else {
                        message.what = GETNULL;
                        handler.sendMessage(message);
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }
        });

    }
}