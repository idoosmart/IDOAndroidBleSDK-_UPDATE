package test.com.ido.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 日期格式化辅助类
 */
public class DateUtil {
    public static final String DATE_FORMAT_YMDHms_2 = "yyyy-MM-dd_HH:mm:ss";

    public static final String DATE_FORMAT_YMDHm = "yyyy/MM/dd HH:mm";
    public static final String DATE_FORMAT_YMDHms = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_MD = "M/d";
    public static final String DATE_FORMAT_MD_2 = "MM/dd";
    public static final String DATE_FORMAT_MDs = "MM-dd";
    public static final String DATE_FORMAT_YM = "yyyy/M";
    public static final String DATE_FORMAT_YM_2 = "yyyy/MM";
    public static final String DATE_FORMAT_YMD = "yyyy-MM-dd";
    public static final String DATE_FORMAT_YMD_2 = "yyyy/MM/dd";
    public static final String DATE_FORMAT_YM_3 = "yyyy-MM";
    public static final String DATE_FORMAT_Hm = "HH:mm";
    public static final String DATE_FORMAT_MDHm = "MM/dd HH:mm";
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_YMDHms, Locale.CHINA);
    public static SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(DATE_FORMAT_MDs, Locale.CHINA);
    public static final long DAY = 1000 * 60 * 60 * 24; //1天的毫秒值
    public static final long HOUR = 1000 * 60 * 60;    //1小时的毫秒值
    public static final long MINUTE = 1000 * 60;    //1分钟的毫秒值
    public static final int UNIT_DAY = 0;
    public static final int UNIT_HOER = 1;
    public static final int UNIT_MINUTE = 2;

    public static Date parse(String dateStr, String format) {
        try {
            return new SimpleDateFormat(format, Locale.getDefault()).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMonthDay(Date date) {
        return new SimpleDateFormat("MM-dd", Locale.getDefault()).format(date);
    }


    /**
     * 获取日在周的位置
     *
     * @param date
     * @return
     */
    public static String getWeekDayIndex(String date, String format) {
        if (TextUtils.isEmpty(date)) return "";
        if (TextUtils.isEmpty(format)) {
            format = "yyyy-MM-dd";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
    }

    /**
     * 根据时分秒组合
     *
     * @param hour   时
     * @param min    分
     * @param second 秒
     * @return 20180611054921 时间格式字符串
     */
    public static long getSportStartTime(int hour, int min, int second) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        String date = format.format(new Date());
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(date);
        stringBuffer.append(String.format(Locale.CHINA, "%02d", hour));
        stringBuffer.append(String.format(Locale.CHINA, "%02d", min));
        stringBuffer.append(String.format(Locale.CHINA, "%02d", second));
        return Long.parseLong(stringBuffer.toString());
    }

    /**
     * 返回一个年月日，时分秒为0的Date对象
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static Date getDate(int year, int month, int day) {
        return new Date(year - 1900, month - 1, day, 0, 0, 0);
    }

    /**
     * 获取月份
     *
     * @param dateTime 日期
     * @param pattern  日期的显示格式
     * @return 返回月
     */
    public static String getMouth(String dateTime, String pattern) {
        String result = "";
        if (TextUtils.isEmpty(dateTime) || TextUtils.isEmpty(pattern)) return result;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.CHINA);
        Date date = null;
        try {
            date = sdf.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            int month = date.getMonth() + 1;
            if (month < 10) result = "0" + month;
            else result = String.valueOf(month);
        }
        return result;
    }

    /**
     * @return x月x日
     */
    public static String getDay(String dateTime, String pattern) {
        String result = "";
        if (TextUtils.isEmpty(dateTime) || TextUtils.isEmpty(pattern)) return result;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.CHINA);
        Date date = null;
        try {
            date = sdf.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            int day = date.getDay();
            if (day < 10) result = "0" + day;
            else result = String.valueOf(day);
        }
        return result;
    }

    /**
     * 获取当前日
     *
     * @return
     */
    public static int getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前的小时
     *
     * @return
     */
    public static int getCurrentHour() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 当前分钟
     *
     * @return
     */
    public static int getCurrentMin() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 当前秒
     *
     * @return
     */
    public static int getCurrentSecond() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.SECOND);
    }

    /**
     * 时间格式化
     *
     * @param time 单位秒
     * @return 01:06
     */
    public static String computeTime(long time) {
        int hour = (int) (time / 60 / 60);
        StringBuffer result = new StringBuffer();
        if (hour > 0) {
            if (hour < 10) {
                result.append("0");
            }
            time -= hour * 60 * 60;
            result.append(hour + ":");
        }
        int minute = (int) (time / 60);
        if (minute < 10) {
            result.append("0");
        }
        int second = (int) (time - minute * 60);
        result.append(minute + ":");

        if (second < 10) {
            result.append("0");
        }
        result.append(second);

        return result.toString();
    }

    /**
     * 时间格式化
     *
     * @param time 单位秒
     * @return 5'55''
     */
    public static String computeTimeMS(int time) {
        int minute = time / 60;
        int second = time % 60;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.CHINA, "%02d", minute));
        sb.append("'");
        sb.append(String.format(Locale.CHINA, "%02d", second));
        sb.append("''");
        return sb.toString();
    }

    /**
     * 格式化配速
     *
     * @param pace
     * @return
     */
    public static String computeTimePace(String pace) {
        if (pace == null) pace = "";
        if (pace.contains(".")) {
            String[] paces = pace.split("\\.");
            return String.format(Locale.CHINA, "%02d", Integer.parseInt(paces[0])) + "'" + String.format(Locale.CHINA, "%02d", Integer.parseInt(paces[1]) * 60 / 100) + "''";
        } else if (pace.contains(",")) {
            String[] paces = pace.split(",");
            return String.format(Locale.CHINA, "%02d", Integer.parseInt(paces[0])) + "'" + String.format(Locale.CHINA, "%02d", Integer.parseInt(paces[1]) * 60 / 100) + "''";
        } else {
            return String.format(Locale.CHINA, "%02d", 0) + "'" + String.format(Locale.CHINA, "%02d", 0) + "''";
        }

    }

    /**
     * 时间格式化
     *
     * @param time 66
     * @return 00:01:06 单位秒
     */
    public static String computeTimeHMS(long time) {

        int hour = (int) (time / 60 / 60);
        StringBuffer result = new StringBuffer();
        time -= hour * 60 * 60;
        result.append(String.format(Locale.CHINA, "%02d", hour));
        result.append(":");
        int minute = (int) (time / 60);
        int second = (int) (time - minute * 60);
        result.append(String.format(Locale.CHINA, "%02d", minute));
        result.append(":");
        result.append(String.format(Locale.CHINA, "%02d", second));

        return result.toString();
    }

    /**
     * 时间格式化
     *
     * @param time 66
     * @return 00:01:06
     */
    public static String computeTimeHM(long time) {

        int hour = (int) (time / 60 / 60);
        StringBuffer result = new StringBuffer();
        time -= hour * 60 * 60;
        result.append(String.format(Locale.CHINA, "%02d", hour));
        result.append(":");
        int minute = (int) (time / 60);
        result.append(String.format(Locale.CHINA, "%02d", minute));
        return result.toString();
    }

    /**
     * 时间格式化
     *
     * @param time 66
     * @return 00:01:06
     */
    public static String computeTimeMS(long time) {

        int hour = (int) (time / 60 / 60);
        StringBuffer result = new StringBuffer();
        time -= hour * 60 * 60;
        //result.append(String.format(Locale.CHINA,"%02d", hour));
        //result.append(":");
        int minute = (int) (time / 60);
        int second = (int) (time - minute * 60);
        result.append(String.format(Locale.CHINA, "%02d", minute));

        return result.toString();
    }


    public static String format2(int year, int mouth, int day) {
        return year + "/" + String.format(Locale.CHINA, "%02d", mouth) + "/" + String.format(Locale.CHINA, "%02d", day);
    }

    public static String format3(int year, int mouth, int day) {
        return "" + year + String.format(Locale.CHINA, "%02d", mouth) + String.format(Locale.CHINA, "%02d", day);
    }

    public static String format(int year, int mouth, int day) {
        return year + "-" + String.format(Locale.CHINA, "%02d", mouth) + "-" + String.format(Locale.CHINA, "%02d", day);
    }

    @SuppressLint("DefaultLocale")
    public static String format(int year, int mouth, int day, int h, int min, int second) {
        return year + "-" + String.format("%02d", mouth) + "-" + String.format("%02d", day) + " "
                + String.format("%02d", h) + ":" + String.format("%02d", min) + ":" + String.format("%02d", second);
    }

    /**
     * 将一个整数以两位数输出,如果不足两位数,则用0补
     *
     * @param mouth 1
     * @return 01
     */
    public static String format(int mouth) {
        return String.format(Locale.CHINA, "%02d", mouth);
    }

    /**
     * 将指定日期按照指定格式显示
     *
     * @param pattern 日期格式
     * @param date    日期对象
     */
    public static String getFormatDate(String pattern, Date date) {
        if (date == null) date = new Date();
        if (TextUtils.isEmpty(pattern)) pattern = DATE_FORMAT_YMDHms;
        return new SimpleDateFormat(pattern, Locale.CHINA).format(date);
    }

    public static String getFormatDate(String pattern, Calendar calendar) {
        if (calendar == null) calendar = Calendar.getInstance();
        return getFormatDate(pattern, calendar.getTime());
    }

    public static String getFormatDate(String pattern, long time) {
        Date date = new Date(time);
        return getFormatDate(pattern, date);
    }

    public static String getFormatDate(String pattern, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return getFormatDate(pattern, calendar.getTime());
    }

    public static String getFormatDate(String pattern, int year, int month, int day, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        return getFormatDate(pattern, calendar.getTime());
    }

    public static String formatAdjustDate(SimpleDateFormat dateFormat, Date date) {
//        date.setYear(date.getYear()-1900);
        Date adjustDate = (Date) date.clone();
        adjustDate.setYear(date.getYear() - 1900);
        return dateFormat.format(adjustDate);
    }

    /**
     * 获取今天的日期
     *
     * @return
     */
    public static Date getTodayDate() {
        return Calendar.getInstance().getTime();
    }

    /**
     * 是否为今天
     *
     * @param date
     * @return
     */
    public static boolean isToday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int[] currentDate = getCurrentDate();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        return currentDate[0] == year && currentDate[1] == month && currentDate[2] == day;
    }

    /**
     * 是否为今天
     *
     * @param millis
     * @return
     */
    public static boolean isToday(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        int[] currentDate = getCurrentDate();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        return currentDate[0] == year && currentDate[1] == month && currentDate[2] == day;
    }

    public static int[] todayYearMonthDay() {
        Calendar calendar = Calendar.getInstance();
        int[] date = new int[3];
        date[0] = calendar.get(Calendar.YEAR);
        date[1] = calendar.get(Calendar.MONTH) + 1;
        date[2] = calendar.get(Calendar.DAY_OF_MONTH);
        return date;
    }

    /**
     * 返回当天的小时
     *
     * @param date
     * @return
     */
    public static int getDataDayHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 返回当天天数
     *
     * @param date
     * @return
     */
    public static int getDataDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取日在周的位置
     *
     * @param date
     * @return
     */
    public static int getDataWeekDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取第几月
     *
     * @param date
     * @return
     */
    public static int getDataMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }


    public static int getTodayHour() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getTodayMin() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MINUTE);
    }


    /**
     * 上传服务器的时间
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static String getUpLoadServiceDate(int year, int month, int day) {
        return String.format(Locale.CHINA, "%04d", year) + "-" + String.format(Locale.CHINA, "%02d", month) + "-" + String.format(Locale.CHINA, "%02d", day) + " 00:00:00";
    }

    /**
     * @return 2014-11-18
     */
    public static String getDay() {
        Calendar calendar = Calendar.getInstance();
        final StringBuffer buffer = new StringBuffer();

        final int year = calendar.get(Calendar.YEAR);
        if (year < 10) {
            buffer.append("0" + year);
        } else {
            buffer.append(year);
        }
        buffer.append("-");
        final int mouth = (calendar.get(Calendar.MONTH) + 1);
        if (mouth < 10) {
            buffer.append("0" + mouth);
        } else {
            buffer.append(mouth);
        }
        buffer.append("-");
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day < 10) {
            buffer.append("0" + day);
        } else {
            buffer.append(day);
        }

        return new String(buffer);
    }

    public static long getLongFromDateStr(String dateStr) {
        return getLongFromDateStr(dateStr, DATE_FORMAT_YMDHms);
    }

    public static long getLongFromDateStr(String dateStr, String format) {
        try {
            return new SimpleDateFormat(format, Locale.CHINA).parse(dateStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 通过实践返回时间戳
     *
     * @param dateStr
     * @return
     */
    public static Date getLongFromDate(String dateStr) {
        try {
            return new SimpleDateFormat(DATE_FORMAT_YMDHms, Locale.CHINA).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static Date getDateByHMS(int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate, Date bdate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        long between_days = 0;
        try {
            smdate = sdf.parse(sdf.format(smdate));
            bdate = sdf.parse(sdf.format(bdate));
            Calendar cal = Calendar.getInstance();
            cal.setTime(smdate);
            long time1 = cal.getTimeInMillis();
            cal.setTime(bdate);
            long time2 = cal.getTimeInMillis();
            between_days = (time2 - time1) / (1000 * 3600 * 24);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(String startDate, String endDate) {
        if (TextUtils.equals(startDate, endDate)) return 0;
        if (TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate)) return 0;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YMD, Locale.CHINA);
        long between_days = 0;
        try {
            Date sDate = sdf.parse(startDate);
            Date eDate = sdf.parse(endDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(sDate);
            long startTime = cal.getTimeInMillis();
            cal.setTime(eDate);
            long endTime = cal.getTimeInMillis();
            between_days = (endTime - startTime) / (1000 * 3600 * 24);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (int) between_days;
    }

    /**
     * 获得指定日期的前days天对应的日期
     *
     * @param specifiedDay 指定日期
     * @param pattern      日志格式
     * @param days         指定日期前几天
     * @return 指定日期的前days天对应的日期
     */
    public static String getSpecifiedDayBefore(String specifiedDay, String pattern, int days) {
        if (TextUtils.isEmpty(pattern) || TextUtils.isEmpty(specifiedDay)) return "";
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(pattern, Locale.CHINA).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - days);
        return new SimpleDateFormat(pattern, Locale.CHINA).format(c.getTime());
    }

    /**
     * 判断传入的时间是否在当前时间之前
     *
     * @param date
     * @return
     */
    public static boolean isBeforeCurrent(Date date) {
        Calendar calendar = Calendar.getInstance();
        /*calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);*/
        return date.before(calendar.getTime());
    }

    /**
     * 字符串根据格式转换为时间
     *
     * @param dateString 时间字符串
     * @param style      时间格式
     * @return 时间
     */
    public static Date string2Date(String dateString, String style) {
        Date date = new Date();
        SimpleDateFormat strToDate = new SimpleDateFormat(style, Locale.CHINA);
        if (!TextUtils.isEmpty(dateString)) {
            try {
                date = strToDate.parse(dateString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    public static int[] getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new int[]{year, month, day};
    }

    /**
     * 获取当前年份
     *
     * @return
     */
    public static int getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取当前月
     *
     * @return
     */
    public static int getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取当前月
     *
     * @return
     */
    public static int getCurrentMonthNew() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH);
    }

    /**
     * 获取年、月、日
     *
     * @return
     */
    public static int[] getYearMonthDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new int[]{year, month, day};
    }



    /**
     * 根据传入的日期和提前天数，计算与当前时间间隔（毫秒值）
     *
     * @param dateStr    下一次的提醒日期 yyyy/MM/DD HH:mm
     * @param dateFormat 格式
     * @return
     */
    public static long getTimeInterval(String dateStr, int cycleLength, String dateFormat) {
        Calendar calendar = Calendar.getInstance();
        // 获取当前时间的毫秒值
        long currentTimeMillis = calendar.getTimeInMillis();
        try {
            // 设置为下一次提醒时间
            calendar.setTime(new SimpleDateFormat(dateFormat, Locale.CHINA).parse(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 如果提醒时间过期，往后推算一个周期
        long interval = calendar.getTimeInMillis() - currentTimeMillis;
        while (interval <= 0) {
            dateStr = getSpecifiedDayBefore(dateStr, dateFormat, -cycleLength);
            getTimeInterval(dateStr, cycleLength, dateFormat);
        }
        return interval;
    }

    /**
     * 将年月日格式的日期字符串分割为月日
     *
     * @param dateStr   年月日字符串
     * @param separator 分割符
     * @return
     */
    public static String getDateFormatMd(String dateStr, String separator) {
        String[] split = dateStr.split(" ");
        String[] strings = split[0].split(separator);
        if (strings.length >= 3) {
            return strings[1] + separator + strings[2];
        }
        return dateStr;
    }

    /**
     * 根据传入的时间差毫秒值，计算单位（天、小时、分钟）和值
     *
     * @param timeInterval
     * @return
     */
    public static int[] getIntervalTypeAndValue(long timeInterval) {
        int[] ints = new int[2];
        if (timeInterval / DAY > 0) {
            // 大于1天
            ints[0] = UNIT_DAY;
            ints[1] = (int) (timeInterval / DAY);
        } else if (timeInterval / HOUR > 0) {
            // 不足1天，大于1小时
            ints[0] = UNIT_HOER;
            ints[1] = (int) (timeInterval / HOUR);
        } else {
            // 不足1小时
            ints[0] = UNIT_MINUTE;
            ints[1] = (int) (timeInterval / MINUTE);
        }
        return ints;
    }

    /**
     * 获取本周周一日期
     *
     * @return
     */
    public static Date getMondayOfThisWeek() {
        Calendar calendar = Calendar.getInstance();
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0)
            day_of_week = 7;
        calendar.add(Calendar.DATE, 1 - day_of_week);
        return calendar.getTime();
    }

    /**
     * 获取日期所在周周一日期
     *
     * @return
     */
    public static Date getMondayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0)
            day_of_week = 7;
        calendar.add(Calendar.DATE, 1 - day_of_week);
        return calendar.getTime();
    }

    /**
     * 获取日期所在周周日日期
     *
     * @return
     */
    public static Date getSundayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DATE, 1 - day_of_week);
        return calendar.getTime();
    }

    /**
     * 获取日期所在周上周周六日期
     *
     * @return
     */
    public static Date getSaturdayOfLastWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
        if (day_of_week == 7) {
            day_of_week = 0;
        }
        calendar.add(Calendar.DATE, -day_of_week);
        return calendar.getTime();
    }

    /**
     * 获取日期所在周周日日期(周日为一周开始日)
     *
     * @return
     */
    public static Date getSundayOfNextWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0)
            day_of_week = 7;
        calendar.add(Calendar.DATE, 7 - day_of_week);
        return calendar.getTime();
    }

    /**
     * 获取上一周的周一
     *
     * @param date
     * @return
     */
    public static Date getMondayOfLastWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0)
            day_of_week = 7;
        calendar.add(Calendar.DATE, -day_of_week - 6);
        return calendar.getTime();
    }

    /**
     * 获取本周下一周的周一
     *
     * @param date
     * @return
     */
    public static Date getMondayOfNextWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0)
            day_of_week = 7;
        calendar.add(Calendar.DATE, 8 - day_of_week);
        return calendar.getTime();
    }

    /**
     * 获取当前月的第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, 1);
        return calendar.getTime();
    }

    /**
     * 获取上个月的第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfLastMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DATE, 1);
        return calendar.getTime();
    }

    /**
     * 获取上个月的最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfLastMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, 1);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    /**
     * 获取下个月的第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfNextMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DATE, 1);
        return calendar.getTime();
    }

    /**
     * 获取当前月的最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DATE, 1);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    /**
     * 获取某月的最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfYearMonth(Date date) {
        Date todayDate = getTodayDate();
        if (date.before(todayDate)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DATE, 1);
            calendar.add(Calendar.DATE, -1);
            return calendar.getTime();
        } else {
            return todayDate;
        }
    }

    /**
     * 获取每年1月的字符串
     *
     * @param year
     * @return 格式 2020/01
     */
    public static String getFirstMonthStr(int year) {
        return year + "/01";
    }

    /**
     * 获取当前月的第一天日期
     *
     * @return
     */
    public static Date getFirstDayOfCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 1);
        return calendar.getTime();
    }

    /**
     * 将日期转换成指定格式
     *
     * @param date
     * @param format
     * @return
     */
    public static String format(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        return sdf.format(date);
    }

    /**
     * 将日期转换成指定格式
     *
     * @param calendar
     * @param format
     * @return
     */
    public static String format(Calendar calendar, String format) {
        return format(calendar.getTime(), format);
    }

    /**
     * 将时间戳转换成指定格式
     *
     * @param timeMillis 时间戳（毫秒值）
     * @param format
     * @return
     */
    public static String format(long timeMillis, String format) {
        return format(new Date(timeMillis), format);
    }

    /**
     * 获取指定日期前xx天的日期
     *
     * @param date
     * @param days
     * @return
     */
    public static Date getSpecifiedDayBefore(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -days);
        return calendar.getTime();
    }


    /**
     * 将yyyy/MM/dd HH:ss转化为dd/MM/yyyy HH:ss格式的日期字符串
     * 备注：dd/MM/yyyy HH:ss格式为国外大部分国家的日期格式
     *
     * @param dateStr
     * @return
     */
    private static String getDmyDate(String dateStr, String splitRule) {
        //开始倒序业务--yyyy/MM/dd HH:ss-->dd/MM/yyyy HH:ss
        StringBuilder dateStrRe = new StringBuilder("");
        try {
            //处理的日期为年月日时分
            //年月日与时分用“ ”隔开，时分用“:”隔开
            if (dateStr.contains(" ") && dateStr.contains(":")) {
                String[] dateArrs = dateStr.split(" ");
                //年月日时间
                String ymd = dateArrs[0];
                String[] ymdArrs = ymd.split(splitRule);
                //倒序
                for (int i = ymdArrs.length - 1; i >= 0; i--) {
                    if (i != 0) {
                        dateStrRe.append(ymdArrs[i]).append(splitRule);
                    } else {
                        dateStrRe.append(ymdArrs[i]);
                    }
                }
                dateStrRe.append(" ").append(dateArrs[1]);
                //处理的日期为年月日或年月或月日
            } else if (!dateStr.contains(" ") && !dateStr.contains(":")) {
                String[] ymdArrs = dateStr.split(splitRule);
                //倒序
                for (int i = ymdArrs.length - 1; i >= 0; i--) {
                    if (i != 0) {
                        dateStrRe.append(ymdArrs[i]).append(splitRule);
                    } else {
                        dateStrRe.append(ymdArrs[i]);
                    }
                }
            }
            if (TextUtils.isEmpty(dateStrRe)) {
                return dateStr;
            }
            return dateStrRe.toString();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("xiaobing", "时间倒序至dd/MM/yyyy HH:ss转换异常-->" + e.getMessage());
            return dateStr;
        }
    }

    /**
     * 将yyyy/MM/dd HH:ss转化为 MM/dd/yyyy HH:ss格式的日期字符串
     * 备注：MM/dd/yyyy HH:ss格式为国外较少部分国家的日期格式
     * 如：us-美国、es-pa(巴拿马)、es-pr(波多黎各-->美国自由邦)
     *
     * @param dateStr
     * @param splitRule
     * @return
     */
    private static String getMdyDate(String dateStr, String splitRule) {
        //开始倒序业务--yyyy/MM/dd HH:ss-->MM/dd/yyyy HH:ss
        StringBuilder dateStrRe = new StringBuilder("");
        try {
            //处理的日期为年月日时分
            //年月日与时分用“ ”隔开，时分用“:”隔开
            if (dateStr.contains(" ") && dateStr.contains(":")) {
                String[] dateArrs = dateStr.split(" ");
                //年月日时间
                String ymd = dateArrs[0];
                String[] ymdArrs = ymd.split(splitRule);
                //倒序--将年放置在月日后
                dateStrRe.append(ymdArrs[1]).append(splitRule).append(ymdArrs[2]).append(splitRule).append(ymdArrs[0]);
                //加上时分
                dateStrRe.append(" ").append(dateArrs[1]);
                //处理的日期为年月日或年月或月日
            } else if (!dateStr.contains(" ") && !dateStr.contains(":")) {
                String[] ymdArrs = dateStr.split(splitRule);
                //原格式为年月日
                if (ymdArrs.length == 3) {
                    //倒序--将年放置在月日后
                    dateStrRe.append(ymdArrs[1]).append(splitRule).append(ymdArrs[2]).append(splitRule).append(ymdArrs[0]);
                    //原格式为年月或月日
                } else if (ymdArrs.length == 2) {
                    //原格式为年月--需倒置
                    if (ymdArrs[0].length() == 4) {
                        dateStrRe.append(ymdArrs[1]).append(splitRule).append(ymdArrs[0]);
                        //月日格式无需倒置
                    } else {
                        return dateStr;
                    }
                } else {
                    return dateStr;
                }
            }
            if (TextUtils.isEmpty(dateStrRe)) {
                return dateStr;
            }
            return dateStrRe.toString();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("xiaobing", "时间倒序至MM/dd/yyyy HH:ss转换异常-->" + e.getMessage());
            return dateStr;
        }
    }

    /**
     * 获取一个月的天数
     *
     * @param date
     * @return
     */
    public static int getDaysOfMonth(Date date) {
        Date lastDayOfMonth = getLastDayOfMonth(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastDayOfMonth);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }


    /**
     * 判断传入的时间是否在当前时间之前，返回boolean true: 过期 false: 还没过期
     *
     * @param date
     * @return
     */
    public static boolean isExpire(Date date) {
        if (date == null) return false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.CHINA);
        String dateStr = sdf.format(new Date());
        Date curDate = null;
        try {
            curDate = sdf.parse(dateStr);
            if (curDate.before(date)) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 获取指定日期间所有日期的集合，包含开始、结束日期
     *
     * @param startDate
     * @param endDate
     * @return
     * @throws ParseException
     */
    public static List<String> getDates(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YMD, Locale.CHINA);
        //保存日期的集合 
        List<String> list = new ArrayList<String>();
        try {
            Date date_end = sdf.parse(endDate);
            Date date = sdf.parse(startDate);
            //用Calendar 进行日期比较判断
            Calendar calendar = Calendar.getInstance();
            while (date.getTime() <= date_end.getTime()) {
                list.add(sdf.format(date));
                calendar.setTime(date);
                //增加一天 放入集合
                calendar.add(Calendar.DATE, 1);
                date = calendar.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取指定月的所有日期列表
     * 格式:yyyy-MM-dd
     */
    public static List<String> getDateListByMonth(int year, int month) {
        List<String> result = new ArrayList<>();
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < max; i++) {
            result.add(format(calendar.getTime(), DATE_FORMAT_YMD));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return result;
    }


    /**
     * 时间格式化
     *
     * @param time 66
     * @return 00:01:06
     */
    public static String computeTimeHMPoint(long time) {

        int hour = (int) (time / 60 / 60);
        StringBuffer result = new StringBuffer();
        time -= hour * 60 * 60;
        result.append(String.format("%02d", hour));
        result.append(".");
        int minute = (int) (time / 60);
        result.append(String.format("%02d", minute));


        return result.toString();
    }

    /**
     * 时间格式化
     *
     * @param time 66
     * @return 1.06
     */
    public static String computeTimeHDMPoint(long time) {

        int hour = (int) (time / 60 / 60);
        StringBuffer result = new StringBuffer();
        time -= hour * 60 * 60;
        result.append(String.format(Locale.CHINA, "%d", hour));
        result.append(".");
        int minute = (int) (time / 60);
        result.append(String.format(Locale.CHINA, "%02d", minute));


        return result.toString();
    }

    /**
     * 根据传入时间获取xx秒后的时间
     *
     * @param date
     * @param afterSecond 秒
     */
    public static Date getTimeAfterSeconds(Date date, int afterSecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, afterSecond);
        return calendar.getTime();
    }

    /**
     * 将时间戳转换为Date对象
     *
     * @param timeMills
     * @return
     */
    public static Date formatTimeMills2Date(long timeMills) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMills);
        return calendar.getTime();
    }

    /**
     * 获取当前时分
     *
     * @return
     */
    public static int[] getCurrentHM() {
        Calendar calendar = Calendar.getInstance();
        return new int[]{calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)};
    }

    /**
     * 获取某年的最后一天
     *
     * @param year
     * @return
     */
    public static Date getLastDateOfYear(int year) {
        int[] currentDate = getCurrentDate();
        if (currentDate[0] == year) {
            return getTodayDate();
        } else {
            return getDate(year, 12, 31);
        }
    }

    /**
     * 通过实践返回时间戳
     *
     * @param dateStr yyyy-MM-dd
     * @return
     */
    public static Date getLongFromDate2(String dateStr) {
        try {
            return new SimpleDateFormat(DATE_FORMAT_YMD, Locale.CHINA).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static Date utcToLocalDate(String utcTime, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date utcDate, localDate = null;
        try {
            utcDate = sdf.parse(utcTime);
            sdf.setTimeZone(TimeZone.getDefault());
            String localTime = sdf.format(utcDate);
            localDate = sdf.parse(localTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localDate;
    }

    /**
     * <p>Description: 本地时间转化为UTC时间</p>
     *
     * @param localTime
     * @return
     * @author wgs
     * @date 2018年10月19日 下午2:23:43
     */
    public static Date localToUTC(String localTime, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date localDate = null;
        try {
            localDate = sdf.parse(localTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long localTimeInMillis = localDate.getTime();
        /** long时间转换成Calendar */
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(localTimeInMillis);
        /** 取得时间偏移量 */
        int zoneOffset = calendar.get(Calendar.ZONE_OFFSET);
        /** 取得夏令时差 */
        int dstOffset = calendar.get(Calendar.DST_OFFSET);
        /** 从本地时间里扣除这些差量，即可以取得UTC时间*/
        calendar.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        /** 取得的时间就是UTC标准时间 */
        Date utcDate = new Date(calendar.getTimeInMillis());
        return utcDate;
    }

    /**
     * 通过时间戳获取年份
     *
     * @param timeMillis 时间戳（毫秒值）
     * @return 年份
     */
    public static int byTimeMillisGetYear(Long timeMillis) {
        Date date = new Date(timeMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        return Integer.valueOf(simpleDateFormat.format(date));
    }

    /**
     * 通过时间戳获取月份
     *
     * @param timeMillis 时间戳（毫秒值）
     * @return 月份
     */
    public static int byTimeMillisGetMonth(Long timeMillis) {
        Date date = new Date(timeMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String month = simpleDateFormat.format(date);
        return Integer.valueOf(month.substring(5, 7));
    }

    /**
     * 通过时间戳获取天
     *
     * @param timeMillis 时间戳（毫秒值）
     * @return 年份
     */
    public static int byTimeMillisGetDay(Long timeMillis) {
        Date date = new Date(timeMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
        String day = simpleDateFormat.format(date);
        return Integer.valueOf(day);
    }


}
