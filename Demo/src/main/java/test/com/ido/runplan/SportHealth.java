package test.com.ido.runplan;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ido.ble.protocol.model.Sport100Type;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.List;

import test.com.ido.exgdata.demo.DaoSession;
import test.com.ido.runplan.data.ConvertGps;
import test.com.ido.runplan.data.ConvertItem;
import test.com.ido.runplan.data.ConvertListColorItem;
import test.com.ido.runplan.data.ConvertPaceItem;
import test.com.ido.runplan.data.ConvertRealPaceItem;
import test.com.ido.runplan.data.ConvertSwimSwolf;
import test.com.ido.runplan.data.SportItem;
import test.com.ido.runplan.data.SportItemPace;
import test.com.ido.runplan.data.SportRealTimePace;
import test.com.ido.runplan.data.SportSwimSwolf;
import test.com.ido.utils.GsonUtil;


/**
 * Copyright (c) 2019 深圳市爱都科技有限公司. All rights reserved.
 *
 * @Author: xyb
 * @CreateDate: 2020-05-06 11:19
 * @description 运动信息实体类
 */
@Entity(
        active = true
)
public class SportHealth implements Serializable, Parcelable {
    public static final long serialVersionUID = 1L;
    /**
     * 运动中跳转到运动详情为0，运动记录跳转到运动详情为1；
     */
    public static final int DATA_FROM_RUN = 0;
    public static final int DATA_FROM_RECORD = 1;
    /**
     * 数据来源必须为1:手机APP,或者2: 终端手环手表设备 3、手环手机连接发起运动
     */
    public static final int DATA_FROM_APP = 1;
    public static final int DATA_FROM_DEVICE = 2;
    public static final int DATA_FROM_APP_AND_DEVICE = 3;
    public static final int DATA_FROM_DEVICE_AND_APP = 4;
    /**
     * 有无轨迹：0无轨迹 1有轨迹
     */
    public static final int DATA_IS_N0_LOCUS = 0;
    /**
     * 有轨迹
     */
    public static final int DATA_IS_LOCUS = 1;

    /**
     * (1IOS 2Android 3Other) 数据来源App系统
     */
    public static final int DATA_SOURCE_OS_ANDROID = 2;
    /**
     * 数据来源ios
     */
    public static final int DATA_SOURCE_OS_IOS = 1;

    /**
     * （0无轨迹，1 APPGPS轨迹来源，2 固件GPS轨迹来源）
     */
    public static final int DATA_GPS_SOURCE_TYPE_NO = 0;
    /**
     * gps的来源
     */
    public static final int DATA_GPS_SOURCE_TYPE_APP = 1;
    /**
     * gps数据来源设备
     */
    public static final int DATA_GPS_SOURCE_TYPE_DEVICE = 2;

    /**
     * 接口时返回的sid值
     */
    private String sid;
    /**
     * 运动时间
     */
    @SerializedName("datetime")
    private String dateTime;
    /**
     * 运动类型
     */
    @SerializedName("type")
    private int type;
    /**
     * 游泳类型
     */
    private int subType;
    /**
     * 跑步计划类型
     */
    @SerializedName("actType")
    public int actType;
    /**
     * 运动总时长
     */
    private int totalSeconds;
    /**
     * 运动总消耗卡路里
     */
    private int numCalories;
    /**
     * 运动总步数
     */
    private int numSteps;
    /**
     * 运动总距离
     */
    private int distance;
    /**
     * 开始运动时间
     */
    private String startTime;
    /**
     * 结束运动时间
     */
    private String endTime;
    /**
     * 运动目标类型
     */
    private int targetType;
    /**
     * 运动目标值
     */
    private int targetValue;
    /**
     * 热身运动时长
     */
    private int warmupSeconds;
    /**
     * 燃脂运动时长
     */
    private int burnFatSeconds;
    /**
     * 有氧运动时长
     */
    private int aerobicSeconds;
    /**
     * 无氧运动时长
     */
    @SerializedName("anaerobicSeconds")
    private int anaerobicSecond;
    /**
     * 极限运动时长
     */
    @SerializedName("extremeSeconds")
    private int extremeSecond;
    /**
     * mac 地址
     */
    private String sourceMac;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 数据来源必须为1:手机APP,或者2: 终端手环手表设备 3、手环手机连接发起运动
     */
    private int sourceType;
    @SerializedName("training_offset")
    public int training_offset;//训练的课程日期偏移 从零开始
    /**
     * 最小心率
     */
    private int minHrValue;
    /**
     * 最大心率值
     */
    private int maxHrValue;
    /**
     * 平均心率
     */
    private int avgHrValue;
    /**
     * 最慢速度
     */
    private int minSpeed;
    /**
     * 最快速度
     */
    private int maxSpeed;
    /**
     * 平均速度
     */
    private int avgSpeed;
    /**
     * 最小配速 最小值 其实是最大配速
     */
    private int minPace;
    /**
     * 最大配速
     */
    private int maxPace;
    /**
     * 平均配速
     */
    private int avgPace;
    /**
     * 有无轨迹：0无轨迹 1有轨迹
     */
    private int isLocus;
    /**
     * 平均步幅
     */
    private int stepRange;
    /**
     * 最小步频
     */
    private int minRate;
    /**
     * 最大步频
     */
    @SerializedName("maxRate")
    private int stepRateMax;
    /**
     * 平均步频
     */
    @SerializedName("avgRate")
    private int stepRate;

    /**
     * 最大步频
     */
    //private int maxRate;

    /**
     * 泳姿  // 0 : 混合泳; 1 : 自由泳; 2 : 蛙泳; 0x03 : 仰泳; 0x04 : 蝶泳;5 : 其他;
     */
    @SerializedName("swType")
    private int swimmingPosture;

    /**
     * 划水总次数
     */
    @SerializedName("swHitNums")
    private int totalStrokesNumber;

    /**
     * 泳池长度（cm）
     */
    @SerializedName("swPoolLength")
    private int poolDistance;

    /**
     * 游泳趟数
     */
    @SerializedName("swTrips")
    private int trips;

    /**
     * 最佳SWOLF  越低越好
     */
    @SerializedName("minSwolfValue")
    private int bestSWOLF;

    /**
     * 最高SWOLF
     */
    @SerializedName("maxSwolfValue")
    private int maxSwolf;

    /**
     * 平均SWOLF  平均评分
     */
    @SerializedName("avgSwolfValue")
    private int averageSWOLF;

    /**
     * (1IOS 2Android 3Other) 数据来源App系统
     */
    private int sourceOs;

    @Convert(converter = ConvertGps.class, columnType = String.class)
    private SportGps gps;

    /**
     * 心率数据
     */
    @Convert(converter = ConvertItem.class, columnType = String.class)
    private SportItem heartrate;

    /**
     * 步频集合
     */
    @Convert(converter = ConvertItem.class, columnType = String.class)
    private SportItem rate;

    /**
     * 步幅集合
     */
    @Convert(converter = ConvertItem.class, columnType = String.class)
    private SportItem range;

    /**
     * 服务器返的对象
     */
    @Convert(converter = ConvertPaceItem.class, columnType = String.class)
    private SportItemPace pace;

    /**
     * 实时配速
     */
    @Convert(converter = ConvertRealPaceItem.class, columnType = String.class)
    private SportRealTimePace realTimePace;

    /**
     * 服务器接收 游泳效率 游泳配速 游泳频率  样本数据细项
     * "swolf": {
     * "items": [{"duration":123,"strokesNumber":123,"swolf":123,"frequency":123,"speed":123,"differenceTime":123,"stopTime":123}]
     * }
     */
    @Convert(converter = ConvertSwimSwolf.class, columnType = String.class)
    private SportSwimSwolf swolf;
    /**
     * 最大步幅
     */
    @Expose(serialize = false, deserialize = false)
    private int stepRangeMax;
    /**
     * 是否上传到strava(废弃)
     */
    @Expose(serialize = false, deserialize = false)
    @Deprecated
    private boolean isUploadedStrava;
    /**
     * 是否上传到strava
     */
    private int uploadedStrava;
    /**
     * 数据是否上传
     */
    @Expose(serialize = false, deserialize = false)
    private boolean isUploaded;
    //---------------以下为数据区-------------------
    @Id(autoincrement = true)
    @Expose(serialize = false, deserialize = false)
    private Long activityId;
    /**
     * 用户id
     */
    @Expose(serialize = false,deserialize = false)
    private long userId;

    /**
     * 心率数据
     */
    @Expose(serialize = false, deserialize = false)
    private String hr_data_vlaue_json;

    /**
     * 每分钟保存数据集合 json字符串 最大保存6小时
     */
    @Expose(serialize = false, deserialize = false)
    private String stepItem;
    /**
     * 幅度
     */
    @Expose(serialize = false, deserialize = false)
    private String rangeItem;

    /**
     * 心率间隔时间
     */
    @Expose(serialize = false, deserialize = false)
    private int hrDataIntervalMinute;

    /**
     * 最快公里配速
     */
    @Expose(serialize = false, deserialize = false)
    private int fast_km_speed;


    /**
     * 平均游泳划水频率
     */
    @Expose(serialize = false, deserialize = false)
    private int avgFrequency;

    /**
     * 最大游泳划水频率
     */
    @Expose(serialize = false, deserialize = false)
    private int maxFrequency;

    /**
     * 游泳趟数HealthSwimmingDetail数组转json字符串存储
     */
    @Expose(serialize = false, deserialize = false)
    private String swimmingDetailStr;

    /**
     * 心率间隔
     */
    @Expose(serialize = false, deserialize = false)
    private int intervalSecond;
    /**
     * 时间戳 兼容ios的
     */
    private long timestamp;
    /**
     * 轨迹来源
     */
    private int gpsSourceType;

    /**
     * 运动图片上传
     */
    private String icon;

    /**
     * 加载详情
     */
    private boolean isLoadDetail;

    /**
     * 最大摄氧量
     */
    @SerializedName("maximalOxygenUptake")
    private int vo2max;

    /**
     * 恢复时长(小时)
     */
    private int recoverTime;

    /**
     * 恢复日期时间：（2021-12-6 11:20:9）
     */
    @SerializedName("recoveDatetime")
    private String discoverDateTime;

    /**
     * 训练得分(废弃)
     */
    @Deprecated
    private int trainingEffect;

    /**
     * 训练得分
     */
    private float trainingEffectScore;


    /**
     * 是否支持训练效果(废弃)
     */
    @Deprecated
    private boolean isSupportTrain;
    /**
     * 是否支持训练效果
     */
    private int isSupportTrainingEffect;

    /**
     * 累计爬升
     */
    private int cumulativeClimb;
    /**
     * 	累计下降
     */
    private int cumulativeDecline;
    /**
     * 最高海拔
     */
    private double highestAltitude;
    /**
     * 最低海拔
     */
    private double lowestAltitude;

    /**
     * 最大摄氧量等级(需求4131 最大摄氧量优化，固件传值增加等级（固件）,从1开始)
     */
    @SerializedName("maximalOxygenLevel")
    private int grade;
    /**
     * 3d距离 单位m
     */
    @Property(nameInDb = "DISTANCE_3D")
    private Integer distance3d;
    /**
     *平均3d速度 单位km/h
     */
    @Property(nameInDb = "AVG_3D_SPEED")
    @SerializedName("avg3dSpeed")
    private Integer avg_3d_speed;
    /**
     * 平均垂直速度 单位m/h
     */
    @Property(nameInDb = "AVG_VERTICAL_SPEED")
    @SerializedName("avgVerticalSpeed")
    private Integer avg_vertical_speed;
    /**
     * 平均坡度 单位度 0 ~ 90
     */
    @Property(nameInDb = "AVG_SLOPE")
    @SerializedName("avgSlope")
    private Integer avg_slope;
    /**
     * 最高海拔高度 单位米 -500 ~ 9000
     */
    @Property(nameInDb = "MAX_ALTITUDE")
    private Integer max_altitude;
    /**
     * 最低海拔高度 单位米 -500 ~ 9000
     */
    @Property(nameInDb = "MIN_ALTITUDE")
    private Integer min_altitude;
    /**
     * 累计海拔上升 单位米
     */
    @Property(nameInDb = "CUMULATIVE_ALTITUDE_RISE")
    private Integer cumulative_altitude_rise;
    /**
     * 累计海拔下降 单位米
     */
    @Property(nameInDb = "CUMULATIVE_ALTITUDE_LOSS")
    private Integer cumulative_altitude_loss;
    /**
     * 海拔高度详情个数
     */
    @Property(nameInDb = "ALTITUDE_COUNT")
    private Integer altitude_count;
    /**
     * 海拔高度数据 单位米 范围-500~9000 30s一组值
     */
    @Property(nameInDb = "ALTITUDE_ITEM")
    @Convert(converter = ConvertListColorItem.class, columnType = String.class)
    private List<Integer> altitude_item;
    /**
     * 海拔详情
     * 上传数据使用
     */
    @Transient
    public String altitudeDetails;
    /**
     * 热身表现
     */
    @Expose(serialize = false, deserialize = false)
    private int warmUpPerformance;
    /**
     * 动作完成率
     */
    @Expose(serialize = false, deserialize = false)
    private int completionRate;
    /**
     * 心率控制率
     */
    @Expose(serialize = false, deserialize = false)
    private int hrCompletionRate;

    /**
     * 课程内卡路里
     */
    @Expose(serialize = false, deserialize = false)
    private int inClassCalories;
    /**
     * 跑步计划ActionTemp数组转json字符串存储
     */
    @Expose(serialize = false, deserialize = false)
    private String runningPullUp;
    /**
     * 实时配速（5s）
     */
    @Expose(serialize = false, deserialize = false)
    private String currentTimePace;
    /**
     * 实时步频（5s）
     */
    @Expose(serialize = false, deserialize = false)
    private String currentTimeStrideRate;
    /**
     * 实时速度
     */
    @Expose(serialize = false, deserialize = false)
    private String currentTimeSpeed;
    /**
     * 实时配速、实时步频、实时速度时间间隔
     */
    @Expose(serialize = false, deserialize = false)
    private int intervalTimeChart;
    //运动强度
    private String exerciseIntensity;
    /**
     * 平均海拔高度 单位米
     */
    @Property(nameInDb = "avg_altitude")
    @SerializedName("avgAltitude")
    private Integer avg_altitude;
    /**
     * 游泳休息时长
     */
    @Property(nameInDb = "TOTAL_REST_TIME")
    private int total_rest_time;
    //GPS状态 0:无效 1:开启 2:未开启(未开启时展示`距离` 开启则展示`3D距离`)
    @Property(nameInDb = "GPS_STATUS")
    private Integer gpsStatus;

    public SportHealth(String sourceMac) {
        this.sourceMac = sourceMac;
    }

    protected SportHealth(Parcel in) {
        sid = in.readString();
        isUploaded = in.readByte() != 0;
        if (in.readByte() == 0) {
            activityId = null;
        } else {
            activityId = in.readLong();
        }
        if (in.readByte() == 0) {
            userId = -1;
        } else {
            userId = in.readLong();
        }
        dateTime = in.readString();
        type = in.readInt();
        totalSeconds = in.readInt();
        numCalories = in.readInt();
        distance = in.readInt();
        numSteps = in.readInt();
        startTime = in.readString();
        endTime = in.readString();
        maxHrValue = in.readInt();
        warmupSeconds = in.readInt();
        burnFatSeconds = in.readInt();
        aerobicSeconds = in.readInt();
        extremeSecond = in.readInt();
        sourceMac = in.readString();
        avgHrValue = in.readInt();
        minHrValue = in.readInt();
        anaerobicSecond = in.readInt();
        isLocus = in.readInt();
        maxSpeed = in.readInt();
        minSpeed = in.readInt();
        avgSpeed = in.readInt();
        stepRate = in.readInt();
        stepRateMax = in.readInt();
        stepRange = in.readInt();
        stepRangeMax = in.readInt();
        sourceType = in.readInt();
        avgPace = in.readInt();
        maxPace = in.readInt();
        uploadedStrava = in.readInt();
        hr_data_vlaue_json = in.readString();
        stepItem = in.readString();
        hrDataIntervalMinute = in.readInt();
        fast_km_speed = in.readInt();
        averageSWOLF = in.readInt();
        bestSWOLF = in.readInt();
        maxSwolf = in.readInt();
        avgFrequency = in.readInt();
        maxFrequency = in.readInt();
        swimmingPosture = in.readInt();
        trips = in.readInt();
        totalStrokesNumber = in.readInt();
        poolDistance = in.readInt();
        swimmingDetailStr = in.readString();
        targetType = in.readInt();
        targetValue = in.readInt();
        deviceName = in.readString();
        minPace = in.readInt();
        timestamp = in.readLong();
        intervalSecond = in.readInt();
        actType = in.readInt();
        training_offset = in.readInt();
        subType = in.readInt();
        minRate = in.readInt();
        sourceOs = in.readInt();
        gpsSourceType = in.readInt();
        icon = in.readString();
        isLoadDetail = in.readByte() != 0;
        vo2max = in.readInt();
        recoverTime = in.readInt();
        trainingEffectScore = in.readFloat();
        discoverDateTime = in.readString();
        isSupportTrainingEffect = in.readInt();
        cumulativeDecline = in.readInt();
        cumulativeClimb = in.readInt();
        this.inClassCalories = in.readInt();
        total_rest_time = in.readInt();
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sid);
        dest.writeByte((byte) (isUploaded ? 1 : 0));
        if (activityId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(activityId);
        }
        if (userId == 0) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(userId);
        }
        dest.writeString(dateTime);
        dest.writeInt(type);
        dest.writeInt(totalSeconds);
        dest.writeInt(numCalories);
        dest.writeInt(distance);
        dest.writeInt(numSteps);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeInt(maxHrValue);
        dest.writeInt(warmupSeconds);
        dest.writeInt(burnFatSeconds);
        dest.writeInt(aerobicSeconds);
        dest.writeInt(extremeSecond);
        dest.writeString(sourceMac);
        dest.writeInt(avgHrValue);
        dest.writeInt(minHrValue);
        dest.writeInt(anaerobicSecond);
        dest.writeInt(isLocus);
        dest.writeInt(maxSpeed);
        dest.writeInt(minSpeed);
        dest.writeInt(avgSpeed);
        dest.writeInt(stepRate);
        dest.writeInt(stepRateMax);
        dest.writeInt(stepRange);
        dest.writeInt(stepRangeMax);
        dest.writeInt(sourceType);
        dest.writeInt(avgPace);
        dest.writeInt(maxPace);
        dest.writeInt(uploadedStrava);
        dest.writeString(hr_data_vlaue_json);
        dest.writeString(stepItem);
        dest.writeInt(hrDataIntervalMinute);
        dest.writeInt(fast_km_speed);
        dest.writeInt(averageSWOLF);
        dest.writeInt(bestSWOLF);
        dest.writeInt(maxSwolf);
        dest.writeInt(avgFrequency);
        dest.writeInt(maxFrequency);
        dest.writeInt(swimmingPosture);
        dest.writeInt(trips);
        dest.writeInt(totalStrokesNumber);
        dest.writeInt(poolDistance);
        dest.writeString(swimmingDetailStr);
        dest.writeLong(timestamp);
        dest.writeInt(sourceOs);
        dest.writeInt(gpsSourceType);
        dest.writeString(icon);
        dest.writeInt(vo2max);
        dest.writeInt(recoverTime);
        dest.writeFloat(trainingEffectScore);
        dest.writeString(discoverDateTime);
        dest.writeInt(isSupportTrainingEffect);
        dest.writeInt(cumulativeDecline);
        dest.writeInt(cumulativeClimb);
        dest.writeInt(inClassCalories);
        dest.writeInt(actType);
        dest.writeInt(training_offset);
    }

    @Keep
    @Generated()
    public SportHealth() {
    }

    @Generated(hash = 2010926090)
    public SportHealth(String sid, String dateTime, int type, int subType, int actType, int totalSeconds, int numCalories,
                       int numSteps, int distance, String startTime, String endTime, int targetType, int targetValue, int warmupSeconds,
                       int burnFatSeconds, int aerobicSeconds, int anaerobicSecond, int extremeSecond, String sourceMac, String deviceName,
                       int sourceType, int training_offset, int minHrValue, int maxHrValue, int avgHrValue, int minSpeed, int maxSpeed,
                       int avgSpeed, int minPace, int maxPace, int avgPace, int isLocus, int stepRange, int minRate, int stepRateMax,
                       int stepRate, int swimmingPosture, int totalStrokesNumber, int poolDistance, int trips, int bestSWOLF, int maxSwolf,
                       int averageSWOLF, int sourceOs, SportGps gps, SportItem heartrate, SportItem rate, SportItem range, SportItemPace pace,
                       SportRealTimePace realTimePace, SportSwimSwolf swolf, int stepRangeMax, boolean isUploadedStrava, int uploadedStrava,
                       boolean isUploaded, Long activityId, long userId, String hr_data_vlaue_json, String stepItem, String rangeItem,
                       int hrDataIntervalMinute, int fast_km_speed, int avgFrequency, int maxFrequency, String swimmingDetailStr,
                       int intervalSecond, long timestamp, int gpsSourceType, String icon, boolean isLoadDetail, int vo2max, int recoverTime,
                       String discoverDateTime, int trainingEffect, float trainingEffectScore, boolean isSupportTrain,
                       int isSupportTrainingEffect, int cumulativeClimb, int cumulativeDecline, double highestAltitude, double lowestAltitude,
                       int grade, Integer distance3d, Integer avg_3d_speed, Integer avg_vertical_speed, Integer avg_slope, Integer max_altitude,
                       Integer min_altitude, Integer cumulative_altitude_rise, Integer cumulative_altitude_loss, Integer altitude_count,
                       List<Integer> altitude_item, int warmUpPerformance, int completionRate, int hrCompletionRate, int inClassCalories,
                       String runningPullUp, String currentTimePace, String currentTimeStrideRate, String currentTimeSpeed,
                       int intervalTimeChart, String exerciseIntensity, Integer avg_altitude, int total_rest_time, Integer gpsStatus) {
        this.sid = sid;
        this.dateTime = dateTime;
        this.type = type;
        this.subType = subType;
        this.actType = actType;
        this.totalSeconds = totalSeconds;
        this.numCalories = numCalories;
        this.numSteps = numSteps;
        this.distance = distance;
        this.startTime = startTime;
        this.endTime = endTime;
        this.targetType = targetType;
        this.targetValue = targetValue;
        this.warmupSeconds = warmupSeconds;
        this.burnFatSeconds = burnFatSeconds;
        this.aerobicSeconds = aerobicSeconds;
        this.anaerobicSecond = anaerobicSecond;
        this.extremeSecond = extremeSecond;
        this.sourceMac = sourceMac;
        this.deviceName = deviceName;
        this.sourceType = sourceType;
        this.training_offset = training_offset;
        this.minHrValue = minHrValue;
        this.maxHrValue = maxHrValue;
        this.avgHrValue = avgHrValue;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.avgSpeed = avgSpeed;
        this.minPace = minPace;
        this.maxPace = maxPace;
        this.avgPace = avgPace;
        this.isLocus = isLocus;
        this.stepRange = stepRange;
        this.minRate = minRate;
        this.stepRateMax = stepRateMax;
        this.stepRate = stepRate;
        this.swimmingPosture = swimmingPosture;
        this.totalStrokesNumber = totalStrokesNumber;
        this.poolDistance = poolDistance;
        this.trips = trips;
        this.bestSWOLF = bestSWOLF;
        this.maxSwolf = maxSwolf;
        this.averageSWOLF = averageSWOLF;
        this.sourceOs = sourceOs;
        this.gps = gps;
        this.heartrate = heartrate;
        this.rate = rate;
        this.range = range;
        this.pace = pace;
        this.realTimePace = realTimePace;
        this.swolf = swolf;
        this.stepRangeMax = stepRangeMax;
        this.isUploadedStrava = isUploadedStrava;
        this.uploadedStrava = uploadedStrava;
        this.isUploaded = isUploaded;
        this.activityId = activityId;
        this.userId = userId;
        this.hr_data_vlaue_json = hr_data_vlaue_json;
        this.stepItem = stepItem;
        this.rangeItem = rangeItem;
        this.hrDataIntervalMinute = hrDataIntervalMinute;
        this.fast_km_speed = fast_km_speed;
        this.avgFrequency = avgFrequency;
        this.maxFrequency = maxFrequency;
        this.swimmingDetailStr = swimmingDetailStr;
        this.intervalSecond = intervalSecond;
        this.timestamp = timestamp;
        this.gpsSourceType = gpsSourceType;
        this.icon = icon;
        this.isLoadDetail = isLoadDetail;
        this.vo2max = vo2max;
        this.recoverTime = recoverTime;
        this.discoverDateTime = discoverDateTime;
        this.trainingEffect = trainingEffect;
        this.trainingEffectScore = trainingEffectScore;
        this.isSupportTrain = isSupportTrain;
        this.isSupportTrainingEffect = isSupportTrainingEffect;
        this.cumulativeClimb = cumulativeClimb;
        this.cumulativeDecline = cumulativeDecline;
        this.highestAltitude = highestAltitude;
        this.lowestAltitude = lowestAltitude;
        this.grade = grade;
        this.distance3d = distance3d;
        this.avg_3d_speed = avg_3d_speed;
        this.avg_vertical_speed = avg_vertical_speed;
        this.avg_slope = avg_slope;
        this.max_altitude = max_altitude;
        this.min_altitude = min_altitude;
        this.cumulative_altitude_rise = cumulative_altitude_rise;
        this.cumulative_altitude_loss = cumulative_altitude_loss;
        this.altitude_count = altitude_count;
        this.altitude_item = altitude_item;
        this.warmUpPerformance = warmUpPerformance;
        this.completionRate = completionRate;
        this.hrCompletionRate = hrCompletionRate;
        this.inClassCalories = inClassCalories;
        this.runningPullUp = runningPullUp;
        this.currentTimePace = currentTimePace;
        this.currentTimeStrideRate = currentTimeStrideRate;
        this.currentTimeSpeed = currentTimeSpeed;
        this.intervalTimeChart = intervalTimeChart;
        this.exerciseIntensity = exerciseIntensity;
        this.avg_altitude = avg_altitude;
        this.total_rest_time = total_rest_time;
        this.gpsStatus = gpsStatus;
    }


    public static final Creator<SportHealth> CREATOR = new Creator<SportHealth>() {
        @Override
        public SportHealth createFromParcel(Parcel in) {
            return new SportHealth(in);
        }

        @Override
        public SportHealth[] newArray(int size) {
            return new SportHealth[size];
        }
    };
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 168366028)
    private transient SportHealthDao myDao;

    public int getMinRate() {
        return minRate;
    }

    public void setMinRate(int minRate) {
        this.minRate = minRate;
    }


    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public static int getDataFromApp() {
        return DATA_FROM_APP;
    }

    public static int getDataFromDevice() {
        return DATA_FROM_DEVICE;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public int getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(int totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    public int getNumCalories() {
        return numCalories;
    }

    public void setNumCalories(int numCalories) {
        this.numCalories = numCalories;
    }

    public int getNumSteps() {
        return numSteps;
    }

    public void setNumSteps(int numSteps) {
        this.numSteps = numSteps;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getMaxHrValue() {
        return maxHrValue;
    }

    public void setMaxHrValue(int maxHrValue) {
        this.maxHrValue = maxHrValue;
    }

    public int getWarmupSeconds() {
        return warmupSeconds;
    }

    public void setWarmupSeconds(int warmupSeconds) {
        this.warmupSeconds = warmupSeconds;
    }

    public int getBurnFatSeconds() {
        return burnFatSeconds;
    }

    public void setBurnFatSeconds(int burnFatSeconds) {
        this.burnFatSeconds = burnFatSeconds;
    }

    public int getAerobicSeconds() {
        return aerobicSeconds;
    }

    public void setAerobicSeconds(int aerobicSeconds) {
        this.aerobicSeconds = aerobicSeconds;
    }

    public int getExtremeSecond() {
        return extremeSecond;
    }

    public void setExtremeSecond(int extremeSecond) {
        this.extremeSecond = extremeSecond;
    }

    public String getSourceMac() {
        return sourceMac;
    }

    public void setSourceMac(String sourceMac) {
        this.sourceMac = sourceMac;
    }

    public int getAvgHrValue() {
        return avgHrValue;
    }

    public void setAvgHrValue(int avgHrValue) {
        this.avgHrValue = avgHrValue;
    }

    public int getMinHrValue() {
        return minHrValue;
    }

    public void setMinHrValue(int minHrValue) {
        this.minHrValue = minHrValue;
    }

    public int getAnaerobicSecond() {
        return anaerobicSecond;
    }

    public void setAnaerobicSecond(int anaerobicSecond) {
        this.anaerobicSecond = anaerobicSecond;
    }

    public int getIsLocus() {
        return isLocus;
    }

    public void setIsLocus(int isLocus) {
        this.isLocus = isLocus;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(int minSpeed) {
        this.minSpeed = minSpeed;
    }

    public int getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(int avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public int getStepRate() {
        return stepRate;
    }

    public void setStepRate(int stepRate) {
        this.stepRate = stepRate;
    }

    public int getStepRange() {
        return stepRange;
    }

    public void setStepRange(int stepRange) {
        this.stepRange = stepRange;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public int getAvgPace() {
        return avgPace;
    }

    public void setAvgPace(int avgPace) {
        this.avgPace = avgPace;
    }

    public int getMaxPace() {
        return maxPace;
    }

    public void setMaxPace(int maxPace) {
        this.maxPace = maxPace;
        this.fast_km_speed = maxPace;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getHr_data_vlaue_json() {
        return hr_data_vlaue_json;
    }

    public void setHr_data_vlaue_json(String hr_data_vlaue_json) {
        this.hr_data_vlaue_json = hr_data_vlaue_json;
        SportItem sportItem = new SportItem(hr_data_vlaue_json);
        if(intervalSecond == 0){
            //v2是五秒一个点
            sportItem.setInterval(5);
        }else {
            sportItem.setInterval(intervalSecond);
        }

        heartrate = sportItem;
    }


    public int getAverageSWOLF() {
        return averageSWOLF;
    }

    public void setAverageSWOLF(int averageSWOLF) {
        this.averageSWOLF = averageSWOLF;
    }

    public int getBestSWOLF() {
        return bestSWOLF;
    }

    public void setBestSWOLF(int bestSWOLF) {
        this.bestSWOLF = bestSWOLF;
    }

    public int getMaxSwolf() {
        return maxSwolf;
    }

    public void setmaxSwolf(int maxSwolf) {
        this.maxSwolf = maxSwolf;
    }

    public int getAvgFrequency() {
        return avgFrequency;
    }

    public void setAvgFrequency(int avgFrequency) {
        this.avgFrequency = avgFrequency;
    }

    public int getMaxFrequency() {
        return maxFrequency;
    }

    public void setMaxFrequency(int maxFrequency) {
        this.maxFrequency = maxFrequency;
    }


    public int getSubType() {
        return subType;
    }

    public void setSubType(int subType) {
        this.subType = subType;
    }


    public int getHrDataIntervalMinute() {
        return hrDataIntervalMinute;
    }

    public void setHrDataIntervalMinute(int hrDataIntervalMinute) {
        this.hrDataIntervalMinute = hrDataIntervalMinute;
    }

    public int getIntervalSecond() {
        return intervalSecond;
    }

    public void setIntervalSecond(int intervalSecond) {
        this.intervalSecond = intervalSecond;
    }

    /**
     * 返回泳姿type
     *
     * @return
     */
    public int getSwimmingPosture() {
        return swimmingPosture;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getMinPeace() {
        return minPace;
    }

    public void setMinPeace(int minPeace) {
        this.minPace = minPeace;
    }

    public int getTargetType() {
        return targetType;
    }

    public void setTargetType(int targetType) {
        this.targetType = targetType;
    }

    public String getRangeItem() {
        return rangeItem;
    }

    public void setRangeItem(String rangeItem) {
        this.rangeItem = rangeItem;
        this.range = new SportItem(rangeItem);
    }

    public int getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(int targetValue) {
        this.targetValue = targetValue;
    }

    public int getGpsSourceType() {
        return gpsSourceType;
    }

    public void setGpsSourceType(int gpsSourceType) {
        this.gpsSourceType = gpsSourceType;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isLoadDetail() {
        return isLoadDetail;
    }

    public void setLoadDetail(boolean loadDetail) {
        isLoadDetail = loadDetail;
    }



    public void setSwimmingPosture(int swimmingPosture) {
        this.swimmingPosture = swimmingPosture;
    }

    public int getTrips() {
        return trips;
    }

    public void setTrips(int trips) {
        this.trips = trips;
    }

    public int getTotalStrokesNumber() {
        return totalStrokesNumber;
    }

    public void setTotalStrokesNumber(int totalStrokesNumber) {
        this.totalStrokesNumber = totalStrokesNumber;
    }

    public int getPoolDistance() {
        return poolDistance;
    }

    public void setPoolDistance(int poolDistance) {
        this.poolDistance = poolDistance;
    }

    public String getSwimmingDetailStr() {
        return swimmingDetailStr;
    }

    public void setSwimmingDetailStr(String swimmingDetailStr) {
        this.swimmingDetailStr = swimmingDetailStr;
    }

    public int getStepRateMax() {
        return stepRateMax;
    }

    public void setStepRateMax(int stepRateMax) {
        this.stepRateMax = stepRateMax;
    }

    public int getStepRangeMax() {
        return stepRangeMax;
    }

    public void setStepRangeMax(int stepRangeMax) {
        this.stepRangeMax = stepRangeMax;
    }

    public String getStepItem() {
        return stepItem;
    }

    public void setStepItem(String stepItem) {
        this.stepItem = stepItem;
        this.rate = new SportItem(stepItem);
    }

    public boolean getIsUploaded() {
        return this.isUploaded;
    }

    public void setIsUploaded(boolean isUploaded) {
        this.isUploaded = isUploaded;
    }

    public void setMaxSwolf(int maxSwolf) {
        this.maxSwolf = maxSwolf;
    }

    public int getFast_km_speed() {
        if(fast_km_speed ==0){
            return minPace;
        }
        return this.fast_km_speed;
    }

    public void setFast_km_speed(int fast_km_speed) {
        this.fast_km_speed = fast_km_speed;
        /// TODO 为什么要设置maxPace
//        this.maxPace = fast_km_speed;
    }


    public SportItem getHeartrate() {
        return heartrate;
    }

    public void setHeartrate(SportItem heartrate) {
        this.heartrate = heartrate;
        if (heartrate != null) {
            hr_data_vlaue_json = heartrate.getItmes();
        }
    }

    public SportItem getRate() {
        return rate;
    }

    public void setRate(SportItem rate) {
        this.rate = rate;
        if (rate != null) {
            stepItem = rate.getItmes();
        }
    }

    public SportItem getRange() {
        return range;
    }

    public void setRange(SportItem range) {
        this.range = range;
        if (range != null) {
            rangeItem = range.getItmes();
        }
    }

    public SportItemPace getPace() {
        return pace;
    }

    public void setPace(SportItemPace pace) {
        this.pace = pace;
    }

    public SportSwimSwolf getSwolf() {
        return swolf;
    }

    public void setSwolf(SportSwimSwolf swolf) {
        this.swolf = swolf;
    }

    public int getActType() {
        return actType;
    }

    public void setActType(int actType) {
        this.actType = actType;
    }

    public int getTraining_offset() {
        return training_offset;
    }

    public void setTraining_offset(int training_offset) {
        this.training_offset = training_offset;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }
    public String getCurrentTimePace() {
        return currentTimePace;
    }
    public void setCurrentTimePace(String currentTimePace) {
        this.currentTimePace = currentTimePace;
    }

    public void setCurrentTimePace(boolean isCover, String currentTimePace) {
        if (isCover) {
            this.currentTimePace = currentTimePace;
        } else {
            SportItem sportItem = new SportItem(currentTimePace);
            if (intervalTimeChart == 0) {
                sportItem.setInterval(5);
            } else {
                sportItem.setInterval(intervalTimeChart);
            }
            this.currentTimePace = GsonUtil.toJson(sportItem);
        }
    }
    public void setCurrentTimeStrideRate(String currentTimeStrideRate) {
        this.currentTimeStrideRate = currentTimeStrideRate;
    }
    public String getCurrentTimeStrideRate() {
        return currentTimeStrideRate;
    }
    public void setCurrentTimeStrideRate(boolean isCover, String currentTimeStrideRate) {
        if (isCover) {
            this.currentTimeStrideRate = currentTimeStrideRate;
        } else {
            SportItem sportItem = new SportItem(currentTimeStrideRate);
            if (intervalTimeChart == 0) {
                sportItem.setInterval(5);
            } else {
                sportItem.setInterval(intervalTimeChart);
            }
            this.currentTimeStrideRate = GsonUtil.toJson(sportItem);
        }
    }

    public int getSourceOs() {
        return sourceOs;
    }

    public void setSourceOs(int sourceOs) {
        this.sourceOs = sourceOs;
    }

    @Override
    public String toString() {
        return "SportHealth{" +
                "sid='" + sid + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", type=" + type +
                ", subType=" + subType +
                ", totalSeconds=" + totalSeconds +
                ", numCalories=" + numCalories +
                ", numSteps=" + numSteps +
                ", distance=" + distance +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", targetType=" + targetType +
                ", targetValue=" + targetValue +
                ", warmupSeconds=" + warmupSeconds +
                ", burnFatSeconds=" + burnFatSeconds +
                ", aerobicSeconds=" + aerobicSeconds +
                ", anaerobicSecond=" + anaerobicSecond +
                ", extremeSecond=" + extremeSecond +
                ", sourceMac='" + sourceMac + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", sourceType=" + sourceType +
                ", minHrValue=" + minHrValue +
                ", maxHrValue=" + maxHrValue +
                ", avgHrValue=" + avgHrValue +
                ", minSpeed=" + minSpeed +
                ", maxSpeed=" + maxSpeed +
                ", avgSpeed=" + avgSpeed +
                ", minPace=" + minPace +
                ", maxPace=" + maxPace +
                ", avgPace=" + avgPace +
                ", isLocus=" + isLocus +
                ", stepRange=" + stepRange +
                ", minRate=" + minRate +
                ", stepRateMax=" + stepRateMax +
                ", stepRate=" + stepRate +
                ", swimmingPosture=" + swimmingPosture +
                ", totalStrokesNumber=" + totalStrokesNumber +
                ", poolDistance=" + poolDistance +
                ", trips=" + trips +
                ", bestSWOLF=" + bestSWOLF +
                ", maxSwolf=" + maxSwolf +
                ", averageSWOLF=" + averageSWOLF +
                ", sourceOs=" + sourceOs +
                ", gps=" + gps +
                ", heartrate=" + heartrate +
                ", rate=" + rate +
                ", range=" + range +
                ", pace=" + pace +
                ", swolf=" + swolf +
                ", stepRangeMax=" + stepRangeMax +
                ", isUploadedStrava=" + uploadedStrava +
                ", isUploaded=" + isUploaded +
                ", activityId=" + activityId +
                ", userId=" + userId +
                ", hr_data_vlaue_json='" + hr_data_vlaue_json + '\'' +
                ", stepItem='" + stepItem + '\'' +
                ", rangeItem='" + rangeItem + '\'' +
                ", hrDataIntervalMinute=" + hrDataIntervalMinute +
                ", fast_km_speed=" + fast_km_speed +
                ", avgFrequency=" + avgFrequency +
                ", maxFrequency=" + maxFrequency +
                ", swimmingDetailStr='" + swimmingDetailStr + '\'' +
                ", intervalSecond=" + intervalSecond +
                ", timestamp=" + timestamp +
                ", gpsSourceType=" + gpsSourceType +
                ", icon='" + icon + '\'' +
                ", isLoadDetail=" + isLoadDetail +
                ", vo2max=" + vo2max +
                ", recoverTime=" + recoverTime +
                ", discoverDateTime='" + discoverDateTime + '\'' +
                ", isSupportTrainingEffect=" + isSupportTrainingEffect +
                ", trainingEffectScore=" + trainingEffectScore +
                ", distance3d= " + distance3d +
                ", avg_3d_speed=" + avg_3d_speed +
                ", avg_vertical_speed=" + avg_vertical_speed +
                ", avg_slope=" + avg_slope +
                ", max_altitude=" + max_altitude +
                ", min_altitude=" + min_altitude +
                ", cumulative_altitude_rise=" + cumulative_altitude_rise +
                ", cumulative_altitude_loss=" + cumulative_altitude_loss +
                ", altitude_count=" + altitude_count +
                ",total_rest_time="+total_rest_time+
                ",gpsStatus="+gpsStatus+
                ",highestAltitude="+ highestAltitude +
                ",lowestAltitude="+ lowestAltitude +
                '}';
    }


    public SportGps getGps() {
        return this.gps;
    }

    public void setGps(SportGps gps) {
        this.gps = gps;
    }

    public int getMinPace() {
        if(minPace ==0){
            return fast_km_speed;
        }
        return this.minPace;
    }

    public void setMinPace(int minPace) {
        this.minPace = minPace;
    }


    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getGpsStatus() {
        return gpsStatus;
    }

    public void setGpsStatus(Integer gpsStatus) {
        this.gpsStatus = gpsStatus;
    }

    public int getVo2max() {
        return vo2max;
    }

    public void setVo2max(int vo2max) {
        this.vo2max = vo2max;
    }

    public int getRecoverTime() {
        return recoverTime;
    }

    public void setRecoverTime(int recoverTime) {
        this.recoverTime = recoverTime;
    }

    public boolean getIsLoadDetail() {
        return this.isLoadDetail;
    }

    public void setIsLoadDetail(boolean isLoadDetail) {
        this.isLoadDetail = isLoadDetail;
    }

    public String getDiscoverDateTime() {
        return this.discoverDateTime;
    }

    public void setDiscoverDateTime(String discoverDateTime) {
        this.discoverDateTime = discoverDateTime;
    }

    public SportRealTimePace getRealTimePace() {
        return realTimePace;
    }

    public void setRealTimePace(SportRealTimePace realTimePace) {
        this.realTimePace = realTimePace;
    }

    public int getUploadedStrava() {
        return this.uploadedStrava;
    }

    @Keep
    public void setUploadedStrava(int uploadedStrava) {
        this.uploadedStrava = uploadedStrava;
        this.isUploadedStrava = uploadedStrava==0?false:true;
    }

    @Keep
    @Deprecated
    public boolean getIsUploadedStrava() {
        return this.isUploadedStrava;
    }
    @Keep
    @Deprecated
    public void setIsUploadedStrava(boolean isUploadedStrava) {
        this.isUploadedStrava = isUploadedStrava;
        this.uploadedStrava = isUploadedStrava?1:0;
    }

    @Keep
    public boolean isSupportTrain() {
        return isSupportTrainingEffect!=0;
    }

    @Keep
    @Deprecated
    public void setIsSupportTrain(boolean isSupportTrain) {
        this.isSupportTrain = isSupportTrain;
    }
    @Keep
    public int getIsSupportTrainingEffect() {
        return this.isSupportTrainingEffect;
    }
    @Keep
    public void setIsSupportTrainingEffect(int isSupportTrainingEffect) {
        this.isSupportTrainingEffect = isSupportTrainingEffect;
        this.isSupportTrain = isSupportTrainingEffect!=0;
    }

    public int getCumulativeClimb() {
        return this.cumulativeClimb;
    }

    public void setCumulativeClimb(int cumulativeClimb) {
        this.cumulativeClimb = cumulativeClimb;
    }

    public int getCumulativeDecline() {
        return this.cumulativeDecline;
    }

    public void setCumulativeDecline(int cumulativeDecline) {
        this.cumulativeDecline = cumulativeDecline;
    }

    public float getTrainingEffectScore() {
        return this.trainingEffectScore;
    }

    public void setTrainingEffectScore(float trainingEffectScore) {
        this.trainingEffectScore = trainingEffectScore;
    }

    public int getGrade() {
        return grade;
    }

    /**
     * 固件是否传等级，0未传
     * @return
     */
    public boolean isGradePro(){
        return grade > 0;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public Integer getDistance3d() {
        return distance3d;
    }

    public void setDistance3d(Integer distance3d) {
        this.distance3d = distance3d;
    }

    public Integer getAvg_3d_speed() {
        return avg_3d_speed;
    }

    public void setAvg_3d_speed(Integer avg_3d_speed) {
        this.avg_3d_speed = avg_3d_speed;
    }

    public Integer getAvg_vertical_speed() {
        return avg_vertical_speed;
    }

    public void setAvg_vertical_speed(Integer avg_vertical_speed) {
        this.avg_vertical_speed = avg_vertical_speed;
    }

    public Integer getAvg_slope() {
        return avg_slope;
    }

    public void setAvg_slope(Integer avg_slope) {
        this.avg_slope = avg_slope;
    }

    public Integer getMax_altitude() {
        return max_altitude;
    }

    public void setMax_altitude(Integer max_altitude) {
        this.max_altitude = max_altitude;
        this.highestAltitude = max_altitude;
    }

    public Integer getMin_altitude() {
        return min_altitude;
    }

    public void setMin_altitude(Integer min_altitude) {
        this.min_altitude = min_altitude;
        this.lowestAltitude = min_altitude;
    }

    public Integer getCumulative_altitude_rise() {
        return cumulative_altitude_rise;
    }

    public void setCumulative_altitude_rise(Integer cumulative_altitude_rise) {
        this.cumulative_altitude_rise = cumulative_altitude_rise;
        this.cumulativeClimb = cumulative_altitude_rise;
    }

    public Integer getCumulative_altitude_loss() {
        return cumulative_altitude_loss;
    }

    public void setCumulative_altitude_loss(Integer cumulative_altitude_loss) {
        this.cumulative_altitude_loss = cumulative_altitude_loss;
        this.cumulativeDecline = cumulative_altitude_loss;
    }

    public Integer getAltitude_count() {
        return altitude_count;
    }

    public void setAltitude_count(Integer altitude_count) {
        this.altitude_count = altitude_count;
    }

    public List<Integer> getAltitude_item() {
        return altitude_item;
    }

    public void setAltitude_item(List<Integer> altitude_item) {
        this.altitude_item = altitude_item;
    }
    public int getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(int completionRate) {
        this.completionRate = completionRate;
    }
    public String getRunningPullUp() {
        return this.runningPullUp;
    }

    public void setRunningPullUp(String runningPullUp) {
        this.runningPullUp = runningPullUp;
    }


    @Keep
    @Deprecated
    public int getTrainingEffect() {
        return this.trainingEffect;
    }
    @Keep
    @Deprecated
    public void setTrainingEffect(int trainingEffect) {
        this.trainingEffect = trainingEffect;
    }

    public boolean getIsSupportTrain() {
        return this.isSupportTrain;
    }
    public int getHrCompletionRate() {
        return hrCompletionRate;
    }

    public void setHrCompletionRate(int hrCompletionRate) {
        this.hrCompletionRate = hrCompletionRate;
    }
    public int getInClassCalories() {
        return inClassCalories;
    }

    public void setInClassCalories(int inClassCalories) {
        this.inClassCalories = inClassCalories;
    }

    /**
     * 计算平均海拔高度
     * @return
     */
    public String getAvgAltitute(){
        if(avg_altitude == null){
            return "--";
        }
        return avg_altitude.toString();
    }

    public Integer getAvg_altitude() {
        return avg_altitude;
    }

    public void setAvg_altitude(Integer avg_altitude) {
        this.avg_altitude = avg_altitude;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    public String getExerciseIntensity() {
        return exerciseIntensity;
    }

    public void setExerciseIntensity(String exerciseIntensity) {
        this.exerciseIntensity = exerciseIntensity;
    }

    public int getWarmUpPerformance() {
        return warmUpPerformance;
    }

    public void setWarmUpPerformance(int warmUpPerformance) {
        this.warmUpPerformance = warmUpPerformance;
    }

    public double getHighestAltitude() {
        return highestAltitude;
    }

    public void setHighestAltitude(double highestAltitude) {
        this.highestAltitude = highestAltitude;
    }

    public double getLowestAltitude() {
        return lowestAltitude;
    }

    public void setLowestAltitude(double lowestAltitude) {
        this.lowestAltitude = lowestAltitude;
    }
    public String getCurrentTimeSpeed() {
        return currentTimeSpeed;
    }
    public void setCurrentTimeSpeed(String currentTimeSpeed) {
        this.currentTimeSpeed = currentTimeSpeed;
    }
    public void setCurrentTimeSpeed(boolean isCover, String currentTimeSpeed) {
        if (isCover) {
            this.currentTimeSpeed = currentTimeSpeed;
        } else {
            SportItem sportItem = new SportItem(currentTimeSpeed);
            if (intervalTimeChart == 0) {
                sportItem.setInterval(5);
            } else {
                sportItem.setInterval(intervalTimeChart);
            }
            this.currentTimeSpeed = GsonUtil.toJson(sportItem);
        }
    }
    public int getIntervalTimeChart() {
        return intervalTimeChart;
    }

    public int getTotal_rest_time() {
        return total_rest_time;
    }

    public void setTotal_rest_time(int total_rest_time) {
        this.total_rest_time = total_rest_time;
    }



    /**
     * 运动记录支持显示3d距离
     * :(越野跑、徒步、定向越野、登山、山地骑行、越野滑雪)
     * @return
     */
    public static boolean supportRecordShow3d(int type,Integer distance3d){
        boolean support = false;
        if(type == Sport100Type.SPORT_TYPE_CROSS_RUNNING
                || type == Sport100Type.SPORT_TYPE_ONFOOT
                || type == Sport100Type.SPORT_TYPE_ORIENTEERING
                || type == Sport100Type.SPORT_TYPE_CLIMB
                || type == Sport100Type.SPORT_TYPE_MOUNTAIN_BIKING
                || type == Sport100Type.SPORT_TYPE_CROSS_COUNTRY_SKIING
        ){
            if(distance3d != null && distance3d > 0){
                support = true;
            }
        }
        return support;
    }
    public static boolean supportRecordShow3d(int type){
        boolean support = false;
        if(type == Sport100Type.SPORT_TYPE_CROSS_RUNNING
                || type == Sport100Type.SPORT_TYPE_ONFOOT
                || type == Sport100Type.SPORT_TYPE_ORIENTEERING
                || type == Sport100Type.SPORT_TYPE_CLIMB
                || type == Sport100Type.SPORT_TYPE_MOUNTAIN_BIKING
                || type == Sport100Type.SPORT_TYPE_CROSS_COUNTRY_SKIING
        ){
            support = true;
        }
        return support;
    }

    public void setIntervalTimeChart(int intervalTimeChart) {
        this.intervalTimeChart = intervalTimeChart;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1625545524)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getSportHealthDao() : null;
    }
}

