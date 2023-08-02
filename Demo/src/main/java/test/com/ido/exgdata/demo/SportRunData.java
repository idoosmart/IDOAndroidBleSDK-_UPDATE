package test.com.ido.exgdata.demo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.Date;

/**
 */
@Entity
public class SportRunData implements Serializable{
    public static final long serialVersionUID=1l;
    public static final int DATA_FROM_APP = 1;
    public static final int DATA_FROM_DEVICE = 2;
    public static final int DATA_WAIT_MERGE = 3;// app端发起轨迹运动时，app先保存数据，等到手环的数据同步回来后合并再改成2。
    private boolean isUploaded;
    //---------------以下为数据区-------------------
    @Id(autoincrement = true)
    private Long activityId;

    private String macAddress;//设备mac地址
    // 开始的年月日时分秒
    private int year;//年
    private int month;//月
    private int day;//日
    private int hour;//时
    private int minute;//分
    private int second;//秒

    private int hr_data_interval_minute;

    private int type; //运动类型
    private int step; //步数
    private int durations; //时长
    private int calories;  //卡路里
    private int distance;  //距离

    private int avg_hr_value;//平均心率
    private int max_hr_value;//最大心率
    private int warmUpMins; //热身运动分钟数
    private int burn_fat_mins; //脂肪燃烧时长
    private int aerobic_mins;  //有氧运动
    private int anaerobicMins; //无氧燃烧分钟数据
    private int limit_mins;		//极限锻炼时长


    /**
     * 数据来自哪里
     */
    private int dataFrom = DATA_FROM_DEVICE;
    private String hr_data_vlaue_json;
    private Date date;//时间戳
    @Generated(hash = 1689298063)
    public SportRunData(boolean isUploaded, Long activityId, String macAddress, int year,
            int month, int day, int hour, int minute, int second, int hr_data_interval_minute,
            int type, int step, int durations, int calories, int distance, int avg_hr_value,
            int max_hr_value, int warmUpMins, int burn_fat_mins, int aerobic_mins,
            int anaerobicMins, int limit_mins, int dataFrom, String hr_data_vlaue_json,
            Date date) {
        this.isUploaded = isUploaded;
        this.activityId = activityId;
        this.macAddress = macAddress;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.hr_data_interval_minute = hr_data_interval_minute;
        this.type = type;
        this.step = step;
        this.durations = durations;
        this.calories = calories;
        this.distance = distance;
        this.avg_hr_value = avg_hr_value;
        this.max_hr_value = max_hr_value;
        this.warmUpMins = warmUpMins;
        this.burn_fat_mins = burn_fat_mins;
        this.aerobic_mins = aerobic_mins;
        this.anaerobicMins = anaerobicMins;
        this.limit_mins = limit_mins;
        this.dataFrom = dataFrom;
        this.hr_data_vlaue_json = hr_data_vlaue_json;
        this.date = date;
    }
    @Generated(hash = 1400225533)
    public SportRunData() {
    }
    public boolean getIsUploaded() {
        return this.isUploaded;
    }
    public void setIsUploaded(boolean isUploaded) {
        this.isUploaded = isUploaded;
    }
    public Long getActivityId() {
        return this.activityId;
    }
    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
    public String getMacAddress() {
        return this.macAddress;
    }
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    public int getYear() {
        return this.year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public int getMonth() {
        return this.month;
    }
    public void setMonth(int month) {
        this.month = month;
    }
    public int getDay() {
        return this.day;
    }
    public void setDay(int day) {
        this.day = day;
    }
    public int getHour() {
        return this.hour;
    }
    public void setHour(int hour) {
        this.hour = hour;
    }
    public int getMinute() {
        return this.minute;
    }
    public void setMinute(int minute) {
        this.minute = minute;
    }
    public int getSecond() {
        return this.second;
    }
    public void setSecond(int second) {
        this.second = second;
    }
    public int getHr_data_interval_minute() {
        return this.hr_data_interval_minute;
    }
    public void setHr_data_interval_minute(int hr_data_interval_minute) {
        this.hr_data_interval_minute = hr_data_interval_minute;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getStep() {
        return this.step;
    }
    public void setStep(int step) {
        this.step = step;
    }
    public int getDurations() {
        return this.durations;
    }
    public void setDurations(int durations) {
        this.durations = durations;
    }
    public int getCalories() {
        return this.calories;
    }
    public void setCalories(int calories) {
        this.calories = calories;
    }
    public int getDistance() {
        return this.distance;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }
    public int getAvg_hr_value() {
        return this.avg_hr_value;
    }
    public void setAvg_hr_value(int avg_hr_value) {
        this.avg_hr_value = avg_hr_value;
    }
    public int getMax_hr_value() {
        return this.max_hr_value;
    }
    public void setMax_hr_value(int max_hr_value) {
        this.max_hr_value = max_hr_value;
    }
    public int getBurn_fat_mins() {
        return this.burn_fat_mins;
    }
    public void setBurn_fat_mins(int burn_fat_mins) {
        this.burn_fat_mins = burn_fat_mins;
    }
    public int getAerobic_mins() {
        return this.aerobic_mins;
    }
    public void setAerobic_mins(int aerobic_mins) {
        this.aerobic_mins = aerobic_mins;
    }
    public int getLimit_mins() {
        return this.limit_mins;
    }
    public void setLimit_mins(int limit_mins) {
        this.limit_mins = limit_mins;
    }
    public int getDataFrom() {
        return this.dataFrom;
    }
    public void setDataFrom(int dataFrom) {
        this.dataFrom = dataFrom;
    }
    public String getHr_data_vlaue_json() {
        return this.hr_data_vlaue_json;
    }
    public void setHr_data_vlaue_json(String hr_data_vlaue_json) {
        this.hr_data_vlaue_json = hr_data_vlaue_json;
    }
    public Date getDate() {
        return this.date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public int getWarmUpMins() {
        return this.warmUpMins;
    }
    public void setWarmUpMins(int warmUpMins) {
        this.warmUpMins = warmUpMins;
    }
    public int getAnaerobicMins() {
        return this.anaerobicMins;
    }
    public void setAnaerobicMins(int anaerobicMins) {
        this.anaerobicMins = anaerobicMins;
    }

    @Override
    public String toString() {
        return "ProHealthActivity{" +
                "isUploaded=" + isUploaded +
                ", activityId=" + activityId +
                ", macAddress='" + macAddress + '\'' +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hour=" + hour +
                ", minute=" + minute +
                ", second=" + second +
                ", hr_data_interval_minute=" + hr_data_interval_minute +
                ", type=" + type +
                ", step=" + step +
                ", durations=" + durations +
                ", calories=" + calories +
                ", distance=" + distance +
                ", avg_hr_value=" + avg_hr_value +
                ", max_hr_value=" + max_hr_value +
                ", warmUpMins=" + warmUpMins +
                ", burn_fat_mins=" + burn_fat_mins +
                ", aerobic_mins=" + aerobic_mins +
                ", anaerobicMins=" + anaerobicMins +
                ", limit_mins=" + limit_mins +
                ", dataFrom=" + dataFrom +
                ", hr_data_vlaue_json='" + hr_data_vlaue_json + '\'' +
                ", date=" + date +
                '}';
    }
}
