package com.example.jackyjun.focus.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by jackyjun on 16/8/24.
 */
public class PlanDaysCalculator {
    public static long countdownDays(Calendar deadline){
        Date date1 = Calendar.getInstance().getTime();
        Date date2 = deadline.getTime();

        long diff = date2.getTime() - date1.getTime();
        if (diff/1000/3600/24>=0){
            return diff/1000/3600/24;
        }
        //cause zero actually means we have one day to spend
        return -1;
    }

    public static long countupDays(Calendar startDate){
        Date date1 = Calendar.getInstance().getTime();
        Date date2 = startDate.getTime();

        long diff = date1.getTime() - date2.getTime();
        return diff/1000/3600/24;
    }

    public static long countDays(Calendar startdate,Calendar deadline){
        Date date1 = startdate.getTime();
        Date date2 = deadline.getTime();

        long diff = date2.getTime() - date1.getTime();
        if (diff/1000/3600/24>=0){
            return diff/1000/3600/24;
        }
        return 0;
    }
}
