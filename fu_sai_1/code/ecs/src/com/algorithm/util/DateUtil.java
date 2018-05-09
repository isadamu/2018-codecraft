package com.algorithm.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 日期处理类
 *
 * @author long
 */
public class DateUtil {

    // 默认的输入日期格式
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // 将输入的日期字符串转化为Calendar类
    public static Calendar stringToCalender(String dateStr) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(dateStr));
        } catch (ParseException e) {
            System.err.println("日期解析出错");
            e.printStackTrace();
        }
        return calendar;
    }

    public static void addDay(Calendar calendar, int num) {
        calendar.add(Calendar.DAY_OF_MONTH, num);
    }

    public static int getYear(Calendar calendar) {
        return calendar.get(Calendar.YEAR);
    }

    public static int getMonth(Calendar calendar) {
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static int getDayOfMonth(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getDayInWeek(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static int getHourInDay(Calendar calendar) {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static void printDateAndWeek(Calendar calendar) {
        System.out.println(calendar.get(Calendar.YEAR) + " " + (calendar.get(Calendar.MONTH) + 1) + " "
                + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.DAY_OF_WEEK));
    }

    public static boolean isSameDay(Calendar c1, Calendar c2) {
        if ( getYear(c1) == getYear(c2) && getMonth(c1) == getMonth(c2) && getDayOfMonth(c1) == getDayOfMonth(c2) ) {
            return true;
        }
        return false;
    }

    /**
     * 判断两个日期是不是同一个星期几
     * @param c1
     * @param c2
     * @return
     */
    public static boolean isSameDayInWeek(Calendar c1, Calendar c2) {
        if ( getDayInWeek(c1) == getDayInWeek(c2) ) {
            return true;
        }
        return false;
    }

    /**
     * 计算两个日期之间相差多少天
     * @param c1
     * @param c2
     * @return
     */
    public static int diffByDay(Calendar c1, Calendar c2) {
        Calendar begin = null;
        Calendar end = null;
        if ( !isBigger(c1, c2) ) {
            begin = (Calendar)c1.clone();
            end = (Calendar)c2.clone();
        } else {
            end = (Calendar)c1.clone();
            begin = (Calendar)c2.clone();
        }
        int len = 0;
        while ( !DateUtil.isSameDay(begin, end) ) {
            DateUtil.addDay(begin, 1);
            len++;
        }
        return len;
    }

    /**
     * 计算第一个日期是否比第二个日期大，只精确到天数
     * @param c1
     * @param c2
     * @return
     */
    public static boolean isBigger(Calendar c1, Calendar c2) {
        if ( getYear(c1) > getYear(c2) ) {
            return true;
        } else if ( getYear(c1) == getYear(c2) ) {
            if ( getMonth(c1) > getMonth(c2) ) {
                return true;
            } else if ( getMonth(c1) == getMonth(c2) ) {
                if ( getDayOfMonth(c1) > getDayOfMonth(c2) ) {
                    return true;
                }
            }
        }
        return false;
    }
}
