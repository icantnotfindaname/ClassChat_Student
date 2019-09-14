package com.example.classchat.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.classchat.Object.Course;
import com.example.classchat.Object.MySubject;
import com.example.classchat.R;

import java.util.List;

public class Adapter_Course extends RecyclerView.Adapter<Adapter_Course.ViewHolder> {
    private List<Course> mySubjectList;

    public Adapter_Course(List<Course> mySubjects) {
        mySubjectList = mySubjects;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView courseName;
        TextView courseSignTimes;

        public ViewHolder(View view) {
            super(view);
            courseName = view.findViewById(R.id.courseitem_coursename);
            courseSignTimes = view.findViewById(R.id.courseitem_coursesigntimes);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.course_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Course myCourse = mySubjectList.get(position);
        viewHolder.courseName.setText(myCourse.getClassName());
        viewHolder.courseSignTimes.setText("签到次数"+myCourse.getSignTime());
    }

    @Override
    public int getItemCount() {
        return mySubjectList.size();
    }
}
