package test.com.ido.utils;

import android.content.Context;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 时间工具
 */
public class TimeUtil {
    public static final String DATE_FORMAT_YMDHms = "yyyy-MM-dd HH:mm:ss";
    /**
     * 判断是否24小时制度
     *
     * @param context
     * @return
     */
    public static boolean is24HourFormat(Context context) {
        return DateFormat.is24HourFormat(context);
    }

    /**
     * 开始时间的时间戳
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return
     */
    public static long getStartDateToStamp(int year, int month, int day) {
        return dateToStamp(year, month, day, 0, 0, 0);
    }

    /**
     * 开始时间
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return
     */
    public static Date getStartDate(int year, int month, int day) {
        return getDate(year, month, day, 0, 0, 0);
    }

    /**
     * 结束时间的时间戳
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return
     */
    public static long getEndDateToStamp(int year, int month, int day) {
        return dateToStamp(year, month, day, 23, 59, 59);
    }

    /**
     * 结束时间
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return
     */
    public static Date getEndDate(int year, int month, int day) {
        return getDate(year, month, day, 23, 59, 59);
    }

    /**
     * 将时间转换为时间戳
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @param hour  时
     * @param min   分
     * @param ss    秒
     * @return
     */
    public static long dateToStamp(int year, int month, int day, int hour, int min, int ss) {
        Date date = getDate(year, month, day, hour, min, ss);
        if (null != date) {
            return date.getTime();
        }
        return 0L;
    }

    /**
     * 获取月份
     *
     * @return
     */
    public static int getMonth() {
        return GregorianCalendar.getInstance().get(Calendar.MONTH) + 1;
    }

    /**
     * 获取年份
     *
     * @return
     */
    public static int getYear() {
        return GregorianCalendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * 获取日
     *
     * @return
     */
    public static int getDay() {
        return GregorianCalendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取小时
     *
     * @return
     */
    public static int getHour() {
        return GregorianCalendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取分钟
     *
     * @return
     */
    public static int getMinute() {
        return GregorianCalendar.getInstance().get(Calendar.MINUTE);
    }

    /**
     * 获取秒
     *
     * @return
     */
    public static int getSecond() {
        return GregorianCalendar.getInstance().get(Calendar.SECOND);
    }

    /**
     * 获取Date对象
     *
     * @return
     */
    public static Date getDate() {
        return getDate(getYear(), getMonth(), getDay());
    }

    /**
     * 获取Date对象
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return
     */
    public static Date getDate(int year, int month, int day) {
        return getDate(year, month, day, 0, 0, 0);
    }

    /**
     * 获取Date对象
     *
     * @param year   年
     * @param month  月
     * @param day    日
     * @param hour   时
     * @param minute 分
     * @param second 秒
     * @return
     */
    public static Date getDate(int year, int month, int day, int hour, int minute, int second) {
        return new GregorianCalendar(year, month - 1, day, hour, minute, second).getTime();
    }

    public static boolean isSameDay(long timeMs1, long timeMs2){
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(new Date(timeMs1));

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(new Date(timeMs2));

        if (calendar1.get(Calendar.YEAR) != calendar2.get(Calendar.YEAR)
                || calendar1.get(Calendar.MONTH) != calendar2.get(Calendar.MONTH)
                || calendar1.get(Calendar.DAY_OF_MONTH) != calendar2.get(Calendar.DAY_OF_MONTH)){
            return false;
        }

        return true;
    }
    /**
     * 转换时间格式，格式yyyy-MM-dd HH:mm:s
     *
     * @param time
     * @return
     */
    public static String convTimeDetail(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YMDHms);
        Date dt = new Date(time);
        String sDateTime = sdf.format(dt);
        return sDateTime;
    }

    public static long addSecond(String date,int second){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YMDHms);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(date));
            calendar.add(Calendar.SECOND,second);
            return calendar.getTime().getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String second2Minute(int second){
        int m = second / 60;
        int s = second-m*60;
        return String.format("%02d:%02d",m,s);
    }
}
