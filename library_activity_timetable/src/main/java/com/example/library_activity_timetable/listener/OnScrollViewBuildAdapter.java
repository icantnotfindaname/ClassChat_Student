package com.example.library_activity_timetable.listener;

import android.view.LayoutInflater;
import android.view.View;

import com.example.library_activity_timetable.R;

/**
 * 滚动布局构建监听的默认实现
 */

public class OnScrollViewBuildAdapter implements ISchedule.OnScrollViewBuildListener {
    @Override
    public View getScrollView(LayoutInflater mInflate) {
        return mInflate.inflate(R.layout.xml_scrollview,null,false);
    }
}
