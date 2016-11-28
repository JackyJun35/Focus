package com.example.jackyjun.focus.Model;

import android.content.Context;

import com.example.jackyjun.focus.DB.MyDatabase;
import com.example.jackyjun.focus.util.CalendarUtil;

import java.util.Calendar;
import java.util.List;

/**
 * Created by jackyjun on 16/10/10.
 */
public class TimeDataManager {
    private static TimeDataManager sTimeDataManager;
    private List<TimeData> timeDataList;
    private MyDatabase db;


    public static TimeDataManager get(Context context) {
        if (sTimeDataManager == null) {
            sTimeDataManager = new TimeDataManager(context);
        }
        return sTimeDataManager;
    }

    public TimeDataManager(Context context){
        db = MyDatabase.getInstance(context);
        timeDataList = db.getAllTimeData();
    }

    // warning: this algorithm is inefficient, need to change it soon
    public long getTotalMinuteSpent(){
        long timeSpent = 0;
        for (TimeData timeData: timeDataList){
            timeSpent += timeData.getSecond();
        }
        return timeSpent/60;
    }

    public long getMinuteSpentToday(){
        long timeSpent = 0;
        for (TimeData timeData: timeDataList){
            if (CalendarUtil.inSameDay(timeData.getDate(),Calendar.getInstance())){
                timeSpent +=timeData.getSecond();
            }
        }
        return timeSpent/60;
    }

    public long getMinuteSpentByDate(Calendar calendar){
        long timeSpent = 0;
        for (TimeData timeData: timeDataList){
           if (CalendarUtil.inSameDay(timeData.getDate(),calendar)){
               timeSpent += timeData.getSecond();
           }
        }
        return timeSpent/60;
    }

    public long getMinuteSpentThisMonth(int month){
        long timeSpent = 0;
        for (TimeData timeData : timeDataList){
            if (CalendarUtil.inSameMonth(timeData.getDate(),month)){
                timeSpent += timeData.getSecond();
            }
        }
        return timeSpent/60;
    }
}
