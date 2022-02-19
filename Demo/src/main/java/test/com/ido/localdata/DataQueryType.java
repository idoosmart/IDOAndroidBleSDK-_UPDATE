package test.com.ido.localdata;

import java.io.Serializable;

/**
 * @author: zhouzj
 * @date: 2017/11/13 15:17
 */
class DataQueryType implements Serializable{

    public static final int DATA_TYPE_ACTIVITY = 1;
    public static final int DATA_TYPE_SPORT = 2;
    public static final int DATA_TYPE_HEART_RATE = 3;
    public static final int DATA_TYPE_SLEEP = 4;
    public static final int DATA_TYPE_BLOOD = 5;

    public static final int QUERY_TYPE_ONE_DAY = 11;
    public static final int QUERY_TYPE_PERIOD = 12;
    public static final int QUERY_TYPE_WEEK = 13;

    public static final String INTENT_EXTRA_FLAG = "DataQueryType";

    public int dataType;
    public int queryType;

    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;
    public int second;
    public int endYear;
    public int endMonth;
    public int endDay;

    public int week;

}
