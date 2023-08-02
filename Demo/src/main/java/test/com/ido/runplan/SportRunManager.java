package test.com.ido.runplan;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import com.google.gson.JsonObject;
import com.ido.ble.LocalDataManager;
import com.ido.ble.data.manage.database.HealthActivityV3;
import com.ido.ble.logs.LogTool;
import com.ido.ble.protocol.model.ActivitySwitch;
import com.ido.ble.protocol.model.AppExchangeDataIngDeviceReplyData;
import com.ido.ble.protocol.model.AppExchangeDataIngPara;
import com.ido.ble.protocol.model.AppExchangeDataPauseDeviceReplyData;
import com.ido.ble.protocol.model.AppExchangeDataResumeDeviceReplyData;
import com.ido.ble.protocol.model.AppExchangeDataStartDeviceReplyData;
import com.ido.ble.protocol.model.AppExchangeDataStartPara;
import com.ido.ble.protocol.model.AppExchangeDataStopDeviceReplyData;
import com.ido.ble.protocol.model.DeviceNoticeAppExchangeDataPauseAppReplyData;
import com.ido.ble.protocol.model.DeviceNoticeAppExchangeDataPausePara;
import com.ido.ble.protocol.model.DeviceNoticeAppExchangeDataResumeAppReplyData;
import com.ido.ble.protocol.model.DeviceNoticeAppExchangeDataResumePara;
import com.ido.ble.protocol.model.DeviceNoticeAppExchangeDataStopAppReplyData;
import com.ido.ble.protocol.model.DeviceNoticeAppExchangeDataStopPara;
import com.ido.ble.protocol.model.SportType;
import com.ido.ble.protocol.model.SupportFunctionInfo;
import com.ido.ble.protocol.model.V3AppExchangeDataDeviceReplayEndData;
import com.ido.ble.protocol.model.V3AppExchangeDataHeartRate;
import com.ido.ble.protocol.model.V3AppExchangeDataIngDeviceReplyData;
import com.ido.ble.protocol.model.V3AppExchangeDataIngPara;
import com.veryfit.multi.nativeprotocol.Protocol;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import test.com.ido.exgdata.demo.BleSdkWrapper;
import test.com.ido.runplan.data.LatLngBean;
import test.com.ido.runplan.data.RunPlanCountDownTimeBean;
import test.com.ido.runplan.data.SportDataUtil;
import test.com.ido.runplan.data.SportHealthDataManager;
import test.com.ido.runplan.data.SportItemPace;
import test.com.ido.runplan.sync.AppExchangeDataCallBackWrapper;
import test.com.ido.runplan.sync.AppExchangeV3DataCallBackWrapper;
import test.com.ido.runplan.sync.BaseConnCallback;
import test.com.ido.runplan.utils.StringUtil;
import test.com.ido.runplan.utils.ThreadUtil;
import test.com.ido.utils.DateUtil;
import test.com.ido.utils.GsonUtil;
import test.com.ido.utils.TimeUtil;

import static test.com.ido.runplan.SportHealth.DATA_FROM_APP;
import static test.com.ido.runplan.SportHealth.DATA_FROM_APP_AND_DEVICE;
import static test.com.ido.runplan.SportHealth.DATA_FROM_DEVICE_AND_APP;

/**
 * Copyright (c) 2019 深圳市爱都科技有限公司. All rights reserved.
 *
 * @Author: xyb
 * @CreateDate: 2020-05-07 11:03
 * @description
 */
public class SportRunManager {
    /**
     * 运动过程中断线超时时间
     */
    private final static int TIME_OUT_CONNECT = 20 * 1000;
    /**
     * 5s的实时配速
     */
    private static final int TIME_FIVE_MINUTE = 5;
    /**
     * 发起运动超时时长(10s)
     */
    private static final int CONNECT_TIME_OUT = 10 * 1000;

    private static final String TAG = "SportRunManager";

    // ble设备回复交换运动数据开始请求
    static final int BLE_START_SUCCESS = 0x00;    //成功
    static final int BLE_START_SPORT_FAIL = 0x01;    //设备已经进入运动模式失败
    static final int BLE_START_LOW_POWER = 0x02;    //设备电量低失败
    static final int BLE_START_CHARGE_POWER = 0x03; // 设备充电中
    static final int BLE_START_SPORT_IN_ALEXA = 0x04;    //正在使用Alexa
    static final int BLE_START_IN_CALLING = 0x05; //通话中

    /**
     * 运动状态
     */
    private final int SPORT_STATE_NONE = 0;
    private final int SPORT_STATE_RUNNING = 1;
    private final int SPORT_STATE_STOP = 2;
    private final int SPORT_STATE_PAUSE = 3;
    /**
     * app和手环交互开始时的数据结构
     */
    private AppExchangeDataStartPara switchDataAppStart = new AppExchangeDataStartPara();

    /**
     * app和手环交互数据时的数据结构
     */
    private AppExchangeDataIngPara mSwitchDataAppIng = new AppExchangeDataIngPara();
    /**
     * 实时速度
     */
    private List<Integer> mRealSpeedItemsList = new ArrayList<>();
    /**
     * 实时步频
     */
    private List<Integer> mRealRateItemsList = new ArrayList<>();
    /**
     * 实时配速
     */
    private List<Integer> mRealPaceItemsList = new ArrayList<>();
    /**
     * app和手环交互数据的数据结构
     */
    private V3AppExchangeDataIngPara mV3SwitchDataAppIng = new V3AppExchangeDataIngPara();
    /**
     * 跑步计划 动作详情
     */
    private List<V3AppExchangeDataDeviceReplayEndData.ActionData> mActionItemsList = new ArrayList<>();
    /**
     * 轨迹点
     */
    public List<LatLngBean> mLatLngDomainList = new ArrayList<>();

    /**
     * 最大心率
     */
    private int maxHeartRate = 0;

    /**
     * 根据时间戳的差计算当前运动时间
     */
    private long lastCurrentTimeMillis = 0;

    private static volatile SportRunManager instance;
    /**
     * 运动中的回调
     */
    private ISportRunCallBack mSportRunCallBack;
    /**
     * 开始运动的回调
     */
    private ISportStartCallBack mSportStart;

    /**
     * 连接回调
     */
    private ISportConnectCallBack connectCallBack;

    /**
     * 定时器
     */
    RunTimerTask mRunTimerTask = new RunTimerTask();

    /**
     * 数据更新回调间隔时间
     */
    private final int UPDATE_DATA_INTERVAL = 1000;

    /**
     * 刷新数据的定时器
     */
    private Handler updateHandler = new Handler(Looper.getMainLooper());
    private Timer updateTimer;

    /**
     * 运动健康的实体类
     */
    private SportHealth mSportHealth;

    private long changeIndex;

    /**
     * 连接手环一起运动时为false,
     * 未连接手环时APP单独运动为true
     */
    private boolean mIsAppComplete;

    /**
     * gps信号值
     */
    private int mGpsSignValue = Constants.GPS_INVALID;

    /**
     * 交互数据时发送给手环的距离
     */
    private double mSendDistance = 0;

    private int sportState = SPORT_STATE_NONE;

    /**
     * 心率值
     */
    private int mHeartRate;

    /**
     * 带序列号的心率数组
     */
    private LinkedHashMap<Integer, int[]> serialHeartRate = new LinkedHashMap<>();

    /**
     * 心率
     */
    private List<Integer> mHeartRateList = new ArrayList<>();

    /**
     * 步频集合
     */
    private List<Integer> mRateList = new ArrayList<>();

    /**
     * 公里配速
     */
    private List<Integer> mMetricItemsList = new ArrayList<>();

    /**
     * 英里配速集合
     */
    private List<Integer> mBritishItemsList = new ArrayList<>();

    /**
     * 当前的经纬度
     */
    private LatLngBean mCurrentLatLng;

    /**
     * 是否保存数据
     */
    private boolean mIsSaveData = true;

    /**
     * 运动是否完成
     */
    protected boolean mIsCompleteRun = true;

    // 手环点击结束
    int NO_SAVE_END = 0x00;    //不保存
    int SAVE_END = 0x01;    //保存

    /**
     * 运动类型
     */
    private int mType;


    //1:手机APP,或者2: 终端手环手表设备
    private int mSourceType = DATA_FROM_APP;

    /**
     * 上次定位的经纬度
     */
    protected LatLngBean lastLatlng = null;
    //运动的纵距离
    protected double mTotalDistance = 0;

    // 强制开始
    int FORCE_START_INVALID = 0x00;    //强制开始无效
    int FORCE_START_VALID = 0x01;    //强制开始有效

    /**
     * v3协议
     */
    private boolean mIsV3Exchange = false;

    /**
     * 是否结束
     */
    public boolean mIsEnd = false;
    /**
     * 是否返回跑步计划数据
     */
    public boolean mRunPlanData;
    /**
     * 是否是户外运动
     */
    private boolean mIsOut;
    /**
     * 是否加载过地图
     */
    private boolean mIsLoadMap;

    /**
     * 是否移除第一个网络点
     */
    private boolean mIsRemoveFirstPoint;

    /**
     * 算法部纠偏单例类
     */
    private Protocol mProtocol;


    /**
     * 运动类型
     */
    private String mSportName;

    /**
     * 是否支持实时配速
     */
    private boolean mIsSupportRealPace;

    /**
     * 5秒的距离数组
     */
    private List<Double> mFiveMinuteDistances = new ArrayList();


    private double mOneMinuteDistances;

    /**
     * 3.自动暂停条件：实时速度≤3km/h 持续的时间秒
     * 4.自动恢复条件：实时速度>3km/h,持续的时间秒
     */
    private int targetDuration;
    /**
     * 户外骑行是否自动暂停
     */
    private boolean isAutoPauseByOutdoorCycle;
    /**
     * 自动暂停条件：实时速度≤3km/h 持续5秒
     * 自动暂停后，产生的距离，用来计算自动恢复（自动恢复条件：实时速度>3km/h,持续3秒）
     * 1s的距离
     */
    private double mOneSencondDistance;

    /**
     * 当前用户的id
     */
    private long mUserId;
    /**
     * 用户跑步计划的倒计时时间
     */
    private RunPlanCountDownTimeBean mRunPlanCountDownTime;
    /**
     * 是否是设备发起运动(设备联动)
     */
    private boolean mIsDeviceStartSport = false;
    /**
     * 设置运动开始时间
     */


    public void setSportRunCallback(ISportRunCallBack sportRunCallBack) {
        mSportRunCallBack = sportRunCallBack;
    }

    public void setSportStartCallback(ISportStartCallBack sportStartCallback) {
        mSportStart = sportStartCallback;
    }

    public void setConnectCallBack(ISportConnectCallBack connectCallBack) {
        this.connectCallBack = connectCallBack;
    }
    public boolean isDeviceStartSport() {
        return mIsDeviceStartSport;
    }

    public void setIsDeviceStartSport(boolean mIsDeviceStartSport) {
        this.mIsDeviceStartSport = mIsDeviceStartSport;
    }

    public long getUserId() {
        return mUserId;
    }

    public void setUserId(long userId) {
        mUserId = userId;
    }
    /**
     * 是否是户外运动
     *
     * @param isOutDoor
     */
    public void setIsOutDoor(boolean isOutDoor) {
        mIsOut = isOutDoor;
    }

    /**
     * 是否结束
     *
     * @return
     */
    public boolean isCompleteRun() {
        return mIsCompleteRun;
    }


    public interface ISportRunCallBack {
        void sportPause(boolean isSuccess);

        void sportResume(boolean isSuccess);

        void sportStop(boolean isSuccess, SportHealth sportRunningBean, List<LatLngBean> latLngBeanList);

        void sportRunning(SportHealth sportRunningBean, int heartRate,
                          LatLngBean latLngDomain, int gpsSignValue, boolean isRemoveFistPoint, RunPlanCountDownTimeBean bean);

        void sportFivePeaceAndSpeed(String realTimePeace, String realTimeSpeed);

    }

    public interface ISportStartCallBack {
        void sportStartSuccess();

        void sportStartFailedByLowPower();

        void sportStartFailed();

        void sportChargePower();

        //正在使用Alexa
        void sportRunInAlexa();

        //通话中
        void sportStartInCalling();
    }

    public interface ISportConnectCallBack {
        void connectTimeOut();

        void bleDisconnect();

        void bleConnect();

        void startConnect();
    }

    /**
     * 开启定位
     */
    public void startLocation() {
        //户外运动才可以发起定位
        if (mIsOut) {

        }
    }



    public void stopLocation() {

    }

    private SportRunManager() {

    }



    public static SportRunManager getInstance() {
        if (instance == null) {
            synchronized (SportRunManager.class) {
                if (instance == null) {
                    instance = new SportRunManager();
                }
            }
        }
        return instance;
    }



    /**
     * 获取功能表走v3协议还是v2协议
     */
    private void getFunction() {
        SupportFunctionInfo supportFunctionInfo = LocalDataManager.getSupportFunctionInfo();
        if (supportFunctionInfo != null && BleSdkWrapper.isConnected()) {
            mIsV3Exchange = supportFunctionInfo.ex_table_main9_v3_activity_exchange_data;
            mIsSupportRealPace = supportFunctionInfo.V3_support_v3_exchange_data_reply_add_real_time_speed_pace;
        }
    }


    /**
     * v3的数据交换
     */
    private final AppExchangeV3DataCallBackWrapper changeV3CallBack = new AppExchangeV3DataCallBackWrapper() {

        @Override
        public void onReplyExchangeDateIng(V3AppExchangeDataIngDeviceReplyData data) {
            super.onReplyExchangeDateIng(data);
            if (mRunPlanCountDownTime != null) {
                mRunPlanCountDownTime.setKm_speed(data.km_speed);
                mRunPlanCountDownTime.setCount_hour(data.count_hour);
                mRunPlanCountDownTime.setCount_minute(data.count_minute);
                mRunPlanCountDownTime.setCount_second(data.count_second);
            }
            if (data.status == AppExchangeDataIngDeviceReplyData.STATUS_SUCCESS) {
                //固件2秒和 40秒返回的数据不一致现在app这段做兼容
                if (data.real_time_calories != 0) {
                    mSportHealth.setNumCalories(data.real_time_calories);
                }
                if (data.steps != 0) {
                    mSportHealth.setNumSteps(data.steps
                    );
                }
                if (data.distance != 0) {
                    mOneMinuteDistances = data.distance - mSportHealth.getDistance();
                    mSportHealth.setDistance(data.distance);
                }
                if (data.duration != 0) {
                    mSportHealth.setTotalSeconds(data.duration);
                }
                String pace;
                pace = DateUtil.computeTimeMS(data.real_time_speed_pace);

                String speed;
                speed = SportDataUtil.formatAvgSpeed(data.real_time_speed / 100f);

                if (mSportRunCallBack != null && mIsSupportRealPace) {
                    LogTool.e(TAG, "onReplyExchangeDateIng:配速 " + pace + "," + speed + "," + mIsSupportRealPace);
                    mSportRunCallBack.sportFivePeaceAndSpeed(pace, speed);
                }
                //平均配速
               /* if (data.km_speed != 0) {
                    mSportHealth.setAvgPace(data.real_time_speed);
                }*/

                mHeartRate = data.heart_rate;

            } else if (data.status == 3) {
                //todo ios有这个问题固件端异常重启 app结束运动不保存数据不在运中结束
//                if (mSportRunCallBack != null) {
//                    mSportRunCallBack.sportStop(mSportHealth.getTotalSeconds() >= 60,mSportHealth,mLatLngDomainList);
//                }
            }
        }

        @Override
        public void onReplyExchangeDataEndData(V3AppExchangeDataDeviceReplayEndData data) {
            super.onReplyExchangeDataEndData(data);
            //数据交换结束
            //CommonLogUtil.d(TAG, "onReplyExchangeDataEndData: v3 " + data.toString());
            LogTool.e(TAG,
                    "onReplyExchangeDataEndData: " + data.toString() + mIsEnd);

           /* String log = "onReplyExchangeDataEndData: v3 " + data.toString();
            CommonLogUtil.printAndSave(log);*/
            //todo 运动结束是否要考虑状态
            /*if (data.errCode == AppExchangeDataStopDeviceReplyData.CODE_SUCCESS) {
                stopSuccess(mIsSaveData);
            } else {*/
            setSportHealth(data);
            if (mIsEnd) {
                if (mSportRunCallBack != null) {
                    mSportRunCallBack.sportStop(mIsSaveData, mSportHealth, mLatLngDomainList);
                }
            }
            mIsCompleteRun = true;
        }

        @Override
        public void onReplyExchangeHeartRateData(V3AppExchangeDataHeartRate data) {
            super.onReplyExchangeHeartRateData(data);
            //心率交换
            LogTool.e(TAG,
                    "心率数据 onReplyExchangeHeartRateData: " + data.toString());
            if (data.heart_rate_history == null) {
                return;

            }
            mHeartRateList.addAll(data.heart_rate_history);

        }
    };

    /**
     * 设置40s数据交换对象
     *
     * @param data
     */
    private void setSportHealth(V3AppExchangeDataDeviceReplayEndData data) {
        if (mSportHealth == null) {
            LogTool.e(TAG, "设置40s数据sportHealth为空");
            return;
        }
        //运动距离
        if (data.distance != 0) {
            mSportHealth.setDistance(data.distance);
        }
        //平均速度
        if (data.avg_speed != 0) {
            mSportHealth.setAvgSpeed(data.avg_speed);
        }
        if(mType == 0){
            mType= data.type;
        }
        //最大速度
        mSportHealth.setMaxSpeed(data.max_speed);
        //平均配速
        mSportHealth.setAvgPace(data.km_speed);
        //最大配速
        mSportHealth.setMinPace(data.fast_km_speed);
        mSportHealth.setFast_km_speed(data.fast_km_speed);
        //平均步频
        mSportHealth.setStepRate(data.avg_step_frequency);
        //最大步频
        mSportHealth.setStepRateMax(data.max_step_frequency);
        //平均步幅
        mSportHealth.setStepRange(data.avg_step_stride);
        //最大步幅
        mSportHealth.setStepRangeMax(data.max_step_stride);
        //步数
        mSportHealth.setNumSteps(data.step);
        //时间
        mSportHealth.setTotalSeconds(data.durations);
        //卡路里
        mSportHealth.setNumCalories(data.calories);
        //平均心率
        mSportHealth.setAvgHrValue(data.avg_hr_value);
        //最大心率
        mSportHealth.setMaxHrValue(data.max_hr_value);
        //极限锻炼
        mSportHealth.setExtremeSecond(data.extreme_exercise_time);
        //无氧耐力
        mSportHealth.setAnaerobicSecond(data.anaerobic_exercise_time);
        //有氧耐力
        mSportHealth.setAerobicSeconds(data.aerobic_exercise_time);
        //燃脂心率
        mSportHealth.setBurnFatSeconds(data.fat_burning_time);
        //热身心率
        mSportHealth.setWarmupSeconds(data.warm_up_time);
        //心率间隔
        mSportHealth.setIntervalSecond(data.hr_data_interval_minute);

        //心率间隔 v3是1秒一个点 之前基本是5秒一个点
        mSportHealth.setHrDataIntervalMinute(data.hr_data_interval_minute);
        //实时配速、实时速度
        SupportFunctionInfo functionInfo = LocalDataManager.getSupportFunctionInfo();
        if (functionInfo != null) {
            //功能表支持就保存数据
            if (functionInfo.v3_support_activity_sync_real_time) {
                //实时速度
                if (data.item_real_speed != null) {
                    mRealSpeedItemsList.addAll(data.item_real_speed);
                }
            }
            if (functionInfo.v3_support_activity_sync_real_time || functionInfo.V3_support_v3_exchange_data_reply_add_real_time_speed_pace) {
//                //实时步频
                if (data.steps_frequency != null) {
                    mRealRateItemsList.addAll(data.steps_frequency);
                }
                //实时配速
                if (data.pace_speed_items != null) {
                    mRealPaceItemsList.addAll(data.pace_speed_items);
                }
            }
        }
        //步频集合
        if (data.steps_frequency != null) {
            List<Integer> steps_frequency = data.steps_frequency;
            mRateList.addAll(steps_frequency);
            //mSportHealth.setStepItem(steps_frequency.toString());
        }
        //公里配速的集合
        if (data.km_speed_s != null) {
            mMetricItemsList.addAll(data.km_speed_s);
        }
        //英里配速的集合
        if (data.items_mi_speed != null) {
            mBritishItemsList.addAll(data.items_mi_speed);
        }
        //跑步计划的集合
        if (data.items != null) {
            mActionItemsList.addAll(data.items);
        }
        //恢复时长
        if (data.recover_time > 0) {
            mSportHealth.setRecoverTime(data.recover_time);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY, data.recover_time);
            mSportHealth.setDiscoverDateTime(DateUtil.format(calendar, DateUtil.DATE_FORMAT_YMDHms));
        }
        //训练得分
        if (data.training_effect != 0) {
            mSportHealth.setTrainingEffectScore(data.training_effect / 10f);
        }
        //最大摄氧量
        mSportHealth.setVo2max(data.vo2max);
        //等级
        mSportHealth.setGrade(data.grade);
        mSportHealth.setDistance3d(data.distance3d);
        mSportHealth.setAvg_3d_speed(data.avg_3d_speed);
        mSportHealth.setAvg_vertical_speed(data.avg_vertical_speed);
        mSportHealth.setAvg_slope(data.avg_slope);
        mSportHealth.setMax_altitude(data.max_altitude);
        mSportHealth.setMin_altitude(data.min_altitude);
        mSportHealth.setCumulative_altitude_rise(data.cumulative_altitude_rise);
        mSportHealth.setCumulative_altitude_loss(data.cumulative_altitude_loss);
        mSportHealth.setAltitude_count(data.altitude_count);
        mSportHealth.setAltitude_item(data.altitude_item);
        mSportHealth.setAvg_altitude(data.avg_altitude);
        //课程内卡路里
        mSportHealth.setInClassCalories(data.in_class_calories);
        //动作完成率
        mSportHealth.setCompletionRate(data.completion_rate);
        //心率控制率
        mSportHealth.setHrCompletionRate(data.hr_completion_rate);
        mSportHealth.setTraining_offset(data.training_offset);
        //保存数据
        saveSportRunData();

    }

    /**
     * v3之前的数据交换
     */
    private AppExchangeDataCallBackWrapper changCallBack = new AppExchangeDataCallBackWrapper() {
        @Override
        public void onReplyExchangeDataStart(final AppExchangeDataStartDeviceReplyData appExchangeDataStartDeviceReplyData) {
            //CommonLogUtil.d(TAG, "onReplyExchangeDataStart: ");
            LogTool.e(TAG, "onReplyExchangeDataStart: ");
            handlerReplay(appExchangeDataStartDeviceReplyData.ret_code);
        }

        @Override
        public void onReplyExchangeDateIng(final AppExchangeDataIngDeviceReplyData data) {
            String log = "onReplyExchangeDateIng----code=" + data.toString();
            LogTool.e(TAG, log);
            if (data.status == AppExchangeDataIngDeviceReplyData.STATUS_SUCCESS) {
                int[] hrs = data.hr_value;
                //心率时间间隔大于0的心率数据才有用
                if (hrs != null && hrs.length > 0 && data.interval_second > 0) {
                    serialHeartRate.put(data.hr_value_serial, hrs);
                    for (int j = 0; j < hrs.length; j++) {
                        LogTool.e("有效心率", String.valueOf(hrs[j]));
                        mHeartRateList.add(hrs[j]);
                    }
                    int sum = 0, size = mHeartRateList.size();
                    for (int j = 0; j < size; j++) {
                        sum += mHeartRateList.get(j);
                        maxHeartRate = Math.max(maxHeartRate, mHeartRateList.get(j));
                    }

                    mSportHealth.setAvgHrValue(sum / size);
                    mSportHealth.setMaxHrValue(maxHeartRate);

                }
                mSportHealth.setNumCalories(data.calories);
                mSportHealth.setNumSteps(data.step);
                mSportHealth.setDistance(data.distance);
                mHeartRate = data.cur_hr_value;
            }
        }

        @Override
        public void onReplyExchangeDateStop(final AppExchangeDataStopDeviceReplyData data) {
            //CommonLogUtil.d(TAG, "onReplyExchangeDateStop: ");
            LogTool.e(TAG, "onReplyExchangeDateStop--设备回复结束运动指令的回调: "+data);

            if (data.errCode == AppExchangeDataStopDeviceReplyData.CODE_SUCCESS) {
                mIsEnd = true;
                if(mIsV3Exchange){
                    stopSuccess(mIsSaveData,false);
                    LogTool.e(TAG, "onReplyExchangeDateStop--设备回复结束运动指令的回调,调用endV3Cmd获取最新的运动记录");
                    endV3Cmd();
                }else {
                    stopSuccess(mIsSaveData,true);
                }

            } else {
                //CommonLogUtil.printAndSave("onSysEvt_SWITCH_APP_STOP");
                if (mSportRunCallBack != null) {
                    mSportRunCallBack.sportStop(mIsSaveData, mSportHealth, mLatLngDomainList);
                }
            }
        }

        @Override
        public void onReplyExchangeDatePause(final AppExchangeDataPauseDeviceReplyData data) {
            //CommonLogUtil.d(TAG, "onReplyExchangeDatePause: ");
            LogTool.e(TAG, "onReplyExchangeDatePause: " + data.toString());
            if (data.err_code == AppExchangeDataPauseDeviceReplyData.CODE_SUCCESS) {
                pauseSuccess();
            } else {
                LogTool.e(TAG,"onSysEvt_SWITCH_APP_PAUSE");
                if (mSportRunCallBack != null) {
                    mSportRunCallBack.sportPause(false);
                }
            }
        }

        @Override
        public void onReplyExchangeDateResume(final AppExchangeDataResumeDeviceReplyData data) {
            LogTool.e(TAG,
                    "onReplyExchangeDateResume:  " + data.toString());
            if (data.err_code == AppExchangeDataResumeDeviceReplyData.CODE_SUCCESS) {
                resumeSuccess(true);
            } else {
                if (mSportRunCallBack != null) {
                    mSportRunCallBack.sportResume(false);
                }
            }
        }

        @Override
        public void onDeviceNoticeAppStop(final DeviceNoticeAppExchangeDataStopPara data) {
            LogTool.e(TAG,"onDeviceNoticeAppStop--设备通知APP结束: " + data.toString());
            DeviceNoticeAppExchangeDataStopAppReplyData d = new DeviceNoticeAppExchangeDataStopAppReplyData();
            //todo 这个可能为空
            d.err_code = DeviceNoticeAppExchangeDataStopAppReplyData.CODE_SUCCESS;
            if (mSportHealth == null) {
                LogTool.e(TAG, "onDeviceNoticeAppStop: " + "对象为空");
                return;
            }

            d.calories = mSportHealth.getNumCalories();
            d.duration = mSportHealth.getTotalSeconds();
            d.distance = mSportHealth.getDistance();
            BleSdkWrapper.replyDeviceNoticeAppExchangeDataStop(d);
//            mIsSaveData = data.is_save == DeviceNoticeAppExchangeDataStopPara.IS_SAVE_YES; // V3协议该字段无效
            mIsSaveData = mSportHealth.getTotalSeconds() >= 60;
            LogTool.e(TAG, "run: " + mIsSaveData + "," + mIsEnd);
            mIsEnd = true;
            if(mIsV3Exchange) {
                LogTool.e(TAG, "onDeviceNoticeAppStop--设备通知APP结束,调用endV3Cmd获取最新的运动记录");
                endV3Cmd();
            }else{
                stopSuccess(mIsSaveData,true);
            }
            //收到固件暂停指令，还在运动就先暂停运动
            if(sportState == SPORT_STATE_RUNNING){
                pauseSuccess();
            }
        }

        @Override
        public void onDeviceNoticeAppPause(final DeviceNoticeAppExchangeDataPausePara deviceNoticeAppExchangeDataPausePara) {
            //CommonLogUtil.d(TAG, "onDeviceNoticeAppPause: ");
            LogTool.e(TAG, "onDeviceNoticeAppPause: "+(deviceNoticeAppExchangeDataPausePara==null?"null":deviceNoticeAppExchangeDataPausePara.toString()));
            DeviceNoticeAppExchangeDataPauseAppReplyData data = new DeviceNoticeAppExchangeDataPauseAppReplyData();
            data.err_code = DeviceNoticeAppExchangeDataPauseAppReplyData.CODE_SUCCESS;
            BleSdkWrapper.replyDeviceNoticeAppExchangeDataPause(data);
            pauseSuccess();

        }

        @Override
        public void onDeviceNoticeAppResume(final DeviceNoticeAppExchangeDataResumePara deviceNoticeAppExchangeDataResumePara) {

            String log = "onDeviceNoticeAppResume----code=" + deviceNoticeAppExchangeDataResumePara.toString();
            //CommonLogUtil.d(log);
            LogTool.e(TAG, "run: " + log);
            DeviceNoticeAppExchangeDataResumeAppReplyData data = new DeviceNoticeAppExchangeDataResumeAppReplyData();
            data.err_code = DeviceNoticeAppExchangeDataResumeAppReplyData.CODE_SUCCESS;
            BleSdkWrapper.replyDeviceNoticeAppExchangeDataResume(data);
            resumeSuccess(true);
            //CommonLogUtil.d("resumeRun---4");

        }
    };

    /**
     * 运动结束保存运动的数据
     *
     * @param isSave
     */
    private void stopSuccess(final boolean isSave,boolean isEndSport) {
        LogTool.e(TAG,"运动结束保存运动的数据---isSave: " + isSave);
        mIsCompleteRun = true;
        sportState = SPORT_STATE_STOP;
        mIsSaveData = isSave;
        stopUpdateTimer();
        //停止定位
        //CommonLogUtil.d(TAG, "stopSuccess:stopLocation ");
        stopLocation();
        BleSdkWrapper.unregisterConnectCallBack(connCallBack);
        BleSdkWrapper.unregisterAppExchangeDataCallBack(changCallBack);
        saveSportRunData();
        if (isEndSport) {
            updateHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mSportRunCallBack != null) {
                        mSportRunCallBack.sportStop(mIsSaveData, mSportHealth, mLatLngDomainList);
                    }
                }
            });
        }

    }

    /**
     * 保存运动中的数据
     */
    private void saveSportRunData() {
        if (mSportHealth == null) {
            LogTool.e(TAG, "saveSportRunData: 保存运动信息为空");
            return;
        }
        LogTool.e(TAG, "saveSportRunData: "
                + "时间，" + mSportHealth.getTotalSeconds() + "距离," + mSportHealth.getDistance() +" ,mIsSaveData="+mIsSaveData);
//        mSportHealth.setEndTime(TimeUtil.convTimeDetail(System.currentTimeMillis()));
        mSportHealth.setEndTime(TimeUtil.convTimeDetail(TimeUtil.addSecond(mSportHealth.getStartTime(),mSportHealth.getTotalSeconds())));
        mSportHealth.setHr_data_vlaue_json(GsonUtil.toJson(mHeartRateList));
        if (GsonUtil.toJson(mActionItemsList) != null) {
            List<HealthActivityV3.ActionTemp> mActionTemps = new ArrayList<>();
            for (int i = 0; i < mActionItemsList.size(); i++) {
                HealthActivityV3.ActionTemp actionTemp = new HealthActivityV3.ActionTemp();
                actionTemp.actual_time = mActionItemsList.get(i).time;
                actionTemp.goal_time = mActionItemsList.get(i).goal_time;
                actionTemp.heart_value = mActionItemsList.get(i).heart_con_value;
                actionTemp.type = mActionItemsList.get(i).type;
                mActionTemps.add(actionTemp);
            }
            LogTool.e("debug",GsonUtil.toJson(mActionTemps));
            if (GsonUtil.toJson(mActionTemps) != null)
                mSportHealth.setRunningPullUp(Objects.requireNonNull(GsonUtil.toJson(mActionTemps)).replace("\"", ""));
        }
        //步频集合改为叠加的形式
        mSportHealth.setStepItem(GsonUtil.toJson(mRateList));
        mSportHealth.setSourceType(mSourceType);

        SportItemPace sportItemPace = mSportHealth.getPace();
        if (sportItemPace == null) {
            sportItemPace = new SportItemPace();
        }
        sportItemPace.setMetricItems(GsonUtil.toJson(mMetricItemsList));
        sportItemPace.setBritishItems(GsonUtil.toJson(mBritishItemsList));
        mSportHealth.setPace(sportItemPace);
        /*CommonLogUtil.d(TAG, "saveSportRunData: " + mSportHealth.toString() +
                ","+mMetricItemsList.toString() + mBritishItemsList.toString());*/
        //实时配速、实时速度
        SupportFunctionInfo functionInfo = LocalDataManager.getSupportFunctionInfo();
        if (functionInfo != null) {
            //功能表支持就保存数据
            if (functionInfo.v3_support_activity_sync_real_time) {
                mSportHealth.setCurrentTimeSpeed(false, GsonUtil.toJson(mRealSpeedItemsList));
            }
            if (functionInfo.v3_support_activity_sync_real_time || functionInfo.V3_support_v3_exchange_data_reply_add_real_time_speed_pace) {
                //实时步频
                mSportHealth.setCurrentTimeStrideRate(false, GsonUtil.toJson(mRealRateItemsList));
                mSportHealth.setCurrentTimePace(false, GsonUtil.toJson(mRealPaceItemsList));
            }
        }
        // 如果没有达到目标值不保存数据
        double cumulativeClimb=0;
        double cumulativeDecline=0;
        if (mIsSaveData) {
            // 保存心率数据
            //mhrDataVlaueJson = new Gson().toJson(mHeartRateList);
            if (mLatLngDomainList != null && mLatLngDomainList.size() > 0) {
//                SportHealthDataManager.addSportGpsData(mLatLngDomainList,
//                        TimeUtil.convTimeYmdhmsToLong(mSportHealth.getStartTime()), mUserId);
                //分段保存经纬度信息
                if (mLatLngDomainList != null) {
                    //保存累计爬升  和累计下降
                    for (int i = 0; i <mLatLngDomainList.size() ; i++) {

                        if (i!=0){
                            if (mLatLngDomainList.get(i).altitude>mLatLngDomainList.get(i-1).altitude){
                                cumulativeClimb=cumulativeClimb+(mLatLngDomainList.get(i).altitude-mLatLngDomainList.get(i-1).altitude);
                            }else {
                                cumulativeDecline=cumulativeDecline+(mLatLngDomainList.get(i-1).altitude-mLatLngDomainList.get(i).altitude);
                            }
                        }
                    }
                    mLatLngDomainList.clear();
                }
                mSportHealth.setIsLocus(SportHealth.DATA_IS_LOCUS);
                //（0无轨迹，1 APPGPS轨迹来源，2 固件GPS轨迹来源）
                mSportHealth.setGpsSourceType(SportHealth.DATA_GPS_SOURCE_TYPE_APP);

            }
            mSportHealth.setCumulativeDecline((int) cumulativeDecline);
            mSportHealth.setCumulativeClimb((int) cumulativeClimb);
            mSportHealth.setDateTime(mSportHealth.getStartTime());
            mSportHealth.setUploaded(false);
            //运动的类型
            mSportHealth.setType(mType);
            //设置运动目标

            //如果是跑步计划  补上跑步计划的类型
//            RunPlanInfo runPlanInfo = GreenDaoUtil.queryRunPlanData(mUserId);
            //先写死 跑步计划3km
            mSportHealth.actType = 1;
            mSportHealth.setLoadDetail(true);

            if (functionInfo != null) {
                boolean isSupportTrain = functionInfo.V3_sync_v3_activity_add_param;
                mSportHealth.setIsSupportTrainingEffect(isSupportTrain ? 1 : 0);
            }
            //v3需要保存更多数据 兼容手环重启产生的数据
            if (mSportHealth.getTotalSeconds() > 60) {
                mSportHealth.setUserId(mUserId);
                //设备联动的运动，不保存数据，只保存经纬度
                if(!mIsDeviceStartSport)
                SportHealthDataManager.addDataFromApp(mSportHealth);
            }

        }
    }



    private void resumeSuccess(boolean isDelay) {
        if (sportState == SPORT_STATE_RUNNING) {
            LogTool.e(TAG, "resumeSuccess--已经是运动状态");
            return;
        }
        sportState = SPORT_STATE_RUNNING;
        setRunStateByOutdoorCycle();
        LogTool.e(TAG, "*************************resumeSuccess: ");
        startUpdateTimer(isDelay);
        startLocation();
        updateHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mSportRunCallBack != null) {
                    mSportRunCallBack.sportResume(true);
                }
            }
        });
    }


    /**
     * 初始化交互数据结构对象
     *
     * @param sportType
     * @param force_start
     */
    public void initSwitchData(int sportType, int force_start,HealthManagerPresenter.StartTimeBean startTimeBean) {
        /*Time t = new Time(Time.getCurrentTimezone());
        t.setToNow();*/
       /* com.ido.life.util.TimeUtil.getHour();
        com.ido.life.util.TimeUtil.getHour();
        com.ido.life.util.TimeUtil.getMinute();
        com.ido.life.util.TimeUtil.getSecond();*/
        switchDataAppStart.day = TimeUtil.getDay();
        if(startTimeBean != null){
            switchDataAppStart.hour = startTimeBean.getHour();
            switchDataAppStart.minute = startTimeBean.getMinute();
            switchDataAppStart.second = startTimeBean.getSecond();
        } else {
            switchDataAppStart.hour = TimeUtil.getHour();
            switchDataAppStart.minute = TimeUtil.getMinute();
            switchDataAppStart.second = TimeUtil.getSecond();
        }
        switchDataAppStart.sportType = sportType;
        switchDataAppStart.force_start = force_start;

    }

    /**
     * 轨迹运动断线20秒后未连接，超时提醒
     * 多运动断线就提醒
     */
    private Runnable disConnRun = new Runnable() {
        @Override
        public void run() {
            LogTool.e(TAG, "run: " + "超时了.......");
            mIsAppComplete = true;
            if (connectCallBack != null) {
                connectCallBack.connectTimeOut();
            }
        }
    };
    /**
     * 连接超时
     */
    private final Runnable runnableConnectTimeOut = ()->{
        LogTool.e(TAG, "连接固件: " + "超时了.......");
        if (connectCallBack != null) {
            connectCallBack.connectTimeOut();
        }
    };

    /**
     * 是否设置了断线超时连接
     */
    private BaseConnCallback connCallBack = new BaseConnCallback() {

        @Override
        public void onConnectStart(String macAddress) {
            super.onConnectStart(macAddress);
            LogTool.e(TAG, "onConnectStart: macAddress=" + macAddress);
//            if (connectCallBack != null){
//                connectCallBack.startConnect();
//            }
//            updateHandler.postDelayed(runnableConnectTimeOut,CONNECT_TIME_OUT);
        }

        @Override
        public void onInitCompleted(String macAddress) {
            super.onInitCompleted(macAddress);
            //这个方法无效了
            //CommonLogUtil.d("onInitCompleted....");
           /* SportLogHelper.saveSportLog(TAG, "onInitCompleted: ");
            mIsAppComplete = false;
            updateHandler.removeCallbacks(disConnRun);
            if (connectCallBack != null) {
                connectCallBack.bleConnect();
            }*/
        }

        @Override
        public void onConnectBreak(String macAddress) {
            //断开连接了
            LogTool.e(TAG, "onConnectBreak: macAddress=" + macAddress);
            if (connectCallBack != null) {
                connectCallBack.bleDisconnect();
            }
            updateHandler.postDelayed(disConnRun, TIME_OUT_CONNECT);


        }

        @Override
        public void onConnectSuccess(String macAddress) {
            super.onConnectSuccess(macAddress);
            //重新连接成功
            LogTool.e(TAG, "onConnectSuccess: " + macAddress);
            mIsAppComplete = false;
            updateHandler.removeCallbacks(disConnRun);
//            updateHandler.removeCallbacks(runnableConnectTimeOut);
            if (connectCallBack != null) {
                connectCallBack.bleConnect();
            }
        }
    };

    private void handlerReplay(int startRelayCode) {
        LogTool.e(TAG,
                "handlerReplay: startRelayCode=" + startRelayCode);
        switch (startRelayCode) {
            //如果开启运动成功...
            case BLE_START_SUCCESS://成功
                sportStartSuccess();
                break;
            //设备已经进入运动模式失败
            case BLE_START_SPORT_FAIL:
                if (mIsDeviceStartSport) {
                    //设备发起运动
                    sportStartSuccess();
                } else if (mSportStart != null) {
                    mSportStart.sportStartFailed();
                }
                break;
            //设备电量低失败
            case BLE_START_LOW_POWER:
                if (mSportStart != null) {
                    mSportStart.sportStartFailedByLowPower();
                }
                break;
            //正在Alexa语音，无法运动
            case BLE_START_SPORT_IN_ALEXA:
                if (mSportStart != null) {
                    mSportStart.sportRunInAlexa();
                }
                break;
            //设备充电中
            case BLE_START_CHARGE_POWER:
                if (mSportStart != null) {
                    mSportStart.sportChargePower();
                }
                break;
            //通话中
            case BLE_START_IN_CALLING:
                if (mSportStart != null) {
                    mSportStart.sportStartInCalling();
                }
                break;
        }
    }

    /**
     * 开启成功
     */
    private void sportStartSuccess() {
        mIsCompleteRun = false;
        initTrainDomain();
        LogTool.e(TAG, "sportStartSuccess: ");
        stopUpdateTimer();
        startUpdateTimer(true);
        sportState = SPORT_STATE_RUNNING;
        if (mSportStart != null) {
            mSportStart.sportStartSuccess();
        }
    }

    private void initTrainDomain() {
        mSportHealth = new SportHealth();
        //初始化距离
        mSendDistance = 0;
        //设置是否移除第一个无效点
        mIsRemoveFirstPoint = true;
        mGpsSignValue = Constants.GPS_INVALID;
        //运动的时间即开始时间
        Date date = DateUtil.getDateByHMS(switchDataAppStart.hour, switchDataAppStart.minute, switchDataAppStart.second);

        mBritishItemsList = new ArrayList<>();
        mMetricItemsList = new ArrayList<>();
        mRateList = new ArrayList<>();
        mRealSpeedItemsList = new ArrayList<>();
        mRealRateItemsList = new ArrayList<>();
        mRealPaceItemsList = new ArrayList<>();
        mSportHealth.setStartTime(TimeUtil.convTimeDetail(date.getTime()));
        mSportHealth.setDateTime(TimeUtil.convTimeDetail(date.getTime()));
        LogTool.e(TAG,
                "initTrainDomain: " + mSportHealth.toString());
        //设置mac值
        mSportHealth.setSourceMac(BleSdkWrapper.getBindMac());

        /**
         * 初始化算法部轨迹优化方案
         */
        mProtocol = Protocol.getInstance();
        mProtocol.initType(mType);
        mProtocol.initParameter();

    }


    /**
     * 开启更新定时器
     */
    public void startUpdateTimer(boolean isDelay) {
//        lastCurrentTimeMillis = System.currentTimeMillis() / 1000;
//        updateHandler.removeCallbacks(mRunTimerTask);
        LogTool.e(TAG, "startUpdateTimer: " + isDelay);
//        if (isDelay) {
//            updateHandler.postDelayed(mRunTimerTask, UPDATE_DATA_INTERVAL);
//        } else {
//            updateHandler.post(mRunTimerTask);
//        }
        if (mRunTimerTask != null) {
            mRunTimerTask.cancel(); //将原任务从队列中移除(很重要，一定要移除)
            mRunTimerTask = null;
        }
        mRunTimerTask = new RunTimerTask();
        updateTimer = new Timer();
        lastCurrentTimeMillis = System.currentTimeMillis() / 1000;
        if (isDelay) {
            updateTimer.schedule(mRunTimerTask, UPDATE_DATA_INTERVAL, UPDATE_DATA_INTERVAL);
        } else {
            updateTimer.schedule(mRunTimerTask, 0, UPDATE_DATA_INTERVAL);
        }

    }

    /**
     * 结束定时器
     */
    private void stopUpdateTimer() {
        LogTool.e(TAG, "stopUpdateTimer: ");
//        updateHandler.removeCallbacks(mRunTimerTask);
        if (updateTimer != null) {
            updateTimer.cancel();
            updateTimer = null;
        }
    }

    /**
     * 暂停跑步
     */
    public void pauseRun() {
        if (mIsAppComplete || !BleSdkWrapper.isConnected()) {
            pauseSuccess();
            return;
        }
        LogTool.e(TAG, "*********pauseRun: ");
        // pauseSuccess();
        BleSdkWrapper.appSwitchPause(switchDataAppStart.day, switchDataAppStart.hour,
                switchDataAppStart.minute, switchDataAppStart.second);
    }

    private boolean isOutddorCycle() {
        ActivitySwitch activitySwitch = LocalDataManager.getActivitySwitch();
        if(activitySwitch == null){
            activitySwitch = new ActivitySwitch();
        }
        return mType == SportType.SOPRT_TYPE_OUTDOOR_CYCLE && activitySwitch.autoPauseOnOff == ActivitySwitch.SWITCH_ON;
    }

    /**
     * 暂停成功
     */
    private void pauseSuccess() {
        if (sportState == SPORT_STATE_PAUSE) {
            LogTool.e(TAG, "pauseSuccess--已经是暂停状态");
            return;
        }
        sportState = SPORT_STATE_PAUSE;
        stopUpdateTimer();
        if (isOutddorCycle() && isAutoPauseByOutdoorCycle) {
            LogTool.e(TAG, "*********计算户外骑行的速度---pauseSuccess--自动暂停不停止定位");
        } else {
            //停止定位
            LogTool.e(TAG, "*********pauseSuccess--停止定位");
            stopLocation();

        }

        updateHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mSportRunCallBack != null) {
                    mSportRunCallBack.sportPause(true);
                }
            }
        });

    }

    /**
     * 设置户外骑行的恢复运动状态
     */
    private void setRunStateByOutdoorCycle() {
        if (isOutddorCycle()) {
            if (isAutoPauseByOutdoorCycle) {
                isAutoPauseByOutdoorCycle = false;
                LogTool.e(TAG, "计算户外骑行的速度--恢复运动状态");
            }
            targetDuration = 0;
        }
    }

    /**
     * 继续跑步
     */
    public void resumeRun() {
        if (mIsAppComplete || !BleSdkWrapper.isConnected()) {
            resumeSuccess(true);
            LogTool.e(TAG,"resumeRun---1");
            return;
        }
        //resumeSuccess(true);
        LogTool.e(TAG,"resumeRun---2");
        BleSdkWrapper.appSwitchRestore(switchDataAppStart.day, switchDataAppStart.hour,
                switchDataAppStart.minute, switchDataAppStart.second);
    }


    /**
     * 重新继续运动
     */
    public void onRestoreInstanceState() {
        if (mSportRunCallBack != null) {
            mSportRunCallBack.sportRunning(mSportHealth, mHeartRate,
                    mCurrentLatLng, mGpsSignValue, mIsRemoveFirstPoint,mRunPlanCountDownTime);
        }
        if (mIsV3Exchange) {
            //注册v3协议
            BleSdkWrapper.unregisterV3AppExchangeDataCallBack(changeV3CallBack);
            BleSdkWrapper.registerV3AppExchangeDataCallBack(changeV3CallBack);
        }
        BleSdkWrapper.unregisterAppExchangeDataCallBack(changCallBack);
        BleSdkWrapper.registerAppExchangeDataCallBack(changCallBack);

        BleSdkWrapper.registerConnectCallBack(connCallBack);
        if (sportState == SPORT_STATE_PAUSE) {
            pauseSuccess();
        } else {
            startUpdateTimer(false);
        }

    }

    /**
     * 结束运动
     * <p>
     * 2018/5/31
     * <p>
     * APP发起的运动手环都不需要保存数据
     *
     * @param isSave 本地是否保存数据
     */
    public void stopRun(final boolean isSave) {
        /*CommonLogUtil.d("结束运动------isAppComplete:"+mIsAppComplete+",isConnected:"
                + BleSdkWrapper.isConnected());*/
        this.mIsSaveData = isSave;
        ThreadUtil.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                LogTool.e(TAG,
                        "结束运动------isAppComplete:" + mIsAppComplete + ",isConnected:"
                                + BleSdkWrapper.isConnected());
                if (mIsAppComplete || !BleSdkWrapper.isConnected()) {
                    mIsEnd = true;
                    LogTool.e(TAG, "app单独发起运动或者断连结束运动---run: stopRun :mIsEnd" + mIsEnd);
                    stopSuccess(isSave,true);
                    return;
                }
                //结束运动指令
                endCmd(isSave ? SAVE_END : NO_SAVE_END);
            }
        });
    }
    public void stopRunPlan(final boolean isSave) {
        endV3Cmd();
        stopRun(isSave);
    }

    /**
     * 结束命令
     */
    public void endCmd(int is_save) {
        LogTool.e(TAG, "endCmd: " + is_save + ",mSportHealth=" + (mSportHealth==null?"null":mSportHealth.toString()));
        if (mSportHealth == null) {
            return;
        }
        int durations = mSportHealth.getTotalSeconds();
        int calories = mSportHealth.getNumCalories();
        int distance = mSportHealth.getDistance();
        BleSdkWrapper.appSwitchDataEnd(switchDataAppStart.day, switchDataAppStart.hour,
                switchDataAppStart.minute, switchDataAppStart.second, mType, durations,
                calories, distance, is_save);
    }

    /**
     * v3结束命令
     * 最大摄氧量和恢复时长都是结束运动之后返回的,所以要先发结束指令（endCmd）再发获取运动数据的命令（endV3Cmd）
     */
    public void endV3Cmd() {
        LogTool.e(TAG, "endV3Cmd: " + mIsV3Exchange);
        BleSdkWrapper.v3AppSwitchDataEnd();
    }


    public void close() {
        mSportRunCallBack = null;
        unRegisterConnectCallBcak();
        LogTool.e(TAG, "**********close: ");
        stopUpdateTimer();
        BleSdkWrapper.unregisterAppExchangeDataCallBack(changCallBack);
        BleSdkWrapper.unregisterV3AppExchangeDataCallBack(changeV3CallBack);
    }

    public void unRegisterConnectCallBcak() {
        BleSdkWrapper.unregisterConnectCallBack(connCallBack);
    }

    /**
     * 获取运动信息
     */
    public boolean getSportInit() {
        if (mSportHealth == null || TextUtils.isEmpty(mSportHealth.getStartTime())) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 获取开始时间
     *
     * @return
     */
    public String getDateTime() {
        if (mSportHealth != null) {
            return mSportHealth.getStartTime();
        }
        return "";
    }

    /**
     * 获取之前的经纬度
     *
     * @return
     */
    public List<LatLngBean> getmLatLngDomainList() {
        return mLatLngDomainList;
    }

    /**
     * 运动时v3协议回调的设备记录的运动用时
     */
    private int deviceSportTime = -1;
    /**
     * 定时器任务每2S执行changeCmd方法
     * 每1S回调sportRunning方法
     */
    private class RunTimerTask extends TimerTask {
        @Override
        public void run() {
            long currentTimeMillis = System.currentTimeMillis() / 1000;
            //判断是否在运动中的状态
            /*if (sportState != SPORT_STATE_RUNNING) {
                return;
            }*/
            //测试数据
            /*if(BuildConfig.DEBUG){
                testAddGps(currentTimeMillis);
            }*/
            int dtime = (int) (currentTimeMillis - lastCurrentTimeMillis);
            dtime = Math.max(1, dtime);
            int sportTime = mSportHealth.getTotalSeconds() + dtime;
            if(deviceSportTime - sportTime > 2) {
                sportTime = deviceSportTime;
            }
            // 用定时器的时间还是手环的时间 v3的时间和手环交换了v2的时间没有和手环交换
            mSportHealth.setTotalSeconds(sportTime);
            lastCurrentTimeMillis = currentTimeMillis;
            LogTool.e(TAG, "RunTimerTask-duration:" + mSportHealth.getTotalSeconds());
            //计算卡路里
            completeCalorie();
            ThreadUtil.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    if (mSportRunCallBack != null) {
                        mSportRunCallBack.sportRunning(mSportHealth, mHeartRate,
                                mCurrentLatLng, mGpsSignValue, mIsRemoveFirstPoint,mRunPlanCountDownTime);
                    }
                }
            });
            changeIndex++;
            LogTool.e(TAG,
                    "mIsV3Exchange: " + mIsV3Exchange + ",changeIndex=" + changeIndex + ",mIsAppComplete=" + mIsAppComplete + ",isConnected=" + BleSdkWrapper.isConnected());
            //获取5s的距离，计算5s的配速和速度
            getFiveMinutePace();
            //如果是和手环连接的情况,每隔2秒发送一次交换数据
            if (!mIsAppComplete && BleSdkWrapper.isConnected()) {
                if (mIsV3Exchange) {
                    if (changeIndex % 2 == 0) {
                        changeV3Cmd();
                    }
                    //30秒获取一次心率数据
                    if (changeIndex % 30 == 0) {
                        changeV3Rate();
                    }
                    //40秒保存一下数据
                    if (changeIndex % 40 == 0) {
                        endV3Cmd();
                    }
                } else {
                    if (changeIndex % 2 == 0) {
                        changeCmd();
                    }
                }
            }
            //手机单独发起的运动四十秒保存经纬度
            if (mIsAppComplete && changeIndex % 40 == 0) {
                saveSportRunData();
            }
//            updateHandler.postDelayed(this, UPDATE_DATA_INTERVAL);
        }
    }

    /**
     * 获取5s的配速
     */
    private void getFiveMinutePace() {
        String pace = StringUtil.format("%.1f", 0f);
        String speed = StringUtil.format("%.1f", 0f);
        if (mFiveMinuteDistances != null && mFiveMinuteDistances.size() >= TIME_FIVE_MINUTE) {
            mFiveMinuteDistances.remove(0);
        }
        mFiveMinuteDistances.add(mOneMinuteDistances);
        //添加之后制为0因为固件是2s返回运动距离
        mOneMinuteDistances = 0;
        Double fiveDistance = 0d;
        if (mFiveMinuteDistances.size() == TIME_FIVE_MINUTE) {
            for (int i = 0; i < mFiveMinuteDistances.size(); i++) {
                fiveDistance += mFiveMinuteDistances.get(i);
            }
            pace = SportDataUtil.computeTimePace(fiveDistance.intValue(), TIME_FIVE_MINUTE);
            speed = SportDataUtil.computeTimeSpeed(fiveDistance.intValue(), TIME_FIVE_MINUTE);
            autoPauseByOutdoorCycle((float) (fiveDistance / TIME_FIVE_MINUTE * 3.6f));
        }
        completeFastPaceAndFastSpeedByPhone(fiveDistance.intValue(),TIME_FIVE_MINUTE);
        if (!mIsSupportRealPace) {
            LogTool.e(TAG, "App计算: 配速=" + pace + " ,速度=" + speed + " ,是否支持实时配速=" + mIsSupportRealPace + " ,5s的距离=" + fiveDistance);
            String finalPace = pace;
            String finalSpeed = speed;
            updateHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mSportRunCallBack != null) {
                        mSportRunCallBack.sportFivePeaceAndSpeed(finalPace, finalSpeed);
                    }
                }
            });

        }

    }

    int paceByphone,speedByphone;

    //app单独发起的运动需要计算配速和速度
    private void completeFastPaceAndFastSpeedByPhone(int dis,int time){
        if(mIsAppComplete) {
            float kmDistance = dis / 1000f;
            paceByphone = (int) ((time / 60f) / kmDistance);
            speedByphone = (int) ((kmDistance / (time / 3600f)) * 100);//与固件保持一致扩大100倍保存整数
            if(mSportHealth.getMinPace()==0){
                mSportHealth.setMinPace(paceByphone);
            }
            if(paceByphone!=0){
                mSportHealth.setMinPace(Math.min(paceByphone,mSportHealth.getMinPace()));
            }
            mSportHealth.setMaxPace(Math.min(paceByphone,mSportHealth.getMaxPace()));

            if(mSportHealth.getMinSpeed()==0){
                mSportHealth.setMinSpeed(paceByphone);
            }
            if(speedByphone!=0){
                mSportHealth.setMinSpeed(Math.min(speedByphone,mSportHealth.getMinSpeed()));
            }
            mSportHealth.setMaxSpeed(Math.max(speedByphone,mSportHealth.getMaxSpeed()));
        }
    }

    /**
     * 获取v3的心率数据
     */
    private void changeV3Rate() {
        BleSdkWrapper.getExChangeV3DataHeartRateInterval();
    }

    /**
     * v3的数据交换
     */
    private void changeV3Cmd() {
        //CommonLogUtil.d(TAG, "changeV3Cmd: ");
        LogTool.e(TAG,
                "changeV3Cmd: " + mGpsSignValue + "," + mSendDistance);
        mV3SwitchDataAppIng.signalFlag = mGpsSignValue;
        mV3SwitchDataAppIng.distance = (int) mSendDistance;
        BleSdkWrapper.v3AppSwitchDataIng(mV3SwitchDataAppIng);
    }

    /**
     * 交换命令
     */
    private void changeCmd() {
        LogTool.e(TAG, "changeCmd: ");
        int duration = mSportHealth.getTotalSeconds();
        int calories = mSportHealth.getNumCalories();
        mSwitchDataAppIng.status = mGpsSignValue;
        mSwitchDataAppIng.duration = duration;
        mSwitchDataAppIng.calories = calories;
        mSwitchDataAppIng.distance = (int) mSendDistance;
        BleSdkWrapper.appSwitchDataIng(mSwitchDataAppIng);
    }

    /**
     * 计算户外骑行的速度自动恢复
     */
    private void autoResumeByOutdoorCycle() {
        if (isOutddorCycle() && isAutoPauseByOutdoorCycle) {
            float speed = (float) (mOneSencondDistance * 3.6f);//每一秒的速度(km/h)
            //自动恢复条件：实时速度>3km/h,持续3秒
            LogTool.e(TAG, "计算户外骑行的速度---恢复,speed=" + speed + "  ,targetDuration=" + targetDuration + " ,mOneSencondDistance=" + mOneSencondDistance);
            if (speed > 3) {
                targetDuration += 1;
                if (targetDuration >= 3) {
                    targetDuration = 0;
                    LogTool.e(TAG, "计算户外骑行的速度--实时速度3km/h,持续3秒---恢复运动");
                    resumeRun();
                }
            } else {
                targetDuration = 0;
            }
        }
    }

    /**
     * 计算户外骑行的速度自动暂停
     */
    private void autoPauseByOutdoorCycle(float speed) {
        if (isOutddorCycle() && !isAutoPauseByOutdoorCycle) {
            float mSpeed = (float) (mOneSencondDistance * 3.6f);//每一秒的速度（km/h）
            LogTool.e(TAG, "计算户外骑行的速度--暂停,speed=" + mSpeed + " ,5s speed=" + speed + "  ,targetDuration=" + targetDuration + " ,mOneSencondDistance=" + mOneSencondDistance);
            //自动暂停条件：实时速度≤3km/h 持续5秒
            if (mSpeed <= 3) {
                targetDuration += 1;
                if (targetDuration >= 5) {
                    targetDuration = 0;
                    LogTool.e(TAG, "计算户外骑行的速度--实时速度≤3km/h 持续5秒------暂停运动");
                    isAutoPauseByOutdoorCycle = true;
                    pauseRun();
                }
            } else {
                targetDuration = 0;
            }
        }
    }

    /**
     * 计算卡路里
     */
    private void completeCalorie() {
//        int calorie = mSportHealth.getNumCalories();
//        //如果是APP单独发起的运动，或者断线情况下，计算卡路里
//        if (mIsAppComplete || !BleSdkWrapper.isConnected()) {
//            //从前面的界面传递过来
//            calorie = SportDataHelper.completCarloy(mType, mSportHealth.getDistance(), mSportHealth.getNumCalories());
//        }
//        //mView.setSportCalorie(String.valueOf(calorie));
//        mSportHealth.setNumCalories(calorie);
    }
    /**
     * 开启跑步计划运动监听数据
     */
    public void startRunPlan(HealthManagerPresenter.StartTimeBean startTimeBean) {
        clearCache();
        //获取功能表
        getFunction();
        mTotalDistance = 0;
        mHeartRate = 0;
        maxHeartRate = 0;
        mIsAppComplete = false;
        //连接手环运动的，先发送命令
        if (mIsV3Exchange) {
            //注册v3协议
            BleSdkWrapper.registerAppExchangeDataCallBack(changCallBack);
            BleSdkWrapper.registerV3AppExchangeDataCallBack(changeV3CallBack);
        } else {
            BleSdkWrapper.registerAppExchangeDataCallBack(changCallBack);
        }
        mRunPlanCountDownTime = new RunPlanCountDownTimeBean();
        BleSdkWrapper.registerConnectCallBack(connCallBack);
        mIsCompleteRun = false;
        initSwitchData(48, FORCE_START_INVALID,startTimeBean);
        initTrainDomain();
        LogTool.e(TAG, "sportStartSuccess: ");
        stopUpdateTimer();
        startUpdateTimer(true);
        sportState = SPORT_STATE_RUNNING;
        mSourceType = mIsDeviceStartSport ? DATA_FROM_DEVICE_AND_APP : DATA_FROM_APP_AND_DEVICE;;
        if (mSportStart != null) {
            mSportStart.sportStartSuccess();
        }
    }

    /**
     * 清空缓存数据,确保每次运动都是新的数据
     */
    private void clearCache() {
        mLatLngDomainList.clear();
        serialHeartRate.clear();
        mHeartRateList.clear();
        mMetricItemsList.clear();
        mBritishItemsList.clear();
        mActionItemsList.clear();
        mRealRateItemsList.clear();
        mRealPaceItemsList.clear();
        mIsEnd = false;
        maxHeartRate = 0;
        lastCurrentTimeMillis = 0;
        lastLatlng = null;
        mCurrentLatLng = null;
        mIsLoadMap = false;
        mIsV3Exchange = false;
        mIsSupportRealPace = false;
        isAutoPauseByOutdoorCycle = false;
        targetDuration = 0;
        mFiveMinuteDistances.clear();
        mOneMinuteDistances = 0;
        mOneSencondDistance = 0;
        sportState = SPORT_STATE_NONE;
        changeIndex=0;
        mRealSpeedItemsList.clear();
        mRunPlanCountDownTime = null;
    }


    public SportHealth getSportHealth(){
        return mSportHealth;
    }
}
