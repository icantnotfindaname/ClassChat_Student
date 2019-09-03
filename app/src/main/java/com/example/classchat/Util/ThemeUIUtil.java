package com.example.classchat.Util;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

public class ThemeUIUtil {
    /**
     * 切换应用主题
     */
    public static void changeTheme(View rootView, Resources.Theme theme){
        //就是递归调用changeTheme-----递归调用setTheme了
        if(rootView instanceof ThemeUIInterface){
            ((ThemeUIInterface)rootView).setTheme(theme);
            if(rootView instanceof ViewGroup){
                int count = ((ViewGroup) rootView).getChildCount();
                for(int i =0 ;i<count;i++){
                    changeTheme(((ViewGroup) rootView).getChildAt(i),theme);
                }
            }
        }
    }

}
