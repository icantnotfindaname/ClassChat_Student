package com.example.classchat.Fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
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


import com.alibaba.fastjson.JSON;
import com.example.classchat.Activity.Activity_AddTodo;
import com.example.classchat.Adapter.Adapter_Memo;
import com.example.classchat.Object.Object_TodoList;
import com.example.classchat.R;
import com.example.classchat.Util.Util_NetUtil;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Fragment_Memo extends Fragment {

    private static final String TAG = "Fragment_Memo";
    private RecyclerView rv1, rv2, rv3, rv4, rv5, rv6, rv7, rv8, rv9;
    private BoomMenuButton add;
    private Object_TodoList object_todoList = new Object_TodoList("1","memo标题",3,"2","12","2",true);
    private List<Object_TodoList> todoLists = new ArrayList<>();

    private static int[] imageResources = new int[]{
            R.drawable.morning, R.drawable.noon, R.drawable.night,
            R.drawable.memo_1, R.drawable.memo_2, R.drawable.memo_3,
            R.drawable.memo_4, R.drawable.memo_5, R.drawable.memo_6

    };
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        add = view.findViewById(R.id.memo_add);
        rv1 = view.findViewById(R.id.rv_memo1);
        rv2 = view.findViewById(R.id.rv_memo2);
        rv3 = view.findViewById(R.id.rv_memo3);
        rv4 = view.findViewById(R.id.rv_memo4);
        rv5 = view.findViewById(R.id.rv_memo5);
        rv6 = view.findViewById(R.id.rv_memo6);
        rv7 = view.findViewById(R.id.rv_memo7);
        rv8 = view.findViewById(R.id.rv_memo8);
        rv9 = view.findViewById(R.id.rv_memo9);
        todoLists.add(object_todoList);
        List<RecyclerView> rvList = new ArrayList<>();
        rvList.add(rv1);rvList.add(rv2);rvList.add(rv3);rvList.add(rv4);
        rvList.add(rv5);rvList.add(rv6);rvList.add(rv7);rvList.add(rv8);rvList.add(rv9);
        for(int i = 0; i < rvList.size(); ++i){
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            rvList.get(i).setLayoutManager(layoutManager);
            //TODO 先读取本地信息
            rvList.get(i).setAdapter(new Adapter_Memo(todoLists));
        }
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
                            Intent intent = new Intent(getActivity(), Activity_AddTodo.class);
                            intent.putExtra("position", index);
                            startActivity(intent);
                        }
                    });
            add.addBuilder(builder);
        }

        getMemoFromWeb();
    }

    private void getMemoFromWeb() {
        final Message message = new Message();
        //构建requestbody
//        RequestBody requestBody = new FormBody.Builder()
//                .add("mcount",)
//                .build();
//        // 发送网络请求，联络信息
//        Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/getallitems", requestBody, new okhttp3.Callback() {
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                // 得到服务器返回的具体内容
//                String responseData = response.body().string();
//                // 转化为具体的对象列表
//
//                // 发送收到成功的信息
//                //由getQuery()按需处理后载入布局
//                message.what = ;
//                handler.sendMessage(message);
//            }
//
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                // 在这里对异常情况进行处理
//                Log.d(TAG, "onFailure: 加载商品失败，没有收到商品");
//            }
//        });
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
