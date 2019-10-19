package com.example.classchat.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.classchat.Activity.Activity_AboutUs;
import com.example.classchat.Activity.Activity_AccountInfo;
import com.example.classchat.Activity.Activity_Enter;
import com.example.classchat.Activity.Activity_HelpAndFeedback;
import com.example.classchat.Activity.Activity_IdAuthentation;
import com.example.classchat.Activity.Activity_MyCourse;
import com.example.classchat.Activity.Activity_Option;
import com.example.classchat.Activity.MainActivity;
import com.example.classchat.Object.Object_TodoList;
import com.example.classchat.Object.Object_Todo_Broadcast_container;
import com.example.classchat.R;
import com.example.classchat.Util.AlarmTimer;
import com.example.classchat.Util.Util_NetUtil;
import com.example.classchat.Util.Util_ToastUtils;
import com.example.library_cache.Cache;
import com.maning.updatelibrary.InstallUtils;
import com.yzq.zxinglibrary.encode.CodeCreator;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;



public class Fragment_SelfInformationCenter extends Fragment {

    private static final String APK_SAVE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/laoke_update.apk";
    //控件
    private ImageView avatarImageView;
    private ImageView qrcode;
    private String userId;

    private LinearLayout headclick;

    private LinearLayout linearLayoutforAnquan;
    private LinearLayout linearLayoutforKecheng;
    private LinearLayout linearLayoutforShoucang;
    private LinearLayout linearLayoutforShezhi;
    private LinearLayout linearLayoutforRenzheng;
    private LinearLayout linearLayoutforBangzhu;
    private LinearLayout linearLayoutforGuanyu;
    private LinearLayout linearLayoutforUpdate;
    private LinearLayout linearLayoutQuit;
    private LinearLayout linearLayout_user_manual;
    private TextView textViewforName;
    private TextView textViewforId;

    private InstallUtils.DownloadCallBack downloadCallBack;
    private String apkDownloadPath;

    private ProgressBar progressBar;
    private String correctId;
    private String name;
    private final String theType = "student";
    private String imageUrl;
    private Boolean isAuthentation;
    private String proUni;
    //  private Context context;
    private String downloadUrl;
    private final static int UPDATE = 100;

    private ImageButton guideButton; // 指引按键

    // 搞一个自己的变量
    Fragment_SelfInformationCenter myContext = this;

    //handler处理反应回来的信息
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE:
                    textViewforId.setText(correctId);
                    textViewforName.setText(name);
                    Glide.with(Fragment_SelfInformationCenter.this).load(imageUrl).into(avatarImageView);
                    break;
            }
        }
    };

    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        avatarImageView = view.findViewById(R.id.user_head);

        linearLayoutforAnquan = view.findViewById(R.id.anquan);
        linearLayoutforBangzhu = view.findViewById(R.id.bangzhuyufankui);
        linearLayoutforGuanyu = view.findViewById(R.id.aboutus);
        linearLayoutforKecheng = view.findViewById(R.id.kecheng);
        linearLayoutforRenzheng = view.findViewById(R.id.shimingrenzheng);
        linearLayoutforShezhi = view.findViewById(R.id.shezhi);
        linearLayoutforShoucang = view.findViewById(R.id.shoucang);
        linearLayoutforUpdate  = view.findViewById(R.id.check_for_update);

        linearLayoutQuit = view.findViewById(R.id.quit);

        headclick = view.findViewById(R.id.head_click);

        textViewforId = view.findViewById(R.id.user_stuID);
        textViewforName = view.findViewById(R.id.user_name);

        guideButton = view.findViewById(R.id.btn_guide_me);

        linearLayout_user_manual = view.findViewById(R.id.user_manual);

        //获得用户ID
        MainActivity activity = (MainActivity) getActivity();
        correctId = activity.getId();
        name = activity.getNickName();
        imageUrl = activity.getImageUrl();
        isAuthentation = activity.getAuthentation();
        proUni = activity.getProUni();

        //加载数据和图片
        textViewforId.setText(correctId);
        textViewforName.setText(name);
        Glide.with(this).load(imageUrl).into(avatarImageView);

        // 注册广播监听器
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        // 注册更新界面所需的广播接收器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.theclasschat_UPDATE_ACCOUNTINFO");
        UpdateAccountInfoReceiver updateAccountInfoReceiver = new UpdateAccountInfoReceiver();
        localBroadcastManager.registerReceiver(updateAccountInfoReceiver, intentFilter);


        //加入点击事件
        linearLayoutforAnquan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Activity_AccountInfo.class);
                intent.putExtra("userId", correctId);
                intent.putExtra("userName", name);
                intent.putExtra("headUrl", imageUrl);
                startActivity(intent);
            }
        });
        linearLayoutforBangzhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Activity_HelpAndFeedback.class);
                startActivity(intent);
            }
        });
        linearLayoutforGuanyu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Activity_AboutUs.class);
                startActivity(intent);
            }
        });
        linearLayoutforKecheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Activity_MyCourse.class);
                intent.putExtra("userId", correctId);
                intent.putExtra("proUni", proUni);
                startActivity(intent);
            }
        });
        linearLayoutforRenzheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Activity_IdAuthentation.class);
                intent.putExtra("userId", correctId);
                if (!isAuthentation)
                    startActivity(intent);
                else
                    Util_ToastUtils.showToast(getActivity(), "您已经实名认证过了");
            }
        });
        linearLayoutforShoucang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile();
            }
        });

        linearLayoutforShezhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Activity_Option.class);
                startActivity(intent);
            }
        });

        linearLayoutforUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG", "点击成功");
                getUrl();
                try {
                    checkVersion(v);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        linearLayout_user_manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_guide_dialog();
            }
        });

        linearLayoutQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 这里的属性可以一直设置，因为每次设置后返回的是一个builder对象
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                                Cache.with(myContext.getActivity())
                                        .path(getCacheDir(myContext.getActivity()))
                                        .remove("classBox");
                                // 这里不应该只是一个简单的页面跳转
                                Intent outIntent = new Intent(getActivity(),
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
        });
        headclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity)getActivity();
                userId = mainActivity.getId();
                Resources r = getContext().getResources();
                Bitmap bmp = BitmapFactory.decodeResource(r, R.drawable.icon_logo);
                Bitmap bitmap = CodeCreator.createQRCode(userId, 700, 700, bmp);
                LayoutInflater inflater=LayoutInflater.from(getContext());
                View xxview=inflater.inflate(R.layout.dialog_id_qrcode,null);
                final android.support.v7.app.AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(getContext());
                qrcode = xxview.findViewById(R.id.im_qrcode);
                qrcode.setImageBitmap(bitmap);
                builder.setView(xxview);
                builder.create().show();
            }
        });

        /**
         * 指引的点击事件
         */
        guideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_guide_dialog();
            }
        });


    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fragment__self_information_center, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    Dialog dialog1;
    private void show_guide_dialog() {
        // 自定义对话框
        LayoutInflater inflater= LayoutInflater.from(getContext());
        View myView = inflater.inflate(R.layout.dialog_guide_me,null);//引用自定义布局
//        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
//        builder.setView(myView);
//        dialog = builder.create();  //创建对话框
        dialog1 = new Dialog(getActivity(),R.style.dialog);
        dialog1.setContentView(myView);
        dialog1.show();  //显示对话框

        Window dialogWindow = dialog1.getWindow();
        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高度
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.7); // 高度设置为屏幕的0.6，根据实际情况调整
        p.width = (int) (d.getWidth() * 0.7); // 宽度设置为屏幕的0.65，根据实际情况调整
        dialogWindow.setAttributes(p);
    }

    /*
    接收到更新广播之后，界面如何处理
     */
    class UpdateAccountInfoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            getUserInfo();
        }
    }

    // 取得用户信息方法
    private


    void getUserInfo() {
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", correctId)
                .build();

        Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/getuserinfo/student", requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                JSONObject jsonObject = JSON.parseObject(response.body().string());
                name = jsonObject.getString("nickname");
                imageUrl = jsonObject.getString("ico");
                isAuthentation = Boolean.parseBoolean(jsonObject.getString("authentationstatus"));
                proUni = jsonObject.getString("university") + "_" + jsonObject.getString("school");
                // 向handler发送信息更新你的课程
                Message message = new Message();
                message.what = UPDATE;
                handler.sendMessage(message);

            }
        });
    }

    private void openFile() {
        String path = Environment.getExternalStorageDirectory() + "/RongCloud/Media";
        File file= new File(path);
        Uri fileURI = FileProvider.getUriForFile(getActivity(), "com.example.classchat.FileProvider", file);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setDataAndType(fileURI, "file/*");
        Uri originalUri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3ARongCloud%2FMedia");
        DocumentFile docFile = DocumentFile.fromTreeUri(getActivity(), originalUri);
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, docFile.getUri());
        startActivity(intent);
    }


    /*
     * 获取当前程序的版本名 本地
     */
    private String getVersionName() throws Exception{
        //获取packagemanager的实例
        PackageManager packageManager = getActivity().getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
        Log.e("TAG","版本号"+ packInfo.versionCode);
        Log.e("TAG","版本名"+ packInfo.versionName);
        return packInfo.versionName;
    }

    //对比本程序的版本号和最新程序的版本号
    private void checkVersion(View view){
        if (downloadUrl.equals("")){
            Toast.makeText(getContext(), "已是最新版本", Toast.LENGTH_LONG);
        }else{
            showDialogUpdate();
        }
    }

    //获得url
    public void getUrl(){
        // 向服务器发送一个okHttp请求
        String versionName = null;
        try {
            versionName = getVersionName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final RequestBody requestBody = new FormBody.Builder()
                .add("name", theType)
                .add("version", versionName) //键值对要对应
                .build();

        Util_NetUtil.sendOKHTTPRequest("http://106.12.105.160:8081/version", requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("TAG", "check fail");
                Toast.makeText(getContext(), "检查更新失败", Toast.LENGTH_LONG);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.e("TAG", "response");
//                JSONObject jsonObject = JSON.parseObject(response.body().string());
//                downloadUrl = jsonObject.getString("url");
                downloadUrl = response.body().string();
            }
        });
    }

    public void checkVersion() throws Exception {
        //todo 基本上和上面的一样，除了当已经是最新版本的时候不要显示一个TOAST,唉其实显示也行
        if (downloadUrl.equals("")){
            Toast.makeText(getContext(), "已是最新版本", Toast.LENGTH_LONG);
        }else{
            showDialogUpdate();
        }
    }

    // 红点实验函数
//    public void showRedPoint() {
//        Log.e("TAG", "试图显示红点 ");
//        new QBadgeView(getContext()).bindTarget(textview_check_for_update).setBadgeNumber(-1);
//    }

    /**
     * 提示版本更新的对话框
     */
    private void showDialogUpdate() {
        // 这里的属性可以一直设置，因为每次设置后返回的是一个builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        // 设置提示框的标题
        builder.setTitle("版本升级").
                // 设置提示框的图标
                        setIcon(R.drawable.icon_logo).
                // 设置要显示的信息
                        setMessage("发现新版本！请及时更新").
                // 设置确定按钮
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialog();
                        initCallBack();
                        //下载
                        download();
                    }
                }).

                // 设置取消按钮,null是什么都不做，并关闭对话框
                        setNegativeButton("取消", null);
        // 生产对话框
        AlertDialog alertDialog = builder.create();
        // 显示对话框
        alertDialog.show();
    }

    Dialog dialog;
    public void showDialog(){
        // 自定义对话框
        LayoutInflater inflater= LayoutInflater.from(getContext());
        View myView = inflater.inflate(R.layout.dialog,null);//引用自定义布局
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(myView);
        dialog = builder.create();  //创建对话框
        dialog.show();  //显示对话框
        myView.findViewById(R.id.cancelDownload).setOnClickListener(new View.OnClickListener() {
            //获取布局里面按钮
            @Override
            public void onClick(View v) {
                dialog.cancel();//点击按钮对话框消失
                InstallUtils.cancleDownload();
                Toast.makeText(getContext(), "好无情", Toast.LENGTH_SHORT ).show();
            }
        });
        progressBar = myView.findViewById(R.id.download_progress);
    }

    private void initCallBack() {

        downloadCallBack = new InstallUtils.DownloadCallBack() {

            @Override
            public void onStart() {

                //progressBar.setProgress(0);
                //download.setClickable(false);
            }

            @Override
            public void onComplete(String path) {
                apkDownloadPath = path;
                int progress = 100;
                progressBar.setProgress(progress);
                dialog.dismiss();
                //download.setClickable(true);
                //download.setBackgroundResource(R.color.colorPrimary);

                //先判断有没有安装权限
                InstallUtils.checkInstallPermission(getActivity(), new InstallUtils.InstallPermissionCallBack() {
                    @Override
                    public void onGranted() {
                        //去安装APK
                        installApk(apkDownloadPath);
                    }

                    @Override
                    public void onDenied() {
                        //弹出弹框提醒用户
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                .setTitle("温馨提示")
                                .setMessage("必须授权才能安装APK，请设置允许安装")
                                .setNegativeButton("取消", null)
                                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //打开设置页面
                                        //todo zhuyi
                                        InstallUtils.openInstallPermissionSetting(getActivity(), new InstallUtils.InstallPermissionCallBack() {
                                            @Override
                                            public void onGranted() {
                                                //去安装APK
                                                installApk(apkDownloadPath);
                                            }

                                            @Override
                                            public void onDenied() {
                                                //还是不允许咋搞？
                                                Toast.makeText(getContext(), "不允许安装咋搞？强制更新就退出应用程序吧！", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                })
                                .create();
                        alertDialog.show();
                    }
                });
            }

            @Override
            public void onLoading(long total, long current) {
                //内部做了处理，onLoading 进度转回progress必须是+1，防止频率过
                int progress1 = (int) (current * 100 / total);
                progressBar.setProgress(progress1);
            }

            @Override
            public void onFail(Exception e) {
                Toast.makeText(getContext(), "下载失败", Toast.LENGTH_LONG);
            }

            @Override
            public void cancle() {
                Toast.makeText(getContext(), "下载取消", Toast.LENGTH_LONG).show();
            }
        };
    }

    private void installApk(String path) {
        //todo 注意一下函数的第一个参数
        InstallUtils.installAPK(getActivity(), path, new InstallUtils.InstallCallBack() {
            @Override
            public void onSuccess() {
                //onSuccess：表示系统的安装界面被打开
                //防止用户取消安装，在这里可以关闭当前应用，以免出现安装被取消
                Toast.makeText(getContext(), "正在安装程序", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(Exception e) {
                Toast.makeText(getContext(), "下载失败", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * download函数
     */
    private void download() {
        InstallUtils.with(getContext())
                //必须-下载地址
                .setApkUrl(downloadUrl)
                //非必须-下载保存的文件的完整路径+name.apk
                .setApkPath(APK_SAVE_PATH)
                //非必须-下载回调
                .setCallBack(downloadCallBack)
                //开始下载
                .startDownload();

    }

    /**
     *  退出登陆的时候删除所有的闹钟
     */
    private void deteleAllAlarm(){

        // 闹钟缓存的数组
        List<String> alarm = new ArrayList<>();
        Log.e("deleteAlarm", "start");
        for(int i = 0; i < alarm.size() ; i++){
            Object_Todo_Broadcast_container obj = JSON.parseObject(alarm.get(i), Object_Todo_Broadcast_container.class);
            AlarmTimer alarmTimer = new AlarmTimer(obj);
            alarmTimer.cancelAlarmTimer(getContext());
            Log.e("delete_id", obj.getId() + "");
        }
    }

    /*
     * 获得缓存地址
     * */
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
}







