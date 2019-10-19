package com.example.classchat.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.donkingliang.labels.LabelsView;
import com.example.classchat.Adapter.Adapter_GoodsDetail;
import com.example.classchat.Adapter.NetworkImageHolderView;
import com.example.classchat.Object.Object_Item_Detail;
import com.example.classchat.Object.Object_Stock;
import com.example.classchat.R;
import com.example.classchat.Util.Util_NetUtil;
import com.example.classchat.Util.Util_ToastUtils;

import java.io.IOException;
import java.util.Collections;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Activity_Market_GoodsDetail extends AppCompatActivity{

    private TextView minPrice, name, detailprice;
    private Object_Item_Detail object_item_detail;
    private ConvenientBanner mBanner;
    private RecyclerView rv;
    private Adapter_GoodsDetail myAdapter;
    private PopupWindow popupWindow;
    private LabelsView param1, param2, param3;
    private String paramChosen1, paramChosen2, paramChosen3, itemid;
    private TextView param1Name, param2Name, param3Name, stock, number;
    private Button close, add, sub;
    private RelativeLayout rl_choose;
    private int num = 1;
    final int[] stockNum = new int[1];
    final float[] detailPrice = new float[1];
    final boolean[] exist = {false};

    private final static int GET_DETAIL_SUCCESS = 0;
    private final static int CAN_BUY = 1;
    private final static int CANNOT_BUY = 2;

    //handler处理反应回来的信息
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case GET_DETAIL_SUCCESS:
                    rl_choose.setEnabled(true);
                    mBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
                        @Override
                        public NetworkImageHolderView createHolder() {
                            return new NetworkImageHolderView();
                        }
                    }, Collections.singletonList(object_item_detail.getItem().getImg_list_1()));

                    mBanner.setPointViewVisible(true);
                    mBanner.startTurning(2000);

                    rv = findViewById(R.id.goods_detail_pic);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(Activity_Market_GoodsDetail.this);
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    rv.setLayoutManager(layoutManager);
                    myAdapter = new Adapter_GoodsDetail(Activity_Market_GoodsDetail.this, Collections.singletonList(object_item_detail.getItem().getImg_list_2()));
                    rv.setAdapter(myAdapter);
                    break;
                case CAN_BUY:
                    //TODO 跳转至后续购买界面
                    break;
                case CANNOT_BUY:
                    Util_ToastUtils.showToast(Activity_Market_GoodsDetail.this, "库存不足请重新选择！");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__market__goods_detail);
        minPrice = findViewById(R.id.tv_price);
        name = findViewById(R.id.tv_name);
        rl_choose = findViewById(R.id.goods_detail_choose_rl);
        mBanner = findViewById(R.id.banner);

        minPrice.setText(String.format("%s", object_item_detail.getItem().getPrice()));
        name.setText(object_item_detail.getItem().getName());
        //完成加载商品详情前不能选择属性！
        rl_choose.setEnabled(false);

        Intent intent = getIntent();
        itemid = intent.getStringExtra("itemId");

        getDetailFromWeb();
    }

    private void getDetailFromWeb() {
        final Message message = new Message();
        //构建requestbody
        RequestBody requestBody = new FormBody.Builder()
                .add("itemid", itemid)
                .build();
        // 发送网络请求，联络信息
        Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8090/getitemdetail", requestBody, new okhttp3.Callback() {
            @Override

            public void onResponse(Call call, Response response) throws IOException {
                // 得到服务器返回的具体内容
                object_item_detail = JSON.parseObject(response.body().string(), Object_Item_Detail.class);
                switch (object_item_detail.getParamList().size()){
                    case 1:
                        //初始化默认选项
                        paramChosen1 = object_item_detail.getRangeList().get(0).get(0);
                        break;
                    case 2:
                        paramChosen1 = object_item_detail.getRangeList().get(0).get(0);
                        paramChosen2 = object_item_detail.getRangeList().get(1).get(0);
                        break;
                    case 3:
                        paramChosen1 = object_item_detail.getRangeList().get(0).get(0);
                        paramChosen2 = object_item_detail.getRangeList().get(1).get(0);
                        paramChosen3 = object_item_detail.getRangeList().get(2).get(0);
                        break;
                    default:
                        break;
                }
                message.what = GET_DETAIL_SUCCESS;
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {}
        });

    }

    public void back(View view) {
        finish();
    }

    public void toShoppingChart(View view) {
        //TODO
//        startActivity(new Intent(Activity_Market_GoodsDetail.this, 购物车));
    }

    public void addToShoppingCart(View view) {
        //TODO 添加商品到购物车
    }

    public void buy(View view) {
        //再次发请求确认一下是否有库存
        final Message message = new Message();
        RequestBody requestBody = new FormBody.Builder()
                .add("itemid", itemid)
                .build();
        // 发送网络请求，联络信息
        Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8090/getitemdetail", requestBody, new okhttp3.Callback() {
            @Override

            public void onResponse(Call call, Response response) throws IOException {
                // 得到服务器返回的具体内容
                object_item_detail = JSON.parseObject(response.body().string(), Object_Item_Detail.class);
                switch (object_item_detail.getParamList().size()){
                    case 1:
                        for (Object_Stock s : object_item_detail.getStockList()){
                            if(s.getParam1().equals(paramChosen1))
                                exist[0] = true;
                        }
                        break;
                    case 2:
                        for (Object_Stock s : object_item_detail.getStockList()){
                            if(s.getParam1().equals(paramChosen1) && s.getParam2().equals(paramChosen2))
                                exist[0] = true;
                        }
                        break;
                    case 3:
                        for (Object_Stock s : object_item_detail.getStockList()){
                            if(s.getParam1().equals(paramChosen1) && s.getParam2().equals(paramChosen2) && s.getParam3().equals(paramChosen3))
                                exist[0] = true;
                        }
                        break;
                    default:
                        exist[0] = false;
                        break;
                }
                if(!exist[0])
                    message.what = CANNOT_BUY;
                else message.what = CAN_BUY;
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {}
        });
    }

    public void choose(View view) {
        View inflate = LayoutInflater.from(Activity_Market_GoodsDetail.this).inflate(R.layout.item_choose_goods_popupwindow, null);
        popupWindow = new PopupWindow(inflate, RelativeLayout.LayoutParams.MATCH_PARENT,
                 RelativeLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x292421));
        popupWindow.setAnimationStyle(R.style.Animation_Design_BottomSheetDialog);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        //设置位置
        popupWindow.showAtLocation(inflate, Gravity.BOTTOM, 0, 0);
        setOnPopupViewClick(inflate);
    }

    private void setOnPopupViewClick(View view) {
        close = view.findViewById(R.id.close_choose);
        add = view.findViewById(R.id.add_count);
        sub = view.findViewById(R.id.sub_count);
        //属性选择栏
        param1 = view.findViewById(R.id.param1);
        param2 = view.findViewById(R.id.param2);
        param3 = view.findViewById(R.id.param3);
        //属性名称显示
        param1Name = view.findViewById(R.id.param1_name);
        param2Name = view.findViewById(R.id.param2_name);
        param3Name = view.findViewById(R.id.param3_name);
        //购买数量
        number = view.findViewById(R.id.goods_detail_num);
        //库存量
        stock = view.findViewById(R.id.stock);
        //具体价格
        detailprice = view.findViewById(R.id.price);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        switch (object_item_detail.getParamList().size()){
            case 1:
                //初始化价格及库存
                setCountAndPricefor1Param();
                //设置属性名称
                param1Name.setText(object_item_detail.getParamList().get(0));
                //绑定属性选择范围列表
                param1.setLabels(object_item_detail.getRangeList().get(0));

                param1.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
                    @Override
                    public void onLabelSelectChange(TextView label, Object data, boolean isSelect, int position) {
                        //label是被选中的标签，data是标签所对应的数据，isSelect是是否选中，position是标签的位置。
                        if(isSelect){
                            paramChosen1 = (String) data;
                            setCountAndPricefor1Param();
                        }
                    }
                });
                break;
            case 2:
                setCountAndPricefor2Param();

                param1Name.setText(object_item_detail.getParamList().get(0));
                param2Name.setText(object_item_detail.getParamList().get(1));
                param1.setLabels(object_item_detail.getRangeList().get(0));
                param2.setLabels(object_item_detail.getRangeList().get(1));

                param1.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
                    @Override
                    public void onLabelSelectChange(TextView label, Object data, boolean isSelect, int position) {
                        if(isSelect){
                            paramChosen1 = (String) data;
                            setCountAndPricefor2Param();
                        }
                    }
                });

                param2.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
                    @Override
                    public void onLabelSelectChange(TextView label, Object data, boolean isSelect, int position) {
                        if(isSelect){
                            paramChosen2 = (String) data;
                            setCountAndPricefor2Param();
                        }
                    }
                });
                break;
            case 3:
                setCountAndPricefor3Param();

                param1Name.setText(object_item_detail.getParamList().get(0));
                param2Name.setText(object_item_detail.getParamList().get(1));
                param3Name.setText(object_item_detail.getParamList().get(2));
                param1.setLabels(object_item_detail.getRangeList().get(0));
                param2.setLabels(object_item_detail.getRangeList().get(1));
                param3.setLabels(object_item_detail.getRangeList().get(2));

                param1.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
                    @Override
                    public void onLabelSelectChange(TextView label, Object data, boolean isSelect, int position) {
                        if(isSelect){
                            paramChosen1 = (String) data;
                            setCountAndPricefor3Param();
                        }
                    }
                });

                param2.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
                    @Override
                    public void onLabelSelectChange(TextView label, Object data, boolean isSelect, int position) {
                        if(isSelect){
                            paramChosen2 = (String) data;
                            setCountAndPricefor3Param();
                        }
                    }
                });

                param3.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
                    @Override
                    public void onLabelSelectChange(TextView label, Object data, boolean isSelect, int position) {
                        if(isSelect){
                            paramChosen3 = (String) data;
                            setCountAndPricefor3Param();
                        }
                    }
                });
                break;
            default:
                break;
        }

        number.setText(num);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(num == 1)
                    sub.setEnabled(true);
                num ++;
                if(num == stockNum[0])
                    add.setEnabled(false);

                number.setText(num);

            }
        });

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num --;
                if(num == 1)
                    sub.setEnabled(false);
                if(num == (stockNum[0] - 1))
                    add.setEnabled(true);

                number.setText(num);
            }
        });

    }

    private void setCountAndPricefor1Param(){
        for (Object_Stock s : object_item_detail.getStockList()){
            if(s.getParam1().equals(paramChosen1)){
                exist[0] = true;
                stockNum[0] = s.getCount();
                detailPrice[0] = s.getPrice();
            }
        }

        if(!exist[0]){
            stockNum[0] = 0;
            detailPrice[0] = 0;
        }

        detailprice.setText(detailPrice[0] + "");
        stock.setText(stockNum[0]);

        if(num > stockNum[0])
            Util_ToastUtils.showToast(Activity_Market_GoodsDetail.this, "库存不足，请修改购买数量！");
        if(num < stockNum[0])
            add.setEnabled(true);
    }

    private void setCountAndPricefor2Param(){
        for (Object_Stock s : object_item_detail.getStockList()){
            if(s.getParam1().equals(paramChosen1) && s.getParam2().equals(paramChosen2)){
                exist[0] = true;
                stockNum[0] = s.getCount();
                detailPrice[0] = s.getPrice();
            }
        }
        if(!exist[0]){
            stockNum[0] = 0;
            detailPrice[0] = 0;
        }

        detailprice.setText(detailPrice[0] + "");
        stock.setText(stockNum[0]);
        if(num > stockNum[0])
            Util_ToastUtils.showToast(Activity_Market_GoodsDetail.this, "库存不足，请修改购买数量！");
        if(num < stockNum[0])
            add.setEnabled(true);
    }

    private void setCountAndPricefor3Param(){
        for (Object_Stock s : object_item_detail.getStockList()){
            if(s.getParam1().equals(paramChosen1) && s.getParam2().equals(paramChosen2) && s.getParam3().equals(paramChosen3)){
                exist[0] = true;
                stockNum[0] = s.getCount();
                detailPrice[0] = s.getPrice();
            }
        }

        if(!exist[0]){
            stockNum[0] = 0;
            detailPrice[0] = 0;
        }
        detailprice.setText(detailPrice[0] + "");
        stock.setText(stockNum[0]);

        if(num > stockNum[0])
            Util_ToastUtils.showToast(Activity_Market_GoodsDetail.this, "库存不足，请修改购买数量！");
        if(num < stockNum[0])
            add.setEnabled(true);
    }
}



