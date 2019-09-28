package com.example.classchat.Activity;

import android.os.Build;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.classchat.R;

/**
 * 这是帮助与反馈界面
 */
public class Activity_HelpAndFeedback extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__help_and_feedback);

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

    public void back(View view) {
        finish();
    }
}
