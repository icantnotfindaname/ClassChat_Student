package com.example.classchat.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.classchat.R;
import com.example.classchat.Util.TimingButton;
import com.example.classchat.Util.Util_NetUtil;
import com.example.classchat.Util.Util_ToastUtils;
import java.io.IOException;
import java.util.Calendar;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.classchat.Util.Util_getSerialNumber.getSerialNumber;

public class Activity_Register extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Activity_Register";

    private EditText editTextP, editSMS, editTextCT;
    private Button button;
    private TimingButton SMSBtn;
    private TextView enterText;
    private ImageView returnImage;
    //在主线程里接收到信息并报错
    public static final int REGISTER_SUCCESS = 0;
    public static final int REGISTER_FAILED = 1;
    public static final int INIT_TODO_SUCCESS = 2;

    //重写handler，接收网络线程中回来的信息
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REGISTER_SUCCESS:
                    Toast.makeText(Activity_Register.this, "注册成功", Toast.LENGTH_SHORT).show();
                    initTodoItem();
                    break;
                case REGISTER_FAILED:
                    //用户名存在
                    Toast.makeText(Activity_Register.this, "此账户已存在", Toast.LENGTH_SHORT).show();
                    editTextP.setText(null);
                    editSMS.setText(null);
                    editTextCT.setText(null);
                    break;
                case INIT_TODO_SUCCESS:
                    Intent intent = new Intent(Activity_Register.this, Activity_Enter.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

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
                            Util_ToastUtils.showToast(Activity_Register.this, "请查收短信");
                            // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达
                        } else {
                            // 处理错误的结果
                            Log.d(TAG, "handleMessage: " + result);
                            editSMS.setText(null);
                            Util_ToastUtils.showToast(Activity_Register.this, "验证码服务出错，请稍后再试试？");
                            ((Throwable) data).printStackTrace();
                        }
                    } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            // 处理验证码验证通过的结果

                            /*
                            建立网络线程询问能否注册
                             */
                            //TODO 注册时增加一个用户设备号信息 获取设备号方法：getSerialNumber()，后端确保设备号不重复
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("username", editTextP.getText().toString())
                                    .add("password", editTextCT.getText().toString())
                                    .add("serialnumber", getSerialNumber())
                                    .build();   //构建请求体

                            Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/register/student", requestBody, new Callback() {
                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    // 得到服务器返回的具体内容
                                    assert response.body() != null;
                                    boolean responseData = Boolean.parseBoolean(response.body().string());

                                    Message message = new Message();    // 准备发送信息通知UI线程

                                    if (responseData) {
                                        message.what = REGISTER_SUCCESS;
                                        handler.sendMessage(message);   // 注册成功
                                    } else {
                                        message.what = REGISTER_FAILED;
                                        handler.sendMessage(message);   // 注册失败
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    // 在这里对异常情况进行处理
                                }
                            });
                        } else {
                            // TODO 处理错误的结果
                            Util_ToastUtils.showToast(Activity_Register.this, "验证码错误");
                            ((Throwable) data).printStackTrace();
                        }
                    }
                    // TODO 其他接口的返回结果也类似，根据event判断当前数据属于哪个接口
                    return false;
                }
            }).sendMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util_ToastUtils.showToast(this,"  注册后签到将绑定此设备,更换（一年至多2次）请联系客服");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__register);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            //After LOLLIPOP not translucent status bar
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //Then call setStatusBarColor.
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.theme));
        }
        init();
        SMSSDK.registerEventHandler(eventHandler);
    }

    /*
    控件初始化
     */

    private void init() {
        editTextP = findViewById(R.id.et_phone_num);
        editSMS = findViewById(R.id.et_sms_code);
        editTextCT = findViewById(R.id.et_password);
        button = findViewById(R.id.bn_immediateRegistration);
        button.setOnClickListener(this);
        enterText = findViewById(R.id.tv_enter);
        enterText.setOnClickListener(this);
        returnImage = findViewById(R.id.iv_return);
        returnImage.setOnClickListener(this);
        SMSBtn = findViewById(R.id.bn_sms_code);
        SMSBtn.setOnClickListener(this);
    }

    /*
    按钮点击响应事件
     */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bn_immediateRegistration:
                register();
                break;
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
            Util_ToastUtils.showToast(this, "手机号不能为空！");
            editTextP.requestFocus();//使输入框失去焦点
            return;
        } else if (TextUtils.isEmpty(password)) {//当验证码没有输入时
            Util_ToastUtils.showToast(this, "验证码不能为空！");
            editSMS.requestFocus();//使输入框失去焦点
            return;
        } else if (TextUtils.isEmpty(confirm_password)) {//当注册密码没有输入时
            Util_ToastUtils.showToast(this, "密码不能为空！");
            editTextCT.requestFocus();//使输入框失去焦点
            return;
        }
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {

            SMSSDK.submitVerificationCode("86", editTextP.getText().toString(), editSMS.getText().toString());

        }
    }

    private void initTodoItem() {
        RequestBody requestBody = new FormBody.Builder()
                .add("userID", editTextP.getText().toString().trim())
                .add("todoTitle", "再忙也要吃早餐撒")
                .add("weekList", "1a2a3a4a5a6a7a8a9a10a11a12a13a14a15a16a17a18a19a20a21a22a23a24a25")
                .add("dayChosen", (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1) + "")
                .add("timeSlot", "0")
                .add("detailTime", 8 + " " + 15)
                .add("isClock", "false")
                .add("content", "")
                .add("todoItemID", "init")
                .build();   //构建请求体
        //TODO
        Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/addnewitem", requestBody, new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 得到服务器返回的具体内容
                boolean responseData = Boolean.parseBoolean(response.body().string());
                Message message = new Message();
                if (responseData) {
                    message.what = INIT_TODO_SUCCESS;
                    handler.sendMessage(message);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // 在这里对异常情况进行处理
            }
        });

    }


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
