package com.example.classchat.Util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.classchat.Object.Object_Todo_Broadcast_container;

public class AlarmTimer {

    private Object_Todo_Broadcast_container obj;

    public AlarmTimer(){

    }

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

    private long calculate_week(int week){
        return week * 7 * 24 * 60 * 60 * 1000;
    }

}
