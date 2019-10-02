package com.example.classchat.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.classchat.R;
import com.example.classchat.Util.Util_NetUtil;
import com.example.classchat.Util.Util_ToastUtils;
import com.example.library_activity_timetable.model.ScheduleSupport;
import com.example.library_cache.Cache;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Activity_ComparisonDetail extends AppCompatActivity {
    private Button picker_back, picker_save, delete;
    private EditText getTitle;
    private TextView setWeek;
    private NumberPicker weekPicker;
    private String[] week = new String[25];
    private int weekTemp;
    private Dialog weekPickerDialog;
    private String mBeginClassTime;
    private int currentWeek;
    private static final int COMPARE_TABLE = 1;
    private List<String> classBoxData = new ArrayList<>();
    private List<String> compareActivity = new ArrayList<>();
    private String compareActivityStr;
    private String title;
    private List<String> Temp = new ArrayList<>();
    private AlertDialog builder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__comparison_detail);
        setWeek = findViewById(R.id.get_compare_week);
        getTitle = findViewById(R.id.get_activity_title);
        delete = findViewById(R.id.compare_delete);


        Intent intent = getIntent();
        title = intent.getStringExtra("activity");
        getTitle.setText(title);
        getTitle.setEnabled(false);
        setWeek.setEnabled(false);

        //沉浸式状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            //After LOLLIPOP not translucent status bar
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //Then call setStatusBarColor.
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.theme));
        }

        //初始化周选择数组
        for(int i =0; i < week.length; ++i){
            week[i] = "第" + (i + 1) + "周";
        }
        //获取当前周
        mBeginClassTime = Cache.with(Activity_ComparisonDetail.this)
                .path(getCacheDir(Activity_ComparisonDetail.this))
                .getCache("BeginClassTime",String.class);
        if(mBeginClassTime == null || mBeginClassTime.length() <= 0){
            Calendar calendar=Calendar.getInstance();
            mBeginClassTime = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " 00:00:00";
        }
        currentWeek = ScheduleSupport.timeTransfrom(mBeginClassTime);
        setWeek.setText(week[currentWeek - 1]);

        compareActivityStr =(Cache.with(Activity_ComparisonDetail.this)
                .path(getCacheDir(Activity_ComparisonDetail.this))
                .getCache("compareActivityName", String.class));
        if(compareActivityStr != null)
            Temp = Arrays.asList(compareActivityStr.split("[,\\s+]"));
        for(String item:Temp)
            compareActivity.add(item);
        compareActivity.remove("null");
        compareActivity.remove(",");
        Log.e("DetailonCreate111",compareActivity.toString());

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(Activity_ComparisonDetail.this)
                        .setTitle("温馨提示：")
                        .setMessage("宁真的要删除🐎？")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        removeCompareActivityDetailCache();
                                        removeCompareActivityListCache();
                                        String s = (Cache.with(Activity_ComparisonDetail.this)
                                                .path(getCacheDir(Activity_ComparisonDetail.this))
                                                .getCache("compareActivityName", String.class));
                                        if(s != null)Log.e("s", s);
                                        Log.e("beforeDelete119", compareActivity.toString());
                                        Log.e("beforeDelete120", compareActivity.size()+"");
                                        //TODO 逗号删除不净
                                        compareActivity.remove(getTitle.getText().toString());
                                        Log.e("afterDeleteSize121", compareActivity.toString().length()+"");
                                        Log.e("afterDelete122", compareActivity.toString());
                                        Cache.with(Activity_ComparisonDetail.this)
                                                .path(getCacheDir(Activity_ComparisonDetail.this))
                                                .saveCache("compareActivityName", compareActivity.size() > 0? compareActivity.toString().substring(1,compareActivity.toString().length() - 1):",");
                                        finish();
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
        //TODO
    }

    public void back(View view) {
        finish();
    }

    public void pickWeek(View view) {
        LayoutInflater inflater=LayoutInflater.from(Activity_ComparisonDetail.this);
        View myview = inflater.inflate(R.layout.dialog_comparison_week_picker,null);
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Activity_ComparisonDetail.this);
        picker_back = myview.findViewById(R.id.back_from_pick);
        picker_save = myview.findViewById(R.id.set_time);
        weekPicker = myview.findViewById(R.id.week_picker);
        builder.setView(myview);
        weekPickerDialog = builder.create();
        weekPickerDialog.show();
        weekPicker.setDisplayedValues(week);
        //设置最大最小值
        weekPicker.setMinValue(0);
        weekPicker.setMaxValue(week.length - 1);
        //设置默认的位置
        weekPicker.setValue(currentWeek - 1);
        weekTemp = weekPicker.getValue();
        weekPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        weekPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                weekTemp = newVal;
            }
        });

        picker_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekPickerDialog.dismiss();
            }
        });

        picker_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWeek.setText(week[weekTemp]);
                weekPickerDialog.dismiss();
            }
        });
    }

    private boolean isValid(){
        if(!getTitle.getText().toString().equals("")) {
            if(!compareActivity.contains(getTitle.getText().toString())){
                return true;
            }else {
                Util_ToastUtils.showToast(Activity_ComparisonDetail.this, "此活动已存在，请修改活动名称");
                return false;
            }
        }
        else {
            Util_ToastUtils.showToast(Activity_ComparisonDetail.this, "活动名称不能为空！");
            return false;
        }
    }

    public void add(View view) {
        if(isValid()){
            importTable();
        }
    }

    public void save(View view) {
        //获得数据后存入缓存
        if(isValid()) {
            Cache.with(Activity_ComparisonDetail.this)
                    .path(getCacheDir(Activity_ComparisonDetail.this))
                    .saveCache("compare" + getTitle.getText().toString(), classBoxData);
            compareActivityStr += "," + getTitle.getText().toString();
            Cache.with(Activity_ComparisonDetail.this)
                    .path(getCacheDir(Activity_ComparisonDetail.this))
                    .saveCache("compareActivityName", compareActivityStr);
            finish();
        }
    }



    private void removeCompareActivityDetailCache(){
        Cache.with(Activity_ComparisonDetail.this)
                .path(getCacheDir(Activity_ComparisonDetail.this))
                .remove("compare" + getTitle.getText().toString());
    }

    private void removeCompareActivityListCache(){
        Cache.with(Activity_ComparisonDetail.this)
                .path(getCacheDir(Activity_ComparisonDetail.this))
                .remove("compareActivityName");
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
    public static void sendOKHTTPRequest(String address, okhttp3.RequestBody requestBody, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    // 带有Requestbody的get请求
    public static void sendOKHTTPRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }


    private void importTable(){
        Intent intent = new Intent(Activity_ComparisonDetail.this, CaptureActivity.class);
        /*ZxingConfig是配置类
         *可以设置是否显示底部布局，闪光灯，相册，
         * 是否播放提示音  震动
         * 设置扫描框颜色等
         * 也可以不传这个参数
         * */
        ZxingConfig config = new ZxingConfig();
        config.setPlayBeep(true);//是否播放扫描声音 默认为true
        config.setShake(true);//是否震动  默认为true
        config.setDecodeBarCode(true);//是否扫描条形码 默认为true
        config.setReactColor(R.color.theme);//设置扫描框四个角的颜色 默认为白色
        config.setFrameLineColor(R.color.theme);//设置扫描框边框颜色 默认无色
        config.setScanLineColor(R.color.theme);//设置扫描线的颜色 默认白色
        config.setFullScreenScan(true);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
        startActivityForResult(intent, COMPARE_TABLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case COMPARE_TABLE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        String content = data.getStringExtra(Constant.CODED_CONTENT);

                        Util_NetUtil.sendOKHTTPRequest(content, new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {}

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                // 得到服务器返回的具体内容
                                String responseData = response.body().string();
                                //TODO 对数据classBoxData进行简化
                                classBoxData.add(responseData);
                                // 转化为具体的对象列表
                                List<String> jsonlist = JSON.parseArray(responseData, String.class);
                            }
                        });
                    }
                }
                break;
            default:
                break;
        }
    }


    public void edit(View view) {
        setWeek.setEnabled(true);
        getTitle.setEnabled(true);
    }
}

