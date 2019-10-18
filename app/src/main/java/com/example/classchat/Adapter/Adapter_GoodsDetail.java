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
    private LayoutInflater mLayoutInflater;

    public Adapter_GoodsDetail(Context context, List<String>imageList){
        mContext = context;
        this.imageList = imageList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView pic;

        public ViewHolder(View view) {
            super(view);
            pic = view.findViewById(R.id.memo_title);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class ViewHolder{
        public ImageView pic;
    }

}
