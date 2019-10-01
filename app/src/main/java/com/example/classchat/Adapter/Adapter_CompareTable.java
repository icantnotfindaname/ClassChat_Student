package com.example.classchat.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.classchat.R;

import java.util.ArrayList;
import java.util.List;

public class Adapter_CompareTable extends RecyclerView.Adapter<Adapter_CompareTable.ViewHolder> {

    private Context mContext;
    private List<String>compareActivity = new ArrayList<>();

    public Adapter_CompareTable(Context context, List<String> list){
        mContext = context;
        compareActivity = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;

        public ViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.compare_title);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.compare_item, viewGroup, false);
        Adapter_CompareTable.ViewHolder holder = new Adapter_CompareTable.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.title.setText(compareActivity.get(i));
    }

    @Override
    public int getItemCount() {
        if(compareActivity !=null)
            return compareActivity.size();
        else
            return 0;
    }


}
