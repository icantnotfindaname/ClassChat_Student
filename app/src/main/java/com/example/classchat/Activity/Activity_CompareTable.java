
package com.example.classchat.Activity;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.classchat.R;

public class Activity_CompareTable extends AppCompatActivity {

    private ImageView back;
    private android.support.v7.widget.RecyclerView rv;
    private LinearLayout empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__compare_table);
        back = findViewById(R.id.iv_compare_back);
        empty = findViewById(R.id.ll_when_null);

        rv = findViewById(R.id.compare_rv);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        if(列表空)
        empty.setVisibility(View.VISIBLE);

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

    public void add(View view) {
        startActivity(new Intent(Activity_CompareTable.this, Activity_AddNewCommparision.class));

    }
}
