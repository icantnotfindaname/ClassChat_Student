package com.example.classchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.classchat.Activity.Activity_TodoDetail;
import com.example.classchat.Object.Object_TodoList;
import com.example.classchat.R;

import java.util.List;

public class Adapter_Memo extends RecyclerView.Adapter<Adapter_Memo.ViewHolder> {

    private Context mContext;
    private List<Object_TodoList> todoList;

    public Adapter_Memo(Context context, List<Object_TodoList> list){
        todoList = list;
        mContext = context;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;

        public ViewHolder(View view) {
            super(view);
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
        final Object_TodoList item = todoList.get(position);
        holder.title.setText(item.getTodoTitle());

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Activity_TodoDetail.class);
                intent.putExtra("memo", JSON.toJSONString(item));
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        if(todoList !=null)
            return todoList.size();
        else
            return 0;
    }
}
