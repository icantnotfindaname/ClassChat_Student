package com.example.classchat.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.classchat.Activity.Activity_Market_AddCommodity;
import com.example.classchat.Activity.Activity_Market_MyGoods;
import com.example.classchat.Activity.Activity_Market_ShoppingCart;
import com.example.classchat.Adapter.Adapter_CommodityRecycleView;
import com.example.classchat.Object.Object_Commodity;
import com.example.classchat.Object.Object_Commodity_PriceSort;
import com.example.classchat.Object.Object_Main_Brief_Item;
import com.example.classchat.Object.Object_Main_Brief_Item_PriceSort;
import com.example.classchat.R;
import com.example.classchat.Util.Util_NetUtil;
import com.example.classchat.Util.Util_ToastUtils;
import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.TouchTypeDetector;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.qiantao.coordinatormenu.CoordinatorMenu;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Fragment_Market extends Fragment {
    private static final String TAG = "Fragment_Market";
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private int mCount = 0;//用于计数,加载(mCount+1)*10件或所有商品
    private Adapter_CommodityRecycleView mRecyclerViewAdapter;
    private ImageButton toTop;
    private RadioGroup sort;
    private CoordinatorMenu mCoordinatorMenu;
    private Button menu_1, menu_2, menu_3, menu_4, menu_5;
    private LinearLayout hint;

    private BoomMenuButton bmb;
    private static int[] imageResources = new int[]{
            R.drawable.ic_market_mygoods,
            R.drawable.ic_market_shoppingcart,
            R.drawable.ic_market_addgoods
    };
    private static String[] textResources = new String[]{
            "我的商品",
            "我的购物车",
            "发布商品"
    };

    private MaterialSearchBar searchBar;

    private List mlist = new ArrayList<Object_Commodity>();//放入布局的list
    private List list;//处理过的商品list
    private static List itemslist = new ArrayList<Object_Commodity>();//原始获取的商品itemlist
    private List<String> jsonlist;

    //定义接收信息时所需的变量
    protected final static int RECEIVE_SUCCESS = 1;
    protected final static int RECEIVE_NULL = 2;
    protected final static int RECEIVE_MID = 3;
    protected final static int SEARCH_NULL = 4;
    protected final static int REACH_END = 5;

    private final static int  DEFAULT_SORT = R.id.rb_Market_default_sort;//默认顺序
//    private final static int  THUMB_SORT = R.id.rb_Market_thumbs_sort;//点赞数最多
    private final static int  UP_SORT = R.id.rb_Market_up_sort;//价格升序
    private final static int  DOWN_SORT = R.id.rb_Market_down_sort;//价格降序

    private static boolean isFirst = true;//初次加载判断
    private int myType = 0;
    private final static int GROCERY = 0;
    private final static int SNACK_AND_FRUIT = 1;
    private final static int COSMETIC = 2;
    private final static int WOMEN = 3;
    private final static int MEN = 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //手势监听
        Sensey.getInstance().init(getContext());
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_market, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化获取商品列表
        getItemFromWeb(myType);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sort = view.findViewById(R.id.rg_Market_sort);
        sort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                getQuery();
            }
        });
        searchBar = view.findViewById(R.id.searchBar);
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //取消搜索
                if (!searchBar.isSearchEnabled())
                    getItemFromWeb(myType);
            }

            @Override
            public void onSearchConfirmed(CharSequence key) {
                //收起键盘，在MainActivity设置防止导航条上移
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
                searchItem(key.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                switch (buttonCode) {
                    case MaterialSearchBar.BUTTON_BACK:
                        searchBar.disableSearch();
                        mCount = 0;
                        getItemFromWeb(myType);
                        break;
                    default:
                        break;
                }
            }
        });

        //菜单
        bmb = view.findViewById(R.id.bmb);
        assert bmb != null;
        bmb.setButtonEnum(ButtonEnum.TextInsideCircle);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_3_3);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_3_3);
        bmb.setDraggable(true);//可拖动
        //设置菜单各按钮
        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {
            TextInsideCircleButton.Builder builder = new TextInsideCircleButton.Builder()
                    .normalImageRes(imageResources[i])
                    .imagePadding(new Rect(20, 20, 20, 35))
                    .normalText(textResources[i])
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            Intent intent = null;
                            switch (index) {
                                case 0:
                                    intent = new Intent(getActivity(), Activity_Market_MyGoods.class);
                                    break;
                                case 1:
                                    intent = new Intent(getActivity(), Activity_Market_ShoppingCart.class);
                                    break;
                                case 2:
                                    intent = new Intent(getActivity(), Activity_Market_AddCommodity.class);
                                    break;
                                default:
                                    break;
                            }
                            if (intent != null) startActivity(intent);

                        }
                    });
            bmb.addBuilder(builder);
        }

        mPullLoadMoreRecyclerView = view.findViewById(R.id.pullLoadMoreRecyclerView);

        toTop = view.findViewById(R.id.ib_Market_toTop);
        toTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPullLoadMoreRecyclerView.scrollToTop();
                toTop.setVisibility(View.GONE);
            }
        });


        mCoordinatorMenu = view.findViewById(R.id.cm_menu);
        menu_1 = view.findViewById(R.id.menu_1);
        menu_2 = view.findViewById(R.id.menu_2);
        menu_3 = view.findViewById(R.id.menu_3);
        menu_4 = view.findViewById(R.id.menu_4);
        menu_5 = view.findViewById(R.id.menu_5);
        hint = view.findViewById(R.id.hint);
        hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCoordinatorMenu.openMenu();
            }
        });

        changeMenu(menu_1, GROCERY);
        changeMenu(menu_2, SNACK_AND_FRUIT);
        changeMenu(menu_3, COSMETIC);
        changeMenu(menu_4, WOMEN);
        changeMenu(menu_5, MEN);

        TouchTypeDetector.TouchTypListener touchTypListener = new TouchTypeDetector.TouchTypListener() {
            @Override
            public void onDoubleTap() {}
            @Override
            public void onLongPress() {}

            @Override
            public void onScroll(int scrollDirection) {
                if(mCoordinatorMenu.isOpened())
                    mCoordinatorMenu.closeMenu();

                if(mlist.size() > 10){
                    switch (scrollDirection) {
                        case TouchTypeDetector.SCROLL_DIR_UP:
                            //隐藏电梯小火箭
                            toTop.setVisibility(View.GONE);
                            break;
                        case TouchTypeDetector.SCROLL_DIR_DOWN:
                            //显示电梯小火箭
                            toTop.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onSingleTap() {}
            @Override
            public void onSwipe(int swipeDirection) {}
            @Override
            public void onThreeFingerSingleTap() {}
            @Override
            public void onTwoFingerSingleTap() {}
        };
        Sensey.getInstance().startTouchTypeDetection(getContext(),touchTypListener);

    }

    private void changeMenu(final Button button, final int type){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myType = type;
                getItemFromWeb(myType);
                mCoordinatorMenu.closeMenu();
            }
        });
    }


    class PullLoadMoreListener implements PullLoadMoreRecyclerView.PullLoadMoreListener {
        @Override
        public void onRefresh() {
            mPullLoadMoreRecyclerView.setPushRefreshEnable(true);
            //手势监听
            //快速回顶优化

            //更新加载计数
            mCount = 0;
            getItemFromWeb(myType);
        }

        @Override
        public void onLoadMore() {
            ++ mCount;
            getItemFromWeb(myType);
        }
    }

    //用一个Handler去处理反应信息(从数据库去查询信息)
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case RECEIVE_SUCCESS:
                    mRecyclerViewAdapter.notifyDataSetChanged();
                    mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    Util_ToastUtils.showToast(getContext(),"商品加载完毕");
                    break;
                case RECEIVE_NULL:
                    mPullLoadMoreRecyclerView.setAdapter(new Adapter_CommodityRecycleView(getActivity(),mlist));
                    Util_ToastUtils.showToast(getContext(),"暂时没有商品呢");
                    break;
                case RECEIVE_MID:
                    getQuery();
                    break;
                case SEARCH_NULL:
                    Util_ToastUtils.showToast(getActivity(), "骚瑞！没有找到这件宝贝,换个关键词试试吧！");
                    break;
                case REACH_END:
                    mRecyclerViewAdapter.notifyDataSetChanged();
                    mPullLoadMoreRecyclerView.setPushRefreshEnable(false);
                    Util_ToastUtils.showToast(getActivity(),"已加载完所有商品");
                    break;
                default:
                    break;
            }
        }
    };

    private void searchItem(final String key){
        //搜索包含品名关键词key的商品
        list = new ArrayList<Object_Commodity>();
        final Message message = new Message();
        // 搜索
        itemslist = new ArrayList<Object_Commodity>();
        // 构建requestbody
        RequestBody requestBody = new FormBody.Builder()
                .add("keywords", key)
                .build();

        // 发送网络请求，联络信息
        Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/getconditionalitems", requestBody, new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 得到服务器返回的具体内容
                String responseData = response.body().string();
                // 转化为具体的对象列表
                List<String> jsonlist = JSON.parseArray(responseData, String.class);
                for(String s : jsonlist) {
                    Object_Commodity object_commodity = JSON.parseObject(s, Object_Commodity.class);
                    Object_Main_Brief_Item object_main_brief_item = JSON.parseObject(s, Object_Main_Brief_Item.class);
                    itemslist.add(object_commodity);
//                    itemslist.add(object_main_brief_item);
                }

                if(itemslist.size() == 0){
                    message.what = SEARCH_NULL;
                }
                else {
                    message.what = RECEIVE_MID;
                }
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // 在这里对异常情况进行处理
                Log.d(TAG, "onFailure: 搜索商品失败");
            }
        });
    }

    //按需处理商品列表
    private void getQuery()
    {
        //初次加载
        if(isFirst){
            mPullLoadMoreRecyclerView.setStaggeredGridLayout(2);
            mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreListener());
            mRecyclerViewAdapter = new Adapter_CommodityRecycleView(getContext(), mlist);
            mPullLoadMoreRecyclerView.setAdapter(mRecyclerViewAdapter);
            isFirst = false;
        }
        //准备发信息
        Message message = new Message();
        if(itemslist.size() == 0){
            message.what = RECEIVE_NULL;
            mlist.clear();
            handler.sendMessage(message);
        }
        else{
            //按排序选择初始化list
            switch(sort.getCheckedRadioButtonId()){
                case DEFAULT_SORT:
                    list = new ArrayList<Object_Main_Brief_Item>();
                    break;
                default:
                    list = new ArrayList<Object_Main_Brief_Item_PriceSort>();
                    break;
            }

            //对商品排序处理
            for (int i = 0;i < itemslist.size();i++){
                Object_Main_Brief_Item item = (Object_Main_Brief_Item) itemslist.get(i);
                String itemID = item.getId();
                String itemName = item.getName();
                String imageList = item.getImage();
                double price = item.getPrice();

                switch (sort.getCheckedRadioButtonId()){
                    case DEFAULT_SORT:
                        list.add(new Object_Main_Brief_Item(itemID, itemName, imageList, price));
                        break;
//                    case THUMB_SORT:
//                        list.add(new Object_Commodity_ThumbsUpSort(itemID, itemName, imageList, ownerID, price, briefintroduction, detailinformation, thumbedlist));
//                        break;
                    case UP_SORT:
                    case DOWN_SORT:
                        list.add(new Object_Main_Brief_Item_PriceSort(itemID, itemName, imageList, price));
                        break;
                    default:
                        break;
                }
            }

            if(sort.getCheckedRadioButtonId() != DEFAULT_SORT){
                Collections.sort(list);
                if(sort.getCheckedRadioButtonId() == DOWN_SORT)
                    Collections.reverse(list);//默认升序，DOWN_SORT需再倒序
            }

//            //重置mlist
            if(mlist.size() > 0)
                mlist.clear();

            //将list内容传给mlist
            mlist.addAll(list);

            //判断是否到底
            if(jsonlist.size() == 10)
                message.what = RECEIVE_SUCCESS;
            else
                message.what = REACH_END;
            handler.sendMessage(message);
        }
        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
    }

    //TODO 数据库获取数据 所有商品itemslist
    //仅用于初始状态下，获取原始商品信息itemslist
    public void getItemFromWeb(int type){
        //TODO　count是计数刻度
        //准备发消息
        final Message message = new Message();
        //构建requestbody
        RequestBody requestBody = new FormBody.Builder()
                .add("mcount",String.valueOf(mCount))
                .add("type", type + "")
                .build();
        // 发送网络请求，联络信息
        Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8090/getitembytype", requestBody, new okhttp3.Callback() {
            @Override

            public void onResponse(Call call, Response response) throws IOException {
                // 得到服务器返回的具体内容
                String responseData = response.body().string();
                // 转化为具体的对象列表
                //若是新刷新的需重置itemlist
                if(mCount == 0) {
                    itemslist.clear();
                }
                jsonlist = JSON.parseArray(responseData, String.class);
                if(jsonlist == null)
                    message.what = RECEIVE_NULL;
                else {
                    for(int i = 0; i < jsonlist.size(); i++) {
                        Object_Main_Brief_Item object_main_brief_item = JSON.parseObject(jsonlist.get(i), Object_Main_Brief_Item.class);
                        itemslist.add(object_main_brief_item);
                    }
                    Log.e("itemlist", itemslist.toString());
                    // 发送收到成功的信息
                    //由getQuery()按需处理后载入布局
                    message.what = RECEIVE_MID;
                }
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // 在这里对异常情况进行处理
                Log.d(TAG, "onFailure: 加载商品失败，没有收到商品");
            }
        });
    }

}
