package test.com.ido.localdata;

import java.io.Serializable;

/**
 * @author: zhouzj
 * @date: 2017/11/14 14:53
 */

public class DataItemQueryType implements Serializable{
    public static final String INTENT_EXTRA_FLAG = "DataItemQueryType";

    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;
    public int second;

}
