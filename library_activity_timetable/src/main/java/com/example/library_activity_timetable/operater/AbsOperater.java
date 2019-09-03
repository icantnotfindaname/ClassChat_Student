package com.example.library_activity_timetable.operater;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.example.library_activity_timetable.Activity_TimetableView;

/**
 * 抽象的业务逻辑
 */
public abstract class AbsOperater {
    public void init(Context context, AttributeSet attrs, Activity_TimetableView view){};

    public void showView(){};

    public void updateDateView(){};

    public void updateSlideView(){};

    public void changeWeek(int week, boolean isCurWeek){};

    public LinearLayout getFlagLayout(){return null;};

    public LinearLayout getDateLayout(){return null;};

    public void setWeekendsVisiable(boolean isShow){};
}
