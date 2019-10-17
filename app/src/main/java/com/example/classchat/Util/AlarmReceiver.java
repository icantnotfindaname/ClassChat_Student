package com.example.classchat.Util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.classchat.Activity.MainActivity;
import com.example.classchat.Activity.NotificationJumpBack;
import com.example.classchat.Object.Object_Todo_Broadcast_container;
import com.example.classchat.R;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;

public class AlarmReceiver extends BroadcastReceiver{
    private static final String CHANNEL_ID = "com.example.classchat.channelId";


    @Override
    public void onReceive(Context context, Intent intent) {

        if ( intent.getAction().equals("TIMER_ACTION")){

//            // 点击返回的时候就跳转到app的主界面
//            Intent back_Intent = new Intent(context, MainActivity.class);
//            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//            stackBuilder.addParentStack(NotificationJumpBack.class);
//            stackBuilder.addNextIntent(back_Intent);
//
//            // 如果AlarmManager管理的PendingIntent已经存在,让新的Intent更新之前Intent对象数据
//            PendingIntent pi = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            /**
             * NotificationJumpBack
             */
            Intent intent1 = new Intent(context, NotificationJumpBack.class);
            PendingIntent pi = PendingIntent.getActivity(context,0,intent1,0);

            Bundle bundle = intent.getBundleExtra("data");
            Object_Todo_Broadcast_container obj = (Object_Todo_Broadcast_container) bundle.getSerializable("alarm");
            Log.e("rece_title", obj.getTitle());
            String temp = obj.getDetail();
            if (temp == ""){
            }else{
                obj.setDetail(": " + temp);
            }

            // 弄一张bitmap
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_logo);

            // 对通知栏进行属性设置
            // 声明一个通知的builder
            Notification.Builder builder = new Notification.Builder(context);
            if (obj.getMinute() < 10){
                Notification notification = builder.setContentTitle("待办提醒" + " " + obj.getHour() + ":" + "0" + obj.getMinute() + " ")
                        .setContentText(obj.getTitle() + obj.getDetail())  // 消息的详细内容
                        .setTicker("待办提醒")  // 设置显示的提示文字
                        .setLargeIcon(bitmap)  // 添加大
                        .setSmallIcon(R.drawable.icon_logo)  // 小图标
                        .setPriority(Notification.PRIORITY_MAX)
                        .setContentIntent(pi)
                        .build();

                notification.flags |= Notification.FLAG_AUTO_CANCEL;  // 点击通知后自动清除通知
                notification.defaults = Notification.DEFAULT_SOUND;  // 发出默认声音
                // 检测版本
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    builder.setChannelId(CHANNEL_ID);
                }

                // 通知来喽
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                // android 8.0需要添加 channel 才能显示通知
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "LAOKE", IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(channel);
                }

                notificationManager.notify(obj.getId(), notification);
                bitmap.recycle();  // 回收bitmap
            }else{
                Notification notification = builder.setContentTitle("待办提醒" + " " + obj.getHour() + ":" + obj.getMinute() + " ")
                        .setContentText(obj.getTitle() + obj.getDetail())  // 消息的详细内容
                        .setTicker(obj.getTitle() + ": " + obj.getDetail())  // 设置显示的提示文字
                        .setLargeIcon(bitmap)  // 添加大
                        .setSmallIcon(R.drawable.icon_logo)  // 小图标
                        .setContentIntent(pi)
                        .build();
                notification.flags |= Notification.FLAG_AUTO_CANCEL;  // 点击通知后自动清除通知
                notification.defaults = Notification.DEFAULT_SOUND;  // 发出默认声音
                // 检测版本
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    builder.setChannelId(CHANNEL_ID);
                }

                // 通知
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                // android 8.0需要添加 channel 才能显示通知
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "LAOKE", IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(channel);
                }

                notificationManager.notify(obj.getId(), notification);

                bitmap.recycle();  // 回收bitmap
            }
        }
    }


}