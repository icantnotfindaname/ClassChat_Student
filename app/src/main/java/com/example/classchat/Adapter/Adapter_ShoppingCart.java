package com.example.classchat.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.annotation.ColorRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bumptech.glide.Glide;
import com.example.classchat.Object.Object_Pre_Sale;
import com.example.classchat.R;
import com.example.classchat.Util.Util_ToastUtils;
import com.example.library_cache.disklrucache.Util;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用到了商品数据提供类，这里需要修改
 */
public class Adapter_ShoppingCart extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Object_Pre_Sale> datas;
    private TextView tvShopcartTotal;
    private CheckBox checkboxAll;
    //完成状态下的删除checkbox
    private CheckBox cb_all;

    SharedPreferences sp ;
    String jsonString ;
    List<JSONObject> list ;
    SharedPreferences.Editor editor ;

    public Adapter_ShoppingCart(){

    }

    public Adapter_ShoppingCart(Context context, final List<Object_Pre_Sale> datas, TextView tvShopcartTotal, CheckBox checkboxAll,
                                CheckBox cb_all) {
        //接收
        this.mContext = context;
        this.datas = datas;
        this.tvShopcartTotal = tvShopcartTotal;
        this.checkboxAll = checkboxAll;
        this.cb_all = cb_all;

        sp = mContext.getSharedPreferences("shopping_cart_cache" , Context.MODE_PRIVATE );
//        jsonString = sp.getString("cart_information" , "error");
//        list = JSON.parseObject(jsonString , new TypeReference<List<JSONObject>>(){});
        editor = sp.edit();

        //首次加载数据 默认都不选吧
        showTotalPrice();
        checkboxAll.setChecked(false);
        for (int i = 0; i < datas.size(); i++) {
            datas.get(i).setChildSelected(false);
        }
        showTotalPrice();

        // 设置item的点击事件
//        setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClickListener(View view, int position) {
//                //根据位置找到相应的commodity对象
//                Object_Pre_Sale Commodity = datas.get(position);
//                //设置取反状态
//                Commodity.setChildSelected(!Commodity.isChildSelected());
//                //刷新状态
//                notifyItemChanged(position);
//                //校验是否全选
//                checkAll();
//                //重新计算总价格
//                showTotalPrice();
//            }
//        });

        //设置全选点击事件

        // 完成界面的全选按钮
        checkboxAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = getCheckboxAll().isChecked();
                checkAll_none(checked);
                showTotalPrice();
            }
        });

        // 删除界面的全选按钮
        cb_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //得到状态
                boolean checked = getCb_all().isChecked();
                check_all_none(checked);
                showTotalPrice();
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(mContext, R.layout.item_shopping_cart, null));
    }

    /**
     * 绑定数据
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //new一个viewholder
        ViewHolder viewHolder = (ViewHolder) holder;
        //根据位置得到对应的对象并设置数据
        viewHolder.setData(datas.get(position));
    }

    /**
     * 得到item的数量
     * @return
     */
    @Override
    public int getItemCount() {
        if (datas != null){
            return datas.size();
        }else {
            return 0;
        }
    }

    //是否全选，以下checkAll函数的反面
    public void checkAll_none(boolean checked) {
        int temp = 0;
        if (datas != null && datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
                if ( getStorageNumber(datas.get(i).getItemId()) < datas.get(i).getNum() || getStorageNumber(datas.get(i).getItemId()) == 0){
                    datas.get(i).setChildSelected(false);
                    temp += 1;
                }else {
                    datas.get(i).setChildSelected(checked);
                }
                notifyItemChanged(i);
            }
            if ( temp == datas.size() ){
                checkboxAll.setChecked(false);
                Util_ToastUtils.showToast(mContext, "无可选商品");
            }else {
                checkboxAll.setChecked(checked);
            }
        } else {
            checkboxAll.setChecked(false);
        }
    }

    public void check_all_none(boolean checked){
        if (datas != null && datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
                datas.get(i).setChildSelected(checked);
                checkboxAll.setChecked(checked);
                notifyItemChanged(i);
            }
        } else {
            checkboxAll.setChecked(false);
        }
    }

    /**
     * 删除选中的数据
     * datas是购物车类的列表
     */
    public void deleteData(){
        if (datas.size() > 0) {
            for ( int k = datas.size() -1 ; k >= 0 ; k-- ) {
                Object_Pre_Sale cart = datas.get(k);
                if (cart.isChildSelected()) {
                    // 如果被选中，就从缓存里移掉
                    datas.remove(k);
                    // 删完重新保存
                    editor.clear().commit();
                    editor.putString("cart_information", JSON.toJSONString(datas)).commit();
                    // 刷新数据,因为是从最后一个开始循环移除的，所以没有适配的问题
                    notifyItemRemoved(k);
                }
            }
        }
    }

    // 校验是否全选
    public void checkAll() {
        if (datas != null && datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
                if (!datas.get(i).isChildSelected()) {
                    checkboxAll.setChecked(false);
                    cb_all.setChecked(false);
                    return;
                } else {
                    checkboxAll.setChecked(true);
                    cb_all.setChecked(true);
                }
            }
        }
    }

    /**
     * 显示总金额
     */
    public void showTotalPrice() {
        //设置文本
        tvShopcartTotal.setText(getTotalPrice() + "");
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox cbGov;
        private ImageView ivGov;
        private TextView tvDescGov;
        private TextView tvPriceGov;
        private TextView tvAbleToBuy;
        private TextView count;
        private ImageView count_add;
        private ImageView count_sub;
        private TextView check_size;
        private LinearLayout ll_item_name;

        public ViewHolder(View itemView) {
            //转换
            super(itemView);

            cbGov = (CheckBox) itemView.findViewById(R.id.cb_gov);
            ivGov = (ImageView) itemView.findViewById(R.id.iv_gov);
            tvDescGov = (TextView) itemView.findViewById(R.id.tv_desc_gov);
            tvPriceGov = (TextView) itemView.findViewById(R.id.tv_price_gov);
            count = (TextView)itemView.findViewById(R.id.item_count);
            count_add = itemView.findViewById(R.id.count_add);
            count_sub = itemView.findViewById(R.id.count_sub);
            check_size = itemView.findViewById(R.id.check_size);
            tvAbleToBuy = itemView.findViewById(R.id.tv_able_to_buy);
            ll_item_name = itemView.findViewById(R.id.ll_itemName);


//            //设置item的点击事件
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (onItemClickListener != null) {
//                        //回传
//                        onItemClickListener.onItemClickListener(v, getLayoutPosition());
//                    }
//                }
//            });

            // 设置控件的点击事件
            count_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null){
                        onItemClickListener.onItemClickListener(v, getLayoutPosition());
                    }
                }
            });

            count_sub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null){
                        onItemClickListener.onItemClickListener(v, getLayoutPosition());
                    }
                }
            });

            tvDescGov.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null){
                        onItemClickListener.onItemClickListener(v, getLayoutPosition());
                    }
                }
            });

            ivGov.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null){
                        onItemClickListener.onItemClickListener(v, getLayoutPosition());
                    }
                }
            });

            cbGov.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if (onItemClickListener != null){
                    onItemClickListener.onItemClickListener(v, getLayoutPosition());
                }
                }
            });

            ll_item_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null){
                        onItemClickListener.onItemClickListener(v, getLayoutPosition());
                    }
                }
            });

        }

        /**
         * 这个是一个设置数据的函数
         * @param Commodity
         */
        public void setData(final Object_Pre_Sale Commodity) {

            if(Commodity != null){
                //检查是否被选上
                cbGov.setChecked(Commodity.isChildSelected());

                //设置图片
                if(Commodity.getImgurl() != null){
                    Log.e("load picture", "run");
                    Glide.with(mContext)
                            .load(Commodity.getImgurl()) //获得图片
                            .into(ivGov);
                }

                /**
                 * 以下是设置各种文本的语句
                 */
                int storage = getStorageNumber(Commodity.getItemId());

                tvDescGov.setText(Commodity.getItemName()); // 商品名

                // 价格
                // 处理一下金额，保留两位小数
                double total = Commodity.getPrice();
                BigDecimal b = new BigDecimal(total);
                double total_deal = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                tvPriceGov.setText("￥ " + total_deal);

                // 设置数量
                count.setText(Commodity.getNum() + "");
//                if ( Commodity.getNum() <= storage ){
//                    count.setText(Commodity.getNum() + "");
//                }else {
//                    count.setText(storage + "");
//                    Commodity.setNum(storage);
//                }

                // 设置规格
                String temp_string = "" ;
                for (int i = 0; i < Commodity.getParamList().size(); i++){
                    temp_string += Commodity.getParamList().get(i) + " ";
                }
                check_size.setText(temp_string);
                check_size.setTextColor(Color.parseColor("#c0c0c0"));

                // 设置是否有货
                if ( storage > 0 ){
                    if ( Commodity.getNum() > storage ){
                        tvAbleToBuy.setText("仅剩" + storage + "件");
                        tvAbleToBuy.setTextColor(Color.parseColor("#ff0000"));
                    }else {
                        tvAbleToBuy.setText("现在有货");
                        tvAbleToBuy.setTextColor(Color.parseColor("#BB00D8A0"));
                    }
                }else {
                    tvAbleToBuy.setText("暂时无货");
                    tvAbleToBuy.setTextColor(Color.parseColor("#ff0000"));
                }
            }
        }
    }

    /**
     * 计算总金额
     * @return 一个保留二位小数的总金额
     */
    private double getTotalPrice() {

        double total = 0;
        //条件
        if (datas != null && datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
                Object_Pre_Sale commodity = datas.get(i);
                //只计算选中的 乘上商品数量
                if (commodity.isChildSelected()){
                    total += commodity.getPrice() * commodity.getNum();
                    Log.e("number", commodity.getNum()+ "");
                }
            }
        }

        // 处理一下总金额，保留两位小数
        BigDecimal b = new BigDecimal(total);
        double total_deal = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return total_deal;
    }

   public interface OnItemClickListener {
        void onItemClickListener(View view, int position);
    }

    // 回调点击事件的监听
    private OnItemClickListener onItemClickListener;

    // 定义方法并传给外面的使用者
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public CheckBox getCb_all() {
        return cb_all;
    }

    public void setCb_all(CheckBox cb_all) {
        this.cb_all = cb_all;
    }

    public CheckBox getCheckboxAll() {
        return checkboxAll;
    }

    public void setCheckboxAll(CheckBox checkboxAll) {
        this.checkboxAll = checkboxAll;
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
