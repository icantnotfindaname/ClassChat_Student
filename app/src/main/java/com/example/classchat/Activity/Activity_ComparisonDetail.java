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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.classchat.Object.Object_Comparison;
import com.example.classchat.Object.Object_MiniTimeTable;
import com.example.classchat.R;
import com.example.classchat.Util.Util_NetUtil;
import com.example.classchat.Util.Util_ToastUtils;
import com.example.library_cache.Cache;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Activity_ComparisonDetail extends AppCompatActivity {
    private Button picker_back, picker_save, delete;
    private TextView setWeek, getTitle;
    private NumberPicker weekPicker;
    private String[] week = new String[25];
    private int weekTemp;
    private Dialog weekPickerDialog;
    private static final int COMPARE_TABLE = 1;
    private List<Object_Comparison> compareActivity = new ArrayList<>();
    private MiniTimetable mTimaTableView;
    private static List<Object_MiniTimeTable> mList;

    private int index;
    private Object_Comparison activity;
    private String title, comparisonID;
    private AlertDialog builder = null;
    private static final int UPDATE_COMPARISON = 0;
    private static final int GET_RESULT = 1;
    private static final int DELETE_SUCCESS = 2;
    private static final int DELETE_FAILED = 3;
    private static final int WRONG_TYPE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__comparison_detail);
        setWeek = findViewById(R.id.get_compare_week);
        getTitle = findViewById(R.id.get_activity_title);
        delete = findViewById(R.id.compare_delete);
        mTimaTableView = findViewById(R.id.mini_timetable);

        Intent intent = getIntent();
        compareActivity = (List<Object_Comparison>)intent.getSerializableExtra("activityList");
        index = Integer.parseInt(intent.getStringExtra("index"));
        activity = compareActivity.get(index);
        title = activity.getComparisonTitle();
        comparisonID = intent.getStringExtra("userId") + title;
        getTitle.setText(title);

        initList();
        mTimaTableView.setTimeTable(mList);

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
        setWeek.setText("第" + activity.getComparisonWeek() + "周");

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

                                        RequestBody requestBody = new FormBody.Builder()
                                                .add("comparisonID", comparisonID)
                                                .build();

                                        Log.e("comparisonID",comparisonID);
                                        Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/deletecomparison", requestBody,new Callback() {
                                            @Override
                                            public void onFailure(@NotNull Call call, @NotNull IOException e) {}

                                            @Override
                                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                                // 得到服务器返回的具体内容
                                                String responseData = response.body().string();
                                                Log.e("deletecomparison", responseData);

                                                Message message = new Message();
                                                if(Boolean.parseBoolean(responseData)){
                                                    message.what = DELETE_SUCCESS;
                                                    handler.sendMessage(message);
                                                }
                                                else {
                                                    message.what = DELETE_FAILED;
                                                    handler.sendMessage(message);
                                                }
                                            }
                                        });
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
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_COMPARISON:
                    importTable();
                    break;
                case GET_RESULT:
                    String comparisonData =activity.getComparisonData();
                    mList = new ArrayList<>();
                    List<Integer> cData = JSON.parseArray(comparisonData,Integer.class);
                    for(int n = 0; n < cData.size();n+=12) {
                        for (int i = n; i < n + 12; i++) {
                            if (cData.get(i) != 0) {
                                int low = i;
                                int up = i;
                                for (int ii = i + 1; ii < n + 12; ii++) {
                                    if (cData.get(ii) != cData.get(i)) {
                                        i = ii - 1;
                                        up = ii - 1;
                                        break;
                                    }
                                    else if (ii == n + 11){
                                        i = ii;
                                        up = ii;
                                    }
                                }
                                int week = (i + 1)/12 + 1;
                                int start = ((low+1) %12 == 0)? 12 :((low+1) %12 );
                                int end =((up+1) %12 == 0)? 12 :((up+1) %12 );
                                String name = cData.get(i).toString() + "人";
                                mList.add(new Object_MiniTimeTable(start, end, week, name));
                            }
                        }
                    }
                    mTimaTableView.refreshTimeTable(mList);

                    compareActivity.remove(index);
                    compareActivity.add(activity);
                    updateCache();
                    break;
                case DELETE_SUCCESS:
                    compareActivity.remove(index);
                    Log.e("AfterRefresh", compareActivity.toString());
                    updateCache();
                    finish();
                    break;
                case DELETE_FAILED:
                    Util_ToastUtils.showToast(Activity_ComparisonDetail.this, "删除失败请重试！");
                    break;
                case WRONG_TYPE:
                    Util_ToastUtils.showToast(Activity_ComparisonDetail.this, "宁扫的🐎不对哦！");
                default:
                    break;
            }
        }
    };

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
        weekPicker.setValue(activity.getComparisonWeek() - 1);
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
                //TODO 仅修改周数
                RequestBody requestBody = new FormBody.Builder()
                        .add("comparisonID", comparisonID)
                        .add("comparisonWeekChosen", setWeek.getText().toString().substring(1, setWeek.getText().toString().length() - 1))
                        .build();

                Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/updateweekchosen", requestBody,new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {}

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        // 得到服务器返回的具体内容
                        String responseData = response.body().string();
                        activity = JSON.parseObject(responseData, Object_Comparison.class);
                        Log.e("activityAfterUpdateWeek", activity.toString());

                        Message message = new Message();
                        message.what = GET_RESULT;
                        handler.sendMessage(message);
                    }
                });
            }
        });
    }

    private void updateCache(){
        Cache.with(Activity_ComparisonDetail.this)
                .path(getCacheDir(Activity_ComparisonDetail.this))
                .remove("compareTable");
        Cache.with(Activity_ComparisonDetail.this)
                .path(getCacheDir(Activity_ComparisonDetail.this))
                .saveCache("compareTable", JSON.toJSONString(compareActivity));
    }

    public void add(View view) {
        importTable();
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

                        RequestBody requestBody = new FormBody.Builder()
                                .add("comparisonID", comparisonID)
                                .add("otherUserID", content)
                                .build();

                        Log.e("other",content);
                        Log.e("weekChosen", setWeek.getText().toString().substring(1, setWeek.getText().toString().length() - 1));
                        Log.e("comparisonID",comparisonID);
                        Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/updatecomparison", requestBody,new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {}

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                // 得到服务器返回的具体内容
                                String responseData = response.body().string();
                                Message message = new Message();
                                Log.e("responseData372", responseData);
                                if(responseData.equals("ERROR")){
                                    message.what = WRONG_TYPE;
                                    handler.sendMessage(message);
                                }
                                else {
                                    activity = JSON.parseObject(responseData, Object_Comparison.class);
                                    Log.e("activityAfterUpdateScan", activity.toString());
                                    message.what = GET_RESULT;
                                    handler.sendMessage(message);
                                }

                            }
                        });
                    }
                }
                break;
            default:
                break;
        }
    }

    private void initList(){
        String comparisonData = activity.getComparisonData();
        mList = new ArrayList<>();
        List<Integer> cData = JSON.parseArray(comparisonData,Integer.class);
        for(int n = 0; n < cData.size();n+=12) {
            for (int i = n; i < n + 12; i++) {
                if (cData.get(i) != 0) {
                    int low = i;
                    int up = i;
                    for (int ii = i + 1; ii < n + 12; ii++) {
                        if (cData.get(ii) != cData.get(i)) {
                            i = ii - 1;
                            up = ii - 1;
                            break;
                        }
                        else if (ii == n + 11){
                            i = ii;
                            up = ii;
                        }
                    }
                    int week = (i + 1)/12 + 1;
                    int start = ((low+1) %12 == 0)? 12 :((low+1) %12 );
                    int end =((up+1) %12 == 0)? 12 :((up+1) %12 );
                    String name = cData.get(i).toString() + "人";
                    mList.add(new Object_MiniTimeTable(start, end, week, name));
                }
            }
        }
    }
}

