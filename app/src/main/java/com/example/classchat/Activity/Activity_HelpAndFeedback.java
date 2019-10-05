package com.example.classchat.Activity;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import com.example.classchat.Util.Util_ScreenShot;
import com.example.classchat.Util.Util_ToastUtils;

import java.io.IOException;

import io.rong.imageloader.utils.L;

/**
 * 这是帮助与反馈界面
 */
public class Activity_HelpAndFeedback extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__help_and_feedback);

        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("ServerWeChatNumber", "wxid_f4wz6q30s6ou22");
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        Toast.makeText(this, "客胡微信号已户制到剪贴板👌", Toast.LENGTH_LONG).show();

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

    private void getWechatApi(){
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Util_ToastUtils.showToast(Activity_HelpAndFeedback.this, "检查到您手机没有安装微信，请安装后使用该功能");
        }
    }

    public void back(View view) {
        finish();
    }

    public void jumpToWechat(View view) throws IOException {
        Util_ScreenShot.shoot(Activity_HelpAndFeedback.this);
        getWechatApi();
        Toast.makeText(Activity_HelpAndFeedback.this, "已截图保存，扫一扫添加客胡叭\uD83D\uDE48", Toast.LENGTH_LONG).show();
    }
}
