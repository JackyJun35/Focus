package com.example.jackyjun.focus.UI;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.view.BarChartView;
import com.example.jackyjun.focus.Model.TimeDataManager;
import com.example.jackyjun.focus.R;
import com.example.jackyjun.focus.util.CalendarUtil;

import java.util.Calendar;

/**
 * Created by jackyjun on 16/9/22.
 */
public class StatsActivity extends Activity {
    TimeDataManager timeDataManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        timeDataManager = new TimeDataManager(this);

        //load the card that display total time and today's time users track
        loadTotalTimeCard();

        loadWeeklyData();

        loadMonthlyData();

    }

    private void loadTotalTimeCard(){
        TextView todayHourTextView = (TextView) findViewById(R.id.stats_text_view_hour_today);
        TextView todayMinuteTextView = (TextView) findViewById(R.id.stats_text_view_minute_today);
        TextView totalHourTextView = (TextView) findViewById(R.id.stats_text_view_hour_total);
        TextView totalMinuteTextView = (TextView) findViewById(R.id.stats_text_view_minute_total);

        long todayHour = timeDataManager.getMinuteSpentToday()/60;
        long todayMinute = timeDataManager.getMinuteSpentToday()%60;
        long totalHour = timeDataManager.getTotalMinuteSpent()/60;
        long totalMinute = timeDataManager.getTotalMinuteSpent()%60;

        todayHourTextView.setText(String.valueOf(todayHour));
        todayMinuteTextView.setText(String.valueOf(todayMinute));
        totalHourTextView.setText(String.valueOf(totalHour));
        totalMinuteTextView.setText(String.valueOf(totalMinute));
    }

    private void loadWeeklyData(){
        BarChartView weeklyBarChartView = (BarChartView) findViewById(R.id.weekly_bar_chart);
        weeklyBarChartView.setBackgroundColor(getResources().getColor(R.color.StatsColorBlue));

        String[] labels = {"Mon","Tue","Wed","Thur","Fri","Sat","Sun"};
        float[] values = new float[7];
        Calendar[] calendarOfThisWeek = CalendarUtil.getCalendarForThisWeek();

        for (int i = 0; i < 7; i++){
            values[i] = (float) timeDataManager.getMinuteSpentByDate(calendarOfThisWeek[i]);
            Log.d("MinuteThisWeek",String.valueOf(values[i]));
        }

        BarSet weeklyBarSet = new BarSet(labels,values);
        weeklyBarSet.setColor(getResources().getColor(R.color.StatsBarColor));
        weeklyBarChartView.setRoundCorners(16);
        weeklyBarChartView.setTypeface(Typeface.DEFAULT_BOLD);
        weeklyBarChartView.setLabelsColor(getResources().getColor(R.color.White));
        weeklyBarChartView.setXAxis(false);
        weeklyBarChartView.setAxisColor(getResources().getColor(R.color.White));
        weeklyBarChartView.addData(weeklyBarSet);
        weeklyBarChartView.show();
    }

    private void loadMonthlyData(){
        BarChartView monthlyBarChartView = (BarChartView) findViewById(R.id.monthly_bar_chart);
        monthlyBarChartView.setBackgroundColor(getResources().getColor(R.color.StatsColorBlue));

        String[] labels = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        float[] values = new float[12];

        for(int i =0 ; i<12 ; i++) {
            values[i] = timeDataManager.getMinuteSpentThisMonth(i+1);
            Log.d("MinuteThisMonth",String.valueOf(values[i]));
        }

        BarSet monthlyBarSet = new BarSet(labels,values);
        monthlyBarSet.setColor(getResources().getColor(R.color.StatsBarColor));
        monthlyBarChartView.setRoundCorners(16);
        monthlyBarChartView.setTypeface(Typeface.DEFAULT_BOLD);
        monthlyBarChartView.setLabelsColor(getResources().getColor(R.color.White));
        monthlyBarChartView.setXAxis(false);
        monthlyBarChartView.setAxisColor(getResources().getColor(R.color.White));
        monthlyBarChartView.addData(monthlyBarSet);
        monthlyBarChartView.show();
    }

}
