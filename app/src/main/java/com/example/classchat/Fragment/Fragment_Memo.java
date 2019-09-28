package com.example.classchat.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.classchat.Activity.Activity_AddTodo;
import com.example.classchat.Activity.Activity_AllTodo;
import com.example.classchat.Activity.MainActivity;
import com.example.classchat.Adapter.Adapter_Memo;
import com.example.classchat.Object.MySubject;
import com.example.classchat.Object.Object_TodoList;
import com.example.classchat.R;
import com.example.classchat.Util.Util_NetUtil;
import com.example.library_activity_timetable.model.ScheduleSupport;
import com.example.library_cache.Cache;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import org.jetbrains.annotations.NotNull;

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
import okhttp3.RequestBody;
import okhttp3.Response;

public class Fragment_Memo extends Fragment {

    private RecyclerView rv1, rv2, rv3, rv4, rv5, rv6, rv7, rv8, rv9;
    private TextView tv1,tv2,tv3,tv4,tv5,tv6;
    private BoomMenuButton add;
    private List<List<Object_TodoList>> todoLists = new ArrayList<>();
    private String mBeginClassTime = "";
    private String userId;
    private List<String> jsonlist;

    private Button prev, next, seeAll;
    private static int checkcount = 4;//可看前后七天计数
    private TextView dateTitle, dayTitle;
    private Calendar calendar = Calendar.getInstance();

    private final static int SUCCESS = 0;

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
                if(checkcount == 2)
                    prev.setVisibility(View.VISIBLE);
                if(checkcount == 7)
                    next.setVisibility(View.GONE);
                dateTitle.setText(getDate(checkcount - 4));
                calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) +  (checkcount - 4));
                dayTitle.setText(getWeekday(calendar.get(Calendar.DAY_OF_WEEK)));
                refresh(checkcount - 4);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkcount --;
                calendar = Calendar.getInstance();
                if(checkcount == 1)
                    prev.setVisibility(View.GONE);
                if(checkcount == 6)
                    next.setVisibility(View.VISIBLE);
                dateTitle.setText(getDate(checkcount - 4));
                calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) +  (checkcount - 4));
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
                            Intent intent = new Intent(getActivity(),Activity_AddTodo.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("timeslot", index+"");
                            bundle.putString("userId", userId);
                            intent.putExtras(bundle);
                            getActivity().startActivity(intent);
                        }
                    });
            add.addBuilder(builder);
        }

        MainActivity mainActivity = (MainActivity)getActivity();
        userId = mainActivity.getId();
        getMemoFromWeb(0);
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

    private String getWeekdayString(int week){
        switch (week){
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
                default:
                    break;
            }
        }
    };

    private int timeTransfrom(String startTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            long start = sdf.parse(startTime).getTime();
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) +  (checkcount - 4));
            long end = new Date().getTime();
            Log.e("calendar.getTime", calendar.getTime()+"");
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
            mBeginClassTime = getDate(distanceDay) + " 00:00:00";
        }
        int week = timeTransfrom(mBeginClassTime);
        Log.e("week", week + "");
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) +  distanceDay);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        Log.e("day", day + "");
        Log.e("getWeekdayString(day)", getWeekdayString(day) + "");
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
        Log.d("init", "initClassData");
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

}