package com.example.classchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bumptech.glide.Glide;
import com.example.classchat.Activity.Activity_Market_GoodsDetail;
import com.example.classchat.Object.Object_Commodity;
import com.example.classchat.Object.Object_Main_Brief_Item;
import com.example.classchat.R;
import com.hch.thumbsuplib.ThumbsUpCountView;

import java.util.Collections;
import java.util.List;

public class Adapter_CommodityRecycleView extends RecyclerView.Adapter<Adapter_CommodityRecycleView.ViewHolder> {

    private Context mContext;
    private List<Object_Main_Brief_Item> itemList;

    public Adapter_CommodityRecycleView(Context context, List<Object_Main_Brief_Item> itemList ) {
        this.itemList = itemList;
        mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName, itemPrice;
        private ImageView itemPic;
        private CardView card;
//        ThumbsUpCountView thumbs;

        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.tv_market_item_name);
            itemPrice = itemView.findViewById(R.id.tv_market_item_price);
            itemPic = itemView.findViewById(R.id.iv_market_item_pic);
            card = itemView.findViewById(R.id.card_view);
//            thumbs =  itemView.findViewById(R.id.market_item_thumb);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.staggered_recycler_view_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Object_Main_Brief_Item item = itemList.get(position);
        holder.itemName.setText(item.getName());
        holder.itemPrice.setText(Double.toString(item.getPrice()));
        JSONArray list = JSON.parseArray(item.getImage());
        Glide.with(mContext).load(String.valueOf(list.get(0))).override(720,(480 + (int)(Math.random() * 50))).into(holder.itemPic);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Activity_Market_GoodsDetail.class);
                intent.putExtra("item", JSON.toJSONString(item));
                intent.putExtra("itemId",item.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(itemList !=null)
          return itemList.size();
        else
            return 0;
    }


}

