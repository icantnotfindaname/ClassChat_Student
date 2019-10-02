package com.example.classchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.classchat.Activity.Activity_ComparisonDetail;
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
        private LinearLayout ll;

        public ViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.compare_title);
            ll = view.findViewById(R.id.memo_item_ll);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.compare_item, viewGroup, false);
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int heightPixels = metrics.heightPixels;
        int widthPixels = metrics.widthPixels;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = heightPixels / 10;
        layoutParams.width = widthPixels;
        Adapter_CompareTable.ViewHolder holder = new Adapter_CompareTable.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int i) {
        holder.title.setText(compareActivity.get(i));
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Activity_ComparisonDetail.class);
                intent.putExtra("activity", compareActivity.get(i));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(compareActivity !=null)
            return compareActivity.size();
        else
            return 0;
    }


}
