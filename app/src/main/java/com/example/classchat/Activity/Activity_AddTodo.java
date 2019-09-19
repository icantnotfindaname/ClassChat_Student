package com.example.classchat.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.classchat.R;

import java.util.ArrayList;
import java.util.List;

public class Activity_AddTodo extends AppCompatActivity {

    private TextView setWeek;
    //周数多选框
    private AlertDialog.Builder mutilChoicebuilder;
    //配合周数多选框的数组
    private final String[] weeks= new String[]{"第1周","第2周","第3周","第4周","第5周","第6周","第7周","第8周","第9周","第10周","第11周","第12周","第13周","第14周","第15周","第16周","第17周","第18周","第19周","第20周","第21周","第22周","第23周","第24周","第25周"};
    private boolean[] weeksChecked = new boolean[]{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};
    //周数数组
    List<Integer> weeksnum=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__add_todo);
        Intent intent = getIntent();

        setWeek = findViewById(R.id.get_todo_week);

        //周数多选框
        mutilChoicebuilder = new AlertDialog.Builder(this);
        mutilChoicebuilder.setTitle("选择周数");
        mutilChoicebuilder.setMultiChoiceItems(weeks, weeksChecked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

            }
        });
        mutilChoicebuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String s = new String();
                weeksnum=new ArrayList<>();
                int end = 0;
                for(int i=0;i<weeksChecked.length;i++){
                    if(weeksChecked[i])
                        weeksnum.add(i+1);
                }

                for(int i = 0; i < weeksChecked.length; i++)
                {
                    if (weeksChecked[i])
                    {

                        int start = i+1;
                        for(int j=i+1;j<=weeksChecked.length;++j) {
                            if (j == weeksChecked.length && weeksChecked[j - 1]) {
                                end = weeksChecked.length;
                                i = weeksChecked.length - 1;
                                break;
                            } else if (!weeksChecked[j]) {
                                end = j;
                                i = j;
                                break;
                            }
                        }
                        if(start==end)
                            s+="第"+start+"周 ";
                        else
                            s+="第"+start+"~"+end+"周 ";

                    }

                }
                if (weeksnum.size() > 0){
                    setWeek.setText(s);
                }else{
                    //没有选择
                    Toast.makeText(Activity_AddTodo.this, "未选择周数!", Toast.LENGTH_SHORT).show();
                }


            }
        });
        mutilChoicebuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        setWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mutilChoicebuilder.show();
            }
        });

    //TODO weeksnum 为周数列表  添加时需要判断其是否为空
    }
}
