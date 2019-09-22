package com.example.classchat.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.alibaba.fastjson.JSON;
import com.example.classchat.Object.Object_TodoList;
import com.example.classchat.R;
import com.example.classchat.Util.Util_NetUtil;
import com.example.classchat.Util.Util_ToastUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Activity_TodoDetail extends AppCompatActivity {

    private TextView funcTitle, setTimeSlot, setTime, setWeek;
    private Button back, edit, delete, save, picker_back, picker_save;
    private Object_TodoList memo;
    private EditText title, content;
    private Switch isClock;
    private Boolean bisClock;
    private String userID;
    private final static int SAVE_SUCCESS = 0;
    private final static int SAVE_FAILED = 1;
    private final static int DELETE_SUCCESS = 2;
    private final static int DELETE_FAILED = 3;
    //TODO 时间选择 、setTimeSlot(待办的位置，如 早课前、第1~2节)在编辑时的显示
    private Dialog timepicker_dialog;
    private Dialog slotpicker_dialog;
    private TimePicker timePicker;
    private NumberPicker dayPicker;
    private NumberPicker timeSlotPicker;
    private int dayOfweek_ = 1, dayOfweek;
    private int hour_ = 0, hour;
    private int minute__ = 0, minute_;
    private String[] weekdays = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期天"};

    private String[] slotPickerList = {"早课前","第1~2节", "第3~4节", "午休", "第5~6节", "第7~8节", "第9~10节", "第11~12节", "晚课后"};
    private int timeslot, timeslot_;

    //周数多选框
    private AlertDialog.Builder mutilChoicebuilder;
    //配合周数多选框的数组
    private final String[] weeks= new String[]{"第1周","第2周","第3周","第4周","第5周","第6周","第7周","第8周","第9周","第10周","第11周","第12周","第13周","第14周","第15周","第16周","第17周","第18周","第19周","第20周","第21周","第22周","第23周","第24周","第25周"};
    private boolean[] weeksChecked = new boolean[]{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};
    //周数数组
    List<Integer> weeksnum=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__todo_detail);
        Intent intent = getIntent();
        memo = JSON.parseObject(intent.getStringExtra("memo"), Object_TodoList.class);
        userID = memo.getUserID();
        bisClock = memo.isClock();
        setTimeSlot = findViewById(R.id.memo_timeslot);
        timeslot = memo.getTimeSlot();
        setTimeSlotText(timeslot);
        setTime = findViewById(R.id.get_todo_time);
        setWeek = findViewById(R.id.get_todo_week);
        isClock = findViewById(R.id.option_switch_isClock);
        isClock.setChecked(bisClock);
        isClock.setEnabled(false);
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
                isClock.setEnabled(true);
                isClock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        bisClock = isChecked;
                    }
                });
                //周数多选框
                mutilChoicebuilder = new AlertDialog.Builder(Activity_TodoDetail.this);
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
                            Toast.makeText(Activity_TodoDetail.this, "未选择周数!", Toast.LENGTH_SHORT).show();
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

                setTimeSlot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showTimeSlotPicker();
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO 修改
                        final RequestBody requestBody = new FormBody.Builder()
                                .add("userID", userID)
                                .add("todotitle", memo.getTodoTitle())
                                .add("weekChosen", "")//TODO 初始状态下原来选的周要勾
                                .add("dayChosen", dayOfweek + "")//TODO
                                .add("setTimeSlot", timeslot + "")//TODO
                                .add("detailTime", hour + " " + minute_)
                                .add("isClock", bisClock + "")
                                .add("content", content.getText().toString())
//                                .add("todoItemId", memo.getTodoItemId())
                                .build();   //构建请求体
//                        //TODO
                        Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/updatetodoitem", requestBody, new okhttp3.Callback() {
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                // 得到服务器返回的具体内容
                                boolean responseData = Boolean.parseBoolean(response.body().string());
                                Log.e("up",responseData+"");
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
                });


                delete.setVisibility(View.VISIBLE);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO 删除
                        Calendar calendar = Calendar.getInstance();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("userid", userID)
                                .add("todotitle", memo.getTodoTitle())
                                .add("weekchosen", "")//TODO
                                .add("daychosen", calendar.get(Calendar.DAY_OF_WEEK) +"")
                                //     .add("content", content.getText().toString())
                                .build();   //构建请求体
//                        //TODO
                        Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/deletetodoitem", requestBody, new okhttp3.Callback() {
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                // 得到服务器返回的具体内容
                                boolean responseData = Boolean.parseBoolean(response.body().string());
                                Log.e("del",responseData+"");
                                Message message = new Message();
                                if (responseData) {
                                    message.what = DELETE_SUCCESS;
                                    handler.sendMessage(message);
                                } else {
                                    message.what = DELETE_FAILED;
                                    handler.sendMessage(message);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                // 在这里对异常情况进行处理
                            }
                        });
                    }
                });
            }
        });
        funcTitle = findViewById(R.id.memo_func_title);
        funcTitle.setText("待办详情");
    }

    private void setTimeSlotText(int timeslot) {
        switch(timeslot){
            case 0:
                setTimeSlot.setText("早   课   前");
                break;
            case 3:
                setTimeSlot.setText("午   休");
                break;
            case 8:
                setTimeSlot.setText("晚   课   后");
                break;
            case 1:
            case 2:
                setTimeSlot.setText("第 " + (timeslot * 2 - 1) + " ~ " + (timeslot * 2) + "节");
                break;
            case 4:
            case 5:
            case 6:
            case 7:
                setTimeSlot.setText("第 " + ((timeslot - 1) * 2 - 1) + " ~ " + ((timeslot - 1) * 2) + "节");
                break;
        }
    }

    protected  void showTimeSlotPicker(){
        LayoutInflater inflater=LayoutInflater.from(Activity_TodoDetail.this);
        View myviewSlot =inflater.inflate(R.layout.dialog_slot_choose,null);
        final android.support.v7.app.AlertDialog.Builder builderSlot = new android.support.v7.app.AlertDialog.Builder(Activity_TodoDetail.this);

        timeSlotPicker = myviewSlot.findViewById(R.id.slot_picker);
        picker_back = myviewSlot.findViewById(R.id.back_from_pick_slot);
        picker_save = myviewSlot.findViewById(R.id.set_slot);

        builderSlot.setView(myviewSlot);
        slotpicker_dialog = builderSlot.create();
        slotpicker_dialog.show();

        timeSlotPicker.setDisplayedValues(slotPickerList);
        //设置最大最小值
        timeSlotPicker.setMinValue(0);
        timeSlotPicker.setMaxValue(slotPickerList.length - 1);
        //设置默认的位置
        timeSlotPicker.setValue(memo.getTimeSlot());
        //设置不可编辑
        timeSlotPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        timeSlotPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                timeslot_ = newVal;

            }
        });

        picker_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slotpicker_dialog.dismiss();
            }
        });

        picker_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeslot = timeslot_;
                setTimeSlotText(timeslot);
                slotpicker_dialog.dismiss();
            }
        });
    }
    protected void show_timePicker(){
        LayoutInflater inflater=LayoutInflater.from(Activity_TodoDetail.this);
        View myview =inflater.inflate(R.layout.dialog_time_choose,null);
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Activity_TodoDetail.this);

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
        dayPicker.setValue(1);
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
                hour = hour_;
                minute_ = minute__;
                dayOfweek = dayOfweek_;
                if(minute_ > 9)
                    setTime.setText(weekdays[dayOfweek - 1]+ "   " + hour + " : " + minute_);
                else
                    setTime.setText(weekdays[dayOfweek - 1]+ "     "+ hour + " : 0" + minute_);
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
                    Util_ToastUtils.showToast(Activity_TodoDetail.this, "修改成功！");
                    finish();
                    break;
                case SAVE_FAILED:
                case DELETE_FAILED:
                    Util_ToastUtils.showToast(Activity_TodoDetail.this, "网络链接失败，重新试试？");
                    break;
                case DELETE_SUCCESS:
                    Util_ToastUtils.showToast(Activity_TodoDetail.this, "删除成功！");
                    finish();
                    break;
                default:
                    break;
            }
        }
    };
}
