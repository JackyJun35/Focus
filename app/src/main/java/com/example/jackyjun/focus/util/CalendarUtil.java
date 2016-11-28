package com.example.jackyjun.focus.util;

import android.util.Log;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by jackyjun on 16/8/5.
 */
public class CalendarUtil {

    public static String fromCalenderToString(Calendar calendar){
        String myFormat = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String date = sdf.format(calendar.getTime());
        return date;
    }

    public static Calendar fromStringToCalender(String string){
        String myFormat = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Calendar calendar = Calendar.getInstance();
        try{
            calendar.setTime(sdf.parse(string));
        }catch (Exception e){
            e.printStackTrace();
        }
        return calendar;
    }

    public static boolean inSameDay(Calendar calendar1, Calendar calendar2) {

        int year1 = calendar1.get(Calendar.YEAR);
        int day1 = calendar1.get(Calendar.DAY_OF_YEAR);

        int year2 = calendar2.get(Calendar.YEAR);
        int day2 = calendar2.get(Calendar.DAY_OF_YEAR);

        if ((year1 == year2) && (day1 == day2)) {
            return true;
        }
        return false;
    }

    public static boolean inSameMonth(Calendar calendar,int month){
        int month1 = calendar.get(Calendar.MONTH);
        if (month == month1){
            return true;
        }
        return false;
    }

    public static Calendar[] getCalendarForThisWeek(){
        Calendar[] dateOfThisWeek = new Calendar[7];

        for(int i =1; i<8;i++){
            DateTime dt = new DateTime();
            // potential bug. I do not specify the locale.
            dateOfThisWeek[i-1] = dt.withDayOfWeek(i).toCalendar(null);
        }
        return dateOfThisWeek;
    }

}

