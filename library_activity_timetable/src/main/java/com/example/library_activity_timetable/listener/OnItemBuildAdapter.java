package com.example.library_activity_timetable.listener;

import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.library_activity_timetable.model.Schedule;

/**
 * Item构建监听器的默认实现.
 */

public class OnItemBuildAdapter implements ISchedule.OnItemBuildListener {
    @Override
    public String getItemText(Schedule schedule, boolean isThisWeek) {
        if (schedule == null || TextUtils.isEmpty(schedule.getName())) return "未命名";
        if (schedule.getRoom() == null) {
            if (!isThisWeek)
                return "[非本周]\n" + schedule.getName();
            return schedule.getName();
        }
        if (schedule.getName().length()<=9)
        {
            String r = schedule.getName() + "\n\n@" + schedule.getRoom();
            if (!isThisWeek) {
                r = "[非本周]\n" + r;
            }
            return r;
        }
        else
        {
            String r = schedule.getName().substring(0,7)+".." + "\n@" + schedule.getRoom();
            if (!isThisWeek) {
                r = "[非本周]\n" + r;
            }
            return r;
        }

    }

    @Override
    public void onItemUpdate(FrameLayout layout, TextView textView, TextView countTextView, Schedule schedule) {
    }
}
