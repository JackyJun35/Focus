package com.example.jackyjun.focus.util;


/**
 * Created by jackyjun on 16/9/6.
 * this class is to convert the seconds of the total time spend into string that is displayed as HH:MM:SS on the screen
 */
public class TimeAndSecondsConverter {
    public static String fromMilisecondToString(long elapseTime){
        long ss = (elapseTime/1000)%60;
        long mm = (elapseTime/1000/60)%60;
        long hh = (elapseTime/1000/3600);
        String second = Long.toString(ss);
        String minute = Long.toString(mm);
        String hour = Long.toString(hh);
        if(ss<10){second = "0"+second;}
        if (mm<10){minute = "0"+ minute;}
        String ans = hour+":"+minute+":"+second;
        return ans;
    }
}
