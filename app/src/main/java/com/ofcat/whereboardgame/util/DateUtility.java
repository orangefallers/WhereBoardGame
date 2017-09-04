package com.ofcat.whereboardgame.util;

import java.util.Calendar;

/**
 * Created by orangefaller on 2017/9/2.
 */

public class DateUtility {


    public static String getCustomFormatDate(Calendar calendar) {

        int month = calendar.get(Calendar.MONTH);
        int dayofmonth = calendar.get(Calendar.DAY_OF_MONTH);

        String formatDate = (month + 1) + "/" + dayofmonth + "(" + getDayOfWeek(calendar) + ")";
        return formatDate;
    }

    private static String getCustomFormatDate(int year, int month, int dayofmonth) {
        String date = year + "/" + month + "/" + dayofmonth;
        return date;
    }

    private static String getDayOfWeek(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return "日";
            case Calendar.MONDAY:
                return "一";
            case Calendar.TUESDAY:
                return "二";
            case Calendar.WEDNESDAY:
                return "三";
            case Calendar.THURSDAY:
                return "四";
            case Calendar.FRIDAY:
                return "五";
            case Calendar.SATURDAY:
                return "六";
            default:
                return "";
        }
    }


}
