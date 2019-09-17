package com.example.classchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.classchat.Activity.Activity_Option;
import com.example.classchat.Object.MySubject;
import com.example.classchat.R;

import java.util.List;

public class Adapter_Memo extends RecyclerView.Adapter<Adapter_Memo.ViewHolder> {

    private Context mContext;
    private List<MySubject> itemList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Button add, delete;
        private TextView title;

        public ViewHolder(View view) {
            super(view);
            add = view.findViewById(R.id.memo_add);
            delete = view.findViewById(R.id.memo_delete);
            title = view.findViewById(R.id.memo_title);
        }
    }



    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.memo_item, viewGroup, false);
        Adapter_Memo.ViewHolder holder = new Adapter_Memo.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final MySubject item = itemList.get(position);
        holder.title.setText(item.getName());
        holder.title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.add.setVisibility(View.GONE);
                holder.delete.setVisibility(View.VISIBLE);
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //删除后复原
                        holder.add.setVisibility(View.VISIBLE);
                        holder.delete.setVisibility(View.GONE);
                        //TODO 发送网络请求删除 参考my
                    }
                });
                holder.title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.add.setVisibility(View.VISIBLE);
                        holder.delete.setVisibility(View.GONE);
                    }
                });
                return true;
            }
        });
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 改切换活动
                mContext.startActivity(new Intent(mContext, Activity_Option.class));
            }
        });

    }


    @Override
    public int getItemCount() {
//        if(itemList !=null)
//            return itemList.size();
//        else
//            return 0;
        return 10;
    }


}
