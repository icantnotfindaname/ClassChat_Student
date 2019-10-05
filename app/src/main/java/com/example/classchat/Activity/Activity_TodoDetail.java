package com.example.classchat.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.example.classchat.Object.Object_Todo_Broadcast_container;
import com.example.classchat.R;
import com.example.classchat.Util.AlarmTimer;
import com.example.classchat.Util.Util_NetUtil;
import com.example.classchat.Util.Util_ToastUtils;
import com.example.library_activity_timetable.model.ScheduleSupport;
import com.example.library_cache.Cache;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Activity_TodoDetail extends AppCompatActivity {

    private TextView setTimeSlot, setTime, setWeek;
    private Button back, edit, delete, save, picker_back, picker_save;
    private Object_TodoList memo;
    private EditText title, content;
    private Switch isClock;
    private Boolean bisClock;
    private String userID, todoID;
    private final static int SAVE_SUCCESS = 0;
    private final static int SAVE_FAILED = 1;
    private final static int DELETE_SUCCESS = 2;
    private final static int DELETE_FAILED = 3;
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
    private final String[] weeks = new String[]{"第1周","第2周","第3周","第4周","第5周","第6周","第7周","第8周","第9周","第10周","第11周","第12周","第13周","第14周","第15周","第16周","第17周","第18周","第19周","第20周","第21周","第22周","第23周","第24周","第25周"};
    private boolean[] weeksChecked = new boolean[25];
    //周数数组
    List<Integer> weeksnum = new ArrayList<>();

    //删除模式选择
    private AlertDialog builder = null;
    private boolean isSeeAll = false;

    // 获取今天是第几周
    private int week = 0;
    private String mBeginClassTime = "";

    // 闹钟缓存的数组
    private List<String> alarm = new ArrayList<>();

    // 闹钟的id
    private int alarm_id = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__todo_detail);

        //沉浸式状态栏
        if(Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            //After LOLLIPOP not translucent status bar
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //Then call setStatusBarColor.
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.theme));
        }

        /**
         * 获取当前周
         */
        Intent intent = getIntent();
        mBeginClassTime = Cache.with(this)
                .path(getCacheDir(this))
                .getCache("BeginClassTime",String.class);

        if(mBeginClassTime == null || mBeginClassTime.length() <= 0){
            mBeginClassTime = getDate(0) + " 00:00:00";
        }
        week = ScheduleSupport.timeTransfrom(mBeginClassTime);

        memo = JSON.parseObject(intent.getStringExtra("memo"), Object_TodoList.class);
        isSeeAll = Boolean.parseBoolean(intent.getStringExtra("isSeeAll"));
        userID = memo.getUserID();
        todoID = (memo.getWeekChosen().get(0) < 10)? userID + "0" + memo.getWeekChosen().get(0) + memo.getDayChosen(): userID + memo.getWeekChosen().get(0) + memo.getDayChosen();

        bisClock = memo.isClock();


        /**
         *  获取闹钟的缓存
         */
        alarm = Cache.with(this).path(getCacheDir(this)).getCache("alarm", List.class);

        /**
         *  获取id的缓存
         */
        try {
            alarm_id = Cache.with(this).path(getCacheDir(this)).getCache("alarm_id", Integer.class);
        }catch (Exception e){
            Log.e("试图获取缓存", "暂时还没有得到缓存");
        }

        //初始化已选周数列表
        for(int i = 0; i < 25; ++ i){
            weeksChecked[i] = false;
        }
        for(int i = 0; i < memo.getWeekChosen().size(); ++i){
            weeksChecked[memo.getWeekChosen().get(i) - 1] = true;
        }

        setTimeSlot = findViewById(R.id.memo_timeslot);
        timeslot = memo.getTimeSlot();
        dayOfweek = memo.getDayChosen();
        setTimeSlotText(timeslot);
        setTime = findViewById(R.id.get_todo_time);
        setTime.setEnabled(false);

        String[]detailTime = (memo.getDetailTime() + "").split(" ");
        String hr = detailTime[0];
        String min = detailTime[1];
        if(Integer.parseInt(min) > 9)
            setTime.setText(weekdays[memo.getDayChosen() - 1]+ "     " + hr + " : " + min);
        else
            setTime.setText(weekdays[memo.getDayChosen() - 1]+ "     "+ hr + " : 0" + min);

        setWeek = findViewById(R.id.get_todo_week);
        setWeek.setText(setWeekTextView());
        setWeek.setEnabled(false);
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
        save = findViewById(R.id.memo_done);
        save.setVisibility(View.GONE);
        edit = findViewById(R.id.memo_edit);
        edit.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(Activity_TodoDetail.this)
                        .setTitle("温馨提示：")
                        .setMessage("宁真的要删除🐎？")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        final RequestBody requestBody = new FormBody.Builder()
                                                .add("todoID", todoID)
                                                .add("todoItemID", memo.getTodoItemID())
                                                .build();   //构建请求体
                                        if(!isSeeAll){
                                            final int[] choice = new int[1];
                                            final int[] index = new int[1];
                                            builder = new AlertDialog.Builder(Activity_TodoDetail.this)
                                                    .setTitle("删除")
                                                    .setSingleChoiceItems(new CharSequence[] { "仅删除本周", "删除所有周" }, 0, new DialogInterface.OnClickListener() {//添加单选框
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            index[0] = i;
                                                        }
                                                    })
                                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            choice[0] = index[0];


                                                            String address;

                                                            switch(choice[0]){
                                                                case 1:
                                                                    //todo
                                                                    address = "http://106.12.105.160:8081/deletesametodoitem";
                                                                    if (memo.isClock() == true){
                                                                        deleteAlarm(memo);
                                                                    }
                                                                    break;
                                                                default:
                                                                    address = "http://106.12.105.160:8081/deletetodoitem";
                                                                    if (memo.isClock() == true){
                                                                        deleteAlarm_one(week, memo);
                                                                    }
                                                                    break;
                                                            }

                                                            Util_NetUtil.sendOKHTTPRequest(address, requestBody, new okhttp3.Callback() {
                                                                @Override
                                                                public void onResponse(Call call, Response response) throws IOException {
                                                                    // 得到服务器返回的具体内容
                                                                    boolean responseData = Boolean.parseBoolean(response.body().string());
                                                                    Message message = new Message();
                                                                    if (responseData) {
                                                                        message.what = DELETE_SUCCESS;
                                                                        handler.sendMessage(message);
                                                                        deleteAlarm(memo);
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

                                                            builder.dismiss();

                                                        }
                                                    })

                                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加取消
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            builder.dismiss();
                                                        }
                                                    }).show();
                                        }else {
                                            Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/deletesametodoitem", requestBody, new okhttp3.Callback() {
                                                @Override
                                                public void onResponse(Call call, Response response) throws IOException {
                                                    // 得到服务器返回的具体内容
                                                    boolean responseData = Boolean.parseBoolean(response.body().string());
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
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        builder.dismiss();
                                    }
                                }).show();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edit.setVisibility(View.GONE);
                save.setVisibility(View.VISIBLE);
                content.setEnabled(true);
                isClock.setEnabled(true);
                title.setEnabled(true);
                setWeek.setEnabled(true);
                setTime.setEnabled(true);
                setTimeSlot.setEnabled(true);
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

                        if (weeksnum.size() > 0){
                            setWeek.setText(setWeekTextView());
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
                    @RequiresApi(api = Build.VERSION_CODES.M)
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
                        String newWeekString = setWeekString(weeksnum);
                        String oldWeekString = setWeekString(memo.getWeekChosen());

                        final RequestBody requestBody = new FormBody.Builder()
                                .add("userID", userID)
                                .add("todoTitle", title.getText().toString())
                                .add("newWeekList", newWeekString)
                                .add("oldWeekList", oldWeekString)
                                .add("dayChosen", dayOfweek + "")
                                .add("timeSlot", timeslot + "")
                                .add("detailTime", hour + " " + minute_)
                                .add("isClock", bisClock + "")
                                .add("content", content.getText().toString())
                                .add("todoItemID", memo.getTodoItemID())
                                .build();   //构建请求体
//                        //TODO
                        Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/updatetodoitem", requestBody, new okhttp3.Callback() {
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
                });
            }
        });
    }

    private String setWeekTextView(){
        String s = new String();
        weeksnum = new ArrayList<>();
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
        return s;
    }

    private String setWeekString(List<Integer> wl){
        String str = "";
        for(int i =0 ;i < wl.size(); ++i){
            if( i != wl.size() - 1)
                str += (wl.get(i) +"a");
            else str += (wl.get(i) +"");
        }
        return str;
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
        timeSlotPicker.setValue(timeslot);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
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

        hour_ = timePicker.getHour();
        minute__ = timePicker.getMinute();

        dayPicker.setDisplayedValues(weekdays);
        //设置最大最小值
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(weekdays.length);
        //设置默认的位置
        dayPicker.setValue(dayOfweek);
        dayOfweek_ = dayPicker.getValue();
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
                    setTime.setText(weekdays[dayOfweek - 1]+ "     " + hour + " : " + minute_);
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
                    save.setVisibility(View.GONE);
                    content.setEnabled(false);
                    isClock.setEnabled(false);
                    title.setEnabled(false);
                    setWeek.setEnabled(false);
                    setTime.setEnabled(false);
                    setTimeSlot.setEnabled(false);
                    edit.setVisibility(View.VISIBLE);
                    Util_ToastUtils.showToast(Activity_TodoDetail.this, "修改成功！");
                    deleteAlarm(memo);
                    if ( bisClock == true ){
                        Log.e("setAlarm", "start");
                        setAlarm();
                    }
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

    /**
     * 设置闹钟
     */
    private void setAlarm(){
        // 获取选择的星期几
        int []week_day_list = { 0, 1, 2, 3, 4, 5, 6, 7 };
        int weekday_choose = week_day_list[dayOfweek];

        // 获取选择的时间 xx时xx分 已经是24小时表示的了
        // hour, minute_

        // 获取current日期 年/月/日 /星期
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int index = calendar.get(Calendar.DAY_OF_WEEK);  // 从星期日开始算
        Log.e("星期几", index + "");
        int []week_day_list1 = { 0, 7, 1, 2, 3, 4, 5, 6, 7 };
        int weekday = week_day_list1[index] ;

        // 获取起始周
        int first_week = weeksnum.get(0);

        // 获取选择的周列表
        int[] list = new int[weeksnum.size()];
        for( int i = 0 ; i < weeksnum.size() ; i++ ){
            list[i] = weeksnum.get(i);
        }

        // 获取通知显示的标题
        String title1 = title.getText().toString();

        // 获取通知显示的具体内容（可能为空）
        String detail = content.getText().toString();
        Log.e("detail", detail);

        /**
         * 设置多个闹钟
         */
        for (int i = 0; i < list.length; i++){
            int week_ = list[i];
            if(week_ < week || (week_ == week && weekday_choose < weekday) || (week_ == week && weekday_choose == weekday && hour < calendar.get(Calendar.HOUR_OF_DAY)) ||
                    (week_ == week && weekday_choose == weekday && hour == calendar.get(Calendar.HOUR_OF_DAY) && minute_ < calendar.get(Calendar.MINUTE))){

            }else{
                /**
                 * 计算闹钟的日期
                 */
                long l = calculateDate(year, month, day, weekday, hour, minute_, weekday_choose, week_, week);

                /**
                 * 设置闹钟
                 */
                Object_Todo_Broadcast_container object_todo_broadcast_container = new Object_Todo_Broadcast_container(title1, l, week, hour, minute_, detail, alarm_id);
                Log.e("TAG", object_todo_broadcast_container.getTitle());
                AlarmTimer alarmTimer = new AlarmTimer(object_todo_broadcast_container);
                alarmTimer.setAlarm(this);

                alarm_id += 1;

                /**
                 *  Cache 里面保存的是一个数组，每一个位置是一串json的字符串
                 */
                String json_string = JSON.toJSONString(object_todo_broadcast_container);
                alarm.add(json_string);
                Cache.with(this).path(getCacheDir(this)).saveCache("alarm", alarm);

                /**
                 *  存id
                 */
                Cache.with(this).path(getCacheDir(this)).saveCache("alarm_id", alarm_id);

                Log.e("alarm_cache", alarm.size() + "");
            }
        }

    }

    private long calculateDate(int year, int month, int day, int weekday, int hour, int minute, int weekday_choose, int first_week, int week) {
        int distance_week = first_week - week ;  // 相隔的周数
        int distance_weekday = weekday_choose - weekday ;  // 相隔的天数

        int distance_day = distance_week * 7 + distance_weekday;
        long distance_mills = day_to_mills(distance_day, hour, minute);

        Log.e("distance_mills", distance_mills + "");
        Log.e("first_week", first_week + "");
        Log.e("week", week + "");
        Log.e("year", year + "");
        Log.e("month", month + "");
        Log.e("day", day + "");
        Log.e("weekday", weekday + "");
        Log.e("weekday_choose", weekday_choose + "");
        Log.e("hour", hour + "");
        Log.e("minute", minute + "");

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        long begin_mills = calendar.getTimeInMillis();

        Log.e("begin_mills", begin_mills + "");
        long result_mills = begin_mills + distance_mills;
        return result_mills;
    }

    private long day_to_mills(int day, int hour, int minute){
        long mills = day * 24 * 60 * 60 * 1000 + hour * 60 * 60 * 1000 + minute * 60 * 1000 ;
        return mills;
    }

    public static String getDate(int distanceDay) {
        SimpleDateFormat dft = new SimpleDateFormat("MM-dd");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dft.format(endDate);
    }

    /*
     * 获得缓存地址
     * */
    public String getCacheDir(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    /**
     *  删除单个闹钟
     */
    private void deleteAlarm_one(int week, Object_TodoList memo){
        Log.e("deleteAlarm_one", "start");
        if(alarm != null){
            for(int i = 0; i < alarm.size() ; i++){
                Object_Todo_Broadcast_container obj = JSON.parseObject(alarm.get(i), Object_Todo_Broadcast_container.class);
                String detail_time = obj.getHour() + " " + obj.getMinute() ;
                Log.e("detail_time", detail_time);
                if (obj.getTitle().equals(memo.getTodoTitle()) && obj.getDetail().equals(memo.getContent()) && detail_time.equals(memo.getDetailTime()) && week == obj.getWeek()){
                    AlarmTimer alarmTimer = new AlarmTimer(obj);
                    alarmTimer.cancelAlarmTimer(this);
                    Log.e("delete_id", obj.getId() + "");
                }
            }
        }
    }

    /**
     * 删除多个闹钟
     */
    private void deleteAlarm(Object_TodoList memo){
        Log.e("deleteAlarm", "start");
        if (alarm != null){
            for(int i = 0; i < alarm.size() ; i++){
                Object_Todo_Broadcast_container obj = JSON.parseObject(alarm.get(i), Object_Todo_Broadcast_container.class);
                String detail_time = obj.getHour() + " " + obj.getMinute() ;
                Log.e("detail_time", detail_time);
                if (obj.getTitle().equals(memo.getTodoTitle()) && obj.getDetail().equals(memo.getContent()) && detail_time.equals(memo.getDetailTime())){
                    AlarmTimer alarmTimer = new AlarmTimer(obj);
                    alarmTimer.cancelAlarmTimer(this);
                    Log.e("delete_id", obj.getId() + "");
                }
            }
        }
    }

}
