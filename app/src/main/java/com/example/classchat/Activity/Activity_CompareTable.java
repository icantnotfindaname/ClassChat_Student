
package com.example.classchat.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.classchat.Adapter.Adapter_CompareTable;
import com.example.classchat.R;
import com.example.library_cache.Cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Activity_CompareTable extends AppCompatActivity {

    private ImageView back;
    private android.support.v7.widget.RecyclerView rv;
    private LinearLayout empty;
    private List<String> compareActivity = new ArrayList<>();
    private String compareActivityStr;
    private Adapter_CompareTable adapter_compareTable;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__compare_table);
        back = findViewById(R.id.iv_compare_back);
        empty = findViewById(R.id.ll_when_null);
        rv = findViewById(R.id.compare_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initData();

        //沉浸式状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            //After LOLLIPOP not translucent status bar
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //Then call setStatusBarColor.
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.theme));
        }

    }

    private void initData(){
        compareActivity.clear();
        List<String> Temp = new ArrayList<>();
        compareActivityStr = Cache.with(Activity_CompareTable.this)
                .path(getCacheDir(Activity_CompareTable.this))
                .getCache("compareActivityName", String.class);

        if(compareActivityStr != null)
            Temp = Arrays.asList(compareActivityStr.split("[,\\s+]"));
        for(String item:Temp)
            if(!item.equals(""))
                compareActivity.add(item);
        compareActivity.remove("null");
        compareActivity.remove(" ");
        Log.e("compareTable82",compareActivity.toString());

        if(compareActivity.size() > 0){
            adapter_compareTable = new Adapter_CompareTable(this, compareActivity);
            rv.setAdapter(adapter_compareTable);
        }else {
            empty.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(isFirst){
            isFirst = false;
        }else {
            initData();
            Log.e("compareActivity", compareActivity.toString());
            if(!(compareActivity.size() > 0))
                empty.setVisibility(View.VISIBLE);
            else empty.setVisibility(View.GONE);
        }
    }

    public String getCacheDir(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }
    public void add(View view) {
        startActivity(new Intent(Activity_CompareTable.this, Activity_AddNewComparison.class));

    }
}
