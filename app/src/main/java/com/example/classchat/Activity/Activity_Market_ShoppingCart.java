package com.example.classchat.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.classchat.Adapter.Adapter_ShoppingCart;
import com.example.classchat.Object.Object_Commodity;
import com.example.classchat.Object.Object_Pre_Sale;
import com.example.classchat.R;
import com.example.classchat.Util.Util_ToastUtils;
import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.TouchTypeDetector;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import io.rong.imageloader.utils.L;

public class Activity_Market_ShoppingCart extends Activity implements View.OnClickListener {

    private ImageButton ibShopcartBack;
    private TextView tvShopcartEdit;
    private RecyclerView recyclerview;
    private CheckBox checkboxAll;
    private TextView tvShopcartTotal;
    private LinearLayout ll_check_all;
    private LinearLayout ll_delete;
    private CheckBox cb_all;
    private Button btn_delete;
    private Button btnCheckOut;
    private Adapter_ShoppingCart adapter;
    private LinearLayout ll_empty_shopcart;
    private TextView tv_empty_cart_tobuy;

    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;

//    private TextView check_size;
//    private TextView item_name;
//    // 以下是数量选择器的变量
//    private ImageView count_add;
//    private ImageView count_sub;
//
//    private TextView item_count;

    private List<Object_Pre_Sale> datas = new ArrayList<>();
    private  List<Object_Commodity> commodityList = new ArrayList<>();

    /**
     * 编辑状态
     */
    private static final int ACTION_EDIT = 0;
    /**
     * 完成状态
     */
    private static final int ACTION_COMPLETE = 1;

    /**
     * Find the Views in the layout
     */
    private void findViews() {
        /**
         * 绑定控件
         */
        ibShopcartBack = findViewById(R.id.ib_shopcart_back);
        tvShopcartEdit = findViewById(R.id.tv_shoppingcart_edit);
        recyclerview = findViewById(R.id.recyclerview);
        checkboxAll = findViewById(R.id.checkbox_all);
        tvShopcartTotal = findViewById(R.id.tv_shopcart_total);
        btnCheckOut = findViewById(R.id.btn_check_out);
        ll_check_all = findViewById(R.id.ll_check_all);
        ll_delete = findViewById(R.id.ll_delete);
        cb_all = findViewById(R.id.cb_all);
        btn_delete = findViewById(R.id.btn_delete);
        ll_empty_shopcart = findViewById(R.id.ll_empty_shopcart);
        tv_empty_cart_tobuy = findViewById(R.id.tv_empty_cart_tobuy);

        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView)findViewById(R.id.pullLoadMoreRecyclerView);

//        count_add = findViewById(R.id.count_add);
//        count_sub = findViewById(R.id.count_sub);
//        item_count = findViewById(R.id.item_count);
//        check_size = findViewById(R.id.check_size);
//        item_name = findViewById(R.id.tv_desc_gov);

        /**
         * 设置监听
         */
        ibShopcartBack.setOnClickListener(this);
        btnCheckOut.setOnClickListener(this);
        tvShopcartEdit.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        tv_empty_cart_tobuy.setClickable(true); // 空购物车时“去看看”按钮设为true
        tv_empty_cart_tobuy.setOnClickListener(this);
    }

    /**
     * Handle button click events
     */
    @Override
    public void onClick(View v) {
        if (v == ibShopcartBack) {
            // 设置返回按钮的监听事件
            finish(); // 直接退出
        } else if (v == btnCheckOut) {
            //todo 设置结算按钮的点击事件 应该是进入确认订单界面
            Toast.makeText(Activity_Market_ShoppingCart.this, "结算", Toast.LENGTH_SHORT).show();
        } else if (v == tvShopcartEdit) {
            //设置编辑按钮的点击事件
            int tag = (int) tvShopcartEdit.getTag();
            // 检查状态
            if (tag == ACTION_EDIT) {
                // 变成完成状态
                showDelete();
            } else {
                // 变成编辑状态
                hideDelete();
            }
        } else if (v == btn_delete) {
            // 设置删除按钮的点击事件
            try {
                // 删除选中的item
                adapter.deleteData();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(Activity_Market_ShoppingCart.this, "删除失败", Toast.LENGTH_SHORT).show();
            }
            // 显示总金额
            adapter.showTotalPrice();
            // 决定显示哪个页面
            checkData();
            // 校验是否全选
            adapter.checkAll();
        } else if (v == tv_empty_cart_tobuy) {
            // 设置“去看看”按钮的点击事件
            // 以下是跳转到商城首页的语句
            Intent intent = new Intent(Activity_Market_ShoppingCart.this, MainActivity.class);
            intent.putExtra("id",1);
            startActivity(intent);
        }
    }

    /**
     * 隐藏编辑界面
     */
    private void hideDelete() {
        tvShopcartEdit.setText("编辑");
        tvShopcartEdit.setTag(ACTION_EDIT);

        adapter.checkAll_none(false);
        ll_delete.setVisibility(View.GONE);
        ll_check_all.setVisibility(View.VISIBLE);

        adapter.showTotalPrice();
    }

    /**
     * 显示编辑界面
     */
    private void showDelete() {
        tvShopcartEdit.setText("完成");
        tvShopcartEdit.setTag(ACTION_COMPLETE);

        adapter.checkAll_none(false);
        cb_all.setChecked(false);
        checkboxAll.setChecked(false);

        ll_delete.setVisibility(View.VISIBLE);
        ll_check_all.setVisibility(View.GONE);

        adapter.showTotalPrice();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__market__shopping_cart);

        TouchTypeDetector.TouchTypListener touchTypListener = new TouchTypeDetector.TouchTypListener() {
            @Override
            public void onDoubleTap() {}
            @Override
            public void onLongPress() {}
            @Override
            public void onScroll(int scrollDirection) {}
            @Override
            public void onSingleTap() {}
            @Override
            public void onSwipe(int swipeDirection) {
                switch (swipeDirection) {
                    case TouchTypeDetector.SWIPE_DIR_RIGHT:
                        finish();
                        Sensey.getInstance().stopTouchTypeDetection();
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onThreeFingerSingleTap() {}
            @Override
            public void onTwoFingerSingleTap() {}
        };
        Sensey.getInstance().startTouchTypeDetection(this,touchTypListener);

        findViews();
        try {
            showData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("尝试成功", "try successfully");
        checkData();

        tvShopcartEdit.setTag(ACTION_EDIT);
        tvShopcartEdit.setText("编辑");
    }

    /**
     * 根据数据决定要选择哪个页面来显示
     */
    private void checkData() {
        if (adapter != null && adapter.getItemCount() > 0) {
            tvShopcartEdit.setVisibility(View.VISIBLE);
            ll_empty_shopcart.setVisibility(View.GONE);
            ll_check_all.setVisibility(View.VISIBLE);
        } else {
            ll_empty_shopcart.setVisibility(View.VISIBLE);
            tvShopcartEdit.setVisibility(View.GONE);
            ll_check_all.setVisibility(View.GONE);
            ll_delete.setVisibility(View.GONE);
        }
    }

    /**
     * 显示数据
     */
    private void showData() throws JSONException {

        SharedPreferences sp = getSharedPreferences("shopping_cart_cache" , MODE_MULTI_PROCESS);
        String information = sp.getString("cart_information" ,"error");

        // debug专用
        System.out.println("这里是从缓存取出来的information在showData里面：" +information);

//        if(!information.equals("error")){
//            commodityList = JSON.parseObject(information , new TypeReference<List<Object_Commodity>>(){});

            /**
             * 把缓存里面的每一个对象都取出来
             * 在这里进行数据传输
             */
//            for(Object_Commodity object_commodity: commodityList) {
//                Object_Pre_Sale object_item_shoppingcart = new Object_Pre_Sale();
//
//                // set到购物车这个类中
//                object_item_shoppingcart.setImgurl(object_commodity.getImageList().get(0));
//                object_item_shoppingcart.setItemId(object_commodity.getItemID());
//                object_item_shoppingcart.setItemName(object_commodity.getItemName());
//                object_item_shoppingcart.setPrice(object_commodity.getPrice());
//
//                //todo setCount
//
//                datas.add(object_item_shoppingcart);
//            }

            makeData();

            // 以下是商品展示界面的语句
            if (datas != null && datas.size() > 0) {
                Log.e("不为空", "not null");
                tvShopcartEdit.setVisibility(View.VISIBLE);
                ll_empty_shopcart.setVisibility(View.GONE);
                adapter = new Adapter_ShoppingCart(this, datas, tvShopcartTotal, checkboxAll, cb_all);
                recyclerview.setLayoutManager(new LinearLayoutManager(this));
                recyclerview.setAdapter(adapter);
                adapter.setOnItemClickListener(MyItemClickListener);
                mPullLoadMoreRecyclerView.setGridLayout(1);
                mPullLoadMoreRecyclerView.setFooterViewText("下拉刷新");
                mPullLoadMoreRecyclerView.setFooterViewTextColor(R.color.black);
                mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
                    @Override
                    public void onRefresh() {
                        mPullLoadMoreRecyclerView.setPushRefreshEnable(true);
                        adapter.notifyDataSetChanged();
                        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    }

                    @Override
                    public void onLoadMore() {
                        mPullLoadMoreRecyclerView.setPullRefreshEnable(false);
                    }
                });
                mPullLoadMoreRecyclerView.setAdapter(adapter);
            } else {
                //显示空的
                tvShopcartEdit.setVisibility(View.GONE);
                ll_empty_shopcart.setVisibility(View.VISIBLE);
                ll_check_all.setVisibility(View.GONE);
                ll_delete.setVisibility(View.GONE);
            }
        }
//    }

    private void makeData() {
        String string1 = "rlsadkfjskljflakjdlakjsdlaskjd";
        List<String> tempList = new ArrayList<>();
        tempList.add(string1);
        Object_Pre_Sale object_pre_sale1 = new Object_Pre_Sale("1", "1", tempList, 2, 2.22,
                "http://b-ssl.duitang.com/uploads/item/201209/07/20120907181244_tGiNN.jpeg");
        datas.add(object_pre_sale1);

        List<String> tempList1 = new ArrayList<>();
        tempList1.add(string1);
        Object_Pre_Sale object_pre_sale2 = new Object_Pre_Sale("adaksdlkajhskfhajshfkjashfkjashkdjahksjhdakjshd", "1", tempList1, 2, 2.22,
                "http://b-ssl.duitang.com/uploads/item/201209/07/20120907181244_tGiNN.jpeg");
        datas.add(object_pre_sale2);
    }

    //用于手势监听
    @Override public boolean dispatchTouchEvent(MotionEvent event) {
        // Setup onTouchEvent for detecting type of touch gesture
        Sensey.getInstance().setupDispatchTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    private Adapter_ShoppingCart.OnItemClickListener MyItemClickListener = new Adapter_ShoppingCart.OnItemClickListener() {

        @Override
        public void onItemClickListener(View v, int position) {
            switch (v.getId()){
                case R.id.count_add:
                    int count_temp = datas.get(position).getNum();
                    Log.e("count", count_temp + "");
                    int max = getStorageNumber(datas.get(position).getItemId());
                    if (count_temp < max){
                        datas.get(position).setNum(count_temp + 1);
                        adapter.notifyItemChanged(position, R.id.item_count);
                    }else{
                        Toast.makeText(Activity_Market_ShoppingCart.this, "您选择的数量已达到商品的最大库存", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.count_sub:
                    int count_temp1 = datas.get(position).getNum();
                    if (count_temp1 > 1){
                        datas.get(position).setNum(count_temp1 - 1);
                        adapter.notifyItemChanged(position, R.id.item_count);
//                        data_changed(position);
                    }else {
                        Toast.makeText(Activity_Market_ShoppingCart.this, "已是最低数量", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.tv_desc_gov:
                case R.id.iv_gov:
                    // test
                    // todo 跳转到特定的商品详情页面
                    Intent intent = new Intent(Activity_Market_ShoppingCart.this, NotificationJumpBack.class);
                    startActivity(intent);
                    break;
                case R.id.cb_gov:
                    if (getStorageNumber(datas.get(position).getItemId()) < 0 ){
                        datas.get(position).setChildSelected(false);
                        adapter.notifyItemChanged(position, R.id.cb_gov);
                        adapter.showTotalPrice();
                        adapter.checkAll();
                        Util_ToastUtils.showToast(Activity_Market_ShoppingCart.this, "商品暂时缺货，请勿勾选");
                    }else {
                        boolean b = datas.get(position).isChildSelected();
                        datas.get(position).setChildSelected(!b);
                        adapter.notifyItemChanged(position, R.id.cb_gov);
                        adapter.showTotalPrice();
                        adapter.checkAll();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 如果数量有变化，就更新缓存的数据
     * @param index
     */
    private void data_changed(int index){
//        commodityList.get(index).setNum(commodityList.get(0).getNum() + 1 );
        SharedPreferences sp = getSharedPreferences("shopping_cart_cache" , MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().commit();
        editor.putString("cart_information", JSON.toJSONString(commodityList)).commit();
    }

    /**
     * todo 发请求得到库存的数量
     * @param itemID
     * @return
     */
    private int getStorageNumber(String itemID){
        int number = 15;
        return number;
    }
}
