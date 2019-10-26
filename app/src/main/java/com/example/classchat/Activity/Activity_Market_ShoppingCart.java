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
import com.example.classchat.Object.Object_Item_Detail;
import com.example.classchat.Object.Object_Pre_Sale;
import com.example.classchat.R;
import com.example.classchat.Util.Util_ToastUtils;
import com.example.library_cache.disklrucache.Util;
import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.TouchTypeDetector;
import com.googlecode.mp4parser.authoring.tracks.TextTrackImpl;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.json.JSONException;

import java.lang.reflect.Array;
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
        mPullLoadMoreRecyclerView = findViewById(R.id.pullLoadMoreRecyclerView);

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
            // 结算按钮的点击事件
            List<Object_Pre_Sale> toBuy = new ArrayList();
            int no_choose = 0 ;
            if ( datas != null ){
                for (int i = 0 ; i < datas.size() ; i++ ){
                    if ( datas.get(i).isChildSelected() ){
                        toBuy.add(datas.get(i));
                        no_choose += 1 ;
                    }
                }
            }
            if ( no_choose == 0 ){
                Util_ToastUtils.showToast(Activity_Market_ShoppingCart.this, "请先选择想要购买的商品");
                adapter.notifyDataSetChanged();
            }else {
                boolean tag = true;
                for ( int i = 0 ; i < toBuy.size() ; i++ ){
                    if ( toBuy.get(i).getNum() > getStorageNumber(toBuy.get(i).getItemId())){
                        Util_ToastUtils.showToast(Activity_Market_ShoppingCart.this, "存在超过库存的商品");
                        tag = false;
                        break;
                    }
                }
                if ( tag ){
                    // todo 跳转到确认订单界面 传值
                    Toast.makeText(Activity_Market_ShoppingCart.this, "跳转成功", Toast.LENGTH_SHORT).show();
                }else{
                    Util_ToastUtils.showToast(Activity_Market_ShoppingCart.this, "结算失败");
                    adapter.notifyDataSetChanged();
                }
            }
        } else if (v == tvShopcartEdit) {
            //设置编辑按钮的点击事件
            int tag = (int) tvShopcartEdit.getTag();
            // 检查状态
            if (tag == ACTION_EDIT) {
                // 如果是编辑字样，点击就可以编辑
                showDelete();
            } else {
                // 如果是完成字样，点击就不能编辑了
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

        adapter.check_all_none(false);
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

        adapter.check_all_none(false);
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

        /**
         * 数据传输
         */
        if(!information.equals("error") && !information.equals("[{}]")){
            List<Object_Pre_Sale> commodityList = JSON.parseObject(information , new TypeReference<List<Object_Pre_Sale>>(){});
            for (int i = 0 ; i < commodityList.size() ; i++){
                datas.add(commodityList.get(i));
            }

            // 虚拟数据
            // makeData();

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
                mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
                    @Override
                    public void onRefresh() {
                        mPullLoadMoreRecyclerView.setPushRefreshEnable(true);
                        mPullLoadMoreRecyclerView.setFooterViewText("下拉刷新");
                        mPullLoadMoreRecyclerView.setFooterViewTextColor(R.color.black);
                        adapter.notifyDataSetChanged();
                        Util_ToastUtils.showToast(Activity_Market_ShoppingCart.this, "刷新成功");
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
    }

    private void makeData() {
        String string1 = "这是一首简单的小情歌";
        List<String> tempList = new ArrayList<>();
        tempList.add(string1);
        float temp_price = (float)2.22;
        Object_Pre_Sale object_pre_sale1 = new Object_Pre_Sale("我会给你怀抱", "0", tempList, 2, temp_price,
                "http://b-ssl.duitang.com/uploads/item/201209/07/20120907181244_tGiNN.jpeg");
        datas.add(object_pre_sale1);

        List<String> tempList1 = new ArrayList<>();
        string1 = "唱着我们心中的曲折";
        tempList1.add(string1);
        Object_Pre_Sale object_pre_sale2 = new Object_Pre_Sale("明知道 就算大雨让这座城市颠倒", "1", tempList1, 2, temp_price,
                "http://b-ssl.duitang.com/uploads/item/201209/07/20120907181244_tGiNN.jpeg");
        datas.add(object_pre_sale2);

        List<String> tempList2 = new ArrayList<>();
        string1 = "我想我很适合";
        tempList2.add(string1);
        Object_Pre_Sale object_pre_sale3 = new Object_Pre_Sale("受不了 看着你背影来到", "2", tempList2, 16, temp_price,
                "http://b-ssl.duitang.com/uploads/item/201209/07/20120907181244_tGiNN.jpeg");
        datas.add(object_pre_sale3);
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
                        data_changed(position);
                    }else{
                        Util_ToastUtils.showToast(Activity_Market_ShoppingCart.this, "警告：超过库存");
                    }
                    break;
                case R.id.count_sub:
                    int count_temp1 = datas.get(position).getNum();
                        if (count_temp1 > 1){
                        datas.get(position).setNum(count_temp1 - 1);
                        adapter.notifyItemChanged(position, R.id.item_count);
                        data_changed(position);
                    }else {
                        Util_ToastUtils.showToast(Activity_Market_ShoppingCart.this, "已是最低数量");
                    }
                    break;
                case R.id.ll_itemName:
                case R.id.iv_gov:
                case R.id.tv_desc_gov:
                    // test
                    Intent intent = new Intent(Activity_Market_ShoppingCart.this, Activity_Market_GoodsDetail.class);
                    String itemID = datas.get(position).getItemId();
                    intent.putExtra("itemId",itemID);
                    startActivity(intent);
                    break;
                case R.id.cb_gov:
                    int tag = (int) tvShopcartEdit.getTag();
                    if (tag == ACTION_COMPLETE) {
                        // 如果是完成字样
                        boolean b = datas.get(position).isChildSelected();
                        datas.get(position).setChildSelected(!b);
                        adapter.notifyItemChanged(position, R.id.cb_gov);
                        adapter.showTotalPrice();
                        adapter.checkAll();
                    } else {
                        // 如果是编辑字样
                        if ( getStorageNumber(datas.get(position).getItemId()) == 0 ){
                            datas.get(position).setChildSelected(false);
                            adapter.notifyItemChanged(position, R.id.cb_gov);
                            adapter.showTotalPrice();
                            adapter.checkAll();
                            Util_ToastUtils.showToast(Activity_Market_ShoppingCart.this, "商品暂时缺货，请勿勾选");
                        }else if ( getStorageNumber(datas.get(position).getItemId()) > 0 && datas.get(position).getNum() <= getStorageNumber(datas.get(position).getItemId())){
                            boolean b = datas.get(position).isChildSelected();
                            datas.get(position).setChildSelected(!b);
                            adapter.notifyItemChanged(position, R.id.cb_gov);
                            adapter.showTotalPrice();
                            adapter.checkAll();
                        }else if ( getStorageNumber(datas.get(position).getItemId()) > 0 && datas.get(position).getNum() > getStorageNumber(datas.get(position).getItemId())){
                            datas.get(position).setChildSelected(false);
                            adapter.notifyItemChanged(position, R.id.cb_gov);
                            adapter.showTotalPrice();
                            adapter.checkAll();
                            Util_ToastUtils.showToast(Activity_Market_ShoppingCart.this, "警告：超过库存");
                        }
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
        SharedPreferences sp = getSharedPreferences("shopping_cart_cache" , MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().commit();
        editor.putString("cart_information", JSON.toJSONString(datas)).commit();
    }

    /**
     * todo 发请求得到库存的数量
     * @param itemID
     * @return
     */
    private int getStorageNumber(String itemID){
        int number = Integer.parseInt(itemID);
//        int number = 0;
        return number;
    }
}
