package com.example.classchat.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.classchat.R;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class Activity_Option extends AppCompatActivity {
    private ImageView back;
    private Switch aSwitch;
    private TextView signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);



        // 设置沉浸式状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            //After LOLLIPOP not translucent status bar
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //Then call setStatusBarColor.
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.theme));
        }
        back = findViewById(R.id.iv_option_back);
        //返回按钮
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        signOut = findViewById(R.id.sign_out);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog_signOut();
            }
        });
    }
    private void showDialog_signOut(){
        // 这里的属性可以一直设置，因为每次设置后返回的是一个builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Option.this);
        // 设置提示框的标题
        builder.setTitle("退出登录").
                // 设置提示框的图标
                        setIcon(R.drawable.icon_logo).
                // 设置要显示的信息
                        setMessage("确定要退出登录吗？").
                // 设置确定按钮
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 这里不应该只是一个简单的页面跳转
                        Intent outIntent = new Intent(Activity_Option.this,
                                Activity_Enter.class);
                        outIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(outIntent);
                    }
                }).

                // 设置取消按钮,null是什么都不做，并关闭对话框
                        setNegativeButton("取消", null);
        // 生产对话框
        AlertDialog alertDialog = builder.create();
        // 显示对话框
        alertDialog.show();


    }
}
