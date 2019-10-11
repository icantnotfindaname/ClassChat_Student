package com.example.classchat.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.example.classchat.Activity.Activity_AddTodo;
import com.example.classchat.Activity.Activity_AllTodo;
import com.example.classchat.Activity.MainActivity;
import com.example.classchat.Adapter.Adapter_Memo;
import com.example.classchat.Object.MySubject;
import com.example.classchat.Object.Object_TodoList;
import com.example.classchat.Object.WeatherContainer;
import com.example.classchat.R;
import com.example.classchat.Util.Util_NetUtil;
import com.example.library_cache.Cache;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Fragment_Memo extends Fragment {

    Dialog wea_dialog;
    private LinearLayout today_wea;
    private TextView weather;
    private TextView wea_C;
    private ImageView wea_img;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private List<WeatherContainer> weatherContainers = new ArrayList<>();


    private final static String url_head_1 = "https://www.tianqiapi.com/api/?version=v1&appid=[89921899]&appsecret=[89921899]&city=";
    private final static String url_head_2 = "http://api.map.baidu.com/geocoder?output=json&ak=esNPFDwwsXWtsQfw4NMNmur1&location=";

    private String city_name ;
    private String location_;

    public static final int LOCATION_CODE = 301;
    private LocationManager locationManager;
    private String locationProvider = null;


    private RecyclerView rv1, rv2, rv3, rv4, rv5, rv6, rv7, rv8, rv9;
    private TextView tv1,tv2,tv3,tv4,tv5,tv6;
    private BoomMenuButton add;
    private List<List<Object_TodoList>> todoLists = new ArrayList<>();
    private String mBeginClassTime = "";
    private String userId;
    private List<String> jsonlist;

    private Button prev, next, prevNull, nextNull, seeAll;
    private static int checkcount = 4;//可看前后七天计数
    private TextView dateTitle, dayTitle;
    private Calendar calendar = Calendar.getInstance();

    private final static int SUCCESS = 0;
    private final static int LOCATION = 3;
    private final static int WEATHER= 4;

    private static boolean isFirst = true;

    //缓存+课程 -- 左侧栏相关
    private String mClassBox = "";
    private List<MySubject> mySubjects = new ArrayList<>();
    // 搞一个自己的变量
    Fragment_Memo myContext = this;

    private static int[] imageResources = new int[]{
            R.drawable.morning, R.drawable.memo_1, R.drawable.memo_2,
            R.drawable.noon, R.drawable.memo_3, R.drawable.memo_4,
            R.drawable.memo_5, R.drawable.memo_6, R.drawable.night

    };
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        add = view.findViewById(R.id.memo_add);
        prev = view.findViewById(R.id.memo_prev);
        next = view.findViewById(R.id.memo_next);
        prevNull = view.findViewById(R.id.memo_prev_null);
        nextNull = view.findViewById(R.id.memo_next_null);
        seeAll = view.findViewById(R.id.memo_see_all);
        dateTitle = view.findViewById(R.id.memo_title_date);
        dateTitle.setText(getDate(0));
        dateTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResume();
            }
        });
        dayTitle = view.findViewById(R.id.memo_title_day);
        dayTitle.setText(getWeekday(calendar.get(Calendar.DAY_OF_WEEK)));
        rv1 = view.findViewById(R.id.rv_memo1);
        rv2 = view.findViewById(R.id.rv_memo2);
        rv3 = view.findViewById(R.id.rv_memo3);
        rv4 = view.findViewById(R.id.rv_memo4);
        rv5 = view.findViewById(R.id.rv_memo5);
        rv6 = view.findViewById(R.id.rv_memo6);
        rv7 = view.findViewById(R.id.rv_memo7);
        rv8 = view.findViewById(R.id.rv_memo8);
        rv9 = view.findViewById(R.id.rv_memo9);
        tv1 = view.findViewById(R.id.tv_memo2);
        tv2 = view.findViewById(R.id.tv_memo3);
        tv3 = view.findViewById(R.id.tv_memo5);
        tv4 = view.findViewById(R.id.tv_memo6);
        tv5 = view.findViewById(R.id.tv_memo7);
        tv6 = view.findViewById(R.id.tv_memo8);
        weather = view.findViewById(R.id.wea_disc);
        wea_C = view.findViewById(R.id.wea_num);
        wea_img = view.findViewById(R.id.wea_img);
        today_wea = view.findViewById(R.id.today_weather);

        //初始化todolist
        for(int i = 0; i < 9; ++i){
            todoLists.add(new ArrayList<Object_TodoList>());
        }

        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Activity_AllTodo.class);
                Bundle bundle = new Bundle();
                bundle.putString("userId", userId);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkcount ++;
                calendar = Calendar.getInstance();
                if(checkcount == 2){
                    prev.setVisibility(View.VISIBLE);
                    prevNull.setVisibility(View.GONE);
                }
                if(checkcount == 7){
                    next.setVisibility(View.GONE);
                    nextNull.setVisibility(View.VISIBLE);
                }
                dateTitle.setText(getDate(checkcount - 4));
                calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) +  (checkcount - 4));
                Log.e("nextCalendar",calendar.get(Calendar.DAY_OF_WEEK)+"");
                dayTitle.setText(getWeekday(calendar.get(Calendar.DAY_OF_WEEK)));
                refresh(checkcount - 4);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkcount --;
                calendar = Calendar.getInstance();
                if(checkcount == 1){
                    prev.setVisibility(View.GONE);
                    prevNull.setVisibility(View.VISIBLE);
                }
                if(checkcount == 6){
                    next.setVisibility(View.VISIBLE);
                    nextNull.setVisibility(View.GONE);
                }
                dateTitle.setText(getDate(checkcount - 4));
                calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) +  (checkcount - 4));
                Log.e("prevCalendar",calendar.get(Calendar.DAY_OF_WEEK)+"");
                dayTitle.setText(getWeekday(calendar.get(Calendar.DAY_OF_WEEK)));
                refresh(checkcount - 4);
            }
        });


        assert add != null;
        add.setButtonEnum(ButtonEnum.TextInsideCircle);
        add.setPiecePlaceEnum(PiecePlaceEnum.DOT_9_1);
        add.setButtonPlaceEnum(ButtonPlaceEnum.SC_9_1);
        //设置菜单各按钮
        for (int i = 0; i < add.getPiecePlaceEnum().pieceNumber(); i++) {
            TextInsideCircleButton.Builder builder = new TextInsideCircleButton.Builder()
                    .normalImageRes(imageResources[i])
                    .imagePadding(new Rect(20, 20, 20, 35))
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            //获取本周周次week
                            mBeginClassTime = Cache.with(getActivity())
                                    .path(getCacheDir(getActivity()))
                                    .getCache("BeginClassTime",String.class);

                            Intent intent = new Intent(getActivity(),Activity_AddTodo.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("timeslot", index+"");
                            bundle.putString("userId", userId);
                            bundle.putString("begin_time", mBeginClassTime);

                            intent.putExtras(bundle);
                            getActivity().startActivity(intent);
                        }
                    });
            add.addBuilder(builder);
        }

        MainActivity mainActivity = (MainActivity)getActivity();
        userId = mainActivity.getId();
        getMemoFromWeb(0);

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        } else {
            LocationListener locationListener = new LocationListener() {

                // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                // Provider被enable时触发此函数，比如GPS被打开
                @Override
                public void onProviderEnabled(String provider) {

                }

                // Provider被disable时触发此函数，比如GPS被关闭
                @Override
                public void onProviderDisabled(String provider) {

                }

                //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        Log.e("Map", "Location changed : Lat: "
                                + location.getLatitude() + " Lng: "
                                + location.getLongitude());
                    }
                }
            };
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location != null){
                latitude = location.getLatitude(); //经度
                longitude = location.getLongitude(); //纬度
            }
        }
        location_ = String.valueOf(latitude)+","+String.valueOf(longitude);

        sendOKHTTPRequest(url_head_2 + location_  , new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 得到服务器返回的具体内容

                String responseData = response.body().string();

                String city = "";
                String district = "";
                JSONObject one = null;
                JSONObject two = null;


                try {
                    one = new JSONObject(responseData.toString()).getJSONObject("result");
                    two = one.getJSONObject("addressComponent");
                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                }

                try {
                    city = deleteString0(two.get("city").toString(),'市');
                    district =deleteString0( two.get("district").toString(),'区');
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Message message = new Message();
                message.what = LOCATION;
                if(!district.equals("")){
                    city_name = district;
                    handler.sendMessage(message);
                }
                else if(!city.equals("")){
                    city_name = city;
                    handler.sendMessage(message);
                }


            }
        });

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

    private String getWeekday(int week){

        switch (week){
            case 1:
                return "Sun";
            case 2:
                return "Mon";
            case 3:
                return "Tue";
            case 4:
                return "Wed";
            case 5:
                return "Thu";
            case 6:
                return "Fri";
            case 7:
                return "Sat";
            default:
                Log.e("ex",week+"");
                return week+"";
        }
    }

    private String getWeekdayString(int day){
        switch (day){
            case 1:
                return "7";
            case 2:
                return "1";
            case 3:
                return "2";
            case 4:
                return "3";
            case 5:
                return "4";
            case 6:
                return "5";
            case 7:
                return "6";
            default:
                return null;
        }
    }


    private void refresh(int distanceDay){
        todoLists.clear();
        for(int i = 0; i < 9; ++i){
            todoLists.add(new ArrayList<Object_TodoList>());
        }
        List<RecyclerView> rvList = new ArrayList<>();
        rvList.add(rv1);rvList.add(rv2);rvList.add(rv3);rvList.add(rv4);
        rvList.add(rv5);rvList.add(rv6);rvList.add(rv7);rvList.add(rv8);rvList.add(rv9);

        for(int i = 0; i < rvList.size(); ++i){
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            rvList.get(i).setLayoutManager(layoutManager);
            rvList.get(i).setAdapter(new Adapter_Memo(getActivity(), (todoLists.get(i))));
        }
        getMemoFromWeb(distanceDay);
    }

    //
    @Override
    public void onResume() {
        super.onResume();
        //初始化界面不调用避免重复
        if(isFirst) {
           isFirst = false;
           return;
        }


        checkcount = 4;
        prevNull.setVisibility(View.GONE);
        nextNull.setVisibility(View.GONE);
        calendar = Calendar.getInstance();
        next.setVisibility(View.VISIBLE);
        prev.setVisibility(View.VISIBLE);
        dateTitle.setText(getDate(0));
        dayTitle.setText(getWeekday(calendar.get(Calendar.DAY_OF_WEEK)));
        refresh(0);

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case SUCCESS:
                    List<RecyclerView> rvList = new ArrayList<>();
                    rvList.add(rv1);rvList.add(rv2);rvList.add(rv3);rvList.add(rv4);
                    rvList.add(rv5);rvList.add(rv6);rvList.add(rv7);rvList.add(rv8);rvList.add(rv9);

                    for(int i = 0; i < rvList.size(); ++i){
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        rvList.get(i).setLayoutManager(layoutManager);
                        rvList.get(i).setAdapter(new Adapter_Memo(getActivity(), (todoLists.get(i))));
                    }
                    break;
                case LOCATION:
                    sendOKHTTPRequest(url_head_1+city_name, new okhttp3.Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            // 得到服务器返回的具体内容
                            String responseData = response.body().string();
                            JSONArray first = null;
                            String wea;
                            String high;
                            String low;
                            String date;
                            String wea_img;
                            try {
                                first = new JSONObject(responseData.toString()).getJSONArray("data");
                            } catch (org.json.JSONException e) {
                            }
                            try {
                                for(int i = 0; i < 5; i++){
                                    JSONObject day = ((JSONArray) first).getJSONObject(i);
                                    date = day.get("day").toString();
                                    wea = day.get("wea").toString();
                                    high = day.get("tem1").toString();
                                    low = day.get("tem2").toString();
                                    wea_img = day.get("wea_img").toString();
                                    weatherContainers.add(new WeatherContainer(wea,high, low,date,wea_img));

                                }

                                Message message = new Message();
                                message.what = WEATHER;
                                handler.sendMessage(message);
                            } catch (org.json.JSONException e) {
                            }
                        }
                    });
                    break;
                case WEATHER:
                    weather.setText(weatherContainers.get(0).getWea());
                    wea_C.setText(weatherContainers.get(0).getLow()+"~"+weatherContainers.get(0).getHigh());
                    if(weatherContainers.get(0).getWea_img().equals("qing"))
                    wea_img.setBackgroundResource(R.drawable.qing);
                    else if(weatherContainers.get(0).getWea_img().equals("xue"))
                        wea_img.setBackgroundResource(R.drawable.xue);
                    else if(weatherContainers.get(0).getWea_img().equals("lei"))
                        wea_img.setBackgroundResource(R.drawable.lei);
                    else if(weatherContainers.get(0).getWea_img().equals("shachen"))
                        wea_img.setBackgroundResource(R.drawable.shachen);
                    else if(weatherContainers.get(0).getWea_img().equals("wu"))
                        wea_img.setBackgroundResource(R.drawable.wu);
                    else if(weatherContainers.get(0).getWea_img().equals("bingbao"))
                        wea_img.setBackgroundResource(R.drawable.yu);
                    else if(weatherContainers.get(0).getWea_img().equals("yun"))
                        wea_img.setBackgroundResource(R.drawable.yun);
                    else if(weatherContainers.get(0).getWea_img().equals("yu"))
                        wea_img.setBackgroundResource(R.drawable.yu);
                    else if(weatherContainers.get(0).getWea_img().equals("yin"))
                        wea_img.setBackgroundResource(R.drawable.yin);
                    today_wea.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            show_wea_dialog();
                        }
                    });
                    break;

                default:
                    break;
            }
        }
    };

    //日切换同步周切换
    private int timeTransfrom(String startTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            long start = sdf.parse(startTime).getTime();
            long end = sdf.parse(calendar.get(Calendar.YEAR) + "-" + getDate(checkcount - 4) + " 00:00:00").getTime();
            long seconds = (end - start) / 1000;
            long day = seconds / (24 * 3600);
            int week = (int) (Math.floor(day / 7) + 1);
            return week;
        } catch (ParseException e) {
            return -1;
        }
    }

    private void getMemoFromWeb(int distanceDay) {
        //获取本周周次week
        mBeginClassTime = Cache.with(getActivity())
                .path(getCacheDir(getActivity()))
                .getCache("BeginClassTime",String.class);
        //若用户没有设置初始时间
        if(mBeginClassTime == null || mBeginClassTime.length() <= 0){
            mBeginClassTime= "2019-08-26 00:00:00";
        }
        int week = timeTransfrom(mBeginClassTime);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        final Message message = new Message();
        //构建requestbody
        RequestBody requestBody = new FormBody.Builder()
                .add("userID", userId)
                .add("weekChosen", week + "")
                .add("dayChosen", getWeekdayString(day))
                .build();
        // 发送网络请求，联络信息
        Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/getusertodolist", requestBody, new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 得到服务器返回的具体内容
                String responseData = response.body().string();
                // 转化为具体的对象列表
                jsonlist = JSON.parseArray(responseData, String.class);
                if(jsonlist.size() != 0){
                    for (int i = 0; i < jsonlist.size(); i++) {
                        Object_TodoList o = JSON.parseObject(jsonlist.get(i), Object_TodoList.class);
                        int j = o.getTimeSlot();
                        todoLists.get(j).add(o);
                    }
                    // 发送收到成功的信息
                    //由getQuery()按需处理后载入布局
                    message.what = SUCCESS;
                    handler.sendMessage(message);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }
        });
        initClassData( week , day - 1);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fragment__memo, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void initClassData(final int thisweek, final int thisday){
        mClassBox = Cache.with(this.getActivity())
                .path(getCacheDir(this.getActivity()))
                .getCache("classBox", String.class);

        //TODO 添加memo
        if (mClassBox == null||mClassBox.length() <= 0){
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
                    mClassBox = responseData;
                    // 转化为具体的对象列表
                    List<String> tempjsonlist = JSON.parseArray(responseData, String.class);
                    mySubjects.clear();
                    for(String s : tempjsonlist) {
                        MySubject mySubject = JSON.parseObject(s, MySubject.class);
                        if( mySubject.getDay() == thisday ){
                            for( int week : mySubject.getWeekList() ){
                                if ( thisweek == week ){
                                    mySubjects.add(mySubject);
                                    break;
                                }
                            }
                        }
                    }
                    //获得数据后存入缓存
                    Cache.with(myContext.getActivity())
                            .path(getCacheDir(myContext.getActivity()))
                            .remove("classBox");

                    Cache.with(myContext.getActivity())
                            .path(getCacheDir(myContext.getActivity()))
                            .saveCache("classBox", mClassBox);

                }
            });
        } else {
            // 转化为具体的对象列表
            List<String> tempjsonlist = JSON.parseArray(mClassBox, String.class);
            mySubjects.clear();
            for(String s : tempjsonlist) {
                MySubject mySubject = JSON.parseObject(s, MySubject.class);
                if( mySubject.getDay() == thisday ){
                    for( int week : mySubject.getWeekList() ){
                        if ( thisweek == week ){
                            mySubjects.add(mySubject);
                            break;
                        }
                    }
                }
            }
        }

        //初始化
        tv1.setText("");
        tv2.setText("");
        tv3.setText("");
        tv4.setText("");
        tv5.setText("");
        tv6.setText("");

        for ( MySubject tempSubject : mySubjects ){
            switch ( tempSubject.getStart() ){
                case 1: case 2:
                    tv1.setText(tempSubject.getName());
                    Log.d("SubjectName", tempSubject.getName());
                    break;
                case 3: case 4:
                    tv2.setText(tempSubject.getName());
                    Log.d("SubjectName", tempSubject.getName());
                    break;
                case 5: case 6:
                    tv3.setText(tempSubject.getName());
                    Log.d("SubjectName", tempSubject.getName());
                    break;
                case 7: case 8:
                    tv4.setText(tempSubject.getName());
                    Log.d("SubjectName", tempSubject.getName());
                    break;
                case 9: case 10:
                    tv5.setText(tempSubject.getName());
                    Log.d("SubjectName", tempSubject.getName());
                    break;
                case 11:case 12:
                    tv6.setText(tempSubject.getName());
                    Log.d("SubjectName", tempSubject.getName());
                    break;
                default:
                    break;
            }

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
    public static String deleteString0(String str, char delChar){
        String delStr = "";
        for (int i = 0; i < str.length(); i++) {
            if(str.charAt(i) != delChar){
                delStr += str.charAt(i);
            }
        }
        return delStr;
    }

    private TextView wea_disc1,wea_disc2,wea_disc3,wea_disc4,wea_disc5;
    private TextView wea_num1, wea_num2,wea_num3,wea_num4,wea_num5;
    private ImageView wea_img1,wea_img2,wea_img3,wea_img4,wea_img5;
    private TextView wea_date1,wea_date2,wea_date3,wea_date4,wea_date5;
    private TextView city;
    private Button back_wea;
    protected void show_wea_dialog(){
        LayoutInflater inflater=LayoutInflater.from(this.getActivity());
        View myview =inflater.inflate(R.layout.dialog_weather_detail,null);
        final AlertDialog.Builder builder=new AlertDialog.Builder(this.getActivity());

        city = myview.findViewById(R.id.city_name);
        wea_date1 = myview.findViewById(R.id.wea_date1);
        wea_date2 = myview.findViewById(R.id.wea_date2);
        wea_date3 = myview.findViewById(R.id.wea_date3);
        wea_date4 = myview.findViewById(R.id.wea_date4);
        wea_date5 = myview.findViewById(R.id.wea_date5);
        wea_disc1 = myview.findViewById(R.id.wea_disc1);
        wea_disc2 = myview.findViewById(R.id.wea_disc2);
        wea_disc3 = myview.findViewById(R.id.wea_disc3);
        wea_disc4 = myview.findViewById(R.id.wea_disc4);
        wea_disc5 = myview.findViewById(R.id.wea_disc5);
        wea_img1 = myview.findViewById(R.id.wea_img1);
        wea_img2 = myview.findViewById(R.id.wea_img2);
        wea_img3 = myview.findViewById(R.id.wea_img3);
        wea_img4 = myview.findViewById(R.id.wea_img4);
        wea_img5 = myview.findViewById(R.id.wea_img5);
        wea_num1 = myview.findViewById(R.id.wea_num1);
        wea_num2 = myview.findViewById(R.id.wea_num2);
        wea_num3 = myview.findViewById(R.id.wea_num3);
        wea_num4 = myview.findViewById(R.id.wea_num4);
        wea_num5 = myview.findViewById(R.id.wea_num5);
        back_wea = myview.findViewById(R.id.back_from_wea);

        city.setText(city_name);
        wea_date1.setText(weatherContainers.get(0).getDate());
        wea_date2.setText(weatherContainers.get(1).getDate());
        wea_date3.setText(weatherContainers.get(2).getDate());
        wea_date4.setText(weatherContainers.get(3).getDate());
        wea_date5.setText(weatherContainers.get(4).getDate());

        wea_disc1.setText(weatherContainers.get(0).getWea());
        wea_disc2.setText(weatherContainers.get(1).getWea());
        wea_disc3.setText(weatherContainers.get(2).getWea());
        wea_disc4.setText(weatherContainers.get(3).getWea());
        wea_disc5.setText(weatherContainers.get(4).getWea());

        wea_num1.setText(weatherContainers.get(0).getLow()+"~"+weatherContainers.get(0).getHigh());
        wea_num2.setText(weatherContainers.get(1).getLow()+"~"+weatherContainers.get(1).getHigh());
        wea_num3.setText(weatherContainers.get(2).getLow()+"~"+weatherContainers.get(2).getHigh());
        wea_num4.setText(weatherContainers.get(3).getLow()+"~"+weatherContainers.get(3).getHigh());
        wea_num5.setText(weatherContainers.get(4).getLow()+"~"+weatherContainers.get(4).getHigh());

        setRecource(weatherContainers.get(0).getWea_img(),wea_img1);
        setRecource(weatherContainers.get(1).getWea_img(),wea_img2);
        setRecource(weatherContainers.get(2).getWea_img(),wea_img3);
        setRecource(weatherContainers.get(3).getWea_img(),wea_img4);
        setRecource(weatherContainers.get(4).getWea_img(),wea_img5);


        builder.setView(myview);
        wea_dialog=builder.create();
        wea_dialog.show();

         back_wea.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 wea_dialog.dismiss();
             }
         });

         }

         private void setRecource(String wea,ImageView a){
             if(wea.equals("qing"))
                 a.setBackgroundResource(R.drawable.qing_);
             else if(wea.equals("xue"))
                 a.setBackgroundResource(R.drawable.xue_);
             else if(wea.equals("lei"))
                 a.setBackgroundResource(R.drawable.lei_);
             else if(wea.equals("shachen"))
                 a.setBackgroundResource(R.drawable.shachen_);
             else if(wea.equals("wu"))
                 a.setBackgroundResource(R.drawable.wu_);
             else if(wea.equals("bingbao"))
                 a.setBackgroundResource(R.drawable.bingbao_);
             else if(wea.equals("yun"))
                 a.setBackgroundResource(R.drawable.yun_);
             else if(wea.equals("yu"))
                 a.setBackgroundResource(R.drawable.yu_);
             else if(wea.equals("yin"))
                 a.setBackgroundResource(R.drawable.yin_);

         }

}