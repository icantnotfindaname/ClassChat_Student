package com.example.classchat.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.classchat.R;
import com.example.classchat.Util.TimingButton;
import com.example.classchat.Util.Util_NetUtil;
import com.example.classchat.Util.Util_ToastUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Activity_FindPassword extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Activity_FindPassword";
    private EditText phone, smsCode, password;
    private ImageView returnImage;
    private TimingButton SMSBtn;
    private Button findPassword;
    private String cellphone, newPassword;


    private static final int CHANGE_SUCCESS = 11;
    private static final int CHANGE_FAILED = 12;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case CHANGE_SUCCESS:
                    Util_ToastUtils.showToast(Activity_FindPassword.this,"修改密码成功！");
                    finish();
                    break;
                case CHANGE_FAILED:
                    Util_ToastUtils.showToast(Activity_FindPassword.this,"修改失败，请重试");
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__find_password);
        phone = findViewById(R.id.et_phone_num);
        smsCode = findViewById(R.id.et_sms_code);
        password = findViewById(R.id.et_password);
        returnImage = findViewById(R.id.iv_return);
        returnImage.setOnClickListener(this);
        SMSBtn = findViewById(R.id.bn_sms_code);
        SMSBtn.setOnClickListener(this);
        findPassword = findViewById(R.id.btn_find_password);
        findPassword.setOnClickListener(this);
        SMSSDK.registerEventHandler(eventHandler);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_find_password:
                register();
            case R.id.iv_return:
                finish();
                break;
            case R.id.bn_sms_code:
                final String cellphone = phone.getText().toString().trim();

                if (TextUtils.isEmpty(cellphone)){
                    Toast.makeText(this, "手机号不能为空！", Toast.LENGTH_SHORT).show();
                    phone.requestFocus();
                }else {
                    SMSBtn.start();
                    SMSSDK.getVerificationCode("86", phone.getText().toString());
                }
                break;
        }
    }


    public void register() {
        cellphone = phone.getText().toString().trim();
        final String code = smsCode.getText().toString().trim();
        newPassword = this.password.getText().toString().trim();
        if (TextUtils.isEmpty(cellphone)) {  //当手机号没有输入时
            Util_ToastUtils.showToast(this, "手机号不能为空！");
            phone.requestFocus();//使输入框失去焦点
            return;
        } else if (TextUtils.isEmpty(code)) {//当验证码没有输入时
            Util_ToastUtils.showToast(this, "验证码不能为空！");
            smsCode.requestFocus();//使输入框失去焦点
            return;
        } else if (TextUtils.isEmpty(newPassword)) {//当注册密码没有输入时
            Util_ToastUtils.showToast(this, "密码不能为空！");
            this.password.requestFocus();//使输入框失去焦点
            return;
        }
        if (!TextUtils.isEmpty(cellphone) && !TextUtils.isEmpty(code)) {

            SMSSDK.submitVerificationCode("86", phone.getText().toString(), smsCode.getText().toString());

        }
    }


    EventHandler eventHandler = new EventHandler() {
        public void afterEvent(int event, int result, Object data) {
            // afterEvent会在子线程被调用，因此如果后续有UI相关操作，需要将数据发送到UI线程
            Message msg = new Message();
            msg.arg1 = event;
            msg.arg2 = result;
            msg.obj = data;
            new Handler(Looper.getMainLooper(), new Handler.Callback() {
                @Override
                public boolean handleMessage(final Message msg) {
                    int event = msg.arg1;
                    int result = msg.arg2;
                    Object data = msg.obj;
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            // 处理成功得到验证码的结果
                            Util_ToastUtils.showToast(Activity_FindPassword.this, "请查收短信");
                            // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达
                        } else {
                            // 处理错误的结果
                            Log.d(TAG, "handleMessage: " + result);
                            smsCode.setText(null);
                            Util_ToastUtils.showToast(Activity_FindPassword.this, "验证码服务出错，请稍后再试试？");
                            ((Throwable) data).printStackTrace();
                        }
                    } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            // 处理验证码验证通过的结果
/*
                    等待界面，因为登录操作是耗时操作
                    */

                            // 发起网络请求修改密码
                            //TODO 请求体仅返回用户手机号、新密码就能修改密码
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("userId", cellphone)
                                    .add("newpsw", newPassword)
                                    .build();
                            Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/changepsw/student/findpassword", requestBody, new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    Util_ToastUtils.showToast(getApplicationContext(), "修改失败");
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                                    // 得到服务器返回的具体内容
                                    boolean responseData = Boolean.parseBoolean(response.body().string());

                                    Message message = new Message();    // 准备发送信息通知UI线程

                                    if(responseData) {
                                        message.what = CHANGE_SUCCESS;
                                        handler.sendMessage(message);   // 登录成功
                                    } else {
                                        message.what = CHANGE_FAILED;
                                        handler.sendMessage(message);
                                    }
                                }
                            });

                        } else {
                            Util_ToastUtils.showToast(Activity_FindPassword.this, "验证码错误");
                            ((Throwable) data).printStackTrace();
                        }
                    }
                    return false;
                }
            }).sendMessage(msg);
        }
    };


    // 使用完EventHandler需注销，否则可能出现内存泄漏
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }


    @Override
    public Resources getResources() {//禁止app字体大小跟随系统字体大小调节
        Resources resources = super.getResources();
        if (resources != null && resources.getConfiguration().fontScale != 1.0f) {
            android.content.res.Configuration configuration = resources.getConfiguration();
            configuration.fontScale = 1.0f;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        return resources;
    }

}
