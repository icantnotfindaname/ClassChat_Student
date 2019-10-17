package com.example.classchat.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.classchat.R;
import com.example.classchat.Util.Util_NetUtil;
import com.example.classchat.Util.Util_ToastUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Adapter_ComparisonDetail extends RecyclerView.Adapter<Adapter_ComparisonDetail.ViewHolder> {

    private Context mContext;
    private List<String>nameList;
    private List<Integer>num;
    private String comparisonID;
    private int newNumber;

    private List<String> data;
    private final static int DELETE_FAIL = 0;
    private final static int DELETE_SUCESS = 1;
    private final static int EDIT_FAIL = 2;
    private final static int EDIT_SUCESS = 3;
    private String otherUserID;

    public Adapter_ComparisonDetail(Context context, List<String>nameList, List<Integer>num, String comparisonID){
        mContext = context;
        this.nameList = nameList;
        this.num = num;
        this.comparisonID = comparisonID;
    }

    @SuppressLint("HandlerLeak")
    android.os.Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case DELETE_FAIL:
                    Util_ToastUtils.showToast(mContext, "删除失败，请重试！");
                    break;
                case DELETE_SUCESS:
                    num.remove(nameList.indexOf(otherUserID));
                    nameList.remove(otherUserID);
                    notifyDataSetChanged();
                    Util_ToastUtils.showToast(mContext, "删除成功！");
                    break;
                case EDIT_FAIL:
                    Util_ToastUtils.showToast(mContext, "修改失败，请重试！");
                    break;
                case EDIT_SUCESS:
                    num.remove(nameList.indexOf(otherUserID));
                    num.add(nameList.indexOf(otherUserID), newNumber);
                    notifyDataSetChanged();
                    Util_ToastUtils.showToast(mContext, "修改成功！");
                    break;
                default:
                    break;
            }
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private EditText number;
        private Button edit, delete;
        private RelativeLayout rl;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.comparison_detail_name);
            number = itemView.findViewById(R.id.comparison_detail_num);
            edit = itemView.findViewById(R.id.comparison_detail_deit);
            delete = itemView.findViewById(R.id.comparison_detail_delete);
            rl = itemView.findViewById(R.id.rl_comparison_detail);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dialog_comparison_item, viewGroup, false);
        Adapter_ComparisonDetail.ViewHolder holder = new Adapter_ComparisonDetail.ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.e("nameList Adapter", nameList.get(position));
        Log.e("num Adapter", num.get(position)+"");
        holder.name.setText(nameList.get(position).substring(11));
        holder.number.setEnabled(false);
        holder.number.setText(String.valueOf(num.get(position)));
        holder.rl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.edit.setVisibility(View.VISIBLE);
                holder.delete.setVisibility(View.VISIBLE);
                holder.number.setEnabled(true);
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        otherUserID = nameList.get(position);
                        deleteMember(comparisonID, otherUserID.substring(0, 11));
                        holder.edit.setVisibility(View.GONE);
                        holder.delete.setVisibility(View.GONE);
                        holder.number.setEnabled(false);
                    }
                });
                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        otherUserID = nameList.get(position);
                        newNumber = Integer.parseInt(holder.number.getText().toString());
                        editNUmber(comparisonID, otherUserID.substring(0, 11), holder.number.getText().toString());
                        holder.edit.setVisibility(View.GONE);
                        holder.delete.setVisibility(View.GONE);
                        holder.number.setEnabled(false);
                    }
                });
                return false;
            }
        });

    }

    private void editNUmber(String comparisonID, String otherUserID, String newNumber) {
        RequestBody requestBody = new FormBody.Builder()
                .add("comparisonID", comparisonID)
                .add("otherUserID", otherUserID)
                .add("newNumber", newNumber)
                .build();

        Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/changesamemembernumber", requestBody,new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                // 得到服务器返回的具体内容
                String responseData = response.body().string();
                Message message = new Message();
                if(responseData.equals("ERROR")){
                    message.what = EDIT_FAIL;
                    handler.sendMessage(message);
                }
                else {
                    data = Arrays.asList((String)JSON.parseObject(responseData).get("comparisonData"));
                    Log.e("data", data.toString());
                    message.what = EDIT_SUCESS;
                    handler.sendMessage(message);
                }

            }
        });
    }

    private void deleteMember(String comparisonID, final String otherUserID) {
        RequestBody requestBody = new FormBody.Builder()
                .add("comparisonID", comparisonID)
                .add("otherUserID", otherUserID)
                .build();

        Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/deletemember", requestBody,new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                // 得到服务器返回的具体内容
                String responseData = response.body().string();
                Message message = new Message();
                Log.e("responseData", responseData);
                if(responseData.equals("ERROR")){
                    message.what = DELETE_FAIL;
                    handler.sendMessage(message);
                }
                else {
                    data = Arrays.asList((String)JSON.parseObject(responseData).get("comparisonData"));
                    Log.e("data", data.toString());
                    message.what = DELETE_SUCESS;
                    handler.sendMessage(message);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        if(nameList !=null)
            return nameList.size();
        else
            return 0;
    }
}
