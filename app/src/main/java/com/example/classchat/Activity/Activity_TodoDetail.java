package com.example.classchat.Activity;

import android.content.Intent;
import android.icu.util.LocaleData;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.classchat.Object.Object_TodoList;
import com.example.classchat.R;
import com.example.classchat.Util.Util_NetUtil;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Activity_TodoDetail extends AppCompatActivity {

    private TextView funcTitle;
    private Button back, edit, delete, save;
    private Object_TodoList memo;
    private EditText title, content;
    private Switch isClock;
    private Boolean bisClock;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        memo = JSON.parseObject(intent.getStringExtra("memo"), Object_TodoList.class);
        userID = memo.getUserID();
        bisClock = memo.getClock();
        isClock = findViewById(R.id.option_switch_isClock);
        isClock.setChecked(bisClock);
        isClock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bisClock = isChecked;
            }
        });
        setContentView(R.layout.activity__todo_detail);
        title = findViewById(R.id.get_todo_title);
        title.setText(memo.getTodoTitle());
        title.setEnabled(false);
        content = findViewById(R.id.get_todo_details);
        content.setText(memo.getContent());
        content.setEnabled(false);
        back = findViewById(R.id.memo_edit_back);
        delete = findViewById(R.id.memo_delete);
        save = findViewById(R.id.add_todo_button);
        save.setText("保存修改");
        save.setVisibility(View.GONE);
        edit = findViewById(R.id.memo_edit);
        edit.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.setVisibility(View.GONE);
                save.setVisibility(View.VISIBLE);
                content.setEnabled(true);
//                save.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //TODO 修改
//                        RequestBody requestBody = new FormBody.Builder()
//                                .add("userID", userID)
//                                .add("todoTitle", memo.getTodoTitle())
//                                .add("weekChosen", "")
//                                .add("dayChosen", "")
//                                .add("timeSlot", " ")
//                                .add("detailTime", "huikgiu")
//                                .add("isClock", bisClock+"")
//                                .add("content", content.getText().toString())
//                                .build();   //构建请求体
//
//                        //TODO
//                        Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/updatetodoitem", requestBody, new okhttp3.Callback() {
//                            @Override
//                            public void onResponse(Call call, Response response) throws IOException {
//                                // 得到服务器返回的具体内容
//                                boolean responseData = Boolean.parseBoolean(response.body().string());
//                                Message message = new Message();
//                                if (responseData) {
//                                    message.what = SAVE_SUCCESS;
//                                    handler.sendMessage(message);
//                                } else {
//                                    message.what = SAVE_FAILED;
//                                    handler.sendMessage(message);
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                                // 在这里对异常情况进行处理
//                            }
//                        });
//                    }
//                });
//
//                delete.setVisibility(View.VISIBLE);
//                delete.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //TODO 删除
//
//                    }
//                });
            }
        });
        funcTitle = findViewById(R.id.memo_func_title);
        funcTitle.setText("待办详情");
    }
}
