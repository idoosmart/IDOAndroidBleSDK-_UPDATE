package test.com.ido.utils;

import static java.lang.Math.abs;

import android.text.format.DateFormat;

import com.ido.ble.common.TimeUtil;

import test.com.ido.APP;
import test.com.ido.R;

import java.text.DecimalFormat;
import java.util.*;

/**
 * @author tianwei
 * @date 2021/7/29
 * @time 17:46
 * 用途:
 */
public class CalendarUtils {

    /**
     * 获取本机时区
     */
    public static TimeZone getLocalTimezone() {
        return TimeZone.getDefault();
    }

    /**
     * 计算时差
     *
     * @param local        本地id
     * @param comparedZone 对比的id
     * @return 返回：今天 +0小时
     */
    public static String getTimezoneDifferenceStr(TimeZone local, TimeZone comparedZone) {
        int timeDiff = getTimezoneDifferenceInHour(local, comparedZone);
        int dayType = getTypeOfDay(Calendar.getInstance(local), Calendar.getInstance(comparedZone));
        return String.format(APP.getAppContext().getResources().getString(R.string.world_time_time_diff), getTypeOfDayStr(dayType), (timeDiff >= 0) ? "+" : "-", abs(timeDiff));
    }

    /**
     * 计算时差
     *
     * @param local        本地id
     * @param comparedZone 对比的id
     * @return 返回时差，单位小时
     */
    public static int getTimezoneDifferenceInHour(TimeZone local, TimeZone comparedZone) {
        return getTimezoneDifferenceInMillSec(local, comparedZone) / (1000 * 60 * 60);
    }

    /**
     * 计算时差
     *
     * @param local        本地id
     * @param comparedZone 对比的id
     * @return 返回时差，单位小时
     */
    public static int getTimezoneDifferenceInMillSec(TimeZone local, TimeZone comparedZone) {
        Date now = new Date();
        int currentOffsetFromUTC = local.getRawOffset() + (local.inDaylightTime(now) ? local.getDSTSavings() : 0);
        int serverOffsetFromUTC =
                comparedZone.getRawOffset() + ((comparedZone.inDaylightTime(now)) ? comparedZone.getDSTSavings() : 0);
        return (serverOffsetFromUTC - currentOffsetFromUTC);
    }

    public static int getTimezoneOffsetInMin(TimeZone zone) {
        return (zone.getRawOffset() + ((zone.inDaylightTime(new Date())) ? zone.getDSTSavings() : 0)) / (60 * 1000);
    }

    /**
     * 获取时间，格式：根据系统时间制式显示，12小时制：上午 11:33 ，24小时制：11:33
     */
    public static String getTime(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int am_pm = calendar.get(Calendar.AM_PM);
        if (isTimeFormat24()) {//24小时制
            return formatTime(hour) + ":" + formatTime(minute);
        } else {
            return String.format(APP.getAppContext().getResources().getString((am_pm == Calendar.AM) ? R.string.world_time_time_am
                    : R.string.world_time_time_pm), formatTime(format24To12(hour)), formatTime(minute));
        }

    }

    public static int format24To12(int hour) {
        int h = hour % 12;
        if (hour == 12) {
            h = 12;
        } else {
            h = h == 0 ? 12 : hour % 12;
        }
        return h;
    }

    public static int format12To24(int hour, boolean isAm) {
        int h;
        if (isAm) {
            h = hour;
        } else {
            h = hour == 12 ? 0 : hour + 12;
        }
        return h;
    }

    /**
     * 是否是24小时制
     */
    public static boolean isTimeFormat24() {
        return DateFormat.is24HourFormat(APP.getAppContext());
    }

    /**
     * 时间格式化，不足两位补0
     */
    public static String formatTime(int time) {
        DecimalFormat formatter = new DecimalFormat("00");
        return formatter.format(time);
    }

    /**
     * 获取日期，格式：6/20 星期一
     */
    public static String getWorldTimeDate(Calendar calendar) {
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        return String.format(APP.getAppContext().getResources().getString(R.string.world_time_date_week), month, day, getWeek(week));
    }

    /**
     * 获取时间
     */
    public static String getDate(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "-" + month + "-" + day;
    }

    /**
     * 获取星期名称
     */
    private static String getWeek(int week) {
        int weekResId;
        switch (week) {
            case Calendar.SUNDAY:
                weekResId = R.string.public_time_sunday;
                break;
            case Calendar.MONDAY:
                weekResId = R.string.public_time_monday;
                break;
            case Calendar.TUESDAY:
                weekResId = R.string.public_time_tuesday;
                break;
            case Calendar.WEDNESDAY:
                weekResId = R.string.public_time_wednesday;
                break;
            case Calendar.THURSDAY:
                weekResId = R.string.public_time_thursday;
                break;
            case Calendar.FRIDAY:
                weekResId = R.string.public_time_friday;
                break;
            case Calendar.SATURDAY:
                weekResId = R.string.public_time_saturday;
                break;
            default:
                weekResId = -1;
        }
        return (weekResId > 0) ? APP.getAppContext().getResources().getString(weekResId) : "";
    }

    /**
     * 获取日期的列表，0今天、-1昨天、1明天，2未知
     * api 26之后可以考虑LocalDate
     */
    public static int getTypeOfDay(Calendar local, Calendar comparedCalendar) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, comparedCalendar.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, comparedCalendar.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, comparedCalendar.get(Calendar.DAY_OF_MONTH));

        Calendar today = Calendar.getInstance();
        today.set(Calendar.YEAR, local.get(Calendar.YEAR));
        today.set(Calendar.MONTH, local.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH, local.get(Calendar.DAY_OF_MONTH));
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Calendar yesterday = Calendar.getInstance();
        yesterday.set(Calendar.YEAR, today.get(Calendar.YEAR));
        yesterday.set(Calendar.MONTH, today.get(Calendar.MONTH));
        yesterday.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH) - 1);
        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        yesterday.set(Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.set(Calendar.YEAR, today.get(Calendar.YEAR));
        tomorrow.set(Calendar.MONTH, today.get(Calendar.MONTH));
        tomorrow.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH) + 1);
        tomorrow.set(Calendar.HOUR_OF_DAY, 0);
        tomorrow.set(Calendar.MINUTE, 0);
        tomorrow.set(Calendar.SECOND, 0);

        Calendar dayAfterTomorrow = Calendar.getInstance();
        dayAfterTomorrow.set(Calendar.YEAR, today.get(Calendar.YEAR));
        dayAfterTomorrow.set(Calendar.MONTH, today.get(Calendar.MONTH));
        dayAfterTomorrow.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH) + 2);
        dayAfterTomorrow.set(Calendar.HOUR_OF_DAY, 0);
        dayAfterTomorrow.set(Calendar.MINUTE, 0);
        dayAfterTomorrow.set(Calendar.SECOND, 0);

        if (calendar.after(today) && calendar.before(tomorrow)) {
            return 0;
        } else if (calendar.before(today) && calendar.after(yesterday)) {
            return -1;
        } else if (calendar.before(dayAfterTomorrow) && calendar.after(tomorrow)) {
            return 1;
        } else return 2;
    }

    public static String getTypeOfDayStr(int dayType) {
        int resId;
        switch (dayType) {
            case 0:
                resId = R.string.public_time_today;
                break;
            case -1:
                resId = R.string.public_time_yesterday;
                break;
            case 1:
                resId = R.string.public_time_tomorrow;
                break;
            default:
                resId = -1;
        }
        return (resId > 0) ? APP.getAppContext().getResources().getString(resId) : "";
    }
}