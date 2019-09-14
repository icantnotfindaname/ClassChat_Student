package com.example.classchat.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.example.classchat.Activity.Activity_Enter;

public class NetBroadcastReceiver extends BroadcastReceiver {
    public NetEvent event= Activity_Enter.event;

    @Override
    public void onReceive(Context context, Intent intent) {
        // 如果相等的话就说明网络状态发生了变化
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            int netWorkState = Util_NetState.getNetWorkState(context);
            // 接口回调传过去状态的类型
            event.onNetChange(netWorkState);
        }
    }

    //自定义网络切换接口
    public interface NetEvent {
        void onNetChange(int netMobile);
    }

}
