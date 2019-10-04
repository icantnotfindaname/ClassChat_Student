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
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

    private String mBeginClassTime = "";
    private int this_week;
    private String this_week_str;

    private static List<String> alarm_cache = new ArrayList<>();

    // 获取今天是第几周
    private int week = 0;

    // id
    private int id = 0;

    //周数多选框
    private AlertDialog.Builder multiChoiceBuilder;
    //配合周数多选框的数组
    private final String[] weeks= new String[]{"第1周","第2周","第3周","第4周","第5周","第6周","第7周","第8周","第9周","第10周","第11周","第12周","第13周","第14周","第15周","第16周","第17周","第18周","第19周","第20周","第21周","第22周","第23周","第24周","第25周"};
    private boolean[] weeksChecked = new boolean[]{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};
    //周数数组
    List<Integer> weeksnum=new ArrayList<>();
    AlertDialog builder=null;
    private String userId;
    private final static int SAVE_SUCCESS = 0;
    private final static int SAVE_FAILED = 1;
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

        //沉浸式状态栏
        if (Build.VERSION.SDK_INT >= 21) {
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
        mBeginClassTime = intent.getStringExtra("begin_time");
        if(mBeginClassTime == null || mBeginClassTime.length() <= 0){
            Util_ToastUtils.showToast(this, "请前往课程页面设置开学时间");
            mBeginClassTime = getDate(0) + " 00:00:00";
            finish();
        }

        week = ScheduleSupport.timeTransfrom(mBeginClassTime);
        setWeek.setText("第" + week + "周");
        weeksnum.add(week);
        for (int i = 0; i < weeksChecked.length; i++){
            if (i == week - 1){
                weeksChecked[i] = true;
            }
        }




        setWeek.setText("第" + week + "周");

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
        multiChoiceBuilder = new AlertDialog.Builder(this);
        multiChoiceBuilder.setTitle("选择周数");
        multiChoiceBuilder.setMultiChoiceItems(weeks, weeksChecked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) { }
        });
        multiChoiceBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
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

        multiChoiceBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        setWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multiChoiceBuilder.show();
            }
        });

        setTime.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
            show_timePicker();
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
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

        hour_ = timePicker.getHour();
        minute__ = timePicker.getMinute();

        dayPicker.setDisplayedValues(weekdays);
        //设置最大最小值
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(weekdays.length);
        //设置默认的位置
        dayPicker.setValue(dayOfweek);
        //这里设置为不循环显示，默认值为true
        dayPicker.setWrapSelectorWheel(true);
        //设置不可编辑
        dayOfweek_ = dayPicker.getValue();
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
                    Util_ToastUtils.showToast(Activity_AddTodo.this, "保存成功！");
                    if(bisClock == true)
                        if(NotificationManagerCompat.from(Activity_AddTodo.this).areNotificationsEnabled() == false) {
                            Toast.makeText(Activity_AddTodo.this, "未设置通知权限会导致提醒无效，请前往手机设置页面进行设置", Toast.LENGTH_LONG).show();
                        }else {
                            setAlarm();
                        }
                    finish();
                    break;
                case SAVE_FAILED:
                    Util_ToastUtils.showToast(Activity_AddTodo.this, "网络链接失败，重新试试？");
                    break;
                default:
                    break;
            }
        }
    };

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



    public void saveTodo(View view) {
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

            String weekString = "";
            for(int i =0 ;i < weeksnum.size(); ++i){
                if( i != weeksnum.size() - 1)
                    weekString += (weeksnum.get(i) +"a");
                else weekString += (weeksnum.get(i) +"");

            }
            Log.e("weekList", weekString);
            RequestBody requestBody = new FormBody.Builder()
                    .add("userID", userId)
                    .add("todoTitle", title.getText().toString())
                    .add("weekList", weekString)
                    .add("dayChosen", dayOfweek + "")
                    .add("timeSlot", timeslot+"")
                    .add("detailTime", hour + " " + minute_)
                    .add("isClock", bisClock+"")
                    .add("content", content.getText().toString())
                    .add("todoItemID", todoItemId)
                    .build();   //构建请求体


            Log.e("detailTime", hour + " " + minute_);

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
                 * 试图从缓存获取id
                 */
                try {
                    id = Cache.with(this).path(getCacheDir(this)).getCache("alarm_id", Integer.class);
                }catch (Exception e){
                    Log.e("试图获取缓存", "暂时还没有得到缓存");
                }

                /**
                 * 设置闹钟
                 */
                Object_Todo_Broadcast_container object_todo_broadcast_container = new Object_Todo_Broadcast_container(title1, l, week_, hour, minute_, detail, id);
                Log.e("TAG", object_todo_broadcast_container.getTitle());
                Log.e("id", id + "");
                AlarmTimer alarmTimer = new AlarmTimer(object_todo_broadcast_container);
                alarmTimer.setAlarm(this);

                id += 1;

                /**
                 *  Cache 里面保存的是一个数组，每一个位置是一串json的字符串
                 */
                String json_string = JSON.toJSONString(object_todo_broadcast_container);
                alarm_cache.add(json_string);
                Cache.with(this).path(getCacheDir(this)).saveCache("alarm", alarm_cache);

                /**
                 *  存id
                 */
                Cache.with(this).path(getCacheDir(this)).saveCache("alarm_id", id);

                Log.e("alarm_cache", alarm_cache.size() + "");
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

}
