package com.example.classchat.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
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
import com.example.classchat.Activity.MainActivity;
import com.example.classchat.Adapter.Adapter_Memo;
import com.example.classchat.Object.Object_TodoList;
import com.example.classchat.R;
import com.example.classchat.Util.Util_NetUtil;
import com.example.classchat.Util.Util_ToastUtils;
import com.example.library_activity_timetable.model.ScheduleSupport;
import com.example.library_cache.Cache;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;


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

import static com.example.library_cache.disklrucache.Util.getCacheDir;

public class Fragment_Memo extends Fragment {

    //TODO 待办界面左侧获取当天课程，显示在xml的tv_memo中
    private static final String TAG = "Fragment_Memo";
    private RecyclerView rv1, rv2, rv3, rv4, rv5, rv6, rv7, rv8, rv9;
    private BoomMenuButton add;
    private List<List<Object_TodoList>> todoLists = new ArrayList<>();
    private String mBeginClassTime = "";
    private String userId;
    private List<String> jsonlist;

    private Button prev, next;
    private static int checkcount = 4;//可看前后七天计数
    private TextView dateTitle, dayTitle;
    private Calendar calendar = Calendar.getInstance();

    private final static int SUCCESS = 0;
    private final static int RECEIVE_NULL = 1;

    private static boolean isFirst = true;

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

        //初始化todolist
        for(int i = 0; i < 9; ++i){
            todoLists.add(new ArrayList<Object_TodoList>());
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkcount ++;
                if(checkcount == 2)
                    prev.setVisibility(View.VISIBLE);
                if(checkcount == 7)
                    next.setVisibility(View.GONE);
                dateTitle.setText(getDate(checkcount - 4));
                dayTitle.setText(getWeekday(calendar.get(Calendar.DAY_OF_WEEK) + (checkcount - 4)));
                refresh(checkcount - 4);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkcount --;
                if(checkcount == 1)
                    prev.setVisibility(View.GONE);
                if(checkcount == 6)
                    next.setVisibility(View.VISIBLE);
                dateTitle.setText(getDate(checkcount - 4));
                dayTitle.setText(getWeekday(calendar.get(Calendar.DAY_OF_WEEK) + (checkcount - 4)));
                refresh(checkcount - 4);

            }
        });

        assert add != null;
        add.setButtonEnum(ButtonEnum.TextInsideCircle);
        add.setPiecePlaceEnum(PiecePlaceEnum.DOT_9_1);
        add.setButtonPlaceEnum(ButtonPlaceEnum.SC_9_1);
        add.setDraggable(true);//可拖动
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
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
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
            case 8:
                return "Sun";
            case 2:
            case 9:
                return "Mon";
            case 3:
                return "Tue";
            case 4:
                return "Wed";
            case 5:
            case -2:
                return "Thu";
            case 6:
            case -1:
                return "Fri";
            case 7:
            case 0:
                return "Sat";
            default:
                return null;
        }
    }

    private String getWeekdayString(int week){
        switch (week){
            case 1:
            case 8:
                return "7";
            case 2:
            case 9:
                return "1";
            case 3:
                return "2";
            case 4:
                return "3";
            case 5:
            case -2:
                return "4";
            case 6:
            case -1:
                return "5";
            case 7:
            case 0:
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
                case RECEIVE_NULL:
                    Util_ToastUtils.showToast(getContext(), "还没有任务呢，快去添加吧！");
                    break;
                default:
                    break;
            }
        }
    };

    private void getMemoFromWeb(int distanceDay) {
        //获取本周周次week
        mBeginClassTime = Cache.with(getActivity())
                .path(getCacheDir(getActivity()))
                .getCache("BeginClassTime",String.class);
        //若用户没有设置初始时间
        if(mBeginClassTime == null || mBeginClassTime.length() <= 0){
            mBeginClassTime = getDate(distanceDay) + " 00:00:00";
        }
        int week = ScheduleSupport.timeTransfrom(mBeginClassTime);

        final Message message = new Message();
        //构建requestbody
        RequestBody requestBody = new FormBody.Builder()
                .add("userID", userId)
                .add("weekchosen", week + "")
                .add("daychosen", getWeekdayString(calendar.get(Calendar.DAY_OF_WEEK) + distanceDay))
                .build();
        // 发送网络请求，联络信息
        Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/getusertodolist", requestBody, new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 得到服务器返回的具体内容
                String responseData = response.body().string();
                // 转化为具体的对象列表
                jsonlist = JSON.parseArray(responseData, String.class);
                if(jsonlist.size() == 0){
                    message.what = RECEIVE_NULL;
                    handler.sendMessage(message);
                }
                else {
                    for (int i = 0; i < jsonlist.size(); i++) {
                        Object_TodoList o = JSON.parseObject(jsonlist.get(i), Object_TodoList.class);
                        int j = o.getTimeSlot();
                        todoLists.get(j).add(o);
                    }
                    Log.e("jsonlistsize", jsonlist.size()+"");
                    Log.e("todolistsget",todoLists.toString());
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
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fragment__memo, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


}
