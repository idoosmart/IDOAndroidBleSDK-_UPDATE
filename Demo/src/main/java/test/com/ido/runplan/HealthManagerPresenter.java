package test.com.ido.runplan;

import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ido.ble.BLEManager;
import com.ido.ble.LocalDataManager;
import com.ido.ble.bluetooth.device.BLEDevice;
import com.ido.ble.logs.LogTool;
import com.ido.ble.protocol.model.BasicInfo;
import com.ido.ble.protocol.model.NoticeSportActionToggle;
import com.ido.ble.protocol.model.SportPlan;
import com.ido.ble.protocol.model.SupportFunctionInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import test.com.ido.exgdata.demo.BleSdkWrapper;
import test.com.ido.runplan.data.LatLngBean;
import test.com.ido.runplan.data.RunPlanCountDownTimeBean;
import test.com.ido.runplan.sync.NoticeSportActionToggleCallBackWrapper;
import test.com.ido.runplan.utils.RunTimeUtil;
import test.com.ido.utils.DateUtil;
import test.com.ido.utils.TimeUtil;

@Deprecated
public class HealthManagerPresenter {
    private String TAG = "HealthManagerActivity";

    /**
     * 是否支持实时配速
     */
    private boolean mIsSupportRealPace = false;
    /**
     * v3协议
     */
    private boolean mIsV3Exchange = false;
    private IHealthManagerView view;
    SetRunPlanH5Info mCurrRunPlanBean  = null;
    private StartTimeBean mStartTimeBean  = null;
    /**
     * 是否是强制结束
     */
    private boolean isEnd = false;
    /**
     * 运动管理类
     */
    SportRunManager mSportRunManager  = null;
    protected boolean mIsCompleteRun = false;
    private boolean isAccumulateRate = false; //是否累积心率值
    private int avgRate = 0;//心率平均值
    private int avgRateFrequency = 0; //心率平均值
    /**
     * 是否移除过网络定位点
     */
    private boolean mIsAlreadyLoadGps = false;
    private boolean isConnectedDevice = false;

    public SupportFunctionInfo getSupportFunctionInfo() {
        SupportFunctionInfo functionInfo = LocalDataManager.getSupportFunctionInfo();
        return functionInfo == null ? new SupportFunctionInfo() : functionInfo;
    }
    public HealthManagerPresenter (IHealthManagerView view){
        this.view = view;
    }

    public void setCurrRunPlan(SetRunPlanH5Info mRunPlanBean ) {
        mCurrRunPlanBean = mRunPlanBean;
    }
    /**
     * 获取设备信息
     *
     * @return
     */
    public BLEDevice getDeviceInfo() {
        BLEDevice device = LocalDataManager.getCurrentDeviceInfo();
        if (device == null) {
            device = new BLEDevice();
        }
        BasicInfo basicInfo = LocalDataManager.getBasicInfo();
        if (basicInfo != null) {
            device.mDeviceId = basicInfo.deivceId;
            device.version = basicInfo.firmwareVersion;
            if (device.mDeviceId <= 0) {//如果deviceId是负数就重新赋值
                device.mDeviceId = basicInfo.deivceId;
            }
        }
        return device;
    }
    public void sendInitDataToWeb() {
        JsonObject json = new JsonObject();
        String appLanguage = "cn";
        boolean connected = BLEManager.isConnected();
        boolean bind = BLEManager.isBind();
        long mUserId = RunTimeUtil.getInstance().getUserId();

        SupportFunctionInfo functionInfo = getSupportFunctionInfo();
        json.addProperty("language", appLanguage);
        json.addProperty("serverSite", "cn");
        json.addProperty("token", RunTimeUtil.getInstance().getAppToken());
        json.addProperty("timestamp", "HOUR_CLOCK_24");
        json.addProperty("appKey", Constants.APP_KEY);

        String  calorieUnit="KCAL";

        //体重
        String  weightUnit="1";

        String  heightUnit="1";

        json.addProperty("caloriseUnit",calorieUnit);
        //1男  2女
        json.addProperty("gender","1");
        json.addProperty("weightUnit",weightUnit);
        json.addProperty("heightUnit",heightUnit);
        json.addProperty("userId", mUserId);
        json.addProperty("weekStart", "MONDAY");

        if (connected) {
            json.addProperty("deviceName", getDeviceInfo().mDeviceName);
        }
        json.addProperty("deviceVersion", "001");
        json.addProperty("bluetoothStatus", "YES");

        if (connected) json.addProperty("boundNoConnectDeviceStatus", "YES");
        else json.addProperty("boundNoConnectDeviceStatus", "NO");

        if (bind) json.addProperty("equipmentBindingStatus", "YES");
        else json.addProperty("equipmentBindingStatus", "NO");

        json.addProperty("isSyncHealthInfo", "YES");
        if (functionInfo.v3_support_sports_plan) json.addProperty("isSupportRunningPlan", "YES");
        else json.addProperty("isSupportRunningPlan", "NO");
        if (functionInfo.heartRate || functionInfo.ex_main4_v3_hr_data) json.addProperty("isSupportHeartRate", "YES");
        else json.addProperty("isSupportHeartRate", "NO");
        json.addProperty("appName","mentech wear");
        sendNotificationToWeb(json, H5ToAppConstant.GET_USER_INFO_APP);
    }

    private void sendNotificationToWeb(JsonObject json ,String name) {
        if (view != null) {
            view.runPlanAppSendH5(json, name);
        }
    }

    long lastClickTime = 0L;
    long lastSetClickTime = 0L;

    public void sendNotificationToWeb( String name) {
        JsonObject json = new JsonObject();
        //300ms以内只会发送一次通知给H5  防止多次发送

        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= 300) {
            json.addProperty("data", name);
            sendNotificationToWeb(json, Constants.SEND_NOTIFICATION_TOWEB);
            lastClickTime = System.currentTimeMillis();
        }
    }

    public void registerCallBack() {
        getFunction();
        BLEManager.unregisterSportPlanCallBack(sportPlanCallBack);
        BLEManager.unregisterNoticeSportActionToggleCallBack(noticeSportActionToggleCallBack);
        BLEManager.registerSportPlanCallBack(sportPlanCallBack);
        BLEManager.registerNoticeSportActionToggleCallBack(noticeSportActionToggleCallBack);
        if(mSportRunManager != null){
            mSportRunManager.onRestoreInstanceState();
        }
        mSportRunManager = SportRunManager.getInstance();
        mSportRunManager.setUserId(RunTimeUtil.getInstance().getUserId());
        mIsAlreadyLoadGps = false;
        isConnectedDevice = BLEManager.isConnected();
    }

    private NoticeSportActionToggleCallBackWrapper noticeSportActionToggleCallBack = new NoticeSportActionToggleCallBackWrapper(){
        @Override
        public void onSettintResult(int i, boolean b, NoticeSportActionToggle noticeSportActionToggle) {
            super.onSettintResult(i, b, noticeSportActionToggle);
            LogTool.e(TAG,
                    "onSettintResult $b" + noticeSportActionToggle.toString());
            switch (i){
                case 0x01:
                    if (b) {
                        mIsCompleteRun = true;
                        mSportRunManager.startRunPlan(mStartTimeBean);
                        //   sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_START_TRAINING)
                    } else {
                        mIsCompleteRun = false;
                        mSportRunManager.stopRunPlan(true);
                        sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_START_TRAINING_FAIL);
                    }
                    break;
                case 0x02:
                    if (b) {
                        sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_SUSPEND_TRAINING);
                    } else {
                        sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_SUSPEND_TRAINING_FAIL);
                    }
                    break;
                case 0x03:
                    if (b) {
                        sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_RENEW_TRAINING);
                    } else {
                        sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_RENEW_TRAINING_FAIL);
                    }
                    break;
                case 0x04:
                    mIsCompleteRun = false;
                    if (b) {
                        mSportRunManager.mRunPlanData = true;
                        mSportRunManager.mIsEnd = true;
                        if (isEnd) {
                            mSportRunManager.stopRunPlan(false);
                        } else {
                            mSportRunManager.stopRunPlan(true);
                        }
                        sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_STOP_TRAINING);
                    } else {
                        sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_STOP_TRAINING_FAIL);
                    }
                    break;
            }
        }

        @Override
        public void onDeviceNotify(int i, boolean b, NoticeSportActionToggle noticeSportActionToggle) {
            super.onDeviceNotify(i, b, noticeSportActionToggle);
            LogTool.e(TAG,
                    "onDeviceNotify $b" + noticeSportActionToggle.toString());
            switch (i){
                case 0x01:
                    sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_START_TRAINING);
                    break;
                case 0x02:
                    sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_SUSPEND_TRAINING);
                    break;
                case 0x03:
                    sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_RENEW_TRAINING);
                    break;
                case 0x04:
                    mSportRunManager.mRunPlanData = true;
                    mIsCompleteRun = false;
                    mSportRunManager.mIsEnd = true;
                    sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_STOP_TRAINING);
                    break;
                case 0x05:
                    if(noticeSportActionToggle != null){
                        sendActionToggleDataToWeb(noticeSportActionToggle);
                    }
                    break;
            }
        }
    };

    /**
     训练中(推送动作切换等信息)  runningPlanSendActionToggleDataToWeb
     */
    public void sendActionToggleDataToWeb(NoticeSportActionToggle noticeSportActionToggle ) {
        JsonObject json = new JsonObject();
        json.addProperty("operate", noticeSportActionToggle.operate);
        json.addProperty("type", noticeSportActionToggle.type);
        json.addProperty("action_type", noticeSportActionToggle.action_type);
        json.addProperty("time", noticeSportActionToggle.time);
        json.addProperty("nextType", noticeSportActionToggle.type);
        json.addProperty("nextTime", noticeSportActionToggle.time);
        json.addProperty("low_heart", noticeSportActionToggle.low_heart);
        json.addProperty("height_heart", noticeSportActionToggle.height_heart);
        sendNotificationToWeb(json, H5ToAppConstant.RUNNING_PLAN_SEND_ACTION_TOGGLE_DATA_TO_WEB);
    }

    private SportPlanCallBackWrapper sportPlanCallBack = new SportPlanCallBackWrapper(){
        @Override
        public void onStartPlan(boolean b) {
            super.onStartPlan(b);
            LogTool.e(TAG,
                    "开始计划监听onStartPlan $b");
            if (b) sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_ISSET_PLAN_SUCCESS);
            else
                sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_ISSET_PLAN_FAIL);
        }

        @Override
        public void onSportDataSend(boolean b) {
            super.onSportDataSend(b);
            long curClickTime = System.currentTimeMillis();
            if (curClickTime - lastSetClickTime > 1000) {
                if (!b) {
                    LogTool.e(TAG,
                            "下发计划结果onSportDataSend $b");
                    //下发计划失败
                    sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_ISSET_PLAN_FAIL);
                } else {
                    LogTool.e(TAG,
                            "下发计划结果onSportDataSend ${mCurrRunPlanBean.toString()}");
                    if(mCurrRunPlanBean != null){
                        sendSetPlanToDevice(mCurrRunPlanBean, SportPlan.OPERATE_START);
                    }
                }
            }
        }

        @Override
        public void onPlanEnd(boolean b) {
            super.onPlanEnd(b);
            if (b) sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_IS_END_PLAN_SUCCESS);
            else
                sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_IS_END_PLAN_FAIL);
            LogTool.e(TAG,
                    "运动计划结束结果onPlanEnd $b");
        }

        @Override
        public void onQueryResult(boolean b, SportPlan sportPlan) {
            super.onQueryResult(b, sportPlan);
            LogTool.e(TAG,
                    "运动计划查询结果onQueryResult $b" + sportPlan.toString());
            if (b && sportPlan != null) {
                if(mCurrRunPlanBean == null){
                    sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_CONNECTING_DEVICE_STATUS, "3");
                } else{
                    //查询成功
                    if (mCurrRunPlanBean != null
                            && mCurrRunPlanBean.getRunningPlanObj() != null
                            && mCurrRunPlanBean.getRunningPlanObj().getYear() == sportPlan.year
                            && mCurrRunPlanBean.getRunningPlanObj().getMonth() == sportPlan.month
                            && mCurrRunPlanBean.getRunningPlanObj().getDay() == sportPlan.day
                            && mCurrRunPlanBean.getRunningPlanObj().getHour() == sportPlan.hour
                            && mCurrRunPlanBean.getRunningPlanObj().getMinute() == sportPlan.min
                            && mCurrRunPlanBean.getRunningPlanObj().getSecond() == sportPlan.sec) {
                        sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_CONNECTING_DEVICE_STATUS, "1");
                        //    sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_CONNECTINIHealthManagerViewG_DEVICE_SUCCESS_AND_SAME)
                    } else if (sportPlan.year == 0) {
                        //  sendConnectDeviceAllStatusToWeb()
                        sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_CONNECTING_DEVICE_STATUS, "2");
                    } else {
                        sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_CONNECTING_DEVICE_STATUS, "3");
                    }
                }

            } else {
                //    sendSetPlanToDevice(mCurrRunPlanBean!!, SportPlan.OPERATE_SEND)
                sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_CONNECTING_DEVICE_STATUS, "2");
            }

        }
    };

    public void sendNotificationToWeb(String name, String data ) {
        JsonObject json = new JsonObject();
        json.addProperty(name, data);
        sendNotificationToWeb(json, Constants.SEND_NOTIFICATION_TOWEB);
    }

    /**
     2.下发计划到设备 1 开始计划
     */
    public void  sendSetPlanToDevice(SetRunPlanH5Info bean, int type) {
        SportPlan sportPlan = new SportPlan();
        sportPlan.type = bean.getRunningPlanObj().getType();
        sportPlan.version = bean.getRunningPlanObj().getVersion();
        sportPlan.day = bean.getRunningPlanObj().getDay();
        sportPlan.year = bean.getRunningPlanObj().getYear();
        sportPlan.month = bean.getRunningPlanObj().getMonth();
        sportPlan.hour = bean.getRunningPlanObj().getHour();
        sportPlan.min = bean.getRunningPlanObj().getMinute();
        sportPlan.sec = bean.getRunningPlanObj().getSecond();
        sportPlan.day_num = bean.getRunningPlanObj().getDayNum();
        sportPlan.operate = type;//计划下发
        sportPlan.items = new ArrayList<SportPlan.PlanContent>();
        if (type == 2) {
            for (int i=0;i<bean.getRunningPlanObj().getDayPlanContent().size();i++) {
                SportPlan.PlanContent planContent = new SportPlan.PlanContent();
                planContent.item = new ArrayList<SportPlan.ActionContent>();
                planContent.num = bean.getRunningPlanObj().getDayPlanContent().get(i).getNum();
                planContent.type = bean.getRunningPlanObj().getDayPlanContent().get(i).getType();
                if (bean.getRunningPlanObj().getDayPlanContent().get(i) == null || bean.getRunningPlanObj().getDayPlanContent().get(i).getActionContent() == null) {
                    sportPlan.items.add(planContent);
                } else {
                    for (int x= 0; x < bean.getRunningPlanObj().getDayPlanContent().get(i).getActionContent().size();i++) {
                        SportPlan.ActionContent actionContent = new SportPlan.ActionContent();
                        actionContent.low_heart = bean.getRunningPlanObj().getDayPlanContent().get(i).getActionContent().get(x).getLowHeart();
                        actionContent.type = bean.getRunningPlanObj().getDayPlanContent().get(i).getActionContent().get(x).getType();
                        actionContent.height_heart =bean.getRunningPlanObj().getDayPlanContent().get(i).getActionContent().get(x).getHeightHeart();
                        actionContent.time = bean.getRunningPlanObj().getDayPlanContent().get(i).getActionContent().get(x).getTime();
                        planContent.item.add(actionContent);
                    }
                    sportPlan.items.add(planContent);
                }

            }
            BLEManager.setSportPlanDataSend(sportPlan);
        } else {
            BLEManager.setStartSportPlan(sportPlan);
        }
        sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_ISSET_PLAN_SUCCESS);
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
     * 3. app 通知固件运动过程切换
     *  operate; //0x01:开始运动 ，0x02：暂停运动 , 0x03:恢复运动 ，0x04：结束运动 0x05：切换动作
     * @param mRunPlanBean
     */
    public void sendOperationPlanToDevice(RunPlanToggleH5Info mRunPlanBean ) {
        NoticeSportActionToggle mNoticeSportActionToggle = new NoticeSportActionToggle();
        mNoticeSportActionToggle.day = Integer.parseInt(mRunPlanBean.getMessage().getDay());
        mNoticeSportActionToggle.type = Integer.parseInt(mRunPlanBean.getMessage().getType());
        mNoticeSportActionToggle.month = Integer.parseInt(mRunPlanBean.getMessage().getMonth());
        mNoticeSportActionToggle.training_offset = mRunPlanBean.getMessage().getTraining_offset();
        mNoticeSportActionToggle.year = Integer.parseInt(mRunPlanBean.getMessage().getYear());
        //时分秒使用当前时间的
        mNoticeSportActionToggle.hour = TimeUtil.getHour();
        mNoticeSportActionToggle.minute = TimeUtil.getMinute();
        mNoticeSportActionToggle.second = TimeUtil.getSecond();
        mStartTimeBean = new StartTimeBean(mNoticeSportActionToggle.hour,mNoticeSportActionToggle.minute,mNoticeSportActionToggle.second);
        if (mRunPlanBean.getMessage().getOperate() == 1) {
            registerRunBack();
        }
        if (mRunPlanBean.getMessage().getOperate() == 5) {
            //强制结束
            isEnd = true;
            mNoticeSportActionToggle.operate = 4;
        } else {
            isEnd = false;
            mNoticeSportActionToggle.operate = mRunPlanBean.getMessage().getOperate();
        }
        BLEManager.setNoticeSportActionToggle(mNoticeSportActionToggle);

    }

    public void registerRunBack() {
        setOnStartListener();
        setSportRunListener();
    }

    public void setSportRunListener(){
        mSportRunManager.setSportRunCallback(new SportRunManager.ISportRunCallBack() {
            @Override
            public void sportPause(boolean isSuccess) {
                LogTool.e(TAG, "sportPause: $isSuccess");
                if (isSuccess) {
                    if (view == null) {
                        return;
                    }
                    view.setSportStatus(false);
                }
            }

            @Override
            public void sportResume(boolean isSuccess) {
                LogTool.e(TAG, "sportResume: $isSuccess");
                if (isSuccess) {
                    if (view == null) {
                        return;
                    }
                    view.setSportStatus(true);
                }
            }

            @Override
            public void sportStop(boolean isSuccess, SportHealth sportHealth, List<LatLngBean> latLngBeanList) {
                LogTool.e(TAG, "sportStop: " + isSuccess + "" +
                        "," + sportHealth.toString());
                //根据不同类型旋转不同的详情
                if (isSuccess) {
                    mSportRunManager.close();
                    //运动停止成功
                    sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_STOP_TRAINING);
                    sendSportEndDataToWeb(sportHealth);
                } else {
                    //停止失败.不需要保存数据
                    mSportRunManager.close();
                    sendSportEndDataToWeb(sportHealth);
                    sendNotificationToWeb(H5ToAppConstant.RUNNING_PLAN_STOP_TRAINING);
                }
            }

            @Override
            public void sportRunning(SportHealth sportRunningBean, int heartRate, LatLngBean latLngDomain, int gpsSignValue, boolean isRemoveFistPoint, RunPlanCountDownTimeBean bean) {
                if (!isAccumulateRate && sportRunningBean.getTotalSeconds() % 120 == 0 || (sportRunningBean.getTotalSeconds() - 1) % 120 == 0 || (sportRunningBean.getTotalSeconds() + 1) % 120 == 0) {
                    //开始累积心率值
                    avgRate = 0;
                    avgRateFrequency = 0;
                    isAccumulateRate = true;
                }
                if (isAccumulateRate) {
                    avgRate += heartRate;
                    avgRateFrequency++;
                }
                if (sportRunningBean.getTotalSeconds() != 30 && sportRunningBean.getTotalSeconds() != 31 && sportRunningBean.getTotalSeconds() != 29 && ((sportRunningBean.getTotalSeconds() - 30) % 120 == 0 || (sportRunningBean.getTotalSeconds() - 31) % 120 == 0
                        || (sportRunningBean.getTotalSeconds() - 29) % 120 == 0) && avgRateFrequency != 0) {
                    isAccumulateRate = false;
                    sendTrainingAvgRateDataToWebTwo(avgRate / avgRateFrequency);
                    avgRate = 0;
                    avgRateFrequency = 0;
                    //     vibrate()
                }
                sendTrainingDataToWeb(sportRunningBean, heartRate, bean);
            }

            @Override
            public void sportFivePeaceAndSpeed(String realTimePeace, String realTimeSpeed) {
                String mRealTimePeace = realTimePeace;
                //骑行是速度 其余的都是配速 固件没有返回平均配速现在都是自己算（之后会改为实时配速和速度）
                //   SportLogHelper.saveSportLog(TAG, "sportFivePeaceAndSpeed: $realTimePeace,$realTimeSpeed")
                //时间（分钟）/距离（公里） 固件直接给值 0～99'99''
                if (realTimePeace.contains("'")) {
                    try {
                        String[] paces = realTimePeace.split("'");
                        int paceInt = Integer.parseInt(paces[0]);
                        if (paceInt > 99) {
                            mRealTimePeace = DateUtil.computeTimePace("99.99");
                        }
                    } catch (Exception e ) {
                        LogTool.e(TAG, "getAvgPace: $e");
                    }

                }
                speed = mRealTimePeace;
            }
        });
    }

    public String speed = null;


    /**
     9.训练中（推送训练数据）  runningPlanSendTrainingDataToWeb
     */
    public void  sendTrainingDataToWeb(SportHealth data ,int heartRate, RunPlanCountDownTimeBean bean) {

        boolean connected = BLEManager.isConnected();
        JsonObject json = new JsonObject();
        json.addProperty("count_hour", bean.getCount_hour());
        json.addProperty("count_minute", bean.getCount_minute());
        json.addProperty("count_second", bean.getCount_second());
        json.addProperty("progress", data.getTotalSeconds());
        json.addProperty("distance", data.getDistance());
        json.addProperty("bluetoothStatus", connected);
        json.addProperty("duration", data.getTotalSeconds());
        json.addProperty("real_time_speed", bean.getKm_speed());
        json.addProperty("calories", data.getNumCalories());
        json.addProperty("steps", data.getNumSteps());
        json.addProperty("heart_rate", heartRate);
        sendNotificationToWeb(json, H5ToAppConstant.RUNNING_PLAN_SEND_TRAINING_DATA_TO_WEB);
    }
    /**
     9.训练中（推送训练数据）  RUNNING_PLAN_SEND_AVERAGE_HEART_RATE_TO_WEB
     */
    public void sendTrainingAvgRateDataToWebTwo(int avgRate) {
        JsonObject json = new JsonObject();
        json.addProperty("averageHeartRate", avgRate);
        json.addProperty("status", 1);
        sendNotificationToWeb(json, H5ToAppConstant.RUNNING_PLAN_SEND_AVERAGE_HEART_RATE_TO_WEB);
    }

    /**
     12.实时传输给h5的设备连接状态 (所有，若切换其中一个状态则更新四个数据)
     */
    public void sendConnectDeviceAllStatusToWeb() {
        JsonObject json = new JsonObject();
        JsonObject jsonNew = new JsonObject();
        json.addProperty("bluetoothStatus", "YES");

        json.addProperty("boundNoConnectDeviceStatus", "YES");

        json.addProperty("equipmentBindingStatus", "YES");

        SupportFunctionInfo functionInfo = getSupportFunctionInfo();

        json.addProperty("isSupportRunningPlan", "YES");

        json.addProperty("isSupportHeartRate", "YES");

        json.addProperty("synchronizationStatus", "YES");

        jsonNew.addProperty("runningPlanAllDeviceStatus", json.toString());
        sendNotificationToWeb(jsonNew, Constants.SEND_NOTIFICATION_TOWEB);
    }




    /**
     12.运动结束报告数据
     */
    public void sendSportEndDataToWeb(SportHealth sportHealth) {
        JsonObject json = new JsonObject();
        json.addProperty("completionRate", sportHealth.getCompletionRate());
        json.addProperty("hrCompletionRate", sportHealth.getHrCompletionRate());
        json.addProperty("inClassCalories", sportHealth.getInClassCalories());
        json.addProperty("runningPullUp", sportHealth.getRunningPullUp());
        json.addProperty("totalSeconds", sportHealth.getTotalSeconds());
        json.addProperty("numCalories", sportHealth.getNumCalories());
        json.addProperty("numSteps", sportHealth.getNumSteps());
        json.addProperty("distance", sportHealth.getDistance());
        sendNotificationToWeb(json, H5ToAppConstant.RUNNING_PLAN_CURRENT_TRAINING_RECORD_TO_WEB);
    }

    public void setOnStartListener(){
        mSportRunManager.setSportStartCallback(new SportRunManager.ISportStartCallBack() {
            @Override
            public void sportStartSuccess() {
                if (view == null) {
                    return;
                }
                mIsCompleteRun = true;
                view.showSportStartSuccess();
            }

            @Override
            public void sportStartFailedByLowPower() {
                if (view == null) {
                    return;
                }
                view.showSportStartFailedLowPower();
                LogTool.e(TAG, "sportStartFaildByLowPower: ");
            }

            @Override
            public void sportStartFailed() {
                if (view == null) {
                    return;
                }
                view.showSportStartFail();
                LogTool.e(TAG, "sportStartFaild: ");
            }

            @Override
            public void sportChargePower() {

            }

            @Override
            public void sportRunInAlexa() {

            }

            @Override
            public void sportStartInCalling() {

            }
        });

    }

    public void  sportPlanuUnregisterSportPlanCallBack() {
        BLEManager.unregisterSportPlanCallBack(sportPlanCallBack);
    }

    public static class StartTimeBean{
        public int hour;
        public int minute;
        public int second;

        public int getHour() {
            return hour;
        }

        public int getMinute() {
            return minute;
        }

        public int getSecond() {
            return second;
        }

        public StartTimeBean(int hour, int minute, int second){
            this.hour = hour;
            this.minute = minute;
            this.second = second;
        }
    }
}
