package test.com.ido.utils;

import android.util.Pair;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author tianwei
 * @date 2022/11/18
 * @time 17:47
 * 用途:
 */
public class WorldTimeUtils {

    public static final String WORLD_TIME_CITY_PREFIX = "world_time_city_";
    public static final String WORLD_TIME_COUNTRY_PREFIX = "world_time_country_";
    public static final String WORLD_TIME_TIMEZONE_PREFIX = "world_time_timezone_";
    public static final String WORLD_TIME_LATITUDE_PREFIX = "world_time_latitude_";
    public static final String WORLD_TIME_LONGITUDE_PREFIX = "world_time_longitude_";
    public static final String WORLD_TIME_ABBREVIATION_PREFIX = "world_time_Abbreviation_";

    /**
     * 获取时区城市名称
     *
     * @param id
     * @return
     */
    public static int getCityName(int id) {
        return ResUtils.getStringResId(WORLD_TIME_CITY_PREFIX + id);
    }

    /**
     * 获取时区国家名称
     *
     * @param id
     * @return
     */
    public static int getCountryName(int id) {
        return ResUtils.getStringResId(WORLD_TIME_COUNTRY_PREFIX + id);
    }

    /**
     * 获取时区id
     *
     * @param id
     * @return
     */
    public static String getTimezone(int id) {
        return ResUtils.getString(WORLD_TIME_TIMEZONE_PREFIX + id);
    }

    /**
     * 获取经纬度
     *
     * @param id
     * @return first：latitude，second：longitude
     */
    public static Pair<Double, Double> getLatLon(int id) {
        return new Pair<>(Double.parseDouble(ResUtils.getString(WORLD_TIME_LATITUDE_PREFIX + id)), Double.parseDouble(ResUtils.getString(WORLD_TIME_LONGITUDE_PREFIX + id)));
    }

    /**
     * 获取城市简称
     *
     * @param id
     * @return
     */
    public static String getCityAbbreviation(int id) {
        return ResUtils.getString(WORLD_TIME_ABBREVIATION_PREFIX + id);
    }

    /**
     * 获取日出时间
     */
    public static Pair<String, String> getSunRiseTime(Double longitude, Double latitude, Calendar dateTime) {
        String sunRise = SunRiseSetUtils.getSunrise(new BigDecimal(longitude), new BigDecimal(latitude), dateTime.getTime(), dateTime.getTimeZone());
        String[] time = sunRise.split(":");
        return new Pair<>(time[0], time[1]);
    }

    /**
     * 获取日落时间
     */
    public static Pair<String, String> getSunSetTime(Double longitude, Double latitude, Calendar dateTime) {
        String sunSet = SunRiseSetUtils.getSunset(new BigDecimal(longitude), new BigDecimal(latitude), dateTime.getTime(), dateTime.getTimeZone());
        String[] time = sunSet.split(":");
        return new Pair<>(time[0], time[1]);
    }
}
