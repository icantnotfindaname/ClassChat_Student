package com.example.classchat.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.classchat.R;
import com.example.classchat.Util.Util_NetUtil;
import com.example.classchat.Util.Util_ToastUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
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
    private Button picker_back, picker_save;
    private EditText title, content;
    private Switch isClock;
    private Boolean bisClock = true;
    private TextView setTime;

    //时间选择
    private Dialog timepicker_dialog;
    private TimePicker timePicker;
    private  NumberPicker dayPicker;
    private Calendar calendar = Calendar.getInstance();
    private int dayOfweek_ = 1, dayOfweek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
    private int hour_ = 0, hour;
    private int minute__ = 0, minute_;
    private Boolean timeChecked = false;
    private String[] weekdays = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期天"};

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
        setTime = findViewById(R.id.get_todo_time);

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
            public void onClick(DialogInterface dialog, int which, boolean isChecked) { }
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

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            show_timePicker();
            }
        });


    }

    protected void show_timePicker(){
        LayoutInflater inflater=LayoutInflater.from(Activity_AddTodo.this);
        View myview =inflater.inflate(R.layout.dialog_time_choose,null);
        final android.support.v7.app.AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(Activity_AddTodo.this);

        timePicker = myview.findViewById(R.id.time_picker);
        dayPicker = myview.findViewById(R.id.day_picker);
        picker_back = myview.findViewById(R.id.back_from_pick);
        picker_save = myview.findViewById(R.id.set_time);



        builder.setView(myview);
        timepicker_dialog = builder.create();
        timepicker_dialog.show();



        dayPicker.setDisplayedValues(weekdays);
        //设置最大最小值
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(weekdays.length);
        //设置默认的位置

        dayPicker.setValue(dayOfweek);
        //这里设置为不循环显示，默认值为true
        dayPicker.setWrapSelectorWheel(true);
        //设置不可编辑
        dayPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        dayPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                dayOfweek_ = newVal;

            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                hour_ = hourOfDay;
                minute__ = minute;

            }
        });

        picker_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timepicker_dialog.dismiss();
            }
        });

        picker_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeChecked = true;
                 hour = hour_;
                 minute_ = minute__;
                 dayOfweek = dayOfweek_;
                Log.e("day", dayOfweek+"");
                Log.e("weekdays[]", weekdays[dayOfweek - 1]);
                if(minute_ > 9)  setTime.setText(weekdays[dayOfweek - 1]+ "   " + hour + " : " + minute_);
                 else setTime.setText(weekdays[dayOfweek - 1]+ "     "+ hour + " : 0" + minute_);
                 timepicker_dialog.dismiss();
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
                        Log.e("save", "保存");
                        finish();
                    }
                    break;
                case SAVE_FAILED:
                    Util_ToastUtils.showToast(Activity_AddTodo.this, "网络链接失败，重新试试？");
                    break;
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
            else if(!timeChecked){
                builder = new AlertDialog.Builder(Activity_AddTodo.this)
                        .setTitle("温馨提示：")
                        .setMessage("请填写时间哦！")
                        .setNegativeButton("知道了",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        builder.dismiss();
                                    }
                                }).show();
            }
            else {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                String todoItemId = simpleDateFormat.format(date);

                RequestBody requestBody = new FormBody.Builder()
                        .add("userID", userId)
                        .add("todoTitle", title.getText().toString())
                        .add("weekList", weeksnum + "")
                        .add("dayChosen", dayOfweek + "")
                        .add("timeSlot", timeslot+"")
                        .add("detailTime", hour + " " + minute_)
                        .add("isClock", bisClock+"")
                        .add("content", content.getText().toString())
                        .add("todoItemID", todoItemId)
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
