package com.example.classchat.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bumptech.glide.Glide;
import com.example.classchat.Adapter.Adapter_CompareTable;
import com.example.classchat.Adapter.Adapter_GoodsDetail;
import com.example.classchat.Adapter.Adapter_ShoppingCart;
import com.example.classchat.Adapter.NetworkImageHolderView;
import com.example.classchat.Object.Object_Commodity;
import com.example.classchat.Object.Object_Commodity_Shoppingcart;
import com.example.classchat.Object.Object_Item_Detail;
import com.example.classchat.R;
import com.example.classchat.Util.Util_ScreenShot;

import com.example.classchat.Util.Util_ToastUtils;
import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.TouchTypeDetector;
import com.hankkin.library.GradationScrollView;
import com.hankkin.library.MyImageLoader;
import com.hankkin.library.NoScrollListView;
import com.hankkin.library.ScrollViewContainer;
import com.hankkin.library.StatusBarUtil;
import com.hch.thumbsuplib.ThumbsUpCountView;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Activity_Market_GoodsDetail extends AppCompatActivity{

    private TextView price, name;
    private Object_Item_Detail object_item_detail;
    private ConvenientBanner mBanner;
    private RecyclerView rv;
    private Adapter_GoodsDetail myAdapter;


    //handler处理反应回来的信息
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__market__goods_detail);
        price = findViewById(R.id.tv_price);
        name = findViewById(R.id.tv_name);
        price.setText(String.format("%s", object_item_detail.getItem().getPrice()));
        name.setText(object_item_detail.getItem().getName());

        mBanner = findViewById(R.id.banner);
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
        //TODO 购买逻辑
    }
}



