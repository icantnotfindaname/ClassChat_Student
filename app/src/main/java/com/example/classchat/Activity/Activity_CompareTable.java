
package com.example.classchat.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.Collections;
import java.util.List;

public class Activity_CompareTable extends AppCompatActivity {

    private ImageView back;
    private android.support.v7.widget.RecyclerView rv;
    private LinearLayout empty;
    private List<String> compareActivity = new ArrayList<>();
    private Adapter_CompareTable adapter_compareTable;

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

        compareActivity = Collections.singletonList(Cache.with(Activity_CompareTable.this)
                                        .path(getCacheDir(Activity_CompareTable.this))
                                        .getCache("compareActivityName", String.class));
        Log.e("compare",compareActivity.toString());
        String s = compareActivity.get(0)+"d";//缓存没有时compareActivity=[null],直接compareActivity.get(0).equals("null")报错，原因未知

        if(compareActivity.size() == 1 && s.equals("nulld")){
            empty.setVisibility(View.VISIBLE);
        }else {
            adapter_compareTable = new Adapter_CompareTable(this, compareActivity);
            rv.setAdapter(adapter_compareTable);
        }

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

    @Override
    protected void onResume() {
        super.onResume();
        adapter_compareTable.notifyDataSetChanged();
    }

    public String getCacheDir(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }
    public void add(View view) {
        startActivity(new Intent(Activity_CompareTable.this, Activity_AddNewComparision.class));

    }
}
