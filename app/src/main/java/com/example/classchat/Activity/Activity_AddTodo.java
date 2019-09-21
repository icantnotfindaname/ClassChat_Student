package com.example.classchat.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.classchat.R;
import com.example.classchat.Util.Util_NetUtil;
import com.example.classchat.Util.Util_ToastUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Activity_AddTodo extends AppCompatActivity {

    private TextView setWeek, timeSlot;
    private Button back, save;
    private EditText title, content;
    private Switch isClock;
    private Boolean bisClock = true;
    //周数多选框
    private AlertDialog.Builder mutilChoicebuilder;
    //配合周数多选框的数组
    private final String[] weeks= new String[]{"第1周","第2周","第3周","第4周","第5周","第6周","第7周","第8周","第9周","第10周","第11周","第12周","第13周","第14周","第15周","第16周","第17周","第18周","第19周","第20周","第21周","第22周","第23周","第24周","第25周"};
    private boolean[] weeksChecked = new boolean[]{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};
    //周数数组
    List<Integer> weeksnum=new ArrayList<>();
    AlertDialog builder=null;
    private String userId;
    private final static int SAVE_SUCCESS = 0;
    private final static int SAVE_FAILED = 1;
    private static int count = 0;
    private int timeslot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__add_todo);
        Intent intent = getIntent();
        timeslot = Integer.parseInt(intent.getStringExtra("timeslot"));
        userId = intent.getStringExtra("userId");
        back = findViewById(R.id.memo_edit_back);
        save = findViewById(R.id.add_todo_button);
        timeSlot = findViewById(R.id.memo_timeslot);
        content = findViewById(R.id.get_todo_details);
        title = findViewById(R.id.get_todo_title);
        setWeek = findViewById(R.id.get_todo_week);
        isClock = findViewById(R.id.option_switch_isClock);
        isClock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bisClock = isChecked;
            }
        });

        switch (timeslot){
            case 0:
                timeSlot.setText("早   课   前");
                break;
            case 3:
                timeSlot.setText("午   休");
                break;
            case 8:
                timeSlot.setText("晚   课   后");
                break;
            case 1:
            case 2:
                timeSlot.setText("第 " + (timeslot * 2 - 1) + " ~ " + (timeslot * 2) + "节");
                break;
            case 4:
            case 5:
            case 6:
            case 7:
                timeSlot.setText("第 " + ((timeslot - 1) * 2 - 1) + " ~ " + ((timeslot - 1) * 2) + "节");
                break;
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //周数多选框
        mutilChoicebuilder = new AlertDialog.Builder(this);
        mutilChoicebuilder.setTitle("选择周数");
        mutilChoicebuilder.setMultiChoiceItems(weeks, weeksChecked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

            }
        });
        mutilChoicebuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String s = new String();
                weeksnum=new ArrayList<>();
                int end = 0;
                for(int i = 0;i < weeksChecked.length;i ++){
                    if(weeksChecked[i])
                        weeksnum.add(i + 1);
                }

                for(int i = 0; i < weeksChecked.length; i ++)
                {
                    if (weeksChecked[i])
                    {

                        int start = i + 1;
                        for(int j = i + 1;j <= weeksChecked.length; ++ j) {
                            if (j == weeksChecked.length && weeksChecked[j - 1]) {
                                end = weeksChecked.length;
                                i = weeksChecked.length - 1;
                                break;
                            } else if (!weeksChecked[j]) {
                                end = j;
                                i = j;
                                break;
                            }
                        }
                        if(start==end)
                            s += "第" + start + "周 ";
                        else
                            s += "第" + start + "~" + end + "周 ";

                    }

                }
                if (weeksnum.size() > 0){
                    setWeek.setText(s);
                }else{
                    //没有选择
                    Toast.makeText(Activity_AddTodo.this, "未选择周数!", Toast.LENGTH_SHORT).show();
                }


            }
        });
        mutilChoicebuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        setWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mutilChoicebuilder.show();
            }
        });

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SAVE_SUCCESS:
                    count++;
                    if(count == weeksnum.size()){
                        Util_ToastUtils.showToast(Activity_AddTodo.this, "保存成功！");
                        finish();
                    }
                    break;
                case SAVE_FAILED:
                    Util_ToastUtils.showToast(Activity_AddTodo.this, "网络链接失败，重新试试？");
                default:
                    break;
            }
        }
    };


        public void save(View view) {
            if (title.getText() == null || title.getText().toString().equals("")) {
                builder = new AlertDialog.Builder(Activity_AddTodo.this)
                        .setTitle("温馨提示：")
                        .setMessage("标题不能为空哦！")
                        .setNegativeButton("知道了",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        builder.dismiss();
                                    }
                                }).show();
            }
            else {
                Log.e("id",userId);
                Calendar calendar = Calendar.getInstance();
                Date d = new Date(System.currentTimeMillis());
                for(int i = 0;i < weeksnum.size();i ++){
                    RequestBody requestBody = new FormBody.Builder()
                            .add("userID", userId)
                            .add("todoTitle", title.getText().toString())
                            .add("weekChosen", weeksnum.get(i) + "")
                            .add("dayChosen", calendar.get(Calendar.DAY_OF_WEEK) + "")
                            .add("timeSlot", timeslot+"")
                            .add("detailTime", "huikgiu")
                            .add("isClock", bisClock+"")
                            .add("content", content.getText().toString())
                            .build();   //构建请求体

                    //TODO
                    Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/addnewitem", requestBody, new okhttp3.Callback() {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            // 得到服务器返回的具体内容
                            boolean responseData = Boolean.parseBoolean(response.body().string());
                            Message message = new Message();
                            if (responseData) {
                                message.what = SAVE_SUCCESS;
                                handler.sendMessage(message);
                            } else {
                                message.what = SAVE_FAILED;
                                handler.sendMessage(message);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            // 在这里对异常情况进行处理
                        }
                    });
                }

            }
        }
    }
