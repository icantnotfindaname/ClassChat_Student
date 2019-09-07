package com.example.classchat.Activity;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.classchat.R;
import com.example.classchat.Util.TimingButton;
import com.example.classchat.Util.Util_NetUtil;
import com.sdsmdg.tastytoast.TastyToast;

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
    private EditText editTextP, editSMS, editTextCT;
    private ImageView returnImage;
    private TimingButton SMSBtn;
    private Button findPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__find_password);
        editTextP = findViewById(R.id.et_phone_num);
        editSMS = findViewById(R.id.et_sms_code);
        editTextCT = findViewById(R.id.et_password);
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
            case R.id.tv_enter:
            case R.id.iv_return:
                finish();
                break;
            case R.id.bn_sms_code:
                final String username = editTextP.getText().toString().trim();

                if (TextUtils.isEmpty(username)){
                    Toast.makeText(this, "手机号不能为空！", Toast.LENGTH_SHORT).show();
                    editTextP.requestFocus();
                }else {
                    SMSBtn.start();
                    SMSSDK.getVerificationCode("86", editTextP.getText().toString());
                }
                break;
        }
    }

    public void register() {
        final String username = editTextP.getText().toString().trim();
        final String password = editSMS.getText().toString().trim();
        String confirm_password = editTextCT.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {  //当手机号没有输入时
            TastyToast.makeText(this, "手机号不能为空！", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
            editTextP.requestFocus();//使输入框失去焦点
            return;
        } else if (TextUtils.isEmpty(password)) {//当验证码没有输入时
            TastyToast.makeText(this, "验证码不能为空！", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
            editSMS.requestFocus();//使输入框失去焦点
            return;
        } else if (TextUtils.isEmpty(confirm_password)) {//当注册密码没有输入时
            TastyToast.makeText(this, "密码不能为空！", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
            editTextCT.requestFocus();//使输入框失去焦点
            return;
        }
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {

            SMSSDK.submitVerificationCode("86", editTextP.getText().toString(), editSMS.getText().toString());

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
                            TastyToast.makeText(Activity_FindPassword.this, "请查收短信", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                            // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达
                        } else {
                            // 处理错误的结果
                            Log.d(TAG, "handleMessage: " + result);
                            editSMS.setText(null);
                            TastyToast.makeText(Activity_FindPassword.this, "验证码服务出错，请稍后再试试？", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                            ((Throwable) data).printStackTrace();
                        }
                    } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            // 处理验证码验证通过的结果


                        } else {
                            // TODO 处理错误的结果
                            TastyToast.makeText(Activity_FindPassword.this, "验证码错误", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                            ((Throwable) data).printStackTrace();
                        }
                    }
                    // TODO 其他接口的返回结果也类似，根据event判断当前数据属于哪个接口
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
