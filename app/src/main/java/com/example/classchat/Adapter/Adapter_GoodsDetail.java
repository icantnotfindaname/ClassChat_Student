package com.example.classchat.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.classchat.R;

import java.util.List;

public class Adapter_GoodsDetail extends RecyclerView.Adapter<Adapter_GoodsDetail.ViewHolder> {

    private Context mContext;
    private List<String>imageList;

    public Adapter_GoodsDetail(Context context, List<String>imageList){
        mContext = context;
        this.imageList = imageList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView pic;

        public ViewHolder(View view) {
            super(view);
            pic = view.findViewById(R.id.iv_adapter_good_detail_img);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_good_detail_imgs, viewGroup, false);
        Adapter_GoodsDetail.ViewHolder holder = new Adapter_GoodsDetail.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(mContext).load(imageList.get(position)).into(holder.pic);
    }

    @Override
    public int getItemCount() {
        if(imageList !=null)
            return imageList.size();
        else
            return 0;
    }

}
