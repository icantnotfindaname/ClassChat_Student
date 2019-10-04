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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.classchat.Activity.Activity_TodoDetail;
import com.example.classchat.Object.Object_TodoList;
import com.example.classchat.R;

import java.util.List;

public class Adapter_See_All_Memo extends RecyclerView.Adapter<Adapter_See_All_Memo.ViewHolder> {

    private Context mContext;
    private List<Object_TodoList> todoList;

    public Adapter_See_All_Memo(Context context, List<Object_TodoList> list){
        todoList = list;
        mContext = context;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, itemWeek, itemDay;
        private LinearLayout layout;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.memo_title);
            itemWeek = view.findViewById(R.id.memo_item_week);
            itemDay = view.findViewById(R.id.memo_item_day);
            layout = view.findViewById(R.id.memo_item_rl);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.memo_see_all_item, viewGroup, false);
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int heightPixels = metrics.heightPixels;
        int widthPixels = metrics.widthPixels;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = heightPixels / 7;
        layoutParams.width = (int) (widthPixels * 0.92);
        Adapter_See_All_Memo.ViewHolder holder = new Adapter_See_All_Memo.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Object_TodoList item = todoList.get(position);
        holder.title.setText(item.getTodoTitle());
        holder.itemDay.setVisibility(View.VISIBLE);
        holder.itemWeek.setVisibility(View.VISIBLE);
        holder.itemWeek.setText(setWeekTextView(item.getWeekChosen()));
        int i = item.getDayChosen();String dayStr;
        switch (i){
            case 1:
                dayStr = "Mon";
                break;
            case 2:
                dayStr = "Tue";
                break;
            case 3:
                dayStr = "Wed";
                break;
            case 4:
                dayStr = "Thu";
                break;
            case 5:
                dayStr = "Fri";
                break;
            case 6:
                dayStr = "Sat";
                break;
            case 7:
                dayStr = "Sun";
                break;
            default:
                dayStr = "";
                break;
        }
        holder.itemDay.setText(dayStr);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Activity_TodoDetail.class);
                intent.putExtra("memo", JSON.toJSONString(item));
                intent.putExtra("isSeeAll", "true");
                mContext.startActivity(intent);
            }
        });
    }

    private String setWeekTextView( List<Integer> l){
        boolean[] weeksChecked = new boolean[25];
        for(int i = 0; i < 25; ++ i){
            weeksChecked[i] = false;
        }
        for(int i = 0; i< l.size(); ++ i){
            weeksChecked[l.get(i) - 1] = true;
        }

        String s = new String();
        List<Integer> weeksnum  = l;
        int end = 0;
        for(int i = 0;i < weeksChecked.length;i ++){
            if(weeksChecked[i])
                weeksnum.add(i + 1);
        }

        for(int i = 0; i < weeksChecked.length; i ++)
        {
            if (weeksChecked[i])
            {

                int start = i + 1;
                for(int j = i + 1;j <= weeksChecked.length; ++ j) {
                    if (j == weeksChecked.length && weeksChecked[j - 1]) {
                        end = weeksChecked.length;
                        i = weeksChecked.length - 1;
                        break;
                    } else if (!weeksChecked[j]) {
                        end = j;
                        i = j;
                        break;
                    }
                }
                if(start==end)
                    s += "第" + start + "周 ";
                else
                    s += "第" + start + "~" + end + "周 ";

            }
        }
        return s;
    }

    @Override
    public int getItemCount() {
        if(todoList != null)
            return todoList.size();
        else
            return 0;
    }
}
