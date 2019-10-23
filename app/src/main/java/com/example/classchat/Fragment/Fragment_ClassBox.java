package com.example.classchat.Fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.classchat.Activity.Activity_AddSearchCourse;
import com.example.classchat.Activity.Activity_AutoPullCourseFromWeb;
import com.example.classchat.Activity.Activity_CompareTable;
import com.example.classchat.Activity.Activity_CourseNote;
import com.example.classchat.Activity.MainActivity;
import com.example.classchat.Object.MySubject;
import com.example.classchat.R;
import com.example.classchat.Util.Util_NetUtil;
import com.example.classchat.Util.Util_ToastUtils;
import com.example.library_activity_timetable.Activity_TimetableView;
import com.example.library_activity_timetable.listener.ISchedule;
import com.example.library_activity_timetable.listener.IWeekView;
import com.example.library_activity_timetable.model.Schedule;
import com.example.library_activity_timetable.view.WeekView;
import com.example.library_cache.Cache;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;
import com.yzq.zxinglibrary.encode.CodeCreator;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import static android.app.Activity.RESULT_OK;
import static com.example.classchat.Util.Util_getSerialNumber.getSerialNumber;
import static io.rong.imkit.RongIM.connect;


public class Fragment_ClassBox extends Fragment implements OnClickListener {

    private Boolean isShowNoThisWeek = false;
    private Boolean isShowWeekend = true;

    private AlertDialog.Builder alertBuilder;

    //初始化照片URI
    private Uri imageUri;

    //初始化 登录等待 控件
    private ProgressDialog loadingForSignIn;

    private static final String TAG = "Activity_Main_Timetable";
    private static final String KEY_OF_REMINDER = "bianqian7456547";
    private static final String ID_OF_REMINDER = "reminder";

    public JSONObject groupChatManager = new JSONObject();

    //百度地图客户端
    private LocationClient client;
    private double now_longitude;
    private double now_latitude;

    public JSONObject signstatus = new JSONObject();
    public JSONObject getSignstatus() {
        return signstatus;
    }

    //周数多选框
    private AlertDialog.Builder mutilChoicebuilder;

    private  final String[] weeks= new String[]{"第1周","第2周","第3周","第4周","第5周","第6周","第7周","第8周","第9周","第10周","第11周","第12周","第13周","第14周","第15周","第16周","第17周","第18周","第19周","第20周","第21周","第22周","第23周","第24周","第25周"};
    private  boolean[] weeksChecked = new boolean[]{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};
    List<Integer> weeksnum=new ArrayList<>();

    //控件
    Activity_TimetableView mTimetableView;
    WeekView mWeekView;
    ImageButton moreButton;
    ImageButton scanButton;
    ImageButton guideButton;
    LinearLayout layout;
    TextView titleTextView;
    List<MySubject> mySubjects = new ArrayList<MySubject>();

    //学生聊天时所需的token
    String token = "";

    //学生ID
    private String userId;

    // 学生专业
    private String proUni;

    public String getProUni() {
        return proUni;
    }

    // 头像Url
    private String imageUrl;

    // 真人头像
    private String headUrl;

    private Bitmap bitmap;

    // 学生真实姓名
    private String realName;

    // 学生是否实名认证
    private Boolean isAuthentation;

    // 对话框
    Dialog coursedetail_dialog;
    Dialog editreminder_dialog;
    Dialog reminder_dialog;

    //记录切换的周次，不一定是当前周
    int target = -1;

    // 搞一个自己的变量
    Fragment_ClassBox myContext = this;

    // 缓存
    private String maddClassBoxData="";
    private String mClassBoxData = "";
    private String mBeginClassTime = "";

    private Boolean diegod = true;

    private UPDATEcastReceiver updatereceiver;
    private UpdateStateReceiver updateStateReceiver;
    private UpdateGroupIdReceiver updateGroupIdReceiver;
    private LocalBroadcastManager localBroadcastManager;

    private Context mcontext;
    private ImageView qrcode;

    private static final int SCAN_TABLE = 2;

    private static final int INIT_TABLE = 1;
    private static final int LOCATION_CORRECT = 2;
    private static final int LOCATION_WRONG = 3;
    private static final int SIGN_IN_SUCCESSED = 4;
    private static final int GET_LOCATION = 5;
    private static final int SIGN_IN_FAILED = 6;
    private static final int UPDATE_TABLE = 7;

    //扫一扫模式选择
    private AlertDialog builder = null;

    /*
    设置handler接收网络线程的信号并处理
     */
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case INIT_TABLE:
                    initTimetableView();
                    break;
                case LOCATION_CORRECT:
                    Toast.makeText(getContext() , "位置校验无误" , Toast.LENGTH_SHORT).show();
                    //更新签到数据
                    RequestBody requestBody = new FormBody.Builder()
                            .add("groupId", signstatus.getString("groupId"))
                            .add("userId" , userId)
                            .add("tablename", proUni)
                            .build();
                    Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/course/updatesignstatus", requestBody, new okhttp3.Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) { }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            Message message = new Message();
                            message.what = SIGN_IN_SUCCESSED;
                            handler.sendMessage(message);
                        }
                    });
                    break;
                case LOCATION_WRONG:
                    Util_ToastUtils.showToast(getContext() , "位置有误，签到失败！");
                    break;
                case SIGN_IN_SUCCESSED:
                    loadingForSignIn.dismiss();
                    Util_ToastUtils.showToast(getContext(),"签到成功！");
                    break;
                case GET_LOCATION:
                    client = new LocationClient(getContext());
                    client.registerLocationListener(new MyLocationListener());
                    initclient();
                    client.start();
                    loadingForSignIn = new ProgressDialog(getContext());  //初始化等待动画
                    loadingForSignIn.setCanceledOnTouchOutside(false); //
                    loadingForSignIn.setMessage("正在获取位置....");  //等待动画的标题
                    loadingForSignIn.show();  //显示等待动画
                    break;
                case SIGN_IN_FAILED:
                    Util_ToastUtils.showToast(getContext(),"签到失败");
                    break;
                case UPDATE_TABLE:
                    //这里需要向服务器发出这些课程，服务器再返回合格的mysubjects
                    RequestBody requestBody1 = new FormBody.Builder()
                            .add("userId", userId)
                            .add("subjects", JSON.toJSONString(mySubjects))
                            .add("tablename" , proUni)
                            .build();
                    Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/autoupdatecourse", requestBody1, new okhttp3.Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) { }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException { }
                    });
                default:
                    break;
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.activity__main__timetable, container, false);

    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //将判断签到状态设置好
        mcontext = this.getActivity();
        MainActivity mainActivity = (MainActivity)getActivity();
        headUrl = mainActivity.getHeadUrl();
        userId = mainActivity.getId();
        isAuthentation = mainActivity.getAuthentation();
        realName = mainActivity.getRealName();
        proUni = mainActivity.getProUni();
        token = mainActivity.getToken();
        imageUrl = mainActivity.getImageUrl();
        scanButton = getActivity().findViewById(R.id.id_scan);
        moreButton = getActivity().findViewById(R.id.id_more);
        guideButton = getActivity().findViewById(R.id.btn_guide);
        moreButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopmenu();
            }
        });
        scanButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 这里面写扫一扫的逻辑
                final int[] choice = new int[1];
                final int[] index = new int[1];
                builder = new AlertDialog.Builder(getContext())
                        .setTitle("选择模式")
                        .setSingleChoiceItems(new CharSequence[] { "导入课表", "对比课表" }, 0, new DialogInterface.OnClickListener() {//添加单选框
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                index[0] = i;
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                choice[0] = index[0];
                                switch(choice[0]){
                                    case 1:
                                        Intent intent = new Intent(getActivity(),Activity_CompareTable.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("userId", userId);
                                        intent.putExtras(bundle);
                                        getActivity().startActivity(intent);
                                        break;
                                    default:
                                        importTable();
                                        break;
                                }
                                builder.dismiss();
                            }
                        })

                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加取消
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                builder.dismiss();
                            }
                        }).show();


            }
        });

        /**
         * 指引的点击事件
         */
        guideButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                show_guide_dialog();
            }
        });

        titleTextView = getActivity().findViewById(R.id.id_title);
        layout = getActivity().findViewById(R.id.id_layout);
        layout.setOnClickListener(this);

        //广播接收
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());

        // 加载页面的广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.broadcasttest.LOCAL_BROADCAST1");
        updatereceiver = new UPDATEcastReceiver();
        localBroadcastManager.registerReceiver(updatereceiver, intentFilter);

        // 实名认证的广播
        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("com.example.broadcasttest.UPDATE_STATE");
        updateStateReceiver = new UpdateStateReceiver();
        localBroadcastManager.registerReceiver(updateStateReceiver, intentFilter1);

        // 获得GroupId的广播
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("com.example.broadcasttest.LOCAL_BROADCAST2");
        updateGroupIdReceiver = new UpdateGroupIdReceiver();
        localBroadcastManager.registerReceiver(updateGroupIdReceiver, intentFilter2);

        initClassBoxData();

        initTimetableView();
    }

    Dialog dialog;
    private void show_guide_dialog() {
        // 自定义对话框
        LayoutInflater inflater= LayoutInflater.from(getContext());
        View myView = inflater.inflate(R.layout.dialog_guide_classbox,null);//引用自定义布局
//        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
//        builder.setView(myView);
//        dialog = builder.create();  //创建对话框
        dialog = new Dialog(getActivity(),R.style.dialog);
        dialog.setContentView(myView);
        dialog.show();  //显示对话框

        Window dialogWindow = dialog.getWindow();
        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.7); // 高度设置为屏幕的0.6，根据实际情况调整
        p.width = (int) (d.getWidth() * 0.7); // 宽度设置为屏幕的0.65，根据实际情况调整
        dialogWindow.setAttributes(p);
    }

    private void importTable(){
        if (!isAuthentation) {
            Toast.makeText(getContext(), "请先实名认证！", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getContext(), CaptureActivity.class);
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
            startActivityForResult(intent, SCAN_TABLE);
        }
    }

    /**
     * 数据读取与存储
     */
    private void initClassBoxData(){


//        Cache.with(myContext.getActivity())
//                .path(getCacheDir(myContext.getActivity()))
//                .remove("classBox");

        mClassBoxData = Cache.with(this.getActivity())
                .path(getCacheDir(this.getActivity()))
                .getCache("classBox", String.class);

        //TODO 添加memo
        if (mClassBoxData == null||mClassBoxData.length() <= 0){
            //TODO  mClassBoxData=接收的json字符串
            // 请求网络方法，获取数据
            System.out.println(userId);
            RequestBody requestBody = new FormBody.Builder()
                    .add("userId", userId)
                    .build();
            Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/getallcourse/student", requestBody,new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    // 得到服务器返回的具体内容
                    String responseData = response.body().string();
                    System.out.println(responseData);
                    mClassBoxData = responseData;
                    // 转化为具体的对象列表
                    List<String> jsonlist = JSON.parseArray(responseData, String.class);
                    mySubjects.clear();
                    for(String s : jsonlist) {
                        MySubject mySubject = JSON.parseObject(s, MySubject.class);
                        mySubjects.add(mySubject);
                    }
                    //获得数据后存入缓存
                    Cache.with(myContext.getActivity())
                            .path(getCacheDir(myContext.getActivity()))
                            .remove("classBox");

                    Cache.with(myContext.getActivity())
                            .path(getCacheDir(myContext.getActivity()))
                            .saveCache("classBox", mClassBoxData);

                    // 获取课程id 和未读消息数的 Key Value 关系
                    groupChatManager = getGroupChatManager(mySubjects);

                    Message message = new Message();
                    message.what = INIT_TABLE;
                    handler.sendMessage(message);

                    // 发送登录聊天的广播
                    Intent intent2 = new Intent("com.example.broadcasttest.LOCAL_BROADCAST2");
                    localBroadcastManager.sendBroadcast(intent2);
                }
            });
        } else {

            mySubjects.clear();
            // 转化为具体的对象列表
            List<String> jsonlist = JSON.parseArray(mClassBoxData, String.class);
            mySubjects.clear();
            for(String s : jsonlist) {
                MySubject mySubject = JSON.parseObject(s, MySubject.class);
                mySubjects.add(mySubject);
            }

            //获取 课程id 和未读消息数的 Key Value 关系
            groupChatManager = getGroupChatManager(mySubjects);

            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);

            // 发送登录聊天的广播
            Intent intent2 = new Intent("com.example.broadcasttest.LOCAL_BROADCAST2");
            localBroadcastManager.sendBroadcast(intent2);
        }

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
     * 初始化课程控件
     */
    private void initTimetableView() {
        //获取控件
        mWeekView = getActivity().findViewById(R.id.id_weekview);
        mTimetableView = getActivity().findViewById(R.id.id_timetableView);
        mBeginClassTime = Cache.with(myContext.getActivity())
                .path(getCacheDir(myContext.getActivity()))
                .getCache("BeginClassTime",String.class);
        //若用户没有设置初始时间
        if(mBeginClassTime == null || mBeginClassTime.length() <= 0){
            mBeginClassTime= "2019-08-26 00:00:00";
        }
        //设置周次选择属性
        mTimetableView.curWeek(mBeginClassTime);
        mWeekView.source(mySubjects)
                .curWeek(mTimetableView.curWeek())
                .callback(new IWeekView.OnWeekItemClickedListener() {
                    @Override
                    public void onWeekClicked(int week) {
                        int cur = mTimetableView.curWeek();
                        //更新切换后的日期，从当前周cur->切换的周week
                        mTimetableView.onDateBuildListener()
                                .onUpdateDate(cur, week);
                        mTimetableView.changeWeekOnly(week);
                    }
                })
                .callback(new IWeekView.OnWeekLeftClickedListener() {
                    @Override
                    public void onWeekLeftClicked() {
                        onWeekLeftLayoutClicked();
                    }
                })
                .isShow(false)//设置隐藏，默认显示
                .showView();

        mTimetableView.source(mySubjects)
                .curTerm("大三下学期")
                .maxSlideItem(12)
                .monthWidthDp(30)
                .callback(new ISchedule.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, Schedule schedule) {
                        if( !schedule.getTeacher() .equals(KEY_OF_REMINDER) )
                            show_classdetails_dialog(schedule);
                        else
                            show_reminder_dialog(schedule);
                    }
                })
                .callback(new ISchedule.OnWeekChangedListener() {
                    @Override
                    public void onWeekChanged(int curWeek) {
                        titleTextView.setText("第" + curWeek + "周");
                    }
                })
                .callback(new ISchedule.OnItemLongClickListener() {
                    @Override
                    public void onLongClick(View v, int day, int start) {

                    }
                })
                //旗标布局点击监听
                .callback(new ISchedule.OnFlaglayoutClickListener() {
                    @Override
                    public void onFlaglayoutClick(int day, int start) {
                        mTimetableView.hideFlaglayout();
                        int day_ = day;
                        int start_ = start;
                        show_editReminder_dialog(day_ + 1,start_);
                    }
                })
                .showView();
        mTimetableView.showFlaglayout();
        hideNonThisWeek();
    }

    /**
     * 更新一下，防止因程序在后台时间过长（超过一天）而导致的日期或高亮不准确问题。
     */
//    @Override
//    public void onStart() {
//        super.onStart();
//        initTimetableView();
//        mTimetableView.onDateBuildListener()
//                .onHighLight();
//    }

    /**
     * 周次选择布局的左侧被点击时回调<br/>
     * 对话框修改当前周次
     */
    protected void onWeekLeftLayoutClicked() {
        Calendar  calendar=Calendar.getInstance();
        new DatePickerDialog(this.getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //此处得到选择的时间
                Cache.with(myContext.getActivity())
                        .path(getCacheDir(myContext.getActivity()))
                        .saveCache("BeginClassTime",year+"-"+ (month + 1) +"-"+dayOfMonth+" 00:00:00");

                initTimetableView();
            }
        }
                //设置初始日期
                ,calendar.get(Calendar.YEAR)
                ,calendar.get(Calendar.MONTH)
                ,calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /*
    * 显示课程详情
    * */
    private TextView textViewforcoursename;
    private TextView textViewforncoursezhou;
    private TextView textViewforcoursetime;
    private TextView textViewforcourseteacher;
    private TextView textViewforcourseroom;
    private ImageView imageViewRemoveClass;
    private ImageView imageViewCloseDialog;
    private LinearLayout linearLayoutChat;
    private LinearLayout linearLayoutCollect;
    private LinearLayout linearLayoutSign;
    private LinearLayout linearLayoutNote;

    private Badge badge = null;

    protected void show_classdetails_dialog(final Schedule bean){
        LayoutInflater inflater=LayoutInflater.from(this.getActivity());
        View myview=inflater.inflate(R.layout.dialog_coursedetail,null);
        final AlertDialog.Builder builder=new AlertDialog.Builder(this.getActivity());

        textViewforcoursename=myview.findViewById(R.id.coursedetail_name);
        textViewforncoursezhou=myview.findViewById(R.id.coursedetail_zhoutime);
        textViewforcoursetime=myview.findViewById(R.id.coursedetail_daytime);
        textViewforcourseteacher=myview.findViewById(R.id.coursedetail_teacher);
        textViewforcourseroom=myview.findViewById(R.id.coursedetail_room);
        imageViewRemoveClass=myview.findViewById(R.id.delete_class);
        imageViewCloseDialog=myview.findViewById(R.id.close_dialog);
        linearLayoutChat = myview.findViewById(R.id.course_chat);
        linearLayoutCollect = myview.findViewById(R.id.course_file);
        linearLayoutSign = myview.findViewById(R.id.course_sign);
        linearLayoutNote = myview.findViewById(R.id.course_note);

        textViewforcoursename.setText(bean.getName());
        textViewforncoursezhou.setText("第"+bean.getWeekList().get(0) + " ~ "+bean.getWeekList().get(bean.getWeekList().size() - 1) +"周");
        textViewforcoursetime.setText("周"+bean.getDay()+"   "+"第"+bean.getStart()+"-"+(bean.getStart()+bean.getStep() -1 )+"节");
        textViewforcourseroom.setText(bean.getRoom());
        textViewforcourseteacher.setText(bean.getTeacher());

        builder.setView(myview);
        coursedetail_dialog=builder.create();
        coursedetail_dialog.show();

        if (bean.getMessagecount() != 0) {
            badge = new QBadgeView(getActivity()).bindTarget(linearLayoutChat).setBadgeNumber(bean.getMessagecount());
        }

        imageViewCloseDialog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                coursedetail_dialog.dismiss();
            }
        });

        imageViewRemoveClass.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alertBuilder = new AlertDialog.Builder(getContext());
                alertBuilder.setTitle("提示");
                alertBuilder.setMessage("确认删除？");
                alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeSubject(new MySubject(bean.getName(), bean.getRoom(), bean.getTeacher(), bean.getWeekList(), bean.getStart(), bean.getStep(), bean.getDay(), bean.getId(), bean.getMessagecount()));
                        coursedetail_dialog.dismiss();
                    }
                });
                alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertBuilder.show();
            }
        });

        linearLayoutChat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                groupChatManager.put(bean.getId(), 0);
                RongIM.getInstance().startGroupChat(getContext(), bean.getId(), bean.getName());
                updateUI(bean.getId());
                if(!(null == badge ))
                    badge.hide(false);
            }
        });

        linearLayoutCollect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 点击后展示这门课的内容
                String path = Environment.getExternalStorageDirectory() + "/RongCloud/Media";
                File file= new File(path);
                Uri fileURI = FileProvider.getUriForFile(getActivity(), "com.example.classchat.FileProvider", file);
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setDataAndType(fileURI, "file/*");
                Uri originalUri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3ARongCloud%2FMedia");
                DocumentFile docFile = DocumentFile.fromTreeUri(getActivity(), originalUri);
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, docFile.getUri());
                startActivity(intent);
            }
        });

        linearLayoutSign.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 增加判断签到时的设备号与注册时的设备号是否相同，若后端该用户的设备号数据为空则将设备号设置为此次签到的设备号（在设备号
                // 与此前已有设备号不重复的情况下），并返回true
                RequestBody requestBody = new FormBody.Builder()
                        .add("time", String.valueOf(System.currentTimeMillis()))
                        .add("groupId", bean.getId())
                        .add("userId", userId)
                        .add("tablename", proUni)
                        .add("serialnumber", getSerialNumber())
                        .build();

                Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/course/issignable", requestBody, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (Boolean.valueOf(response.body().string())) {
                            android.os.Message message = new android.os.Message();
                            message.what = GET_LOCATION;
                            handler.sendMessage(message);
                        }else {
                            android.os.Message message = new android.os.Message();
                            message.what = SIGN_IN_FAILED;
                            handler.sendMessage(message);
                        }
                    }
                });
            }
        });

        linearLayoutNote.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Activity_CourseNote.class);
                startActivity(intent);
            }
        });

    }

    /**
     * 显示编辑便签对话框
     */
    private EditText edit_reminder_title;
    private EditText edit_reminder_details;
    private EditText edit_reminder_start;
    private EditText edit_reminder_end;
    private TextView edit_reminder_week;
    private TextView reminder_dayOfweek;
    private Button back_editReminder;
    private Button save_editReminder;
    protected void show_editReminder_dialog(final int dayofweek, int start){
        LayoutInflater inflater=LayoutInflater.from(this.getActivity());
        View myview=inflater.inflate(R.layout.dialog_edit_reminder,null);
        final AlertDialog.Builder builder=new AlertDialog.Builder(this.getActivity());



        //配合周数多选框的数组
        String[] weeks= new String[]{"第1周","第2周","第3周","第4周","第5周","第6周","第7周","第8周","第9周","第10周","第11周","第12周","第13周","第14周","第15周","第16周","第17周","第18周","第19周","第20周","第21周","第22周","第23周","第24周","第25周"};
        weeksChecked = new boolean[]{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};
        int curweek = mTimetableView.curWeek();
        weeksChecked[curweek - 1] = true;
        weeksnum=new ArrayList<>();
        weeksnum.add(curweek);

        edit_reminder_title = myview.findViewById(R.id.edit_reminder_title);
        edit_reminder_details = myview.findViewById(R.id.edit_reminder_details);
        edit_reminder_start = myview.findViewById(R.id.edit_reminder_start);
        edit_reminder_end = myview.findViewById(R.id.edit_reminder_end);
        edit_reminder_week = myview.findViewById(R.id.edit_reminder_week);
        reminder_dayOfweek = myview.findViewById(R.id.edit_reminder_dayOfweek);
        back_editReminder = myview.findViewById(R.id.dialog_edit_reminder_back);
        save_editReminder = myview.findViewById(R.id.dialog_edit_reminder_save);

        if(dayofweek == 1){ reminder_dayOfweek.setText("周一");}
        else if(dayofweek == 2){ reminder_dayOfweek.setText("周二");}
        else if(dayofweek == 3){ reminder_dayOfweek.setText("周三");}
        else if(dayofweek == 4){ reminder_dayOfweek.setText("周四");}
        else if(dayofweek == 5){ reminder_dayOfweek.setText("周五");}
        else if(dayofweek == 6){ reminder_dayOfweek.setText("周六");}
        else if(dayofweek == 7){ reminder_dayOfweek.setText("周日");}
        String hold_start = Integer.toString(start);
        edit_reminder_start.setText(hold_start);

        builder.setView(myview);
        editreminder_dialog=builder.create();
        editreminder_dialog.show();


        //周数多选框
        mutilChoicebuilder = new AlertDialog.Builder(getActivity());
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
                int end = 0;
                weeksnum = new ArrayList<>();
                for(int i=0;i<weeksChecked.length;i++){
                    if(weeksChecked[i])
                        weeksnum.add(i+1);
                }

                for(int i = 0; i < weeksChecked.length; i++)
                {
                    if (weeksChecked[i])
                    {

                        int start = i+1;
                        for(int j=i+1;j<=weeksChecked.length;++j) {
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
                            s+="第"+start+"周 ";
                        else
                            s+="第"+start+"~"+end+"周 ";

                    }

                }
                if (weeksnum.size() > 0){
                    edit_reminder_week.setText(s);
                }else{
                    //没有选择
                    Toast.makeText(getActivity(), "未选择周数!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        mutilChoicebuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });




        edit_reminder_week.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mutilChoicebuilder.show();
            }
        });

        back_editReminder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editreminder_dialog.dismiss();
            }
        });

        save_editReminder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int start ;
                int end  ;
                int step  ;

                if(TextUtils.isEmpty(edit_reminder_title.getText())){
                    alertBuilder = new AlertDialog.Builder(getContext());
                    alertBuilder.setTitle("提示");
                    alertBuilder.setMessage("便签标题不能为空哦！");
                    alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertBuilder.show();
                }
                else if(TextUtils.isEmpty(edit_reminder_start.getText())||TextUtils.isEmpty(edit_reminder_end.getText())){
                    alertBuilder = new AlertDialog.Builder(getContext());
                    alertBuilder.setTitle("提示");
                    alertBuilder.setMessage("请填完节次信息哦！");
                    alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertBuilder.show();
                }
                else if(weeksnum.size()<1){
                    alertBuilder = new AlertDialog.Builder(getContext());
                    alertBuilder.setTitle("提示");
                    alertBuilder.setMessage("请填完周数哦！");
                    alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertBuilder.show();
                }
                else {
                    start = Integer.parseInt(edit_reminder_start.getText().toString());
                    end = Integer.parseInt(edit_reminder_end.getText().toString());
                    step = end - start + 1;
                    if (end < start||end>12||start<1) {
                        alertBuilder = new AlertDialog.Builder(getContext());
                        alertBuilder.setTitle("提示");
                        alertBuilder.setMessage("请填入正确的节数哦！");
                        alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        alertBuilder.show();
                    }

                    else{
                        String title_course = edit_reminder_title.getText().toString();
                        String detail_place;
                        if(TextUtils.isEmpty(edit_reminder_start.getText())){ detail_place = "(无)";}
                        else { detail_place = edit_reminder_details.getText().toString();}
                        String key_teacher = KEY_OF_REMINDER;

                        MySubject item = new MySubject( title_course, detail_place, key_teacher, weeksnum, start, step, dayofweek , ID_OF_REMINDER,0);
                        if(is_ReminderLegal(item)) {
                            maddClassBoxData = Cache.with(v.getContext())
                                    .path(getCacheDir(v.getContext()))
                                    .getCache("classBox", String.class);
                            List<MySubject> myaddSubjects = JSON.parseArray(maddClassBoxData, MySubject.class);
                            myaddSubjects.add(item);
                            maddClassBoxData = JSON.toJSONString(myaddSubjects);
                            Cache.with(v.getContext())
                                    .path(getCacheDir(v.getContext()))
                                    .saveCache("classBox", maddClassBoxData);

                            Intent intent1 = new Intent("com.example.broadcasttest.LOCAL_BROADCAST1");
                            localBroadcastManager.sendBroadcast(intent1);

                            editreminder_dialog.dismiss();
                        }
                        else{
                            alertBuilder = new AlertDialog.Builder(getContext());
                            alertBuilder.setTitle("提示");
                            alertBuilder.setMessage("当前时间有课或其他便签哦！");
                            alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            alertBuilder.show();
                        }
                    }
                }

            }
        });

    }

    /**
     * 显示便签对话框
     */
    private EditText reminder_title;
    private EditText reminder_details;
    private Button reminder_back;
    private Button reminder_delete;
    private Button reminder_modify;
    private Button reminder_back_modify;
    private Button reminder_save;
    protected  void show_reminder_dialog(final Schedule schedule){
        LayoutInflater inflater=LayoutInflater.from(this.getActivity());
        View myview=inflater.inflate(R.layout.dialog_reminder,null);
        final AlertDialog.Builder builder=new AlertDialog.Builder(this.getActivity());

        reminder_title = myview.findViewById(R.id.reminder_title);
        reminder_details = myview.findViewById(R.id.reminder_detail);
        reminder_title.setText(schedule.getName());
        reminder_details.setText(schedule.getRoom());
        // 设置不可编辑
        reminder_title.setEnabled(false);
        reminder_title.setFocusable(false);
        reminder_title.setFocusableInTouchMode(false);
        reminder_details.setEnabled(false);
        reminder_details.setFocusable(false);
        reminder_details.setFocusableInTouchMode(false);

        reminder_back = myview.findViewById(R.id.back_from_reminder);
        reminder_modify = myview.findViewById(R.id.reminder_modify);
        reminder_delete = myview.findViewById(R.id.reminder_delete);
        reminder_back_modify = myview.findViewById(R.id.reminder_back_modify);
        reminder_save = myview.findViewById(R.id.reminder_save);

        builder.setView(myview);
        reminder_dialog=builder.create();
        reminder_dialog.show();


        reminder_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reminder_dialog.dismiss();
            }
        });
        reminder_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                alertBuilder = new AlertDialog.Builder(getContext());
                alertBuilder.setTitle("提示");
                alertBuilder.setMessage("确认删除？");
                alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeSubject(new MySubject(schedule.getName(), schedule.getRoom(), schedule.getTeacher(), schedule.getWeekList(), schedule.getStart(), schedule.getStep(), schedule.getDay(), schedule.getId(), schedule.getMessagecount()));
                        reminder_dialog.dismiss();
                    }
                });
                alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertBuilder.show();
            }
        });

        reminder_modify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reminder_modify.setVisibility(View.GONE);
                reminder_delete.setVisibility(View.GONE);
                reminder_back_modify.setVisibility(View.VISIBLE);
                reminder_save.setVisibility(View.VISIBLE);
                // 设置可编辑
                reminder_title.setEnabled(true);
                reminder_title.setFocusable(true);
                reminder_title.setFocusableInTouchMode(true);
                reminder_title.setTextColor(Color.parseColor("#AAAAAA"));
                reminder_details.setEnabled(true);
                reminder_details.setFocusable(true);
                reminder_details.setFocusableInTouchMode(true);
                reminder_details.setTextColor(Color.parseColor("#AAAAAA"));
            }
        });


        reminder_back_modify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reminder_back_modify.setVisibility(View.GONE);
                reminder_save.setVisibility(View.GONE);
                reminder_delete.setVisibility(View.VISIBLE);
                reminder_modify.setVisibility(View.VISIBLE);
                // 设置可编辑
                reminder_title.setText(schedule.getName());
                reminder_title.setEnabled(false);
                reminder_title.setFocusable(false);
                reminder_title.setFocusableInTouchMode(false);
                reminder_title.setTextColor(Color.parseColor("#000000"));
                reminder_details.setText(schedule.getRoom());
                reminder_details.setEnabled(false);
                reminder_details.setFocusable(false);
                reminder_details.setFocusableInTouchMode(false);
                reminder_details.setTextColor(Color.parseColor("#000000"));
            }
        });

        reminder_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reminder_title.getText().toString().equals(schedule.getName()) && reminder_details.getText().toString().equals(schedule.getRoom())  ){
                    reminder_back_modify.setVisibility(View.GONE);
                    reminder_save.setVisibility(View.GONE);
                    reminder_delete.setVisibility(View.VISIBLE);
                    reminder_modify.setVisibility(View.VISIBLE);
                    // 设置可编辑
                    reminder_title.setEnabled(false);
                    reminder_title.setFocusable(false);
                    reminder_title.setFocusableInTouchMode(false);
                    reminder_title.setTextColor(Color.parseColor("#000000"));
                    reminder_details.setEnabled(false);
                    reminder_details.setFocusable(false);
                    reminder_details.setFocusableInTouchMode(false);
                    reminder_details.setTextColor(Color.parseColor("#000000"));
                }
                else if(TextUtils.isEmpty(reminder_title.getText())){
                    alertBuilder = new AlertDialog.Builder(getContext());
                    alertBuilder.setTitle("提示");
                    alertBuilder.setMessage("便签标题不能为空哦！");
                    alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertBuilder.show();
                }
                else{
                    if(TextUtils.isEmpty(reminder_details.getText())){
                        reminder_details.setText("(无)");
                    }
                    String title = reminder_title.getText().toString();
                    String details = reminder_details.getText().toString();

                    removeSubject(new MySubject(schedule.getName(), schedule.getRoom(), schedule.getTeacher(), schedule.getWeekList(), schedule.getStart(), schedule.getStep(), schedule.getDay(), schedule.getId(), schedule.getMessagecount()));
                    maddClassBoxData= Cache.with(v.getContext())
                            .path(getCacheDir(v.getContext()))
                            .getCache("classBox", String.class);

                    MySubject item = new MySubject( title, details, KEY_OF_REMINDER, schedule.getWeekList(), schedule.getStart(), schedule.getStep(), schedule.getDay(), ID_OF_REMINDER,0);

                    List<MySubject> myaddSubjects = JSON.parseArray(maddClassBoxData, MySubject.class);
                    myaddSubjects.add(item);
                    maddClassBoxData=JSON.toJSONString(myaddSubjects);
                    Cache.with(v.getContext())
                            .path(getCacheDir(v.getContext()))
                            .saveCache("classBox", maddClassBoxData);
                    Intent intent1 = new Intent("com.example.broadcasttest.LOCAL_BROADCAST1");
                    localBroadcastManager.sendBroadcast(intent1);

                    reminder_back_modify.setVisibility(View.GONE);
                    reminder_save.setVisibility(View.GONE);
                    reminder_delete.setVisibility(View.VISIBLE);
                    reminder_modify.setVisibility(View.VISIBLE);
                    // 设置可编辑
                    reminder_title.setEnabled(false);
                    reminder_title.setFocusable(false);
                    reminder_title.setFocusableInTouchMode(false);
                    reminder_title.setTextColor(Color.parseColor("#000000"));
                    reminder_details.setEnabled(false);
                    reminder_details.setFocusable(false);
                    reminder_details.setFocusableInTouchMode(false);
                    reminder_details.setTextColor(Color.parseColor("#000000"));
                }
            }
        });


    }

     /**
     * 显示弹出菜单
     */
    @SuppressLint("RestrictedApi")
    public void showPopmenu() {
        PopupMenu popup = new PopupMenu(this.getActivity(), moreButton);
        popup.getMenuInflater().inflate(R.menu.tabletime_menu, popup.getMenu());
        if(isShowNoThisWeek){
        popup.getMenu().getItem(3).setChecked(true);
        }
        else {
            popup.getMenu().getItem(3).setChecked(false);
        }
        if(isShowWeekend){
            popup.getMenu().getItem(4).setChecked(true);
        }
        else {
            popup.getMenu().getItem(4).setChecked(false);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_add_course:
                        if (isAuthentation)
                            addSubject();
                        else
                            Toast.makeText(getContext(), "请先实名认证",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menu_import_classes:
                        if (isAuthentation){
                            if (diegod) {
                                importClass();
                            }
                            else {
                                Toast.makeText(getContext(), "实名认证状态还未更新，请稍等片刻再点击", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                            Toast.makeText(getContext(), "请先实名认证", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menu_export_classes:
                        Log.d(TAG, "onLongClick: + " + mClassBoxData);
                        Resources r = getContext().getResources();
                        Bitmap bmp = BitmapFactory.decodeResource(r, R.drawable.icon_logo);
                        Bitmap bitmap = CodeCreator.createQRCode("http://106.12.105.160:8081/getallcourse/student?userId=" + userId, 700, 700, bmp);
                        LayoutInflater inflater=LayoutInflater.from(getContext());
                        View xxview=inflater.inflate(R.layout.fragment_qrcodeshowdialog,null);
                        final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                        qrcode = xxview.findViewById(R.id.iv_qrcode);
                        qrcode.setImageBitmap(bitmap);
                        builder.setView(xxview);
                        builder.create().show();
                        break;

                    case R.id.menu_showweekend:
                        if (isShowWeekend){
                            isShowWeekend = false;
                            hideWeekends();
                        }else {
                             isShowWeekend = true;
                             showWeekends();
                        }
                        break;

                    case R.id.menu_shownotthisweek:
                        if(isShowNoThisWeek){//更改菜单项的选中状态
                            isShowNoThisWeek = false;
                            hideNonThisWeek();
                        }else{
                            isShowNoThisWeek = true;
                            showNonThisWeek();
                        }
                        break;
                }
                return true;
            }
        });
        try { //popupmenu显示icon的关键
            Field mpopup=popup.getClass().getDeclaredField("mPopup");
            mpopup.setAccessible(true);
            MenuPopupHelper mPopup = (MenuPopupHelper) mpopup.get(popup);
            mPopup.setForceShowIcon(true);
        } catch (Exception e) {
        }
        popup.show();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.id_layout:
                //如果周次选择已经显示了，那么将它隐藏，更新课程、日期
                //否则，显示
                if (mWeekView.isShowing()) {
                    mWeekView.isShow(false);
                    int cur = mTimetableView.curWeek();
                    mTimetableView.onDateBuildListener()
                            .onUpdateDate(cur, cur);
                    mTimetableView.changeWeekOnly(cur);
                } else {
                    mWeekView.isShow(true);
                }
                break;
        }
    }

    /**
     * 删除课程
     * 内部使用集合维护课程数据，操作集合的方法来操作它即可
     * 最后更新一下视图（全局更新）
     */
    protected void deleteSubject() {
        int size = mTimetableView.dataSource().size();
        int pos = (int) (Math.random() * size);
        if (size > 0) {
            mTimetableView.dataSource().remove(pos);
            mTimetableView.updateView();
        }
    }

    /**
     * 添加课程
     * 内部使用集合维护课程数据，操作集合的方法来操作它即可
     * 最后更新一下视图（全局更新）
     */
    protected void addSubject() {
        Intent add = new Intent(getActivity(), Activity_AddSearchCourse.class);
        add.putExtra("userId",userId);
        add.putExtra("proUni", proUni);
        startActivity(add);
    }

    /**
     * 隐藏非本周课程
     * 修改了内容的显示，所以必须更新全部（性能不高）
     * 建议：在初始化时设置该属性
     * <p>
     * updateView()被调用后，会重新构建课程，课程会回到当前周
     */
    protected void hideNonThisWeek() {
        mTimetableView.isShowNotCurWeek(false).updateView();
        mWeekView.curWeek(mTimetableView.curWeek()).updateView();
        mTimetableView.changeWeekForce(mTimetableView.curWeek());
    }

    /**
     * 显示非本周课程
     * 修改了内容的显示，所以必须更新全部（性能不高）
     * 建议：在初始化时设置该属性
     */
    protected void showNonThisWeek() {
        mTimetableView.isShowNotCurWeek(true).updateView();
        mWeekView.curWeek(mTimetableView.curWeek()).updateView();
        mTimetableView.changeWeekForce(mTimetableView.curWeek());
    }

    /**
     * 显示WeekView
     */

    protected void showWeekView() {
        mWeekView.isShow(true);
    }

    /**
     * 隐藏WeekView
     */

    protected void hideWeekView() {
        mWeekView.isShow(false);
    }

    /**
     * 教务导入课表
     */
    protected  void importClass() {
        Intent add = new Intent(getActivity(), Activity_AutoPullCourseFromWeb.class);
        add.putExtra("userId",userId);
        add.putExtra("proUni", proUni);
        startActivity(add);
    }


    /**
     * 隐藏周末
     */
    private void hideWeekends() {
        mTimetableView.isShowWeekends(false).updateView();
    }

    /**
     * 显示周末
     */
    private void showWeekends() {
        mTimetableView.isShowWeekends(true).updateView();
    }

    /*
    * 广播
    * */

    class UPDATEcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){

            System.out.println(mcontext);

            mClassBoxData = Cache.with(mcontext)
                    .path(getCacheDir(mcontext))
                    .getCache("classBox", String.class);

            List<String> jsonlist = JSON.parseArray(mClassBoxData, String.class);

            mySubjects.clear();

            for(String s : jsonlist) {
                MySubject mySubject = JSON.parseObject(s, MySubject.class);
                mySubjects.add(mySubject);
            }

            initTimetableView();
        }


    }

    class UpdateGroupIdReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {
            if (isAuthentation)
                connect(token, new RongIMClient.ConnectCallback() {
                    @Override
                    public void onTokenIncorrect() {

                    }

                    @Override
                    public void onSuccess(String s) {

                        Log.d("LoginActivity", "--onSuccess");

                        // 登陆成功
//                        Toast.makeText(context, "可以使用聊天", Toast.LENGTH_SHORT).show();

                        RongIM.getInstance().setCurrentUserInfo(new UserInfo(userId, realName, Uri.parse(imageUrl)));
                        RongIM.getInstance().setMessageAttachedUserInfo(true);
                        RongIM.getInstance().enableNewComingMessageIcon(true);//显示新消息提醒
                        RongIM.getInstance().enableUnreadMessageIcon(true);//显示未读消息数目

                        for (final String groupId :groupChatManager.keySet()) {
                            RongIM.getInstance().getUnreadCount(Conversation.ConversationType.GROUP, groupId, new RongIMClient.ResultCallback<Integer>() {
                                @Override
                                public void onSuccess(Integer integer) {
                                    groupChatManager.put(groupId, integer);
                                    for(int i = 0 ; i < mySubjects.size() ; i++){
                                        if(mySubjects.get(i).getId().equals(groupId))
                                            mySubjects.get(i).setMessageCount(integer);
                                    }

                                    Message message = new Message();
                                    message.what = 1;
                                    handler.sendMessage(message);
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    Log.d(TAG, String.valueOf(errorCode.getValue()));
                                }
                            });
                        }

                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
        }
    }

    class UpdateStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){

            diegod = false;

            RequestBody requestBody = new FormBody.Builder()
                    .add("userId", userId)
                    .build();

            Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/getuserinfo/student", requestBody, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    JSONObject jsonObject = JSON.parseObject(response.body().string());
                    realName = jsonObject.getString("realname");
                    proUni = jsonObject.getString("university") + "_" + jsonObject.getString("school");
                    isAuthentation = Boolean.parseBoolean(jsonObject.getString("authentationstatus"));
                    token = jsonObject.getString("token");
                    headUrl = jsonObject.getString("head");
                    diegod = true;
                    connect(token, new RongIMClient.ConnectCallback() {
                        @Override
                        public void onTokenIncorrect() {

                        }

                        @Override
                        public void onSuccess(String s) {
                            // 去执行下面的函数\
                            Log.d("LoginActivity", "--onSuccess");

                            // 登陆成功
//                            Toast.makeText(getActivity(), "可以使用聊天", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                }
            });
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(updatereceiver);
        localBroadcastManager.unregisterReceiver(updateStateReceiver);
        localBroadcastManager.unregisterReceiver(updateGroupIdReceiver);
        RongIM.getInstance().logout();
    }

    public void removeSubject (MySubject subject) {

        mClassBoxData = Cache.with(myContext.getActivity())
                .path(getCacheDir(myContext.getActivity()))
                .getCache("classBox", String.class);

        List<MySubject> mySubjects = JSON.parseArray(mClassBoxData, MySubject.class);

        int count = 0;

        for (MySubject item : mySubjects) {
            if (item.getId().equals(subject.getId())) {
                count++;
            }
        }
        if(count==0){
            return;
        }

        if (count == 1) {

            //调用服务器
            RequestBody requestBody = new FormBody.Builder()
                    .add("userId", userId)
                    .add("coursename", subject.getName())
                    .build();

            Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/removecourse/student", requestBody, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                }
            });
        }

        for (int i = 0 ; i < mySubjects.size() ; i++) {
            if ((mySubjects.get(i).getId().equals(subject.getId())) && (mySubjects.get(i).getStart() == subject.getStart()) && (mySubjects.get(i).getDay() == subject.getDay()) ) {
                mySubjects.remove(i);
            }
        }
        Log.d("remove", JSON.toJSONString(mySubjects));
        //获得数据后存入缓存
        Cache.with(myContext.getActivity())
                .path(getCacheDir(myContext.getActivity()))
                .remove("classBox");

        Cache.with(myContext.getActivity())
                .path(getCacheDir(myContext.getActivity()))
                .saveCache("classBox", JSON.toJSONString(mySubjects));

        Intent intent1 = new Intent("com.example.broadcasttest.LOCAL_BROADCAST1");
        localBroadcastManager.sendBroadcast(intent1);
    }

    public JSONObject getGroupChatManager(List<MySubject> subjectList){
        JSONObject object = new JSONObject();
        for(MySubject subject : subjectList) {
            object.put(subject.getId(), 0);
        }
        return object;
    }

    public void updateUI(String groupId) {
        for(int i = 0 ; i < mySubjects.size() ; i++){
            if(mySubjects.get(i).getId().equals(groupId))
                mySubjects.get(i).setMessageCount(groupChatManager.getInteger(groupId));
        }

        Message message = new Message();
        message.what = 1;
        handler.sendMessage(message);
    }

    //百度地图客户端初始化
    private void initclient(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        option.setIsNeedAddress(true);
        client.setLocOption(option);
    }

    //百度地图得到结果回调
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation location) {
            now_latitude = Double.valueOf(location.getLatitude());
            now_longitude = Double.valueOf(location.getLongitude());
            Log.d(TAG, "onReceiveLocation: "+ now_latitude);
            Log.d(TAG, "onReceiveLocation: "+ now_longitude);
            // 不要转圈
            loadingForSignIn.dismiss();
            // 判断符不符合
            if((Math.abs((now_latitude - signstatus.getDoubleValue("la")))) < 0.1&&(Math.abs((now_longitude - signstatus.getDoubleValue("lo")))) < 0.1){
                Message message = new Message();
                message.what = LOCATION_CORRECT;
                handler.sendMessage(message);
            }else{
                Message message = new Message();
                message.what = LOCATION_WRONG;
                handler.sendMessage(message);
            }

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case SCAN_TABLE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        String content = data.getStringExtra(Constant.CODED_CONTENT);

                        try{
                            Util_NetUtil.sendOKHTTPRequest(content, new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {}

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    // 得到服务器返回的具体内容
                                    String responseData = response.body().string();
                                    System.out.println(responseData);
                                    mClassBoxData = responseData;
                                    // 转化为具体的对象列表
                                    List<String> jsonlist = JSON.parseArray(responseData, String.class);
                                    mySubjects.clear();
                                    for(String s : jsonlist) {
                                        MySubject mySubject = JSON.parseObject(s, MySubject.class);
                                        mySubjects.add(mySubject);
                                    }
                                    //获得数据后存入缓存
                                    Cache.with(myContext.getActivity())
                                            .path(getCacheDir(myContext.getActivity()))
                                            .remove("classBox");

                                    Cache.with(myContext.getActivity())
                                            .path(getCacheDir(myContext.getActivity()))
                                            .saveCache("classBox", mClassBoxData);

                                    // 获取课程id 和未读消息数的 Key Value 关系
                                    groupChatManager = getGroupChatManager(mySubjects);

                                    Message message = new Message();
                                    message.what = INIT_TABLE;
                                    handler.sendMessage(message);

                                    Message message1 = new Message();
                                    message1.what = UPDATE_TABLE;
                                    handler.sendMessage(message1);

                                    // 发送登录聊天的广播
                                    Intent intent2 = new Intent("com.example.broadcasttest.LOCAL_BROADCAST2");
                                    localBroadcastManager.sendBroadcast(intent2);
                                }
                            });
                        }catch (Exception e){
                            e.printStackTrace();
                            Util_ToastUtils.showToast(getContext(), "宁扫的这是撒🐎哦？");
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    //判断便签是否与其他subject重复
    public Boolean is_ReminderLegal(MySubject subject) {

        List<MySubject> doubtfulList = new ArrayList<>();

        mClassBoxData = Cache.with(getActivity())
                .path(getCacheDir(getActivity()))
                .getCache("classBox", String.class);

        List<MySubject> mySubjects = JSON.parseArray(mClassBoxData, MySubject.class);

        for (MySubject item : mySubjects) {
            //先筛选出可以列表
            if ((item.getDay() == subject.getDay()) && !( (item.getStart() > (subject.getStart() + subject.getStep() - 1))|| ((item.getStart() + item.getStep() - 1) < subject.getStart())) ) {
                doubtfulList.add(item);
            }
        }
        //接下来判断这些可以的item会不会有和要添加的subject周数重合的
        for(MySubject sub : doubtfulList){
            List<Integer> week1 = sub.getWeekList();
            List<Integer> week2 = subject.getWeekList();
            week1.retainAll(week2);
            if(!week1.isEmpty())
                return false;
        }
        return true;
    }



}
