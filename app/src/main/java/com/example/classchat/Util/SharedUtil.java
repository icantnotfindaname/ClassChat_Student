package com.example.classchat.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedUtil {
    /**
     * 拿去参数偏好设置
     * @param context
     * @param name
     * @return
     */
    public static String getShartData(Context context, String name) {
        String temp = context.getSharedPreferences("ssun",context.MODE_PRIVATE).getString(name,"0");
        Log.e("getData:",temp);
        return temp;
    }

    /**
     * 保存参数
     * @param name
     */
    public static void setShartData(Context context,String name){
        SharedPreferences preferences  = context.getSharedPreferences("ssun", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("name", name);
        edit.commit();
    }

}
