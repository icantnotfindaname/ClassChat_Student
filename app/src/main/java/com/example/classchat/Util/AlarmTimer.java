package com.example.classchat.Util;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.coolerfall.daemon.Daemon;
import com.example.classchat.Object.Object_Todo_Broadcast_container;

public class AlarmTimer{


    // todo 闹钟服务不要把对象弄在构造函数里面  给每一个用户保存不同的缓存  退出登录时取消对应的闹钟
    //  重新登录的时候重新设置缓存里面的闹钟（这里的setAlarm函数里面日期的获得方式要重写）

    private Object_Todo_Broadcast_container obj;

    public AlarmTimer(){

    }

//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onCreate(){
//        super.onCreate();
//        Daemon.run(this, AlarmTimer.class, Daemon.INTERVAL_ONE_MINUTE);
//        /**
//         * 如果这里写启动闹钟，那每一次启动服务就会设置一个闹钟
//         * 感觉好像不太对啊
//         */
////        startTimeTask();
////        grayGuard();
//    }

    public AlarmTimer(Object_Todo_Broadcast_container object_todo_broadcast_container){
        this.obj = object_todo_broadcast_container;
    }

    /**
     * 设置闹钟，包括定时闹钟和重复闹钟
     * @param context
     */
    public void setAlarm(Context context){

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notification_Intent = new Intent(context, AlarmReceiver.class);
        notification_Intent.setAction("TIMER_ACTION");
        Bundle bundle = new Bundle();
        bundle.putSerializable("alarm",obj);
        notification_Intent.putExtra("data",bundle);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, obj.getId(), notification_Intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, obj.getCalculated_time(), broadcast);
    }

    // 设置重复闹钟
//    public void setReaptingAlarm(Context context, Object_Todo_Broadcast_container obj, long cycTime ){
//        Intent intent = new Intent();
//        intent.setAction("TIMER_ACTION_REPEATING"); // 设置一个action用于声明设置重复闹钟这一个动作
//        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
//        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        alarm.setRepeating(AlarmManager.RTC_WAKEUP, , , sender);
//    }

    /**
     * 取消闹钟
     */
    public void cancelAlarmTimer(Context context) {
        Intent myIntent = new Intent(context, AlarmReceiver.class);
        myIntent.setAction("TIMER_ACTION");
        PendingIntent sender = PendingIntent.getBroadcast(context, obj.getId() , myIntent,0);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(sender);
    }

//    private void grayGuard() {
//        if (Build.VERSION.SDK_INT < 18) {
//            //API < 18 ，此方法能有效隐藏Notification上的图标
//            startForeground(GRAY_SERVICE_ID, new Notification());
//        } else {
//            Intent innerIntent = new Intent(this, DaemonInnerService.class);
//            startService(innerIntent);
//            startForeground(GRAY_SERVICE_ID, new Notification());
//        }
//
//        //发送唤醒广播来促使挂掉的UI进程重新启动起来
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent alarmIntent = new Intent();
//        alarmIntent.setAction(WakeReceiver.GRAY_WAKE_ACTION);
//        PendingIntent operation = PendingIntent.getBroadcast(this, WAKE_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            alarmManager.setWindow(AlarmManager.RTC_WAKEUP,
//                    System.currentTimeMillis(), ALARM_INTERVAL, operation);
//        }else {
//            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
//                    System.currentTimeMillis(), ALARM_INTERVAL, operation);
//        }
//    }

    /**
     * 给 API >= 18 的平台上用的灰色保活手段
     */
//    public static class DaemonInnerService extends Service {
//        @Override
//        public void onCreate() {
//            Log.i("TAG", "InnerService -> onCreate");
//            super.onCreate();
//        }
//
//        @Override
//        public int onStartCommand(Intent intent, int flags, int startId) {
//            Log.i("TAG", "InnerService -> onStartCommand");
//            startForeground(GRAY_SERVICE_ID, new Notification());
//            //stopForeground(true);
//            stopSelf();
//            return super.onStartCommand(intent, flags, startId);
//        }
//
//        @Override
//        public IBinder onBind(Intent intent) {
//            throw new UnsupportedOperationException("Not yet implemented");
//        }
//
//        @Override
//        public void onDestroy() {
//            Log.i("TAG", "InnerService -> onDestroy");
//            super.onDestroy();
//        }
//    }

}
