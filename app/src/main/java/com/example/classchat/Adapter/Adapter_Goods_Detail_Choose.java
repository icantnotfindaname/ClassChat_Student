package com.example.classchat.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.classchat.R;

import java.util.List;

public class Adapter_Goods_Detail_Choose extends RecyclerView.Adapter<Adapter_Goods_Detail_Choose.ViewHolder> {

    private Context mContext;
    private List<String> paramList;

    public Adapter_Goods_Detail_Choose(Context context, List<String> paramList) {
        mContext = context;
        this.paramList = paramList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView param;

        public ViewHolder(View view) {
            super(view);
            param = view.findViewById(R.id.param);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_goods_choose_param, viewGroup, false);
        Adapter_Goods_Detail_Choose.ViewHolder holder = new Adapter_Goods_Detail_Choose.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.param.setText(paramList.get(position));
    }

    @Override
    public int getItemCount() {
        if (paramList != null)
            return paramList.size();
        else
            return 0;
    }
}