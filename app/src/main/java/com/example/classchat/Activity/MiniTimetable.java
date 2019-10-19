package com.example.classchat.Activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.classchat.Adapter.Adapter_ComparisonDetail;
import com.example.classchat.Object.Object_MiniTimeTable;
import com.example.classchat.R;
import com.example.classchat.Util.Util_NetUtil;
import com.example.classchat.Util.Util_ToastUtils;
import com.example.library_cache.disklrucache.Util;

import org.jetbrains.annotations.NotNull;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 课表显示View
 *
 * @author shallcheek
 */
public class  MiniTimetable extends LinearLayout {
    /**
     * 配色数组
     */
    public static int colors[] = {
            R.drawable.select_label_wu,
            R.drawable.select_label_san,
            R.drawable.select_label_er,
            R.drawable.select_label_ba,
            R.drawable.select_label_sss,
            R.drawable.select_label_sy,
            R.drawable.select_label_yiw,
            R.drawable.select_label_yi};
    public static List<String> colorStr ;


    private final static int START = 0;
    //最大节数
    public final static int MAXNUM = 12;
    //显示到星期几
    public final static int WEEKNUM = 7;
    /**
     * 单个View高度
     */
    private final static int TIME_TABLE_HEIGHT = 50;
    /**
     * 线的高度
     */
    private final static int TIME_TABLE_LINE_HEIGHT = 2;
    private final static int LEFT_TITLE_WIDTH = 20;
    private final static int WEEK_TITLE_HEIGHT = 30;
    /**
     * 第一行的星期显示
     */
    private LinearLayout mHorizontalWeekLayout;
    private LinearLayout mVerticalWeekLaout;
    private String[] mWeekTitle = {"一", "二", "三", "四", "五", "六", "七"};


    private List<Object_MiniTimeTable> mListTimeTable = new ArrayList<Object_MiniTimeTable>();

    private Context mContext;

    private Dialog dialog;

    private RecyclerView rv;
    private Button close;

    public MiniTimetable(Context context) {
        super(context);
    }

    public MiniTimetable(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * 横的分界线
     *
     * @return
     */
    private View getWeekHorizontalLine() {
        View line = new View(getContext());
        line.setBackgroundColor(getResources().getColor(R.color.view_line));
        line.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, TIME_TABLE_LINE_HEIGHT));
        return line;
    }

    /**
     * 竖向分界线
     *
     * @return
     */
    private View getWeekVerticalLine() {
        View line = new View(mContext);
        line.setBackgroundColor(getResources().getColor(R.color.view_line));
        line.setLayoutParams(new ViewGroup.LayoutParams((TIME_TABLE_LINE_HEIGHT), dip2px(WEEK_TITLE_HEIGHT)));
        return line;
    }


    private void initView() {

        mHorizontalWeekLayout = new LinearLayout(getContext());
        mHorizontalWeekLayout.setOrientation(HORIZONTAL);
        mVerticalWeekLaout = new LinearLayout(getContext());
        mVerticalWeekLaout.setOrientation(HORIZONTAL);

        //表格
        for (int i = 0; i <= WEEKNUM; i++) {
            if (i == 0) {
                layoutLeftNumber();
            } else {
                layoutWeekTitleView(i);
                layoutContentView(i);
            }

            mVerticalWeekLaout.addView(createTableVerticalLine());
            mHorizontalWeekLayout.addView(getWeekVerticalLine());
        }
        addView(mHorizontalWeekLayout);
        addView(getWeekHorizontalLine());
        addView(mVerticalWeekLaout);
    }

    @NonNull
    private View createTableVerticalLine() {
        View l = new View(getContext());
        l.setLayoutParams(new ViewGroup.LayoutParams(TIME_TABLE_LINE_HEIGHT, dip2px(TIME_TABLE_HEIGHT * MAXNUM) + (MAXNUM - 2) * TIME_TABLE_LINE_HEIGHT));
        l.setBackgroundColor(getResources().getColor(R.color.view_line));
        return l;
    }

    private void layoutContentView(int week) {
        List<Object_MiniTimeTable> weekClassList = findWeekClassList(week);
        //添加
        LinearLayout mLayout = createWeekTimeTableView(weekClassList, week);
        mLayout.setOrientation(VERTICAL);
        mLayout.setLayoutParams(new ViewGroup.LayoutParams((getViewWidth() - dip2px(20)) / WEEKNUM, LayoutParams.MATCH_PARENT));
        mLayout.setWeightSum(1);
        mVerticalWeekLaout.addView(mLayout);
    }

    /**
     * 遍历出星期1~7的课表
     * 再进行排序
     *
     * @param week 星期
     */
    @NonNull
    private List<Object_MiniTimeTable> findWeekClassList(int week) {
        List<Object_MiniTimeTable> list = new ArrayList<>();
        for (Object_MiniTimeTable objectMiniTimeTable : mListTimeTable) {
            if (objectMiniTimeTable.getWeek() == week) {
                list.add(objectMiniTimeTable);
            }
        }

        Collections.sort(list, new Comparator<Object_MiniTimeTable>() {
            @Override
            public int compare(Object_MiniTimeTable o1, Object_MiniTimeTable o2) {
                return o1.getStartnum() - o2.getStartnum();
            }
        });

        return list;
    }

    private void layoutWeekTitleView(int weekNumber) {
        TextView weekText = new TextView(getContext());
        weekText.setTextColor(getResources().getColor(R.color.text_color));
        weekText.setWidth(((getViewWidth() - dip2px(LEFT_TITLE_WIDTH))) / WEEKNUM);
        weekText.setHeight(dip2px(WEEK_TITLE_HEIGHT));
        weekText.setGravity(Gravity.CENTER);
        weekText.setTextSize(16);
        weekText.setText(mWeekTitle[weekNumber - 1]);
        mHorizontalWeekLayout.addView(weekText);
    }


    private void layoutLeftNumber() {
        //课表出的0,0格子 空白的
        TextView mTime = new TextView(mContext);
        mTime.setLayoutParams(new ViewGroup.LayoutParams(dip2px(LEFT_TITLE_WIDTH), dip2px(WEEK_TITLE_HEIGHT)));
        mHorizontalWeekLayout.addView(mTime);

        //绘制1~MAXNUM
        LinearLayout numberView = new LinearLayout(mContext);
        numberView.setLayoutParams(new ViewGroup.LayoutParams(dip2px(LEFT_TITLE_WIDTH), dip2px(MAXNUM * TIME_TABLE_HEIGHT) + MAXNUM * 2));
        numberView.setOrientation(VERTICAL);
        for (int j = 1; j <= MAXNUM; j++) {
            TextView number = createNumberView(j);
            numberView.addView(number);
            numberView.addView(getWeekHorizontalLine());
        }
        mVerticalWeekLaout.addView(numberView);
    }

    @NonNull
    private TextView createNumberView(int j) {
        TextView number = new TextView(getContext());
        number.setLayoutParams(new ViewGroup.LayoutParams(dip2px(LEFT_TITLE_WIDTH), dip2px(TIME_TABLE_HEIGHT)));
        number.setGravity(Gravity.CENTER);
        number.setTextColor(getResources().getColor(R.color.text_color));
        number.setTextSize(14);
        number.setText(String.valueOf(j));
        return number;
    }

    private int getViewWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    /**
     * 绘制空白
     *
     * @param count 数量
     * @param week  星期
     * @param start 用着计算下标
     */
    private View addBlankView(int count, final int week, final int start) {
        LinearLayout blank = new LinearLayout(getContext());
        blank.setOrientation(VERTICAL);
        for (int i = 1; i < count; i++) {
            View classView = new View(getContext());
            classView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(TIME_TABLE_HEIGHT)));
            blank.addView(classView);
            blank.addView(getWeekHorizontalLine());
            final int num = i;
        }
        return blank;
    }

    /**
     * 星期一到星期天的课表
     *
     * @param weekList 每天的课程列表
     * @param week     周
     */
    private LinearLayout createWeekTimeTableView(List<Object_MiniTimeTable> weekList, int week) {
        LinearLayout weekTableView = new LinearLayout(getContext());
        weekTableView.setOrientation(VERTICAL);
        int size = weekList.size();
        if (weekList.isEmpty()) {
            weekTableView.addView(addBlankView(MAXNUM + 1, week, 0));
        } else {
            for (int i = 0; i < size; i++) {
                Object_MiniTimeTable tableModel = weekList.get(i);
                if (i == 0) {
                    //添加的0到开始节数的空格
                    weekTableView.addView(addBlankView(tableModel.getStartnum(), week, 0));
                    weekTableView.addView(createClassView(tableModel));
                } else if (weekList.get(i).getStartnum() - weekList.get(i - 1).getEndnum() > 0) {
                    //填充
                    weekTableView.addView(addBlankView(weekList.get(i).getStartnum() - weekList.get(i - 1).getEndnum(), week, weekList.get(i - 1).getEndnum()));
                    weekTableView.addView(createClassView(weekList.get(i)));
                }
                //绘制剩下的空白
                if (i + 1 == size) {
                    weekTableView.addView(addBlankView(MAXNUM - weekList.get(i).getEndnum() + 1, week, weekList.get(i).getEndnum()));
                }
            }
        }
        return weekTableView;
    }

    /**
     * 获取单个课表View 也可以自定义我这个
     *
     * @param model 数据类型
     * @return
     */
    @SuppressWarnings("deprecation")
    private View createClassView(final Object_MiniTimeTable model) {
        LinearLayout mTimeTableView = new LinearLayout(getContext());
        mTimeTableView.setOrientation(VERTICAL);
        int num = (model.getEndnum() - model.getStartnum());
        mTimeTableView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px((num + 1) * TIME_TABLE_HEIGHT) + (num + 1) * TIME_TABLE_LINE_HEIGHT));

        TextView mTimeTableNameView = new TextView(getContext());
        mTimeTableNameView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px((num + 1) * TIME_TABLE_HEIGHT) + (num) * TIME_TABLE_LINE_HEIGHT));

        mTimeTableNameView.setTextColor(getContext().getResources().getColor(
                android.R.color.white));
        mTimeTableNameView.setTextSize(16);
        mTimeTableNameView.setGravity(Gravity.CENTER);
        mTimeTableNameView.setText(model.getName());

        mTimeTableView.addView(mTimeTableNameView);
        mTimeTableView.addView(getWeekHorizontalLine());

        mTimeTableView.setBackgroundDrawable(getContext().getResources()
                .getDrawable(colors[(getColorNum(model.getName())+ 1)%8]));
        mTimeTableView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                设置点击有人格子事件
                showDialog(model.getNameList(), model.getNumList(), model.getComparisonID());
            }
        });
        return mTimeTableView;
    }

    private void showDialog(List<String>nameList, List<Integer>numList, String comparisonID){
        // 自定义对话框
        final LayoutInflater inflater= LayoutInflater.from(mContext);
        View myView = inflater.inflate(R.layout.dialog_comparison_detail,null);//引用自定义布局
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        rv = myView.findViewById(R.id.rv_comparison_dialog);
        close = myView.findViewById(R.id.close_dialog);
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setAction("miniTimetable.send");
                mContext.sendBroadcast(intent);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(new Adapter_ComparisonDetail(mContext, nameList, numList, comparisonID));
        builder.setView(myView);
        dialog = builder.create();  //创建对话框
        dialog.show();  //显示对话框
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    dialog.dismiss();
                    Intent intent = new Intent();
                    intent.setAction("miniTimetable.send");
                    mContext.sendBroadcast(intent);
                }
                return true;
            }
        });

    }
    /**
     * 转换dp
     *
     * @param dpValue
     * @return
     */
    public int dip2px(float dpValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale);
    }

    public void setTimeTable(List<Object_MiniTimeTable> mlist) {
        this.mListTimeTable = mlist;
        colorStr = new ArrayList<>();
        for (Object_MiniTimeTable objectMiniTimeTable : mlist) {
            addTimeName(objectMiniTimeTable.getName());
        }
        initView();
        invalidate();
    }

    public void refreshTimeTable(List<Object_MiniTimeTable> mlist) {
        removeView(mHorizontalWeekLayout);
        removeView(getWeekHorizontalLine());
        removeView(mVerticalWeekLaout);
        this.mListTimeTable = mlist;
        colorStr = new ArrayList<>();

        for (Object_MiniTimeTable objectMiniTimeTable : mlist) {
            addTimeName(objectMiniTimeTable.getName());
        }
        initView();
        invalidate();
    }


    /**
     * 输入课表名循环判断是否数组存在该课表 如果存在输出true并退出循环 如果不存在则存入colorSt[20]数组
     *
     * @param name
     */
    private void addTimeName(String name) {
        boolean isRepeat = false;
        for (int i = 0; i < colorStr.size(); i++) {
            if (name.equals(colorStr.get(i))) {
                isRepeat = true;
                break;
            } else {
                isRepeat = false;
            }
        }
        if (!isRepeat) {
            colorStr.add(name);
        }
    }

    /**
     * 获取数组中的课程名
     *
     * @param name
     * @return
     */
    public static int getColorNum(String name) {
        int num = 0;
        for (int i = 0; i < colorStr.size(); i++) {
            if (name.equals(colorStr.get(i))) {
                num = i;
            }
        }
        return num;
    }

}
